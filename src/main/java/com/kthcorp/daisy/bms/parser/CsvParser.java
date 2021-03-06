package com.kthcorp.daisy.bms.parser;

import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class CsvParser extends ParserBase {

    private static final String ENCODING = "encoding";
    private String delim = null;
    private String textEncoding;

    public CsvParser(Map<String, Object> config) {
        super(config);

        String encoding = (String) config.get(ENCODING);
        String delim = (String) config.get("delim");
        if (delim == null) {
            delim = "|";
        }
        this.delim = delim;
        this.textEncoding = encoding;
        log.info("Create csv parser");
    }

    @Override
    public File process(String sourcePath) throws Exception {
        String line;

        File sourceFile = new File(sourcePath);

        String outputFilename = getOutputFilename(sourceFile.getName());

        File target = new File(sourceFile.getParent() + "/" + outputFilename);

        log.debug("CvsParser. source : {}, target : {}", sourceFile, target);

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), textEncoding));
        FileWriter fw = new FileWriter(target);

        try {
            while ((line = in.readLine()) != null) {
                fw.append(line);
                fw.append(System.lineSeparator());
            }
        } finally {
            CollectorUtil.quietlyClose(fw);
            CollectorUtil.quietlyClose(in);
        }
        return target;
    }
}
