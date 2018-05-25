package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsDdMediaRecordFiles;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;

/**
 * Created by devjackie on 2018. 5. 25..
 */
@BmsSchema
public interface BmsDdMediaRecordFilesMapper {

    void insertMediaRecordFiles(BmsDdMediaRecordFiles bmsDdMediaRecordFiles);
}
