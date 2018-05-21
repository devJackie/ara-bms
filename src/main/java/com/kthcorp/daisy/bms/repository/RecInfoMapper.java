package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.RecFileInfo;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@BmsSchema
public interface RecInfoMapper {

    void insertRecFileInfo(RecFileInfo recFileInfo);
}
