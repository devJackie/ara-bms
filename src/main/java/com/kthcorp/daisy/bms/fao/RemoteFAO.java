package com.kthcorp.daisy.bms.fao;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 8..
 */
public interface RemoteFAO {

    boolean isAvailable();

    void close();

    List<RemoteFileInfo> getListRemoteFiles(String remotePath) throws Exception;

    List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p) throws Exception;

    List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p, String fileScanRange, String datePattern) throws Exception;

    boolean copyToLocal(String remote, String local) throws Exception;

    boolean copyToRemote(String localFile, String remotePath) throws Exception;

    InputStream getInputStream(String remote) throws Exception;

    OutputStream getOutputStream(String remote) throws Exception;
}
