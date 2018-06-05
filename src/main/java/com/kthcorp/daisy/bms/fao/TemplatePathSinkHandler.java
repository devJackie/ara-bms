package com.kthcorp.daisy.bms.fao;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 24..
 */
@Slf4j
public class TemplatePathSinkHandler implements SinkHandler {

    private RemoteFAO remoteFAO;
    private String destPath;

    TemplatePathSinkHandler(Map<String, Object> config) throws Exception {
        this.remoteFAO = RemoteFAOFactory.createFAO(config);
        this.destPath = (String) config.get("path");
    }

    @Override
    public String send(Map<String, String> headers, File sourceFile) throws Exception {
        StrSubstitutor sub = new StrSubstitutor(headers);
        String newDstPath = sub.replace(destPath);
        if (!newDstPath.endsWith("/")) {
            newDstPath = newDstPath + "/";
        }

        if (remoteFAO.copyToRemote(sourceFile.getAbsolutePath(), newDstPath)) {
            log.debug("CopyToRemote sourceFile => successfully to store file remote.");
        } else {
            throw new Exception("CopyToRemote sourceFile => Failed to store file remote.");
        }
        log.info("CopyToRemote sourceFile:{} => dstPath:{}", sourceFile.getAbsolutePath(), newDstPath);
        return newDstPath;
    }


    @Override
    public void close() {
        remoteFAO.close();
    }
}
