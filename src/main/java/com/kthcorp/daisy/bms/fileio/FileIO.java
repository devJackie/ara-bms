package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 10..
 */
public interface FileIO {

    List<FileIOInfo> readAmeobaRecFileList(List<RemoteFileInfo> remoteFiles) throws Exception;

    List<Map<String, Object>> readAmeobaIdxRecInfoList(String[] fileArray) throws Exception;

    List<Map<String, Object>> readAtsAdScheList(File remoteFile) throws Exception;

    List<Map<String, Object>> readMssPrgmScheList(File remoteFile) throws Exception;
}
