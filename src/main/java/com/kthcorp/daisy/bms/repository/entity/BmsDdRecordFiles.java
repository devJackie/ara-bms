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
public class BmsDdRecordFiles {

    private String yyyyMMdd;
    private String fileId;
    private String aplnFormId;
    private String adId;
    private String chId;
    private String chNo;
    private String startDt;
    private String recFilePath;
    private String recThumbFilePath;
    private String brdcstDt;
    private String regDt;

    public BmsDdRecordFiles() {}
}
