package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.SinkHandler;
import com.kthcorp.daisy.bms.fao.SourceHandler;
import com.kthcorp.daisy.bms.fileio.FileIO;
import com.kthcorp.daisy.bms.headerextractor.HeaderExtractor;
import com.kthcorp.daisy.bms.indexstore.IndexStore;
import com.kthcorp.daisy.bms.parser.ParserBase;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Slf4j
public abstract class BaseExecutor implements CommonExecutor {

    private static final String INDEX_CONFIG = "indexConfig";
    private static final String SOURCE_CONFIG = "sourceConfig";
    private static final String FILE_IO_CONFIG = "fileIOConfig";
    private static final String SINK_CONFIG = "sinkConfig";
    private static final String PARSER_CONFIG = "parserConfig";
    private static final String HEADER_EXTRACT_CONFIG = "headerExtractConfig";

    protected ApplicationContext context;
    protected Map<String, Object> config;
    protected BmsMetaProperties bmsMetaProperties;
    private final String baseWorkDir;
    protected IndexStore indexStore;
    final SourceHandler sourceHandler;
    final SinkHandler sinkHandler;
    private final List<ParserBase> parsers;
    private final List<HeaderExtractor> headerExtractors;
    final FileIO fileIO;
    final ExecuteService executeService;

    private String executeGroup;
    private String executeName;
    private Date downloadTime = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    BaseExecutor(ApplicationContext context, Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        this.context = context;
        this.config = config;
        this.bmsMetaProperties = bmsMetaProperties;
        this.baseWorkDir = context.getEnvironment().getProperty("daisy.collector.baseWorkDir");
        IndexStore indexStore = context.getBean(IndexStore.class, config.get(INDEX_CONFIG));
        SourceHandler sourceHandler = context.getBean(SourceHandler.class, config.get(SOURCE_CONFIG));
        FileIO fileIO = context.getBean(FileIO.class, config.get(FILE_IO_CONFIG));
        SinkHandler sinkHandler = context.getBean(SinkHandler.class, config.get(SINK_CONFIG));
        List<ParserBase> parsers = (List<ParserBase>) context.getBean("parsers", config.get(PARSER_CONFIG));
        List<HeaderExtractor> headerExtractors = (List<HeaderExtractor>) context.getBean("headerExtractors", config.get(HEADER_EXTRACT_CONFIG));
        ExecuteService executeService = context.getBean(ExecuteService.class, config);

        File baseWorkDir = new File(this.baseWorkDir);
        if (!baseWorkDir.exists()) {
            baseWorkDir.mkdir();
        }
        this.indexStore = indexStore;
        this.sourceHandler = sourceHandler;
        this.fileIO = fileIO;
        this.headerExtractors = headerExtractors;
        this.sinkHandler = sinkHandler;
        this.parsers = parsers;
        this.executeService = executeService;

        this.executeGroup = (String) config.get("executeGroup");
        this.executeName = (String) config.get("executeName");

        log.debug("config: {}", config);
    }

    abstract List<ExecuteFileInfo> getExecuteFileInfos() throws Exception;

    abstract void setIndex(ExecuteFileInfo executeFileInfo) throws Exception;

    protected void createHeaders(ExecuteFileInfo executeFileInfo) {
        for (HeaderExtractor headerExtractor : headerExtractors) {
            headerExtractor.extract(executeFileInfo);
        }
        executeFileInfo.getHeader().put("executeTime", executeFileInfo.getExecuteTime());
    }

    private boolean getSourceFile(ExecuteFileInfo executeFileInfo) throws Exception {
        log.info("{} download start", executeFileInfo.getSourceFile().getFileName());
        File executorWorkDir = new File(baseWorkDir + "/" + executeName);
        if(!executorWorkDir.exists() && !executorWorkDir.isDirectory()) {
            executorWorkDir.mkdirs();
        }
        executeFileInfo.setDownloadFile(new File(baseWorkDir + "/" + executeName + "/" + executeFileInfo.getSourceFile().getFileName()));
        log.info("{}", executeFileInfo);
        long time1 = System.currentTimeMillis();
        boolean result = sourceHandler.get(executeFileInfo.getSourceFile().getAbsolutePath(), executeFileInfo.getDownloadFile().getAbsolutePath());
        long time2 = System.currentTimeMillis();

        downloadTime.setTime(time2);
        executeFileInfo.setExecuteTime(sdf.format(downloadTime));
        executeFileInfo.setDownloadElapsedTime(time2 - time1);
        return result;
    }

