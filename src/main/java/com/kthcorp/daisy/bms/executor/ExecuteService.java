package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.SinkHandler;
import com.kthcorp.daisy.bms.fileio.FileIO;
import com.kthcorp.daisy.bms.parser.ParserBase;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.repository.*;
import com.kthcorp.daisy.bms.repository.entity.*;
import com.kthcorp.daisy.bms.util.DateUtil;
import com.kthcorp.daisy.bms.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.*;

/**
 * Created by devjackie on 2018. 5. 23..
 */
//@Service
@Slf4j
public class ExecuteService {

    protected ApplicationContext context;

    @Autowired
    BmsDdAmoebaRecordFilesMapper bmsDdAmoebaRecordFilesMapper;

    @Autowired
    BmsDdMediaRecordFilesMapper bmsDdMediaRecordFilesMapper;

    @Autowired
    BmsDdAdTmpResScheMapper bmsDdAdTmpResScheMapper;

    @Autowired
    BmsDdAdResScheMapper bmsDdAdResScheMapper;

    @Autowired
    BmsDdAtsAdScheMapper bmsDdAtsAdScheMapper;

    @Autowired
    BmsDdMssPrgmScheMapper bmsDdMssPrgmScheMapper;

    @Autowired
    FileUtil fileUtil;

    Map<String, Object> config = new HashMap<>();
    BmsMetaProperties bmsMetaProperties;

    @Autowired
    public ExecuteService(ApplicationContext context, Map<String, Object> config, BmsMetaProperties bmsMetaProperties) {
        this.context = context;
        this.config = config;
        this.bmsMetaProperties = bmsMetaProperties;
    }

    public void executeAmoebaRecFileCollectTask(ExecuteFileInfo executeFileInfo, SinkHandler sinkHandler, List<ParserBase> parsers, FileIO fileIO) throws Exception {
        // .idx 파일 리스트
        // AAAAAAAAAA-20180517053610_20180517_0109_32-201804019_180416GSPB30.MP4
        // AAAAAAAAAA-20180517053610_20180517_0109_32-201804032_180420RDLN15.MP4
        // AAAAAAAAAA-20180517053610_20180517_0109_32-201804028_180417TMSD30.MP4
        // 녹화 파일
        // AAAAAAAAAA-20180517053610_20180517_0109_32.MP4
        String[] fileArray = FilenameUtils.getBaseName(executeFileInfo.getSourceFile().getIdxRecFilePath()).split("-");
        if (fileArray != null && fileArray.length > 2) {

            List<Map<String, Object>> resultRecInfoFiles = fileIO.readAmeobaIdxRecInfoList(fileArray);

            resultRecInfoFiles.stream().forEach(x -> {
                BmsDdAmoebaRecordFiles bmsDdAmoebaRecordFiles = new BmsDdAmoebaRecordFiles();
                bmsDdAmoebaRecordFiles.setYyyymmdd(executeFileInfo.getSourceFile().getYyyyMMdd());
                bmsDdAmoebaRecordFiles.setFile_id((String) x.get("fileId"));
                bmsDdAmoebaRecordFiles.setApln_form_id((String) x.get("aplnFormId"));
                bmsDdAmoebaRecordFiles.setAd_id((String) x.get("adId"));
                bmsDdAmoebaRecordFiles.setCh_id((String) x.get("chId"));
                bmsDdAmoebaRecordFiles.setCh_no((String) x.get("chNo"));
                bmsDdAmoebaRecordFiles.setStart_dt((String) x.get("startDt"));
                bmsDdAmoebaRecordFiles.setRec_file_path(executeFileInfo.getSourceFile().getAbsolutePath());
                bmsDdAmoebaRecordFiles.setRec_thumb_file_path(executeFileInfo.getSourceFile().getThumbAbsolutePath());
                bmsDdAmoebaRecordFiles.setBrdcst_dt((String) x.get("brdcstDt"));

                bmsDdAmoebaRecordFilesMapper.insertAmoebaRecordFiles(bmsDdAmoebaRecordFiles);
            });

            executeFileInfo.setSuccess(true);
        } else {
            log.info("The idx file line split count is not 3. idxRecFilePath -> {}", executeFileInfo.getSourceFile().getIdxRecFilePath());
        }
    }

