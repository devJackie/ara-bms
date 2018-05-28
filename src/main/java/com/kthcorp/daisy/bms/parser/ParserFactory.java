package com.kthcorp.daisy.bms.parser;

import com.kthcorp.daisy.bms.parser.cvs.AsciiCodeLineParser;
import com.kthcorp.daisy.bms.parser.cvs.CvsParser;
import com.kthcorp.daisy.bms.parser.cvs.TemplateLineParser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Configuration
@Slf4j
public class ParserFactory {

    private static final String TYPE = "type";

    private ParserBase getParser(Map<String, Object> config) {

        log.debug("Parser Config: {}", config);

        ParserBase parserBase;
        String type = (String) config.get(TYPE);

        if ("DAT".equalsIgnoreCase(type) || "TXT".equalsIgnoreCase(type) || "CSV".equalsIgnoreCase(type)) {
            parserBase = new CvsParser(config);
        } else if ("NONE".equalsIgnoreCase(type) || "BYPASS".equalsIgnoreCase(type)) {
            parserBase = new ByPassParser(config);
        } else if ("BYPASS_MAKE_FIN".equalsIgnoreCase(type)) {
            parserBase = new ByPassParserMakeFin(config);
        } else if ("TEMPLATELINEPARSER".equalsIgnoreCase(type)) {
            parserBase = new TemplateLineParser(config);
        } else if ("ASCIICODELINEPARSER".equalsIgnoreCase(type)) {
            parserBase = new AsciiCodeLineParser(config);
        } else {
            throw new IllegalArgumentException("not support type : " + type);
        }
        return parserBase;
    }

    @Bean(name = "parsers")
    @Scope("prototype")
    public List parsers(List<Map<String, Object>> config) throws Exception {
        List<ParserBase> parsers = new ArrayList<>();
        config.forEach(x -> parsers.add(getParser(x)));
        if(parsers.size() == 0){
            parsers.add(new ByPassParser(new HashMap<>()));
        }
        return parsers;
    }
}
