package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.fileio.FileIOInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Slf4j
public class StoredAmoebaRecInfoExecutor extends BaseExecutor {

    StoredAmoebaRecInfoExecutor(ApplicationContext context, Map<String, Object> config, BmsMetaProperties bmsMetaProperties) throws Exception {
        super(context, config, bmsMetaProperties);
    }

    List<ExecuteFileInfo> executeFileInfos = new ArrayList<>();

    List<ExecuteFileInfo> getExecuteFileInfos() throws Exception {
        log.debug("Looking up {}", "getExecuteFileInfos");

        int logIdx = 1;
        List<String> indexStr = indexStore.getIndexAsList();
        List<RemoteFileInfo> remoteFiles = sourceHandler.getRemoteFiles();

        Set<String> finFiles = new HashSet<>();
        List<RemoteFileInfo> idxFiles = new ArrayList<>();
        List<RemoteFileInfo> recFiles = new ArrayList<>();
        List<RemoteFileInfo> thumbFiles = new ArrayList<>();

        for (RemoteFileInfo remoteFile : remoteFiles) {
            if (remoteFile.getFileName().toUpperCase().endsWith(".FIN")) {
                finFiles.add(remoteFile.getFileName().substring(0, remoteFile.getFileName().indexOf(".")));
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".IDX")) {
                idxFiles.add(remoteFile);
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".MP4")) {
                recFiles.add(remoteFile);
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".JPG")) {
                thumbFiles.add(remoteFile);
            }
        }

        // .FIN 파일이 있는지 체크
        List<RemoteFileInfo> finCheckFiles = idxFiles.stream().filter(f ->
                !finFiles.isEmpty()).collect(Collectors.toList());

        List<RemoteFileInfo> resultRemoteRecFiles = new ArrayList<>();
        if (finCheckFiles != null && finCheckFiles.size() > 0) {
            // thumb path set
            recFiles.stream().forEach(x -> thumbFiles.stream().filter(y -> FilenameUtils.getBaseName(x.getFileName()).equals(FilenameUtils.getBaseName(y.getFileName())))
                    .forEach(y -> x.setThumbAbsolutePath(y.getAbsolutePath())));

            List<RemoteFileInfo> remoteRecFiles = recFiles.stream().map(x -> {
                RemoteFileInfo remoteFileInfo = new RemoteFileInfo();
                idxFiles.stream().forEach(y -> {
                    remoteFileInfo.setYyyyMMdd(FilenameUtils.getBaseName(y.getFileName()));
                    remoteFileInfo.setFileId(FilenameUtils.getBaseName(x.getFileName()));
                    remoteFileInfo.setFileName(x.getFileName());
                    remoteFileInfo.setModifyTime(x.getModifyTime());
                    remoteFileInfo.setSize(x.getSize());
                    remoteFileInfo.setPath(x.getPath());
                    remoteFileInfo.setAbsolutePath(x.getAbsolutePath());
                    remoteFileInfo.setThumbAbsolutePath(x.getThumbAbsolutePath());
                    remoteFileInfo.setParent(x.getParent());
                });
                return remoteFileInfo;
                    }).collect(Collectors.toList());

            remoteRecFiles.forEach(x -> {
                log.debug("remoteRecFiles: {}", x);
            });

            List<FileIOInfo> fileIOFiles;
            // .IDX 의 녹화파일 목록
            fileIOFiles = fileIO.readAmeobaRecFileList(idxFiles);

            fileIOFiles.forEach(x -> {
                log.debug("{}", x);
            });

            resultRemoteRecFiles = fileIOFiles.stream().map(x -> {
                RemoteFileInfo remoteFileInfo = new RemoteFileInfo();
                remoteRecFiles.stream().filter(y -> x.getIdxRecFilePath().contains(y.getFileId()))
                        .forEach(y -> {
                            remoteFileInfo.setYyyyMMdd(y.getYyyyMMdd());
                            remoteFileInfo.setFileId(y.getFileId());
                            remoteFileInfo.setFileName(y.getFileName());
                            remoteFileInfo.setModifyTime(y.getModifyTime());
                            remoteFileInfo.setSize(y.getSize());
                            remoteFileInfo.setPath(y.getPath());
                            remoteFileInfo.setAbsolutePath(y.getAbsolutePath());
                            remoteFileInfo.setThumbAbsolutePath(y.getThumbAbsolutePath());
                            remoteFileInfo.setParent(y.getParent());
                            remoteFileInfo.setIdxRecFilePath(x.getIdxRecFilePath());
                            remoteFileInfo.setIdxRecThumbFilePath(x.getIdxRecThumbFilePath());
                        });
                return remoteFileInfo;
            }).collect(Collectors.toList());
        } else {
            log.info("The .FIN file is not found");
        }

        resultRemoteRecFiles.forEach(x -> {
            log.debug("resultRemoteRecFiles: {}", x);
        });

    executeFileInfos = resultRemoteRecFiles.stream().map(x -> {
        ExecuteFileInfo executeFileInfo = new ExecuteFileInfo();
        executeFileInfo.setSourceFile(x);
        executeFileInfo.setFinished(indexStr.contains(x.getAbsolutePath()));
        executeFileInfo.setSuccess(indexStr.contains(x.getAbsolutePath()));
        return executeFileInfo;
    }).collect(Collectors.toList());

        log.info("executeFileInfos : {}", executeFileInfos);
        log.info("1-{}. Remote file`s index checked. ", logIdx++);

        return executeFileInfos;
}

    @Override
    void setIndex(ExecuteFileInfo executeFileInfo) throws Exception {
        List<String> indexes = executeFileInfos.parallelStream().filter(x -> x.isFinished() && x.isSuccess()).map(t1 -> t1.getSourceFile().getAbsolutePath()).collect(Collectors.toList());
        indexStore.setIndex(indexes);
        log.debug("setIndex {}", indexes);
    }
}
