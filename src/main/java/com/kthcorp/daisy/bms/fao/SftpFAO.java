package com.kthcorp.daisy.bms.fao;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 25..
 */
@Slf4j
public class SftpFAO implements RemoteFAO {

    public static final String CHANNEL_SFTP = "sftp";
    public static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    private JSch jsch;
    private Session session;
    private ChannelSftp sftpClient;

    private String hostname = "localhost";
    private int port = 22;
    private String username = "";
    private String password = "";

    private String fingerPrint;


    public SftpFAO(Properties properties) throws Exception {
        this.hostname = (String) properties.get("server");
        this.port = (int) properties.get("port");
        this.username = (String) properties.get("id");
        this.password = (String) properties.get("password");
        log.debug("hostname: {}, port: {}, username: {}, password: {}, limitRate: {}", hostname, port, username, password);

        connect();
    }

    @Override
    public boolean isAvailable() {
        return (sftpClient != null) && sftpClient.isConnected() && !sftpClient.isClosed() && (session != null) && session.isConnected();
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath) throws Exception {
        ArrayList<RemoteFileInfo> ret = new ArrayList<RemoteFileInfo>();
        try {
            Vector vv = sftpClient.ls(remotePath);
            if (vv != null) {
                for (int ii = 0; ii < vv.size(); ii++) {
                    Object obj = vv.elementAt(ii);
                    if (obj instanceof LsEntry) {
                        LsEntry entry = (LsEntry) obj;
                        if (!entry.getAttrs().isDir()) {
                            RemoteFileInfo remoteFile = new RemoteFileInfo();
                            remoteFile.setFileName(entry.getFilename());
                            remoteFile.setModifyTime((long) entry.getAttrs().getMTime());
                            remoteFile.setSize(entry.getAttrs().getSize());
                            remoteFile.setParent(remotePath);
                            remoteFile.setAbsolutePath(remotePath + (remotePath.endsWith("/") ? "" : "/") + entry.getFilename());
                            ret.add(remoteFile);
                        }
                    }
                }
            }
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw e;
            }
        }
        return ret;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p) throws Exception {
        ArrayList<RemoteFileInfo> ret = new ArrayList<RemoteFileInfo>();
        try {
            Vector vv = sftpClient.ls(remotePath);
            if (vv != null) {
                for (int ii = 0; ii < vv.size(); ii++) {
                    Object obj = vv.elementAt(ii);
                    if (obj instanceof LsEntry) {
                        LsEntry entry = (LsEntry) obj;
                        if (!entry.getAttrs().isDir()) {
                            RemoteFileInfo remoteFile = new RemoteFileInfo();
                            remoteFile.setFileName(entry.getFilename());
                            remoteFile.setModifyTime((long) entry.getAttrs().getMTime());
                            remoteFile.setSize(entry.getAttrs().getSize());
                            remoteFile.setParent(remotePath);
                            remoteFile.setAbsolutePath(remotePath + (remotePath.endsWith("/") ? "" : "/") + entry.getFilename());
                            if (p == null || p.matcher(remoteFile.getFileName()).matches()) {
                                ret.add(remoteFile);
                            }
                        }
                    }
                }
            }
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw e;
            }
        }
        log.debug("sftpClient.ls {} : {}", remotePath, ret);
        return ret;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, String fileScanRange, String datePattern) throws Exception {
        int dateRange = 0;
        if (fileScanRange.contains("d")) {
            dateRange = Integer.parseInt(fileScanRange.substring(0, fileScanRange.indexOf("d")));
        }
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        for (int i = 0; i >= dateRange; i--) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.add(Calendar.DATE, i);
            dateList.add(sdf.format(calendar.getTime()));
        }

        ArrayList<RemoteFileInfo> ret = new ArrayList<RemoteFileInfo>();
        try {

            Vector vv = sftpClient.ls(remotePath);
            if (vv != null) {
                for (int ii = 0; ii < vv.size(); ii++) {
                    Object obj = vv.elementAt(ii);
                    if (obj instanceof LsEntry) {
                        LsEntry entry = (LsEntry) obj;
                        if (!entry.getAttrs().isDir()) {
                            RemoteFileInfo remoteFile = new RemoteFileInfo();
                            remoteFile.setFileName(entry.getFilename());
                            remoteFile.setModifyTime((long) entry.getAttrs().getMTime());
                            remoteFile.setSize(entry.getAttrs().getSize());
                            remoteFile.setParent(remotePath);
                            remoteFile.setAbsolutePath(remotePath + (remotePath.endsWith("/") ? "" : "/") + entry.getFilename());
                        }
                    }
                }
            }
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw e;
            }
        }
        List<RemoteFileInfo> newRet = new ArrayList<>();
        ret.stream().forEach(x -> {
            for (String date : dateList) {
                if(x.getFileName().indexOf(date) > 0)
                    newRet.add(x);
            }
        });
        return newRet;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p, String fileScanRange, String datePattern) throws Exception {
        int dateRange = 0;
        if (fileScanRange.contains("d")) {
            dateRange = Integer.parseInt(fileScanRange.substring(0, fileScanRange.indexOf("d")));
        }
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        for (int i = 0; i >= dateRange; i--) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.add(Calendar.DATE, i);
            dateList.add(sdf.format(calendar.getTime()));
        }

        ArrayList<RemoteFileInfo> ret = new ArrayList<RemoteFileInfo>();
        try {

            Vector vv = sftpClient.ls(remotePath);
            if (vv != null) {
                for (int ii = 0; ii < vv.size(); ii++) {
                    Object obj = vv.elementAt(ii);
                    if (obj instanceof LsEntry) {
                        LsEntry entry = (LsEntry) obj;
                        if (!entry.getAttrs().isDir()) {
                            RemoteFileInfo remoteFile = new RemoteFileInfo();
                            remoteFile.setFileName(entry.getFilename());
                            remoteFile.setModifyTime((long) entry.getAttrs().getMTime());
                            remoteFile.setSize(entry.getAttrs().getSize());
                            remoteFile.setParent(remotePath);
                            remoteFile.setAbsolutePath(remotePath + (remotePath.endsWith("/") ? "" : "/") + entry.getFilename());
                            if (p == null || p.matcher(remoteFile.getFileName()).matches()) {
                                ret.add(remoteFile);
                            }
                        }
                    }
                }
            }
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw e;
            }
        }
        List<RemoteFileInfo> newRet = new ArrayList<>();
        ret.stream().forEach(x -> {
            for (String date : dateList) {
                if(x.getFileName().indexOf(date) > 0)
                    newRet.add(x);
            }
        });
        return newRet;
    }

    @Override
    public InputStream getInputStream(String remote) throws Exception {
        return sftpClient.get(remote);
    }

    @Override
    public OutputStream getOutputStream(String remote) throws Exception {
        return sftpClient.put(remote);
    }

    @Override
    public void close() {
        try {
            if (sftpClient != null) {
                sftpClient.disconnect();
                sftpClient.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            sftpClient = null;
            session = null;
        }

    }

    @Override
    public boolean copyToLocal(String remoteFile, String localFilePath) throws Exception {
        String tmpFile = localFilePath + ".tmp";

        sftpClient.get(remoteFile, tmpFile);
        if (new File(tmpFile).exists()) {
            new File(tmpFile).renameTo(new File(localFilePath));
        }
        return new File(localFilePath).exists();
    }

    @Override
    public boolean copyToRemote(String localFile, String remotePath) throws Exception {
        sftpClient.put(localFile, remotePath);
        return true;
    }

    private void connect() throws Exception {
        log.debug("sftp connect");
        this.jsch = new JSch();
        this.session = jsch.getSession(username, hostname);
        Properties hash = new Properties();
        hash.put(STRICT_HOST_KEY_CHECKING, "no");
        this.session.setConfig(hash);
        this.session.setPort(port);
        this.session.setPassword(password);
        this.session.connect();
        log.debug("sftp session.connected");
        if ((fingerPrint != null) && !session.getHostKey().getFingerPrint(jsch).equals(fingerPrint)) {
            throw new RuntimeException("Invalid Fingerprint");
        }
        Channel channel = session.openChannel(CHANNEL_SFTP);
        channel.connect();
        log.debug("sftp channel.connected");
        sftpClient = (ChannelSftp) channel;

    }

}