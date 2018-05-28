package com.kthcorp.daisy.bms.parser;

import lombok.Data;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Data
public abstract class ParserBase implements Parser {

    protected String targetFilenameTemplate;
    protected Map<String, String> header  = new HashMap<>();
    protected final Map<String, Object> config;

    protected ParserBase(Map<String, Object> config){
        this.config = config;
        if(config != null) {
            addHeaders((Map<String, String>) config.get("headers"));
            setTargetFilenameTemplate((String) config.get("outputFilename"));
        }
    }

    public void addHeaders(Map<String, String> headers) {
        if(headers != null) {
            header.putAll(headers);
        }
    }

    protected String getOutputFilename(String sourceFilename) {
        String targetFilename;
        if (targetFilenameTemplate == null || targetFilenameTemplate.isEmpty()) {
            targetFilename = sourceFilename;
        } else {
            StrSubstitutor sub = new StrSubstitutor(header);
            targetFilename = sub.replace(targetFilenameTemplate);
        }
        return targetFilename;
    }
}
