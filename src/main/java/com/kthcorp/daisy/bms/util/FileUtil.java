package com.kthcorp.daisy.bms.util;

import com.kthcorp.daisy.bms.repository.entity.BmsDdAdResSche;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 24..
 */
@Component
@Slf4j
public class FileUtil {

    public Map<String,Object> writeFileAdSches(Map<String, Object> param) throws Exception {
        BufferedWriter bw = null;
        FileWriter fw = null;
        Map<String,Object> result = new LinkedHashMap<>();
        File file = new File("/Users/devjackie/ara-monitoring/bms_sche/");
        try {
            // local
            // path : /Users/devjackie/ara-bms/bms_sche/
            // file : /Users/devjackie/ara-bms/bms_sche/2018042802_24.dat
            // dev, dp
            // path : /Users/devjackie/ara-bms/bms_sche/
            // file : /Users/devjackie/ara-bms/bms_sche/2018042802_24.dat

            List<BmsDdAdResSche> resScheList = (List<BmsDdAdResSche>) param.get("list");

            String yyyyMMddDir = DateUtil.getCurrentDayDateTime();
            file = new File("/Users/devjackie/ara-bms/bms_sche/");

            if(!file.isDirectory()) { //디렉토리가 존재하지 않으면
                file.mkdirs();
            }

            // /Users/devjackie/ara-bms/bms_sche/2018042802_24.dat
            // /Users/devjackie/ara-bms/bms_sche/2018042900_02.dat
            file = new File("/Users/devjackie/ara-bms/bms_sche/2018042802_24.dat");
            if (file.exists()) { // 파일이 존재하면
                log.info("file already exists, {}", file.getAbsolutePath());
                result.put("file", file);
                result.put("filePath", file.getAbsoluteFile().getAbsolutePath());
                return result;
            } else {
                file.createNewFile();

                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));

                for (int i = 0; i < resScheList.size(); i++) {
                    BmsDdAdResSche lines = resScheList.get(i);
                    List<String> strList = new ArrayList<>();
                    strList.add(lines.getYyyymmdd());
                    strList.add(lines.getSche_gubn());
                    strList.add(lines.getApln_form_id());
                    strList.add(lines.getAd_id());
                    strList.add(lines.getAd_nm());
                    strList.add(lines.getAd_length());
                    strList.add(lines.getCh_id());
                    strList.add(lines.getCh_no());
                    strList.add(lines.getCh_nm());
                    strList.add(lines.getBrdcst_dt());
                    strList.add(lines.getStart_dt());
                    strList.add(lines.getEnd_dt());
                    String[] strArray = strList.stream().toArray(x -> new String[x]);
                    String line = StringUtils.join(strArray, '\036');

                    bw.write(line);

                    if (i < resScheList.size() - 1) { // file 의 마지막 개행문자는 제외
                        bw.newLine();
                    }
                }
                result.put("filePath", file.getAbsoluteFile().getAbsolutePath());
            }
        } catch (Exception e) {
            if(!file.exists()) {
                log.error("exception -> file delete, {}", file.getAbsolutePath());
                file.delete();
            }
            result.put("filePath", null);
            throw e;
        } finally {
            if (bw != null) {
                try {
                    CollectorUtil.quietlyClose(bw);
                } catch (Exception e) {
                    throw e;
                }
            }
            if (fw != null) {
                try {
                    CollectorUtil.quietlyClose(fw);
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return result;
    }
}
