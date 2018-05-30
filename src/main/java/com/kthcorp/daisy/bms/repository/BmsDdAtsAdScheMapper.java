package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsDdAtsAdSche;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@BmsSchema
public interface BmsDdAtsAdScheMapper {

    void insertAtsAdSche(BmsDdAtsAdSche bmsDdAtsAdSche);

    void insertAtsAdSche(Map<String, Object> param);
}
