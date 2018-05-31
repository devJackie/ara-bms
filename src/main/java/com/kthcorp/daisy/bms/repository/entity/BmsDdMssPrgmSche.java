package com.kthcorp.daisy.bms.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by devjackie on 2018. 5. 30..
 */
@Data
@ToString
@EqualsAndHashCode
public class BmsDdMssPrgmSche {

    private String yyyymmdd;
    private String ch_no;
    private String prgm_id;
    private String prgm_nm;
    private String screen_gubn;
    private String prgm_start_dt;
    private String prgm_end_dt;

    public BmsDdMssPrgmSche() {}
}
