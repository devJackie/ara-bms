package com.kthcorp.daisy.bms.util;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Created by devjackie on 2018. 5. 24..
 */
@Component
@Slf4j
public class CommonUtil {

    public static String customYYYYMMddHHmmssDateTime() {

        String fmtTime = null;
        try {
            DateTime dt = new DateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYYMMddHHmmss");
            fmtTime = fmt.print(dt);
            log.debug("fmtTime : {}", fmtTime);
        } catch (Exception e) {
            log.error("", e);
        }
        return fmtTime;
    }

}
