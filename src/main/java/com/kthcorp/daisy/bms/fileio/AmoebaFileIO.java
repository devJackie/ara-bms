package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Slf4j
public class AmoebaFileIO extends BaseFileIO {

    AmoebaFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        super(config, bmsMetaProperties);
    }

    @Override
    public List<FileIOInfo> readAmeobaRecFileList(List<RemoteFileInfo> remoteFiles) throws Exception {
        log.debug("config : {}", config);

        List<FileIOInfo> fileIOList = new ArrayList<>();
        List<FileIOInfo> recFileList = new ArrayList<>();
        List<FileIOInfo> recThumbFileList = new ArrayList<>();

        for (RemoteFileInfo remoteFile : remoteFiles) {
            // yyyyMMdd 설정
            remoteFile.setYyyyMMdd(remoteFile.getFileName().split("\\.")[0]);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(remoteFile.getAbsolutePath()), textEncoding));

            try {
                String line;
                while ((line = in.readLine()) != null) {
                    FileIOInfo fileIOInfo;
                    if (line.toUpperCase().endsWith(".MP4")) {
                        fileIOInfo = new FileIOInfo();
                        fileIOInfo.setYyyyMMdd(remoteFile.getYyyyMMdd());
                        fileIOInfo.setFileName(remoteFile.getFileName());
                        fileIOInfo.setParent(remoteFile.getParent());
                        fileIOInfo.setAbsolutePath(remoteFile.getAbsolutePath());
                        fileIOInfo.setIdxRecFilePath(line);
                        recFileList.add(fileIOInfo);
                    } else if (line.toUpperCase().endsWith(".JPG")) {
                        fileIOInfo = new FileIOInfo();
                        fileIOInfo.setYyyyMMdd(remoteFile.getYyyyMMdd());
                        fileIOInfo.setFileName(remoteFile.getFileName());
                        fileIOInfo.setParent(remoteFile.getParent());
                        fileIOInfo.setAbsolutePath(remoteFile.getAbsolutePath());
                        fileIOInfo.setIdxRecThumbFilePath(line);
                        recThumbFileList.add(fileIOInfo);
                    }
                }

                // .MP4 와 .JPG 를 하나의 객체로 merge
                fileIOList = recFileList.stream().map(x -> {
                    FileIOInfo fileIOInfo = new FileIOInfo();
                    recThumbFileList.stream().filter(y -> (FilenameUtils.getBaseName(x.getIdxRecFilePath()).equals(FilenameUtils.getBaseName(y.getIdxRecThumbFilePath()))))
                            .forEach(y -> {
                            fileIOInfo.setYyyyMMdd(x.getYyyyMMdd());
                            fileIOInfo.setFileName(x.getFileName());
                            fileIOInfo.setParent(x.getParent());
                            fileIOInfo.setAbsolutePath(x.getAbsolutePath());
                            fileIOInfo.setIdxRecFilePath(x.getIdxRecFilePath());
                            fileIOInfo.setIdxRecThumbFilePath(y.getIdxRecThumbFilePath());
                    });
                    return fileIOInfo;
                }).collect(Collectors.toList());
            } finally {
                CollectorUtil.quietlyClose(in);
            }
        }
        return fileIOList;
    }

    @Override
    public List<Map<String, Object>> readAmeobaIdxRecInfoList(String[] fileArray) throws Exception {

        // fileArray[0] : [AAAAAAAAAA]
        // fileArray[1] : [20180517053610, 20180517, 0109, 32]
        // fileArray[2] : [201804019, 180416GSPB30]
        List<String> fileInfo = new ArrayList<>(Arrays.asList(fileArray[0]));
        log.debug("fileInfo : {}", fileInfo);

        List<Map<String, Object>> resultRecInfoFiles = new ArrayList<>();

        for (int i = 0; i < fileInfo.size(); i++) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("fileId", fileInfo.get(i));
            String temp1 = fileArray[1];
            String[] array1 = temp1.split("_");
            map.put("startDt", array1[0]);
            map.put("brdcstDt", array1[1]);
            map.put("chId", array1[2]);
            map.put("chNo", array1[3]);

            String temp2 = fileArray[2];
            String[] array2 = temp2.split("_");
            map.put("aplnFormId", array2[0]);
            map.put("adId", array2[1]);

            resultRecInfoFiles.add(map);
        }
        return resultRecInfoFiles;
    }

    @Override
    public List<Map<String, Object>> readAtsAdScheList(File remoteFile) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> readMssPrgmScheList(File remoteFile) throws Exception {
        return null;
    }
}
