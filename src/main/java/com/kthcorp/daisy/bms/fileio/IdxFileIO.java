package com.kthcorp.daisy.bms.fileio;

import com.kthcorp.daisy.bms.fao.RemoteFileInfo;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Slf4j
public class IdxFileIO extends BaseFileIO {

    IdxFileIO(Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        super(config, bmsMetaProperties);
    }

    @Override
    public List<FileIOInfo> getReadFileList(List<RemoteFileInfo> idxFiles) throws Exception {
        log.debug("config : {}", config);

        List<FileIOInfo> fileIOList = new ArrayList<>();
        List<FileIOInfo> recFileList = new ArrayList<>();
        List<FileIOInfo> recThumbFileList = new ArrayList<>();

        for (RemoteFileInfo idxFile : idxFiles) {
            // yyyyMMdd 설정
            idxFile.setYyyyMMdd(idxFile.getFileName().split("\\.")[0]);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(idxFile.getAbsolutePath()), textEncoding));

            try {
                String line;
                while ((line = in.readLine()) != null) {
                    FileIOInfo fileIOInfo;
                    if (line.toUpperCase().endsWith(".MP4")) {
                        fileIOInfo = new FileIOInfo();
                        fileIOInfo.setYyyyMMdd(idxFile.getYyyyMMdd());
                        fileIOInfo.setFileName(idxFile.getFileName());
                        fileIOInfo.setParent(idxFile.getParent());
                        fileIOInfo.setAbsolutePath(idxFile.getAbsolutePath());
                        fileIOInfo.setIdxRecFilePath(line);
                        recFileList.add(fileIOInfo);
                    } else if (line.toUpperCase().endsWith(".JPG")) {
                        fileIOInfo = new FileIOInfo();
                        fileIOInfo.setYyyyMMdd(idxFile.getYyyyMMdd());
                        fileIOInfo.setFileName(idxFile.getFileName());
                        fileIOInfo.setParent(idxFile.getParent());
                        fileIOInfo.setAbsolutePath(idxFile.getAbsolutePath());
                        fileIOInfo.setIdxRecThumbFilePath(line);
                        recThumbFileList.add(fileIOInfo);
                    }
                }

                // .MP4 와 .JPG 를 하나의 객체로 merge
                fileIOList = recFileList.stream().map(x -> {
                    FileIOInfo fileIOInfo = new FileIOInfo();
                    recThumbFileList.stream().filter(y -> (FilenameUtils.getBaseName(x.getIdxRecFilePath()).equals(FilenameUtils.getBaseName(y.getIdxRecThumbFilePath()))))
                            .forEach(y -> {
                            fileIOInfo.setYyyyMMdd(x.getYyyyMMdd());
                            fileIOInfo.setFileName(x.getFileName());
                            fileIOInfo.setParent(x.getParent());
                            fileIOInfo.setAbsolutePath(x.getAbsolutePath());
                            fileIOInfo.setIdxRecFilePath(x.getIdxRecFilePath());
                            fileIOInfo.setIdxRecThumbFilePath(y.getIdxRecThumbFilePath());
                    });
                    return fileIOInfo;
                }).collect(Collectors.toList());
            } finally {
                CollectorUtil.quietlyClose(in);
            }
        }
        return fileIOList;
    }

    private List<String> createPath(String rootPath, Map<String, String> header, List<Map<String, Object>> subAttrs, int depthIdx) throws Exception{
        if(header == null) {
            header = new HashMap();
        }

        Set<String> paths = new HashSet<>();
        if (subAttrs != null && subAttrs.size() > depthIdx) {
            Map<String, Object> attr = (Map) subAttrs.get(depthIdx);
            String type = (String) attr.get(bmsMetaProperties.getBmsMeta().get("common").get("type"));

            if(type.startsWith((String) bmsMetaProperties.getBmsMeta().get("common").get("date"))) {

                log.debug("attr --> {}", attr);
                SimpleDateFormat sdf = new SimpleDateFormat((String) attr.get(bmsMetaProperties.getBmsMeta().get("common").get("date-pattern")));
                String scanRange = (String) attr.get(bmsMetaProperties.getBmsMeta().get("common").get("scan-range"));
                if (scanRange.contains("h")) {
                    throw new IllegalArgumentException("'h' char not support");
                }

                int dateRange = 0;

                if (scanRange.contains("d")) {
                    dateRange = Integer.parseInt(scanRange.substring(0, scanRange.indexOf("d")));
                }

                for (int i = 0; i >= dateRange; i--) {
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.add(Calendar.DATE, i);
                    header.put((String) bmsMetaProperties.getBmsMeta().get("common").get("date"), sdf.format(calendar.getTime()));
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
