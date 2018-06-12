package com.kthcorp.daisy.bms.util;

import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
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

    private BmsMetaProperties bmsMetaProperties;

    public Map<String,Object> writeFileAdResSche(Map<String, Object> param) throws Exception {

        List<BmsDdAdResSche> resScheList = (List<BmsDdAdResSche>) param.get("list");
        bmsMetaProperties = (BmsMetaProperties) param.get("bmsMetaProperties");

        BufferedWriter bw = null;
        BufferedWriter fbw = null;
        Map<String,Object> result = new LinkedHashMap<>();
        File file = new File((String) bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-path"));
        try {
            // local
            // path : /Users/devjackie/ara-bms/bms/sche/
            // file : /Users/devjackie/ara-bms/bms/sche/2018042802_24.dat
            // dev, dp
            // path : /Users/devjackie/ara-bms/bms/sche/
            // file : /Users/devjackie/ara-bms/bms/sche/2018042802_24.dat

            if(!file.isDirectory()) { //디렉토리가 존재하지 않으면
                file.mkdirs();
            }

            // /Users/devjackie/ara-bms/bms/sche/2018042802_24.dat
            // /Users/devjackie/ara-bms/bms/sche/2018042900_02.dat

            String scheGubnFileName = null;
            if ("1".equals(param.get("sche_gubn"))) {
                scheGubnFileName = (String) bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-filename-1");
            } else if ("2".equals(param.get("sche_gubn"))) {
                scheGubnFileName = (String) bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-filename-2");
            }
            file = new File(bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-path") + scheGubnFileName);

            String scheGubnFinFileName = null;
            if ("1".equals(param.get("sche_gubn"))) {
                scheGubnFinFileName = (String) bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-fin-filename-1");
            } else if ("2".equals(param.get("sche_gubn"))) {
                scheGubnFinFileName = (String) bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-fin-filename-2");
            }

            File finFile = new File(bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-fin-path") + scheGubnFinFileName);

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
            result.put("filePath", file);

            // .FIN 파일 생성
            if (file.exists()) {
                fbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-path") + scheGubnFinFileName),"UTF-8"));
                fbw.write("");

                result.put("finFilePath", finFile);
            }
        } catch (Exception e) {
            result.put("filePath", null);
            result.put("finFilePath", null);
            throw e;
        } finally {
            if (bw != null) {
                try {
                    CollectorUtil.quietlyClose(bw);
                } catch (Exception e) {
                    throw e;
                }
            }
            if (fbw != null) {
                try {
                    CollectorUtil.quietlyClose(fbw);
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return result;
    }
}
