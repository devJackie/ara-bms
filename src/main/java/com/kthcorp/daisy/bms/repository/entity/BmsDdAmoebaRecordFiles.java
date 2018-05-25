package com.kthcorp.daisy.bms.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Data
@ToString
@EqualsAndHashCode
public class BmsDdAmoebaRecordFiles {

    private String yyyymmdd;
    private String file_id;
    private String apln_form_id;
    private String ad_id;
    private String ch_id;
    private String ch_no;
    private String start_dt;
    private String rec_file_path;
    private String rec_thumb_file_path;
    private String brdcst_dt;
    private String reg_dt;

    public BmsDdAmoebaRecordFiles() {}
}
