package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsDdAdTmpResSche;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@BmsSchema
public interface BmsDdAdTmpResScheMapper {

    Map<String, Object> selPrgmScheNextDay(Map<String, Object> param);
    List<BmsDdAdTmpResSche> selAdNprgmScheMergeForToDay(Map<String, Object> param);
    List<BmsDdAdTmpResSche> selAdNprgmScheMergeForNextDay(Map<String, Object> param);
    void insertAdTmpResScheForToDay(BmsDdAdTmpResSche bmsDdAdTmpResSche);
    void insertAdTmpResScheForNextDay(BmsDdAdTmpResSche bmsDdAdTmpResSche);
}
