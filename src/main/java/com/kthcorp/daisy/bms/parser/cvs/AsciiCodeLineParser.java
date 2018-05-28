package com.kthcorp.daisy.bms.parser.cvs;

import com.kthcorp.daisy.bms.parser.ParserBase;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class AsciiCodeLineParser extends ParserBase {

    private int asciiDelim;
    private String delim = null;
    private String textEncoding;

    public AsciiCodeLineParser(Map<String, Object> config){
        super(config);
        this.asciiDelim = (int) config.get("asciiDelim");
        this.delim = (String) config.get("delim");
        this.textEncoding = (String) config.get("textEncoding");
        log.info("Create AsciiCodeLine parser");
    }

    @Override
    public File process(String sourcePath) throws Exception {
        String line = null;

        File sourceFile = new File(sourcePath);

        String outputFilename = getOutputFilename(sourceFile.getName());
        File target = new File(sourceFile.getParent() + "/" + outputFilename);

        log.debug("AsciiCodeLineParser. source : {}, target : {}", sourceFile, target);

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), textEncoding));
        FileWriter fw = new FileWriter(target);
        try {
            while ((line = in.readLine()) != null) {
                fw.append(header.get("yyyyMMddHHmmss"));
                fw.append((char) asciiDelim);
                fw.append(sourceFile.getName());
                fw.append((char) asciiDelim);
                String[] lineArr = line.split(delim);
                for (int i = 0; i < lineArr.length; i++) {
                    fw.append(lineArr[i]);
                    if(i < lineArr.length - 1) {
                        fw.append((char) asciiDelim);
                    }
                }
                fw.append(System.lineSeparator());
            }
        } finally {
            CollectorUtil.quietlyClose(fw);
            CollectorUtil.quietlyClose(in);
        }
        return target;
    }
}