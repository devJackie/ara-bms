package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;

import java.util.List;

/**
 * Created by devjackie on 2018. 5. 10..
 */
public interface FileIO {
    List<FileIOInfo> readFileIdxList(List<RemoteFileInfo> idxFiles) throws Exception;
}
