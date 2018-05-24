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
public class BmsDdAdResSche {

    private String yyyymmdd;
    private String sche_gubn;
    private String apln_form_id;
    private String ad_id;
    private String ad_nm;
    private String ad_length;
    private String ch_id;
    private String ch_no;
    private String ch_nm;
    private String brdcst_dt;
    private String start_dt;
    private String end_dt;

    public BmsDdAdResSche() {}
}
