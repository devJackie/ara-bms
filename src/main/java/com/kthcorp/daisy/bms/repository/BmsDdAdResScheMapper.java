package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsDdAdResSche;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@BmsSchema
public interface BmsDdAdResScheMapper {

    List<BmsDdAdResSche> selTmpScheNadminScheMergeForToDay(Map<String, Object> param);

    List<BmsDdAdResSche> selTmpScheNadminScheMergeForNextDay(Map<String, Object> param);

    void insertAdResSche(BmsDdAdResSche bmsDdAdResSche);
}
