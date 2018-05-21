package com.kthcorp.daisy.bms.indexstore;


import com.kthcorp.daisy.bms.ZkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Slf4j
@Configuration
public class IndexStoreFactory {

    private final ApplicationContext context;

    private final ZkClient zkClient;

    @Autowired
    public IndexStoreFactory(ApplicationContext context, ZkClient zkClient) {
        this.context = context;
        this.zkClient = zkClient;
    }

    @Bean
    @Lazy
    @Scope("prototype")
    public IndexStore indexStore(Map<String, Object> config) throws Exception {
        log.debug("create ZkIndexStore URI: {}", config.get("uri"));
        return new ZkIndexStore(zkClient, (String) config.get("uri"));
    }
}
