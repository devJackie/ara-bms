package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsDdAdResSche;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

import java.util.HashMap;
import java.util.List;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@BmsSchema
public interface BmsDdAdResScheMapper {

    int selAdminScheCheck(HashMap<String, Object> param);
    List<BmsDdAdResSche> selTmpScheNadminScheForMerge(HashMap<String, Object> param);
    void insertAdResSche(BmsDdAdResSche bmsDdAdResSche);
}
