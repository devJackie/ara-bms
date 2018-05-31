package com.kthcorp.daisy.bms.fao;

import java.io.Closeable;
import java.util.List;

/**
 * Created by devjackie on 2018. 5. 8..
 */
public interface SourceHandler extends Closeable, AutoCloseable {

    List<RemoteFileInfo> getRemoteFiles() throws Exception;

    boolean get(String remote, String local) throws Exception;

    boolean send(String local, String remote) throws Exception;
}
