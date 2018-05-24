package com.kthcorp.daisy.bms.fao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Configuration
@Slf4j
public class RemoteHandlerFactory {

    @Bean
    @Scope("prototype")
    public SourceHandler sourceHandler(Map<String, Object> config) throws Exception {
        return new TemplatePathSourceHandler(config);
    }

    @Bean
    @Scope("prototype")
    public SinkHandler sinkHandler(Map<String, Object> config) throws Exception {
        return new TemplatePathSinkHandler(config);
    }
}
