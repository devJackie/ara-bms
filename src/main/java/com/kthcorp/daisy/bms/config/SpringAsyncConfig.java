package com.kthcorp.daisy.bms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by devjackie on 2018. 5. 25..
 */
@Configuration
@EnableAsync
@Slf4j
public class SpringAsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor asyncExecutor() {
        log.debug("{}", "asyncExecutor!!!");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("BMS-");
        executor.initialize();
        return executor;
    }
}
