package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015-08-25.
 */
@Data
@Slf4j
public abstract class BaseFileIO implements FileIO {

    protected Map<String, String> header  = new HashMap<>();

    protected final Map<String, Object> config;

    protected final String path;
    protected final String textEncoding;
    protected final List<Map<String, Object>> pathAttributes;

    protected BmsMetaProperties bmsMetaProperties;

    protected BaseFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        this.config = config;
        this.bmsMetaProperties = bmsMetaProperties;
        this.path = (String) config.get(bmsMetaProperties.getBmsMeta().get("common").get("path"));
        this.pathAttributes = (List) config.get(bmsMetaProperties.getBmsMeta().get("common").get("path-attribute"));
        this.textEncoding = (String) config.get(bmsMetaProperties.getBmsMeta().get("common").get("text-encoding"));
    }
}
