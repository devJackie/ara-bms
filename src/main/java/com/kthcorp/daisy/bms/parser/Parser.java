package com.kthcorp.daisy.bms.parser;

import java.io.File;

/**
 * Created by devjackie on 2018. 5. 28..
 */
public interface Parser {

    File process(String sourceFile) throws Exception;
}
