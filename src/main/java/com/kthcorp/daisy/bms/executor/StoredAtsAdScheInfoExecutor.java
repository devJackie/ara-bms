package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 28..
 */
@Slf4j
public class StoredAtsAdScheInfoExecutor extends BaseExecutor {

    StoredAtsAdScheInfoExecutor(ApplicationContext context, Map<String, Object> config, BmsMetaProperties bmsMetaProperties) throws Exception {
        super(context, config, bmsMetaProperties);
    }

    List<ExecuteFileInfo> executeFileInfos = new ArrayList<>();

    List<ExecuteFileInfo> getExecuteFileInfos() throws Exception {
        log.debug("Looking up {}", "getExecuteFileInfos");

        int logIdx = 1;
        List<String> indexStr = indexStore.getIndexAsList();
        List<RemoteFileInfo> remoteFiles = sourceHandler.getRemoteFiles();

        Set<String> finFiles = new HashSet<>();
        List<RemoteFileInfo> atsFiles = new ArrayList<>();

        for (RemoteFileInfo remoteFile : remoteFiles) {
            if (remoteFile.getFileName().toUpperCase().endsWith(".FIN")) {
                finFiles.add(remoteFile.getFileName().substring(0, remoteFile.getFileName().indexOf(".")));
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".DAT")) {
                atsFiles.add(remoteFile);
            }
        }

        // .FIN 파일이 있는지 체크
        List<RemoteFileInfo> resultRemoteFiles = atsFiles.stream().filter(f ->
                f.getSize() > 0 &&
                finFiles.contains(f.getFileName().substring(0, f.getFileName().indexOf(".")))).collect(Collectors.toList());

        executeFileInfos = resultRemoteFiles.stream().map(x -> {
            ExecuteFileInfo executeFileInfo = new ExecuteFileInfo();
            executeFileInfo.setSourceFile(x);
            executeFileInfo.setFinished(indexStr.contains(x.getAbsolutePath()));
            executeFileInfo.setSuccess(indexStr.contains(x.getAbsolutePath()));
            return executeFileInfo;
        }).collect(Collectors.toList());

        log.debug("executeFileInfos : {}", executeFileInfos);
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
