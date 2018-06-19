package com.kthcorp.daisy.bms.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
    public static String getCurrentDay() throws Exception {

        String result;
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
    public static String getPrevDay() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().minusDays(1).toDateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
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
    public static String getNextDay() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().plusDays(1).toDateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 + 2 > next day
     */
    public static String getNext2Day() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().plusDays(2).toDateTime();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 파라미터 날짜 - 1 > prev day
     */
    public static String getPrevDay(String yyyyMMdd) throws Exception {

        String result;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
            DateTime dt = dtf.parseDateTime(yyyyMMdd).minusDays(1);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 파라미터 날짜 + 1 > next day
     */
    public static String getNextDay(String yyyyMMdd) throws Exception {

        String result;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
            DateTime dt = dtf.parseDateTime(yyyyMMdd).plusDays(1);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            fmt.parseDateTime(yyyyMMdd);
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 파라미터 날짜 + 2 > next day
     */
    public static String getNext2Day(String yyyyMMdd) throws Exception {

        String result;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
            DateTime dt = dtf.parseDateTime(yyyyMMdd).plusDays(2);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            fmt.parseDateTime(yyyyMMdd);
            result = fmt.print(dt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 > current month 의 첫번째 일자
     */
    public static String getFirstDayOfCurrentMonth() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().dayOfMonth().withMinimumValue();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = new LocalDate(dt).toString(fmt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 > current month 의 마지막 일자
     */
    public static String getLastDayOfCurrentMonth() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().dayOfMonth().withMaximumValue();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = new LocalDate(dt).toString(fmt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 - 1 month > prev month 의 첫번째 일자
     * @Ref https://gist.github.com/marti1125/7405008
     */
    public static String getFirstDayOfPrevMonth() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().minusMonths(1).dayOfMonth().withMinimumValue();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = new LocalDate(dt).toString(fmt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 현재 날짜 - 1 month > prev month 의 마지막 일자
     * @Ref https://gist.github.com/marti1125/7405008
     */
    public static String getLastDayOfPrevMonth() throws Exception {

        String result;
        try {
            DateTime dt = new DateTime().minusMonths(1).dayOfMonth().withMaximumValue();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = new LocalDate(dt).toString(fmt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 파라미터 날짜 - 1 month > prev month 의 첫번째 일자
     * @Ref https://gist.github.com/marti1125/7405008
     */
    public static String getFirstDayOfPrevMonth(String yyyyMMdd) throws Exception {

        String result;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
            DateTime dt = dtf.parseDateTime(yyyyMMdd).minusMonths(1).dayOfMonth().withMinimumValue();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = new LocalDate(dt).toString(fmt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * @author devjackie
     * @Description 파라미터 날짜 - 1 month > prev month 의 마지막 일자
     * @Ref https://gist.github.com/marti1125/7405008
     */
    public static String getLastDayOfPrevMonth(String yyyyMMdd) throws Exception {

        String result;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
            DateTime dt = dtf.parseDateTime(yyyyMMdd).minusMonths(1).dayOfMonth().withMaximumValue();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
            result = new LocalDate(dt).toString(fmt);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }
}
