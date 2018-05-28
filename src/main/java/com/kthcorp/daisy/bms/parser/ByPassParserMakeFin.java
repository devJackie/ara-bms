package com.kthcorp.daisy.bms.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class ByPassParserMakeFin extends ParserBase {

    protected ByPassParserMakeFin(Map<String, Object> config) {
        super(config);
        log.info("Create bypass make fin parser");
    }

    @Override
    public File process(String sourceFile) throws Exception {
        String finFilename = sourceFile.substring(0, sourceFile.lastIndexOf(".")) + ".fin";
        log.debug("sourceFile: {}, finFilename:{}",sourceFile, finFilename);
        new File(finFilename).createNewFile();
        return new File(finFilename);
    }
}
