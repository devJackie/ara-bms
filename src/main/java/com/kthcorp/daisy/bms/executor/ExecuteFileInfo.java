package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.fileio.FileIOInfo;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Data
@ToString
public class ExecuteFileInfo {

    private boolean isFinished = false;
    private boolean isForce = false;

    private boolean isSuccess = false;

    private Map<String, String> header = new HashMap<>();

    private String executeTime;

    private RemoteFileInfo sourceFile;

    private FileIOInfo recFileInfo;
//    private FileIOInfo recThumbFile;

    private File downloadFile;
    private long downloadElapsedTime;

    private Map<File, String> executeResult = new HashMap<>();

    public void putExecuteResult(File executedFile, String sinkPath) {
        executeResult.put(executedFile, sinkPath);
    }

}
