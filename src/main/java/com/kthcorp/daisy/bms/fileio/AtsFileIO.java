package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.fao.SourceHandler;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class AtsFileIO extends BaseFileIO {

    AtsFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        super(config, bmsMetaProperties);
    }

    @Override
    public List<FileIOInfo> readAmeobaRecFileList(SourceHandler sourceHandler, List<RemoteFileInfo> remoteFiles) throws Exception {
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
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(remoteFile), textEncoding));

            String line = null;
            while ((line = in.readLine()) != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                String[] array = line.split("\\|");
                map.put("ots_ch_no", array[0]);
                map.put("otv_ch_no", array[1]);
                map.put("ch_nm", array[2]);
                map.put("brdcst_dt", array[3]);
                map.put("yyyymmdd", MapUtils.getString(map, "brdcst_dt") != null ? map.get("brdcst_dt") : "");
                map.put("ch_id", array[4]);
                map.put("formation_no", array[5]);
                map.put("formation_id", array[6]);
                map.put("hhmmss", array[7]);
                map.put("ad_order", array[8]);
//                map.put("ad_id", array[9].startsWith("AD", 0) ? array[9].substring(2) : array[9]); //prefix "AD" 문자열 제거
                map.put("ad_id", array[9]);
                map.put("ad_nm", array[10]);
                map.put("ad_length", array[11]);
                map.put("apln_form_id", array[12]);
                map.put("ad_type", array[13]);
                map.put("dt_regist", array[14]);
                mapList.add(map);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            CollectorUtil.quietlyClose(in);
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> readMssPrgmScheList(File remoteFile) throws Exception {
        return null;
    }
}