    @Transactional
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Map<String, Object>> executeCollect() {

        // 작업 처리 결과 (true 성공, false 실패)
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("result", false);

        try {
            log.debug("start {}", "executeCollect");

            List<ExecuteFileInfo> executeFileInfos = getExecuteFileInfos();

            Collections.sort(executeFileInfos, Comparator.comparing(ExecuteFileInfo::getSourceFile));

            long totalTaskCount = executeFileInfos.stream().filter(x -> !x.isFinished()).count();
            log.info("Task count -> {}", totalTaskCount);
            // 수집할 대상이 없으면 true
            if (totalTaskCount == 0) {
                resMap.put("result", true);
            }
            for (ExecuteFileInfo executeFileInfo : executeFileInfos) {

                if (!executeFileInfo.isFinished()) {

                    try {
                        log.info("Start -> {}", executeFileInfo.getSourceFile().getFileName());

                        if (!getSourceFile(executeFileInfo)) {
                            throw new Exception("getSourceFile fail");
                        }

                        log.info("Download end: {}", executeFileInfo);

                        createHeaders(executeFileInfo);

                        log.info("Create headers: {}", executeFileInfo.getHeader());

                        // business logic flow
                        // 1. 아메바 .idx 파일 존재 체크, .FIN 가 있을 경우는 주기마다 체크 하지 않도록 구현
                        // 1-1. .mp4, .jpg 파일 존재 체크 (zookeeper index 에 파일 .mp4 저장)
                        // 1-2. 1 과 1-1 를 fileId 로 매핑 후 merge 하고 녹화된 파일정보 db 저장
                        // 1-3. .idx 가 없을 경우 프로세스 종료, 매분 또는 일정 주기마다 .idx 가 있는지 체크 (30분마다 또는 1시간마다 배치 실행할 예정 또는 sping boot 의 schedule 또는 cron 사용)
                        if ("storedAmoebaRecInfo".equalsIgnoreCase((String) config.get("type"))) {
                            executeService.executeAmoebaRecFileCollectTask(executeFileInfo, sinkHandler, parsers, fileIO);
                        } else if ("storedMediaRecInfo".equalsIgnoreCase((String) config.get("type"))) {
                            // 2. skylife 에서 제공하는 미디어 서버 광고 파일 수집 및 db 저장 (분당 ara 서버에 put 방식으로 진행 예정)
                            executeService.executeMediaRecFileCollectTask(executeFileInfo, sinkHandler, parsers, fileIO);
                        } else if ("storedAtsAdScheInfo".equalsIgnoreCase((String) config.get("type"))) {
                            // 3. 선천 epg 수집, 모듈 개발
                            executeService.executeAtsAdScheCollectTask(executeFileInfo, sinkHandler, parsers, fileIO);
                        } else if ("storedMssPrgmScheInfo".equalsIgnoreCase((String) config.get("type"))) {
                            // 4. mss 프로그램 epg 수집, 모듈 개발
                            executeService.executeMssPrgmScheCollectTask(executeFileInfo, sinkHandler, parsers, fileIO);
                        }
                        // 성공일 때만 true
                        resMap.put("result", true);
                    } catch (Exception e) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw e;
                    } finally {
                        executeFileInfo.setFinished(true);
                    }
                    setIndex(executeFileInfo);

                    log.info("{} end", executeFileInfo.getSourceFile().getAbsolutePath());
                } else {
                    log.debug("{} is not new file", executeFileInfo.getSourceFile());
                }
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            CollectorUtil.quietlyClose(sourceHandler);
            CollectorUtil.quietlyClose(sinkHandler);
        }
        return CompletableFuture.completedFuture(resMap);
    }

    @Transactional
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Map<String, Object>> executeWorkFlow() {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("result", false);

        log.debug("start {}", "executeWorkFlow");

        try {
            // 1. 상위 30개 채널 쿼리 get +  선천 광고 익일 epg 테이블 + 녹화파일 테이블 매핑, 녹화파일이 있으면 녹화파일은 제외
            // 1-1. mss 프로그램 epg 테이블 + 4번 선천 광고 익일 epg 테이블 매핑 (검증 필요)
            // (프로그램 종료시간 -15분을 start_dt, 15분후를 end_dt 로 기준 정함, 광고 epg 생성 되게 쿼리 생성 후 epg 데이터 db 저장)
            resMap = executeService.executeMakeAdScheTask();
        } catch (Exception e) {
            log.error("", e);
        } finally {

        }
        return CompletableFuture.completedFuture(resMap);
    }
}
