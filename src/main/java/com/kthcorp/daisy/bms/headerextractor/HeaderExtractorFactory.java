package com.kthcorp.daisy.bms.headerextractor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
@Configuration
public class HeaderExtractorFactory {

    @Bean
    @Scope("prototype")
    public List<HeaderExtractor> headerExtractors(List<Map<String, String>> configs) {
        List<HeaderExtractor> headerExtractors = new ArrayList<>();

        if(configs == null){
            headerExtractors.add(new CurrentTimeExtractor());
        } else {
            configs.forEach(x -> {
                String type = x.get("type");
                if ("date".equals(type) || "filenameDate".equals(type)) {
                    String regex = x.get("regex");
                    String datePattern = x.get("datePattern");
                    headerExtractors.add(new FilenameDateExtractor(regex, datePattern));
                    log.debug("createHeaders -> FilenameDateExtractor: {} => {}", regex, datePattern);
                } else if ("modifyTime".equals(type)) {
//                    String regex = x.get("regex");
//                    String datePattern = x.get("datePattern");
                    headerExtractors.add(new ModifyTimeExtractor());
                    log.debug("createHeaders -> ModifyTimeExtractor: {} => {}");
                } else if ("currentTime".equals(type)) {
                    headerExtractors.add(new CurrentTimeExtractor());
                    log.debug("createHeaders -> currentTime");
                } else if ("filenameRegex".equals(type)) {
                    String regex = x.get("regex");
                    String keys = x.get("keys");
                    headerExtractors.add(new FilenameRegexExtractor(regex, keys));
                    log.debug("createHeaders -> currentTime");
                }
            });
        }
        if (headerExtractors.size() == 0) {
            headerExtractors.add(new CurrentTimeExtractor());
        }
        return headerExtractors;
    }
}
