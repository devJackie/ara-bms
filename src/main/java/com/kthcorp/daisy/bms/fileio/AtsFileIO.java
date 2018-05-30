package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class AtsFileIO extends BaseFileIO {

    AtsFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        super(config, bmsMetaProperties);
    }

    @Override
    public List<FileIOInfo> readAmeobaRecFileList(List<RemoteFileInfo> idxFiles) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> readAmeobaIdxRecInfoList(String[] fileArray) throws Exception {
      return null;
    }

    @Override
    public List<Map<String, Object>> readAtsAdScheList(File remoteFile) throws Exception {
        log.debug("config : {}", config);

        List<Map<String, Object>>  mapList = new ArrayList<>();
        Scanner sc1 = null;
        Scanner sc2 = null;
        // https://netjs.blogspot.kr/2016/06/reading-delimited-file-in-java-using-scanner.html
        try {
            sc1 = new Scanner(new FileInputStream(remoteFile), "UTF-8");
            while(sc1.hasNextLine()) {
                String line = sc1.nextLine();
                sc2 = new Scanner(line);
                sc2.useDelimiter("\\|");
                Map<String, Object> map = new LinkedHashMap<>();
                while(sc2.hasNext()) {
                    map.put("yyyymmdd", sc2.hasNext() ? sc2.next() : "");
                    map.put("ch_nm", sc2.hasNext() ? sc2.next() : "");
                    map.put("brdcst_dt", sc2.hasNext() ? sc2.next() : "");
                    map.put("hhmmss", sc2.hasNext() ? sc2.next() : "");
                    map.put("ch_id", sc2.hasNext() ? sc2.next() : "");
                    map.put("otv_ch_no", sc2.hasNext() ? sc2.next() : "");
                    map.put("ots_ch_no", sc2.hasNext() ? sc2.next() : "");
                    map.put("ad_order", sc2.hasNext() ? sc2.next() : "");
                    map.put("ad_id", sc2.hasNext() ? sc2.next() : "");
                    map.put("ad_nm", sc2.hasNext() ? sc2.next() : "");
                    map.put("ad_length", sc2.hasNext() ? sc2.next() : "");
                    map.put("apln_form_id", sc2.hasNext() ? sc2.next() : "");
                }
                mapList.add(map);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            CollectorUtil.quietlyClose(sc2);
            CollectorUtil.quietlyClose(sc1);
        }
        return mapList;
    }
}