    public void executeAtsAdScheCollectTask(ExecuteFileInfo executeFileInfo, SinkHandler sinkHandler, List<ParserBase> parsers, FileIO fileIO) throws Exception {
        log.debug("executeAtsAdScheCollectTask -> executeFileInfo: {}", executeFileInfo);

        List<Map<String, Object>> mapList = new ArrayList<>();

        if (parsers != null) {
            for (ParserBase parserBase : parsers) {

                parserBase.addHeaders(executeFileInfo.getHeader());
                File parsedFile = parserBase.process(executeFileInfo.getDownloadFile().getAbsolutePath());
                log.info("parsedFile: {}", parsedFile);
                String sinkPath = sinkHandler.send(parserBase.getHeader(), parsedFile);

                mapList = fileIO.readAtsAdScheList(parsedFile);

                executeFileInfo.putExecuteResult(parsedFile, sinkPath);
            }
        }

        // row insert
//        mapList.stream().forEach(x -> {
//            BmsDdAtsAdSche bmsDdAtsAdSche = new BmsDdAtsAdSche();
//            bmsDdAtsAdSche.setYyyymmdd((String) x.get("yyyymmdd"));
//            bmsDdAtsAdSche.setCh_nm((String) x.get("ch_nm"));
//            bmsDdAtsAdSche.setBrdcst_dt((String) x.get("brdcst_dt"));
//            bmsDdAtsAdSche.setCh_id((String) x.get("ch_id"));
//            bmsDdAtsAdSche.setOtv_ch_no((String) x.get("otv_ch_no"));
//            bmsDdAtsAdSche.setOts_ch_no((String) x.get("ots_ch_no"));
//            bmsDdAtsAdSche.setHhmmss((String) x.get("hhmmss"));
//            bmsDdAtsAdSche.setAd_order((String) x.get("ad_order"));
//            bmsDdAtsAdSche.setAd_id((String) x.get("ad_id"));
//            bmsDdAtsAdSche.setAd_nm((String) x.get("ad_nm"));
//            bmsDdAtsAdSche.setAd_length((String) x.get("ad_length"));
//            bmsDdAtsAdSche.setApln_form_id((String) x.get("apln_form_id"));
//            bmsDdAtsAdScheMapper.insertAtsAdSche(bmsDdAtsAdSche);
//        });


        // bulk insert
        Map<String, Object> param = new LinkedHashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        int totalCount = 0;
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> tmpParam = mapList.get(i);
            list.add(tmpParam);
            if (i > 0 && list.size() % 2000 == 0) { // 2000개씩 bulk insert
                param.put("mapList", list);
                bmsDdAtsAdScheMapper.insertAtsAdSche(param);
                totalCount += list.size();
                list.clear();
            } else if (mapList.size() - totalCount < 2000 && i == (mapList.size() - 1)) { // 2000개 미만 bulk insert
                param.put("mapList", list);
                bmsDdAtsAdScheMapper.insertAtsAdSche(param);
                totalCount += list.size();
                list.clear();
            }
        }
        log.debug("mapList size : {}, ad epg total count : {}", mapList.size(), totalCount);

