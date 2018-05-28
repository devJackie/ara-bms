package com.kthcorp.daisy.bms.headerextractor;

import com.kthcorp.daisy.bms.executor.ExecuteFileInfo;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class ModifyTimeExtractor implements HeaderExtractor {

    private static final SimpleDateFormat sdfyyyyMM = new SimpleDateFormat("yyyyMM");
    private static final SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat sdfyyyyMMddHH = new SimpleDateFormat("yyyyMMddHH");
    private static final SimpleDateFormat sdfyyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat sdfyyyy = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat sdfMM = new SimpleDateFormat("MM");
    private static final SimpleDateFormat sdfdd = new SimpleDateFormat("dd");
    private static final SimpleDateFormat sdfHH = new SimpleDateFormat("HH");
    private static final SimpleDateFormat sdfmm = new SimpleDateFormat("mm");
    private static final SimpleDateFormat sdfss = new SimpleDateFormat("ss");

    @Override
    public void extract(ExecuteFileInfo event) {
        Map<String, String> result = event.getHeader();
        long source = event.getSourceFile().getModifyTime();
        try {

            log.debug("ModifyTimeExtractor marches. source : {}", source);
            try {
                Date data = new Date(source);

                result.put("yyyyMM", sdfyyyyMM.format(data));
                result.put("yyyyMMdd", sdfyyyyMMdd.format(data));
                result.put("yyyyMMddHH", sdfyyyyMMddHH.format(data));
                result.put("yyyyMMddHHmmss", sdfyyyyMMddHHmmss.format(data));

                result.put("yyyy", sdfyyyy.format(data));
                result.put("MM", sdfMM.format(data));
                result.put("dd", sdfdd.format(data));
                result.put("HH", sdfHH.format(data));
                result.put("mm", sdfmm.format(data));
                result.put("ss", sdfss.format(data));

                result.put("date", sdfyyyyMMddHHmmss.format(data));
            } catch (Exception e) {
                log.error("", e);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
