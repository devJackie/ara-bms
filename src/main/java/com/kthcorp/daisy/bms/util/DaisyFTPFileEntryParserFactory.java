package com.kthcorp.daisy.bms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.ParserInitializationException;

/**
 * Created by devjackie on 2018. 5. 25..
 */
@Slf4j
public class DaisyFTPFileEntryParserFactory implements FTPFileEntryParserFactory {

    private FTPClientConfig config;

    public DaisyFTPFileEntryParserFactory(FTPClientConfig config) {
        this.config = config;
    }

    @Override
    public FTPFileEntryParser createFileEntryParser(String key) {
        if (key == null) {
            throw new ParserInitializationException("Parser key cannot be null");
        }
        return createFileEntryParser(key, null);
    }

    @Override
    public FTPFileEntryParser createFileEntryParser(FTPClientConfig config) throws ParserInitializationException {
        String key = config.getServerSystemKey();
        return createFileEntryParser(key, config);
    }

    // Common method to process both key and config parameters.
    private FTPFileEntryParser createFileEntryParser(String key, FTPClientConfig config) {
        FTPFileEntryParser parser = null;

        String ukey = key.toUpperCase();
        if (ukey.indexOf("KO_UNIX") >= 0) {
            parser = new UnixFTPKoreanEntryParser();
        } else {
            parser = new DefaultFTPFileEntryParserFactory().createFileEntryParser(key);
        }
        if (parser instanceof Configurable) {
            ((Configurable) parser).configure(config);
        }
        return parser;
    }
}
