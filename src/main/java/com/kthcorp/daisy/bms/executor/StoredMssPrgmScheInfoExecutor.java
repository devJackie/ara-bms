package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 30..
 */
@Slf4j
public class StoredMssPrgmScheInfoExecutor extends BaseExecutor {

    StoredMssPrgmScheInfoExecutor(ApplicationContext context, Map<String, Object> config, BmsMetaProperties bmsMetaProperties) throws Exception {
        super(context, config, bmsMetaProperties);
    }

    List<ExecuteFileInfo> executeFileInfos = new ArrayList<>();

    List<ExecuteFileInfo> getExecuteFileInfos() throws Exception {
        log.debug("Looking up {}", "getExecuteFileInfos");

        int logIdx = 1;
        List<String> indexStr = indexStore.getIndexAsList();
        List<RemoteFileInfo> firstRemoteFiles = sourceHandler.getRemoteFiles();

        Collections.sort(firstRemoteFiles);
        log.info("1-{}. First remote file listing... - count : {}", logIdx++, firstRemoteFiles.size());
        log.debug("RemoteFiles : \n{}", StringUtils.join(firstRemoteFiles.toArray(), "\n"));

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            throw e;
        }

        List<RemoteFileInfo> secondRemoteFiles = sourceHandler.getRemoteFiles();
        log.info("1-{}. Second remote file listing... - count : {}", logIdx++, secondRemoteFiles.size());
        log.debug("RemoteFiles : \n{}", StringUtils.join(secondRemoteFiles.toArray(), "\n"));

        Map<String, RemoteFileInfo> secondRemoteFileMap = new HashMap<>();
        for (RemoteFileInfo secondRemoteFile : secondRemoteFiles) {
            secondRemoteFileMap.put(secondRemoteFile.getAbsolutePath(), secondRemoteFile);
        }

        List<RemoteFileInfo> modifyCheckedFiles = new ArrayList<>();

        for (int i = 0; i < firstRemoteFiles.size(); i++) {
            RemoteFileInfo firstRemoteFile = firstRemoteFiles.get(i);
            if (secondRemoteFileMap.containsKey(firstRemoteFile.getAbsolutePath()) && firstRemoteFile.getModifyTime() == secondRemoteFileMap.get(firstRemoteFiles.get(i).getAbsolutePath()).getModifyTime()
                    && firstRemoteFile.getSize() == secondRemoteFileMap.get(firstRemoteFiles.get(i).getAbsolutePath()).getSize()) {
                // First & Second remote file 사이즈가 0 이 아닌 경우만
                if (firstRemoteFile.getSize() > 0 && secondRemoteFileMap.get(firstRemoteFiles.get(i).getAbsolutePath()).getSize() > 0) {
                    modifyCheckedFiles.add(firstRemoteFile);
                }
            }
        }

        executeFileInfos = modifyCheckedFiles.stream().map(x -> {
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