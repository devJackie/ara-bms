package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.support.BmsSchema;

import java.util.Map;

/**
 * Created by devjackie on 2018. 6. 18..
 */
@BmsSchema
public interface BmsInitDDLMapper {

    void addPartitionForPlus1day(Map<String, String> param);

    void addPartitionForPlus2day(Map<String, String> param);
}
