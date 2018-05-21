package com.kthcorp.daisy.bms.properties;

/**
 * Created by devjackie on 2018. 5. 3..
 */
public interface DatabaseProperties {

    String getDriverClassName();

    String getUrl();

    String getUserName();

    String getPassword();

    boolean isInitialize();

    int getInitialSize();

    int getMaxActive();

    int getMaxIdle();

    int getMinIdle();

    int getMaxWait();
}