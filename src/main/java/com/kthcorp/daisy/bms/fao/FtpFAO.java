package com.kthcorp.daisy.bms.fao;

import com.kthcorp.daisy.bms.util.DaisyFTPFileEntryParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 25..
 */
@Slf4j
public class FtpFAO implements RemoteFAO {

    private FTPClient ftpClient = null;
    private String hostname = "localhost";
    private int port = 21;
    private String username = "";
    private String password = "";
    private String encoding;
    private FTPClientConfig ftpClientConfig;
    private String dataConnectionMode;
    private String limitRate;
    protected ExecutorService callTimeoutPool;
    private long callTimeout = 1000 * 60 * 3;

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                log.debug("SERVER: {}", aReply);
            }
        }
    }

    public FtpFAO(Properties properties) {
        this.hostname = (String) properties.get("server");
        this.port = (int) properties.get("port");
        this.username = (String) properties.get("id");
        this.password = (String) properties.get("password");
        this.encoding = (String) properties.get("controlEncoding");
        this.dataConnectionMode = (String) properties.get("dataConnectionMode");
        this.limitRate = (String) properties.get("limitRate");

        String systemKey = (String) properties.get("systemKey");
        String defaultDateFormatStr = (String) properties.get("defaultDateFormatStr");
        String recentDateFormatStr = (String) properties.get("recentDateFormatStr");

        this.ftpClientConfig = new FTPClientConfig(systemKey == null ? FTPClientConfig.SYST_UNIX : systemKey);
        if (defaultDateFormatStr != null) {
            ftpClientConfig.setDefaultDateFormatStr(defaultDateFormatStr);
            log.debug("defaultDateFormatStr: '{}'", defaultDateFormatStr);
        }
        if (recentDateFormatStr != null) {
            ftpClientConfig.setRecentDateFormatStr(recentDateFormatStr);
            log.debug("recentDateFormatStr: '{}'", recentDateFormatStr);
        }

        if (recentDateFormatStr != null) {
            ftpClientConfig.setRecentDateFormatStr(recentDateFormatStr);
            log.debug("recentDateFormatStr: '{}'", recentDateFormatStr);
        }
        callTimeoutPool = Executors.newFixedThreadPool(1);
    }

    @Override
    public boolean isAvailable() {
        return ftpClient.isAvailable();
    }

    @Override
    public void close() {
        log.info("close");
        try {
            if (ftpClient.isAvailable()) {
                ftpClient.logout();
            }
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
            }
            callTimeoutPool.shutdownNow();
        } catch (Exception e) {
        } finally {
            ftpClient = null;
        }
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath) throws Exception {
        return getListRemoteFiles(remotePath, null);
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p) throws Exception {
        remotePath = remotePath.endsWith("/") ? remotePath.substring(0, remotePath.length() - 1) : remotePath;
        statusCheck();
        changeMode();
        FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
        List<RemoteFileInfo> resultList = new ArrayList<>(ftpFiles.length);
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                RemoteFileInfo remoteFile = new RemoteFileInfo();
                remoteFile.setFileName(ftpFile.getName().trim());
                remoteFile.setAbsolutePath(remotePath + "/" + ftpFile.getName().trim());
                remoteFile.setSize(ftpFile.getSize());
                remoteFile.setParent(remotePath);
                remoteFile.setModifyTime(ftpFile.getTimestamp().getTimeInMillis());
                if (p == null || p.matcher(remoteFile.getFileName()).matches()) {
                    resultList.add(remoteFile);
                }
            }
        }
        log.debug("resultList - {}", resultList);

        return resultList;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, String fileScanRange, String datePattern) throws Exception {
        return null;
    }

    @Override
    public List<RemoteFileInfo> getListRemoteFiles(String remotePath, Pattern p, String fileScanRange, String datePattern) throws Exception {
        remotePath = remotePath.endsWith("/") ? remotePath.substring(0, remotePath.length() - 1) : remotePath;
        statusCheck();
        changeMode();
        FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
        List<RemoteFileInfo> resultList = new ArrayList<>(ftpFiles.length);

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

        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
//                logger.info("remotePath + ftpFile.getName() -> {}", remotePath + "/" + ftpFile.getName());
                RemoteFileInfo remoteFile = new RemoteFileInfo();
                remoteFile.setFileName(ftpFile.getName().trim());
                remoteFile.setAbsolutePath(remotePath + "/" + ftpFile.getName().trim());
                remoteFile.setSize(ftpFile.getSize());
                remoteFile.setParent(remotePath);
                remoteFile.setModifyTime(ftpFile.getTimestamp().getTimeInMillis());
                if (p == null || p.matcher(remoteFile.getFileName()).matches()) {
                    resultList.add(remoteFile);
                }
            }
        }

        List<RemoteFileInfo> newResultList = new ArrayList<>();
        resultList.stream().forEach(x -> {
            for (String date : dateList) {
                if(x.getFileName().indexOf(date) > 0)
                    newResultList.add(x);
            }
        });
        return newResultList;
    }

    @Override
    public InputStream getInputStream(String remote) throws Exception {
        statusCheck();
        changeMode();
        return ftpClient.retrieveFileStream(remote);
    }

    @Override
    public OutputStream getOutputStream(String remote) throws Exception {
        statusCheck();
        changeMode();
        return ftpClient.storeFileStream(remote);
    }

    @Override
    public boolean copyToLocal(String remoteFile, String localFilePath) throws Exception {
        log.debug("remoteFile {} , localFilePath {}", remoteFile, localFilePath);
        String tmpFile = localFilePath + ".tmp";
        statusCheck();
        changeMode();

        boolean result = false;
        final long size = getListRemoteFiles(remoteFile).get(0).getSize();
        try (InputStream source = getInputStream(remoteFile);
             OutputStream dest = new FileOutputStream(tmpFile)) {

            long total = 0;

            while (total != size) {
                 int dnSize = callWithTimeout(() -> {
                    byte[] buffer = new byte[1024 * 4];
                    int numBytes = source.read(buffer);
                    if (numBytes == 0) {
                        int singleByte = source.read();
                        if (singleByte < 0) {
                            return 0;
                        } else {
                            dest.write(singleByte);
                            return 1;
                        }
                    } else if (numBytes == -1) {
                        return -1;
                    } else {
                        dest.write(buffer, 0, numBytes);
                        return numBytes;
                    }
                });
                 if(dnSize == -1){
                     log.info("{}/{} Err...",total, size);
                     //  이미 -1(EOF) 을 반환했는데 total < size 이면 무한 loop 가 되므로, loop 빠져나오는 로직 추가
                     result = true;
                     break;
                 } else {
                     total += dnSize;
                 }
            }
            ftpClient.completePendingCommand();
        }
        if (result) {
            return false;
        } else {
            Files.move(new File(tmpFile).toPath(), new File(localFilePath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
    }

    @Override
    public boolean copyToRemote(String localFile, String remotePath) throws Exception {
        statusCheck();
        ftpCreateDirectoryTree(ftpClient, remotePath);
        log.debug("create dirTree: {}", remotePath);
        changeMode();
        return ftpClient.storeFile(remotePath + new File(localFile).getName(), new FileInputStream(new File(localFile)));
    }

    private void statusCheck() throws IOException {
        if (ftpClient == null) {
            ftpClient = new FTPClient();
            ftpClient.configure(ftpClientConfig);
            ftpClient.setParserFactory(new DaisyFTPFileEntryParserFactory(ftpClientConfig));
            ftpClient.setAutodetectUTF8(true);
            if (encoding != null && !encoding.trim().isEmpty()) {
                log.debug("Set encoding: {}", encoding);
                ftpClient.setControlEncoding(encoding);
            }

        }
        if (!ftpClient.isAvailable()) {
            ftpClient.connect(this.hostname, this.port);
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                throw new IOException("Connect failed!");
            }
            if (!ftpClient.login(this.username, this.password)) {
                ftpClient.disconnect();
                throw new IllegalArgumentException("Login fail. Check username and password!");
            }
            ftpClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        }
    }

    public void changeMode() {
        if (dataConnectionMode != null || "active".equalsIgnoreCase(dataConnectionMode)) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
    }

    private void ftpCreateDirectoryTree(FTPClient client, String dirTree) throws IOException {
        boolean dirExists = true;
        //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
        String[] directories = dirTree.split("/");
        changeMode();
        client.changeWorkingDirectory("/");
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (dirExists) {
                    changeMode();
                    dirExists = client.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    changeMode();
                    if (!client.makeDirectory(dir)) {
                        throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                    changeMode();
                    if (!client.changeWorkingDirectory(dir)) {
                        throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                }
            }
        }
    }


    private <T> T callWithTimeout(final CallRunner<T> callRunner) throws Exception {
        Future<T> future = callTimeoutPool.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return callRunner.call();
            }
        });

        try {
            if (callTimeout > 0) {
                return future.get(callTimeout, TimeUnit.MILLISECONDS);
            } else {
                return future.get();
            }
        } catch (TimeoutException eT) {
            future.cancel(true);
            throw new IOException("Callable timed out", eT);
        } catch (Exception e1) {
            throw e1;
        }
    }

    private interface CallRunner<T> {
        T call() throws Exception;
    }
}
