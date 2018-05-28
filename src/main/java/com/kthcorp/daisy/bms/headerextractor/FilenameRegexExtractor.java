package com.kthcorp.daisy.bms.headerextractor;

import com.kthcorp.daisy.bms.executor.ExecuteFileInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class FilenameRegexExtractor implements HeaderExtractor {

    private Pattern pattern;
    private String[] keys;

    public FilenameRegexExtractor(String regex, String keyStrings) {
        pattern = Pattern.compile(regex);
        this.keys = keyStrings.split(",");
    }

    @Override
    public void extract(ExecuteFileInfo event) {
        String source = event.getSourceFile().getFileName();
        Map<String, String> result = event.getHeader();

        log.debug("headerExtractor extract. source : {}", source);

        Matcher m = pattern.matcher(source);
        if (m.find()) {
            if (m.groupCount() != keys.length) {
                throw new RuntimeException("groupCount(" + m.groupCount() + ") != keys.length(" + keys.length + ")");
            }
            for (int i = 0; i < m.groupCount(); i++) {
                result.put(keys[i], m.group(i + 1));
            }
        }
    }
}
