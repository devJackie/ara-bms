package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FileIOFactory {


    private BmsMetaProperties bmsMetaProperties;

    @Autowired
    FileIOFactory(BmsMetaProperties bmsMetaProperties){
        this.bmsMetaProperties = bmsMetaProperties;
    }

    @Bean
    @Lazy
    @Scope("prototype")
    public FileIO fileIO(Map<String, Object> config) throws Exception {
        log.debug("config -> {}", config);
        String indexType = (String) config.get("type");
        if ("AMOEBA".equalsIgnoreCase(indexType)) {
            log.info("Create AmoebaFileIO");
            return new AmoebaFileIO(config, bmsMetaProperties);
        } else if ("none".equalsIgnoreCase(indexType)) {
            log.info("Create NoneFileIO");
            return new NoneFileIO(config, bmsMetaProperties);
        }
        throw new IllegalArgumentException("type: " + indexType);
    }
}
