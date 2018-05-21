package com.kthcorp.daisy.bms.fileio;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Comparator;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Data
@ToString
@EqualsAndHashCode
public class FileIOInfo implements Comparator<FileIOInfo>, Comparable<FileIOInfo> {

    private String yyyyMMdd = "";
    private String fileName = "";
    private long modifyTime = 0L;
    private long size = 0L;
    private String path = "";
    private String absolutePath = "";
    private String parent = "";
    private String idxRecFilePath = "";
    private String idxRecThumbFilePath = "";

    @Override
    public int compare(FileIOInfo o1, FileIOInfo o2) {
        return o1.absolutePath.compareTo(o2.absolutePath);
    }

    @Override
    public int compareTo(FileIOInfo o) {
        return this.absolutePath.compareTo(o.absolutePath);
    }

}
