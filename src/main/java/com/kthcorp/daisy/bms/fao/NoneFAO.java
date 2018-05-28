package com.kthcorp.daisy.bms.fao;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class NoneFAO implements RemoteFAO {

    public NoneFAO(Properties properties) {
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath) throws Exception {
        return null;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p) throws Exception {
        return null;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, String fileScanRange, String datePattern) throws Exception {
        return null;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p, String fileScanRange, String datePattern) throws Exception {
        return null;
    }

    @Override
    public boolean copyToLocal(String remote, String local) throws Exception {
        return false;
    }

    @Override
    public boolean copyToRemote(String localFile, String remotePath) throws Exception {
        return false;
    }

    @Override
    public InputStream getInputStream(String remote) throws Exception {
        return null;
    }

    @Override
    public OutputStream getOutputStream(String remote) throws Exception {
        return null;
    }
}
