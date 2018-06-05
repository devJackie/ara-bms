package com.kthcorp.daisy.bms.parser;

import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class TemplateLineParser extends ParserBase {

    private String delimiter = null;
    private String textEncoding;
    private String[] fields;

    public TemplateLineParser(Map<String, Object> config){
        super(config);
        this.fields = ((String) config.get("fieldsString")).split(",");
        this.delimiter = (String) config.get("delimiter");
        this.textEncoding = (String) config.get("textEncoding");
        log.info("Create TemplateLine parser");
    }

    @Override
    public File process(String sourcePath) throws Exception {
        String line = null;

        File sourceFile = new File(sourcePath);

        String outputFilename = getOutputFilename(sourceFile.getName());
        File target = new File(sourceFile.getParent() + "/" + outputFilename);

        log.debug("TemplateLineParser. source : {}, target : {}", sourceFile, target);

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), textEncoding));
        BufferedWriter bw = new BufferedWriter(new FileWriter(target));

        try {
            while ((line = in.readLine()) != null) {
                header.put("line", line);

                for (int i = 0; i < fields.length; i++) {
                    String value = header.get(fields[i]) == null ? "" : header.get(fields[i]);
                    bw.append(value);
                    if(i != fields.length) {
                        bw.append(delimiter);
                    }
                }

                bw.newLine();

                header.remove("line");
            }
        } finally {
            CollectorUtil.quietlyClose(bw);
            CollectorUtil.quietlyClose(in);
        }
        return target;
    }
}