        executeFileInfo.setSuccess(true);
    }

    public void executeMssPrgmScheCollectTask(ExecuteFileInfo executeFileInfo, SinkHandler sinkHandler, List<ParserBase> parsers, FileIO fileIO) throws Exception {
        log.debug("executeMssPrgmScheCollectTask -> executeFileInfo: {}", executeFileInfo);

        List<Map<String, Object>> mapList = new ArrayList<>();

        if (parsers != null) {
            for (ParserBase parserBase : parsers) {

                parserBase.addHeaders(executeFileInfo.getHeader());
                File parsedFile = parserBase.process(executeFileInfo.getDownloadFile().getAbsolutePath());
                log.info("parsedFile: {}", parsedFile);
                String sinkPath = sinkHandler.send(parserBase.getHeader(), parsedFile);

                mapList = fileIO.readMssPrgmScheList(parsedFile);

                executeFileInfo.putExecuteResult(parsedFile, sinkPath);
            }
        }

        // bulk insert
        Map<String, Object> param = new LinkedHashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        int totalCount = 0;
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> tmpParam = mapList.get(i);
            list.add(tmpParam);
            if (i > 0 && list.size() % 2000 == 0) { // 2000개씩 bulk insert
                param.put("mapList", list);
                bmsDdMssPrgmScheMapper.insertMssPrgmSche(param);
                totalCount += list.size();
                list.clear();
            } else if (mapList.size() - totalCount < 2000 && i == (mapList.size() - 1)) { // 2000개 미만 bulk insert
                param.put("mapList", list);
                bmsDdMssPrgmScheMapper.insertMssPrgmSche(param);
                totalCount += list.size();
                list.clear();
            }
        }
        log.debug("mapList size : {}, prgm epg total count : {}", mapList.size(), totalCount);

        executeFileInfo.setSuccess(true);
    }

    public void executeMakeAdScheTask() throws Exception {
        HashMap<String, Object> map = new LinkedHashMap<>();
//        map.put("yyyymmdd", DateUtil.getCurrentDay());
//        map.put("brdcst_dt", DateUtil.getCurrentDay());
//        map.put("prev_from_yyyymmdd", DateUtil.getFirstDayOfPrevMonth());
//        map.put("prev_to_yyyymmdd", DateUtil.getLastDayOfPrevMonth());

        map.put("yyyymmdd", "20180428");
        map.put("brdcst_dt", "20180428");
        map.put("prev_from_yyyymmdd", "20180401");
        map.put("prev_to_yyyymmdd", "20180430");

        List<BmsDdAdTmpResSche> tmpResScheList = bmsDdAdTmpResScheMapper.selAdNprgmEpgForMerge(map);
        tmpResScheList.stream().forEach(x -> bmsDdAdTmpResScheMapper.insertAdTmpResSche(x));

        // 시간되면 -> (1-2번 녹화완료된 데이터와 4번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장)

        // 5. 긴급 광고 편성표 데이터가 있는지 체크, 존재하면
        List<BmsDdAdResSche> resScheList = new ArrayList<>();
//        int res = bmsDdAdResScheMapper.selAdminScheCheck(map);
        int res = 1;
        if (res > 0) {
        // 6. 5번 데이터 + 6번 데이터 매핑, 6번 데이터는 최상위로 편성될 수 있게 생성하는데 이미 5번 데이터에 6번 데이터가 존재하면 스킵, 아니면 db 저장
            resScheList = bmsDdAdResScheMapper.selTmpScheNadminScheForMerge(map);
        }
        resScheList.stream().forEach(x -> bmsDdAdResScheMapper.insertAdResSche(x));

        // 7. 6번 데이터 파일 생성(최종 편성표)
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("list", resScheList);
        args.put("bmsMetaProperties", bmsMetaProperties);
        Map<String, Object> result = fileUtil.writeFileAdResSche(args);

        log.debug((String) result.get("filePath"));
    }

    public void executeMediaRecFileCollectTask(ExecuteFileInfo executeFileInfo, SinkHandler sinkHandler, List<ParserBase> parsers, FileIO fileIO) throws Exception {
        log.debug("executeMediaRecFileCollectTask -> executeFileInfo: {}", executeFileInfo);

        BmsDdMediaRecordFiles bmsDdMediaRecordFiles = new BmsDdMediaRecordFiles();
        bmsDdMediaRecordFiles.setYyyymmdd(executeFileInfo.getSourceFile().getYyyyMMdd());
        bmsDdMediaRecordFiles.setAd_id(FilenameUtils.getBaseName(executeFileInfo.getSourceFile().getFileName()));
        bmsDdMediaRecordFiles.setRec_file_path(executeFileInfo.getSourceFile().getAbsolutePath());

        bmsDdMediaRecordFilesMapper.insertMediaRecordFiles(bmsDdMediaRecordFiles);

        executeFileInfo.setSuccess(true);
    }
}
