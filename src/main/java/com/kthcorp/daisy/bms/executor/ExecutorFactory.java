package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Slf4j
@Configuration
public class ExecutorFactory {

    private final ApplicationContext context;

    @Value("${daisy.collector.baseWorkDir:work}")
    private String baseWorkDir;

    BmsMetaProperties bmsMetaProperties;

    @Autowired
    public ExecutorFactory(ApplicationContext context, BmsMetaProperties bmsMetaProperties) {
        this.context = context;
        this.bmsMetaProperties = bmsMetaProperties;
    }

    @Bean
    @Lazy
    @Scope("prototype")
    public CommonExecutor eventExecutor(Map<String, Object> config) throws Exception {
        log.debug("config -> {}", config);
        String indexType = (String) config.get("type");
        if ("storedRecInfo".equals(indexType)) {
            log.info("Create StoredRecInfoExecutor");
            return new StoredRecInfoExecutor(context, config);
        }
        throw new IllegalArgumentException("type: " + indexType);
    }

}
