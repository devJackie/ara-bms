package com.kthcorp.daisy.bms.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

/**
 * Created by devjackie on 2018. 5. 24..
 */
@Component
public class DateUtil {

    /**
     * @author devjackie
     * @Description 현재 날짜 > year, month, day get
     */
    public static String getCurrentDayDateTime() throws Exception {

        String result = null;
        try {
            DecimalFormat df = new DecimalFormat("00");
            DateTime dateTime = new DateTime();
            result = StringUtils.join(dateTime.getYear(), df.format(dateTime.getMonthOfYear()), df.format(dateTime.getDayOfMonth()));
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 - 1 > prev day
     */
    public static String getPrevDayDateTime() throws Exception {

        String result = null;
        try {
            DateTime dt = new DateTime().minusDays(1).toDateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYYMMdd");
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 + 1 > next day
     */
    public static String getNextDayDateTime() throws Exception {

        String result = null;
        try {
            DateTime dt = new DateTime().plusDays(1).toDateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYYMMdd");
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }
}
