package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsDdMssPrgmSche;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 30..
 */
@BmsSchema
public interface BmsDdMssPrgmScheMapper {

    void insertMssPrgmSche(BmsDdMssPrgmSche bmsDdMssPrgmSche);

    void insertMssPrgmSche(Map<String, Object> param);
}
