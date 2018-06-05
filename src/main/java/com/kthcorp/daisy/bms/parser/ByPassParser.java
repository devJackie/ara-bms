package com.kthcorp.daisy.bms.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class ByPassParser extends ParserBase {

    protected ByPassParser(Map<String, Object> config) {
        super(config);
        log.info("Create bypass parser");
    }

    @Override
    public File process(String sourceFile) throws Exception {
        return new File(sourceFile);
    }
}