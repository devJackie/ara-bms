package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class NoneFileIO extends BaseFileIO {

    NoneFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
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
        return null;
    }

    @Override
    public List<Map<String, Object>> readMssPrgmScheList(File remoteFile) throws Exception {
        return null;
    }
}
