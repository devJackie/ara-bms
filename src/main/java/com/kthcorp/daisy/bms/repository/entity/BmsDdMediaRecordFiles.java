package com.kthcorp.daisy.bms.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by devjackie on 2018. 5. 25..
 */
@Data
@ToString
@EqualsAndHashCode
public class BmsDdMediaRecordFiles {

    private String yyyymmdd;
    private String ad_id;
    private String rec_file_path;
    private String reg_dt;

    public BmsDdMediaRecordFiles() {}
}
