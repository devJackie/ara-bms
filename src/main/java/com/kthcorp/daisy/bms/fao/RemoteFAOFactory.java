package com.kthcorp.daisy.bms.fao;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Properties;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Slf4j
public class RemoteFAOFactory {

    private static final String FTP = "ftp";
    private static final String SFTP = "sftp";
    private static final String LOCAL = "local";
    private static final String NONE = "none";
    private static final String TYPE = "type";

    public static RemoteFAO createFAO(Map<String, Object> config) throws Exception {
        log.debug("RemoteFAO - createFAO config : {}", config);
        RemoteFAO remoteFSHandler = null;

        Properties properties = new Properties();
        properties.putAll(config);

        String type = properties.getProperty(TYPE, "");
        if (LOCAL.equalsIgnoreCase(type)) {
            log.debug("create LocalFAO");
            remoteFSHandler = new LocalFAO(properties);
        } else if (FTP.equalsIgnoreCase(type)) {
            log.debug("create FtpFAO");
            remoteFSHandler = new FtpFAO(properties);
        } else if (SFTP.equalsIgnoreCase(type)) {
            log.debug("create SftpFAO");
            remoteFSHandler = new SftpFAO(properties);
        } else if (NONE.equalsIgnoreCase(type)) {
            log.debug("create NoneFAO");
            remoteFSHandler = new NoneFAO(properties);
        } else {
            throw new IllegalArgumentException("not support type : " + type);
        }
        return remoteFSHandler;
    }
}
