package com.kthcorp.daisy.bms.indexstore;

import java.util.List;

/**
 * Created by devjackie on 2018. 5. 9..
 */
public interface IndexStore {

    String getIndex() throws Exception;

    void creatingIfNeededForDate(String index) throws Exception;

    List<String> getIndexForDate(String index) throws Exception;

    List<String> getIndexAsList() throws Exception;

    void setIndex(String index) throws Exception;

    void setIndex(List<String> indexes) throws Exception;

    void setIndexForDate(List<String> indexes, String date, String index) throws Exception;

    void deleteForDate(String index, String subIndex) throws Exception;
}
