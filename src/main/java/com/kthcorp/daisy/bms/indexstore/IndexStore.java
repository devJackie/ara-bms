package com.kthcorp.daisy.bms.indexstore;

import java.util.List;

/**
 * Created by devjackie on 2018. 5. 9..
 */
public interface IndexStore {

    String getIndex() throws Exception;

    List<String> getIndexAsList() throws Exception;

    void setIndex(String index) throws Exception;

    void setIndex(List<String> indexes) throws Exception;
}
