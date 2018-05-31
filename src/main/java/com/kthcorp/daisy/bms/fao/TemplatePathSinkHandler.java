package com.kthcorp.daisy.bms.fao;


import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 24..
 */
public class TemplatePathSinkHandler implements SinkHandler {

    private Logger logger = LoggerFactory.getLogger(TemplatePathSinkHandler.class);

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
            logger.debug("CopyToRemote sourceFile => successfully to store file remote.");
        } else {
            throw new Exception("CopyToRemote sourceFile => Failed to store file remote.");
        }
        logger.info("CopyToRemote sourceFile:{} => dstPath:{}", sourceFile.getAbsolutePath(), newDstPath);
        return newDstPath;
    }


    @Override
    public void close() {
        remoteFAO.close();
    }
}
