package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.fao.SourceHandler;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class MssFileIO extends BaseFileIO {

    MssFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
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
        return null;
    }

    @Override
    public List<Map<String, Object>> readMssPrgmScheList(File remoteFile) throws Exception {
        log.debug("config : {}", config);

        List<Map<String, Object>>  mapList = new ArrayList<>();
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(remoteFile), textEncoding));

            String line = null;
            while ((line = in.readLine()) != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                String[] array = line.split("\\036");
                map.put("yyyymmdd", array[0]);
                map.put("ch_no", array[1]);
                map.put("prgm_id", array[2]);
                map.put("prgm_nm", array[3]);
                map.put("screen_gubn", array[4]);
                map.put("prgm_start_dt", array[5]);
                map.put("prgm_end_dt", array[6]);
                mapList.add(map);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            CollectorUtil.quietlyClose(in);
        }
        return mapList;
    }
}
