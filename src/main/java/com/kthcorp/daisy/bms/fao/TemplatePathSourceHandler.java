package com.kthcorp.daisy.bms.fao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Slf4j
public class TemplatePathSourceHandler implements SourceHandler {

    private static final String PATH = "path";
    private static final String FILE_PATTERN = "filePattern";
    private static final String PATH_ATTRIBUTE = "pathAttributes";
    private static final String SCAN_RANGE = "scanRange";
    private static final String FILE_SCAN_RANGE = "fileScanRange";
    private static final String DATE_PATTERN = "datePattern";
    private static final String DATE = "date";
    private static final String STATIC = "static";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private final String path;
    private final Pattern filePattern;
    private final List<Map<String, Object>> pathAttributes;
    private final RemoteFAO remoteFSHelper;

    TemplatePathSourceHandler(Map<String, Object> config) throws Exception {
        this.path = (String) config.get(PATH);
        this.pathAttributes = (List) config.get(PATH_ATTRIBUTE);


        String filePatternStr = (String) config.get(FILE_PATTERN);
        if (filePatternStr != null) {
            filePattern = Pattern.compile(filePatternStr);
        } else {
            filePattern = null;
        }

        this.remoteFSHelper = RemoteFAOFactory.createFAO(config);
    }

    @Override
    public List<RemoteFileInfo> getRemoteFiles() throws Exception {
        log.debug("Start getRemoteFiles -  path: {}, pathAttributes: {}", path, pathAttributes);
        List<String> paths = createPath(path, null, pathAttributes, 0);
        log.debug("createPath: {}", paths);

        Set<RemoteFileInfo> remoteFileSet = new HashSet<>();
        for (String globPath : paths) {
            remoteFileSet.addAll(remoteFSHelper.getListRemoteFiles(globPath));
        }

        List<RemoteFileInfo> remoteFiles = new ArrayList<>(remoteFileSet);
        Collections.sort(remoteFiles);
        log.debug("remoteFiles: {}", remoteFiles);

        return remoteFiles;
    }

    @Override
    public boolean get(String remote, String local) throws Exception {
        return remoteFSHelper.copyToLocal(remote, local);
    }

    @Override
    public boolean send(String local, String remote) throws Exception {
        return remoteFSHelper.copyToRemote(local, remote+"/");
    }

    @Override
    public void close() {
        remoteFSHelper.close();
    }

    private List<String> createPath(String rootPath, Map<String, String> header, List<Map<String, Object>> subAttrs, int depthIdx) throws Exception{
        if(header == null) {
            header = new HashMap();
        }

        Set<String> paths = new HashSet<>();
        if (subAttrs != null && subAttrs.size() > depthIdx) {
            Map<String, Object> attr = (Map) subAttrs.get(depthIdx);
            String type = (String) attr.get(TYPE);

            if(type.startsWith(STATIC)){
                String key = (String) attr.get(KEY);
                List<String> values = (List) attr.get(VALUE);
                for (String value : values){
                    header.put(key, value);
                    paths.addAll(createPath(rootPath, header, subAttrs, depthIdx + 1));
                }
            } else if(type.startsWith(DATE)){

                SimpleDateFormat sdf = new SimpleDateFormat((String) attr.get(DATE_PATTERN));
                String scanRange = (String) attr.get(SCAN_RANGE);
                if (scanRange.contains("h")) {
                    throw new IllegalArgumentException("'h' char not support");
                }

                int dateRange = 0;
                int hourRange = 0;

                if (scanRange.contains("d")) {
                    dateRange = Integer.parseInt(scanRange.substring(0, scanRange.indexOf("d")));
                }

                for (int i = 0; i >= dateRange; i--) {
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.add(Calendar.DATE, i);
                    header.put(DATE, sdf.format(calendar.getTime()));
                    paths.addAll(createPath(rootPath, header, subAttrs, depthIdx + 1));
                }
            } else if(type.startsWith("none")){
                paths.add(rootPath);
            }
        } else {
            StrSubstitutor sub = new StrSubstitutor(header);
            paths.add(sub.replace(rootPath));
        }
        List<String> result = new ArrayList<>(paths);
        Collections.sort(result);
        return result;
    }
}
