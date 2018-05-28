package com.kthcorp.daisy.bms.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Data
@ToString
@EqualsAndHashCode
public class BmsDdAtsAdSche {

    private String yyyymmdd;
    private String apln_form_id;
    private String ad_id;
    private String ad_nm;
    private String ad_length;
    private String ad_order;
    private String ch_id;
    private String otv_ch_no;
    private String ots_ch_no;
    private String ch_nm;
    private String brdcst_dt;
    private String hhmmss;

    public BmsDdAtsAdSche() {}
}
