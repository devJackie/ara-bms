package com.kthcorp.daisy.bms;

import com.kthcorp.daisy.bms.executor.CommonExecutor;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.repository.BmsInitDDLMapper;
import com.kthcorp.daisy.bms.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class BmsAppRunner implements ApplicationRunner {

    private final ApplicationContext context;
    private final static String MUTEX = "mutex";
    private final static String LINK_SERVER = "linkServer";
    private final ZkClient zkClient;
    private final BmsMetaProperties bmsMetaProperties;

    String executeGroup;
    String executeDate;
    CompletableFuture<Map<String, Object>> phase1 = null;
    CompletableFuture<Map<String, Object>> phase2 = null;
    private CommonExecutor executor = null;

    @Autowired
    public BmsAppRunner(ApplicationContext context, ZkClient zkClient, BmsMetaProperties bmsMetaProperties) {
        this.context = context;
        this.zkClient = zkClient;
        this.bmsMetaProperties = bmsMetaProperties;
    }

    @Autowired
    BmsInitDDLMapper bmsInitDDLMapper;

    String N1_DAY, N2_DAY;

    @Override
    public void run(ApplicationArguments args) {
        try {
            // Start the clock
            long start = System.currentTimeMillis();

            // business logic start
            if (args.getOptionValues("executeGroup") != null && args.getOptionValues("executeGroup").size() > 0) {
                executeGroup = args.getOptionValues("executeGroup").get(0);
            } else {
                executeGroup = (String) bmsMetaProperties.getBmsMeta().get("execute").get("default-execute-group");
            }
            log.info("executeGroup: {}", executeGroup);

            if (args.getOptionValues("executeDate") != null && args.getOptionValues("executeDate").size() > 0) {
                executeDate = args.getOptionValues("executeDate").get(0);
            }
            log.info("executeDate: {}", executeDate);

            // production 일 때 ppas partition 생성
            if (context.getEnvironment().getActiveProfiles() != null && context.getEnvironment().getActiveProfiles().length > 0) {
                log.debug("activeProfiles: {}", context.getEnvironment().getActiveProfiles()[0]);
                if (context.getEnvironment().getActiveProfiles()[0].equalsIgnoreCase("PRODUCTION")) {
                    if (executeDate != null && executeDate.length() > 0) { // 재처리 수동 날짜가 있을 시 --executeDate=${yyyyMMdd}
                        N1_DAY = DateUtil.getNextDay(executeDate);
                        N2_DAY = DateUtil.getNext2Day(executeDate);
                    } else {
                        N1_DAY = DateUtil.getNextDay();
                        N2_DAY = DateUtil.getNext2Day();
                    }
                    Map<String, String> map1 = new LinkedHashMap<>();
                    map1.put("p_date", "p" + N1_DAY);
                    map1.put("date", N1_DAY);

                    Map<String, String> map2 = new LinkedHashMap<>();
                    map2.put("p_date", "p" + N2_DAY);
                    map2.put("date", N2_DAY);
                    try {
                        bmsInitDDLMapper.addPartitionForPlus1day(map1);
                        bmsInitDDLMapper.addPartitionForPlus2day(map2);
                    } catch (BadSqlGrammarException e) {
                        log.error("{}", e.getMessage());
                    }
                }
            }

            Yaml yaml = new Yaml();
            Map rootConfig = yaml.load(new ClassPathResource("bms-collector.yml").getInputStream());
            Map<String, Object> executeGroupConfig = (Map<String, Object>) rootConfig.get(executeGroup);

            Map mutex = (Map) executeGroupConfig.get(MUTEX);
            if (mutex != null) {
                String groupMutexUri = (String) mutex.get("uri");
                if (!zkClient.acquire(groupMutexUri)) {
                    log.warn("Cannot acquire mutex");
                    System.exit(0);
                }
            }

            // collector
            if ((executeGroupConfig.get(LINK_SERVER)) != null &&
                    ((List<Map<String, String>>) executeGroupConfig.get(LINK_SERVER)).size() > 0) {
                ((List<Map<String, String>>) executeGroupConfig.get(LINK_SERVER)).forEach(x -> x.forEach(this::executeProcess));
                // business work flow
                phase2 = executor.executeWorkFlow();
                // Wait until they are all done
                CompletableFuture.allOf(phase1, phase2).join();
                log.info("Done --> phase1: {}, phase2: {}", phase1.get(), phase2.get());
            } else {
                log.info("linkServer -> executeGroup is not found, process termination");
            }

            if (mutex != null) {
                String groupMutexUri = (String) mutex.get("uri");
                zkClient.acquireRelease(groupMutexUri);
            }

            // Print results, including elapsed time
            log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void executeProcess(String profileName, String ymlPath) {
        try {
            Yaml yaml = new Yaml();
            log.debug("profileName: {}", profileName);
            log.debug("ymlPath: {}", ymlPath);
            Map config = yaml.load(new ClassPathResource(ymlPath).getInputStream());
            config.put("executeGroup", executeGroup);
            config.put("executeDate", executeDate);
            config.put("executeName", profileName);
            config.put("profileName", profileName);
            log.debug("config: {}", config);
            executor = context.getBean(CommonExecutor.class, config);
            phase1 = executor.executeCollect();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
