package com.kthcorp.daisy.bms.fao;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Slf4j
public class LocalFAO implements RemoteFAO {

    private static final String LOCAL = "local";
    private static String TYPE = null;
    public LocalFAO(Properties properties) {
        this.TYPE = properties.getProperty("type", "");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath) throws Exception {
        log.info(remotePath);

        File[] path = new File(remotePath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });
        List<RemoteFileInfo> resultList = new ArrayList<>();
        if(path != null) {
            for (File file : path) {
                RemoteFileInfo remoteFile = new RemoteFileInfo();
                remoteFile.setFileName(file.getName());
                remoteFile.setParent(file.getParent());
                remoteFile.setAbsolutePath(file.getAbsolutePath());
                remoteFile.setModifyTime(file.lastModified());
                remoteFile.setSize(file.length());
                resultList.add(remoteFile);
            }
        }
        return resultList;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, final Pattern p) throws Exception {
        log.info(remotePath);

        File[] path = new File(remotePath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(p != null) {
                    return p.matcher(name).matches();
                } else {
                    return true;
                }
            }
        });
        List<RemoteFileInfo> resultList = new ArrayList<>();
        if(path != null) {
            for (File file : path) {
                RemoteFileInfo remoteFile = new RemoteFileInfo();
                remoteFile.setFileName(file.getName());
                remoteFile.setParent(file.getParent());
                remoteFile.setAbsolutePath(file.getAbsolutePath());
                remoteFile.setModifyTime(file.lastModified());
                remoteFile.setSize(file.length());
                resultList.add(remoteFile);
            }
        }
        return resultList;
    }

    @Override
    public boolean copyToLocal(String remoteFile, String localFilePath) throws Exception {
        File remote = new File(remoteFile);
        File tmpFile = null;
        String modLocalFilePath = null;
        // type 이 local 일 경우
        if (LOCAL.equalsIgnoreCase(this.TYPE)) {
            File tmpLocalFile = new File(localFilePath);
            if (StringUtils.isEmpty(Files.getFileExtension(localFilePath))) {
                // sink type 이 local 일 경우 (localFilePath -> 확장자가 있는 파일명 포함되지 않음)
                tmpFile = new File(tmpLocalFile, remote.getName() + ".tmp");
                tmpFile.getParentFile().mkdirs();
                modLocalFilePath = localFilePath;
            } else {
                // source type 이 local 이고, yml 파일이 BP_ 또는 BYPASS_ 로 시작하는 경우 (localFilePath -> 확장자가 있는 파일명 포함됨)
                tmpFile = new File(tmpLocalFile.getParent(), remote.getName() + ".tmp");
                tmpFile.getParentFile().mkdirs();
                modLocalFilePath = tmpLocalFile.getParent();
            }
        } else {
            tmpFile = new File(localFilePath, remote.getName() + ".tmp");
            tmpFile.getParentFile().mkdirs();
            modLocalFilePath = localFilePath;
        }
        Files.copy(remote, tmpFile);
        if(tmpFile.exists()){
            File localFile = new File(modLocalFilePath, remote.getName());
            Files.move(tmpFile, localFile);
        }
        return new File(modLocalFilePath).exists();
    }

    @Override
    public boolean copyToRemote(String localFile, String remotePath) throws Exception {
        return copyToLocal(localFile, remotePath);
    }
}
