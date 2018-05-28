package com.kthcorp.daisy.bms.headerextractor;

import com.kthcorp.daisy.bms.executor.ExecuteFileInfo;

/**
 * Created by devjackie on 2018. 5. 28..
 */
public interface HeaderExtractor {

    void extract(ExecuteFileInfo event);
}
