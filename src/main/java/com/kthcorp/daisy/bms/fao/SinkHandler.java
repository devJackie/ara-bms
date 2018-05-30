package com.kthcorp.daisy.bms.fao;

import java.io.Closeable;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 24..
 */
public interface SinkHandler extends Closeable, AutoCloseable {

    String send(Map<String, String> headers, File sourceFile) throws Exception;
}
