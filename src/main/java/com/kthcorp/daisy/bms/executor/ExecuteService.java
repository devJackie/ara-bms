package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.SinkHandler;
import com.kthcorp.daisy.bms.fileio.FileIO;
import com.kthcorp.daisy.bms.indexstore.IndexStore;
import com.kthcorp.daisy.bms.parser.ParserBase;
import com.kthcorp.daisy.bms.properties.BmsMetaProperties;
import com.kthcorp.daisy.bms.repository.*;
import com.kthcorp.daisy.bms.repository.entity.BmsDdAdResSche;
import com.kthcorp.daisy.bms.repository.entity.BmsDdAdTmpResSche;
import com.kthcorp.daisy.bms.repository.entity.BmsDdAmoebaRecordFiles;
import com.kthcorp.daisy.bms.repository.entity.BmsDdMediaRecordFiles;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import com.kthcorp.daisy.bms.util.DateUtil;
import com.kthcorp.daisy.bms.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yaml.snakeyaml.Yaml;

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
    private static final String SINK_CONFIG = "sinkConfig";
    String TO_DAY, NEXT_DAY, FIRST_DAY_OF_PREV_MONTH, LAST_DAY_OF_PREV_MONTH;

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

        log.info("start insertAtsAdSche");
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
        log.info("end insertAtsAdSche -> mapList size : {}, ad epg total count : {}", mapList.size(), totalCount);

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

        log.info("start insertMssPrgmSche");
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
        log.debug("end insertMssPrgmSche -> mapList size : {}, prgm epg total count : {}", mapList.size(), totalCount);

        executeFileInfo.setSuccess(true);
    }

    public Map<String, Object> executeMakeAdScheTask(String executeDate) throws Exception {

        if (executeDate != null && executeDate.length() > 0) { // 재처리 수동 날짜가 있을 시 --executeDate=${yyyyMMdd}
            TO_DAY = executeDate;
            NEXT_DAY = DateUtil.getNextDay(executeDate);
            FIRST_DAY_OF_PREV_MONTH = DateUtil.getFirstDayOfPrevMonth(executeDate);
            LAST_DAY_OF_PREV_MONTH = DateUtil.getLastDayOfPrevMonth(executeDate);
        } else {
            TO_DAY = DateUtil.getCurrentDay();
            NEXT_DAY = DateUtil.getNextDay();
            FIRST_DAY_OF_PREV_MONTH = DateUtil.getFirstDayOfPrevMonth();
            LAST_DAY_OF_PREV_MONTH = DateUtil.getLastDayOfPrevMonth();
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("result", false);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("yyyymmdd", TO_DAY);
        map.put("from_yyyymmdd", TO_DAY);
        map.put("to_yyyymmdd", NEXT_DAY);
        map.put("brdcst_dt", TO_DAY);
        map.put("prev_from_yyyymmdd", FIRST_DAY_OF_PREV_MONTH);
        map.put("prev_to_yyyymmdd", LAST_DAY_OF_PREV_MONTH);
        //로컬 테스트용
//        map.put("prev_from_yyyymmdd", "20180401");
//        map.put("prev_to_yyyymmdd", "20180430");


        // get mss prgm sche zookeeper index
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("uri", bmsMetaProperties.getBmsMeta().get("zookeeper-info").get("mss-prgm-sche-step1-uri"));
        IndexStore mssIndexStoreStep1 = context.getBean(IndexStore.class, config);
        IndexStore mssIndexStoreStep2 = null;

        // sink handler 생성 및 최종 편성표 파일, fin 파일 sftp 전송
        Yaml yaml = new Yaml();
        Map rootConfig = yaml.load(new ClassPathResource((String) bmsMetaProperties.getBmsMeta().get("file-info").get("res-sche-yaml-path")).getInputStream());
        SinkHandler sinkHandler = context.getBean(SinkHandler.class, rootConfig.get(SINK_CONFIG));
        Map<String, String> sinkConfig = (Map<String, String>) rootConfig.get(SINK_CONFIG);

        try {
            // mss 의 익일 편성표가 있을 때 생성
            Map<String, Object> latelyMap = bmsDdAdTmpResScheMapper.selPrgmScheNextDay(map);
            if (MapUtils.isEmpty(latelyMap)) {
                latelyMap = new LinkedHashMap<>();
                latelyMap.put("lately_day", "");
            }
            log.debug("{}", latelyMap.toString());

            // 편성표는 두벌 만들어야 함
            // mss 의 익일 편성표가 있는지 체크
            mssIndexStoreStep1.creatingIfNeededForDate(TO_DAY);
            mssIndexStoreStep1.creatingIfNeededForDate(NEXT_DAY);
            List<String> ToDayExistStep1 = mssIndexStoreStep1.getIndexForDate(TO_DAY);
            List<String> NextDayExistStep1 = mssIndexStoreStep1.getIndexForDate(NEXT_DAY);

            log.debug("{}", ToDayExistStep1);
            log.debug("{}", NextDayExistStep1);

            try {
                // mss 의 금일 편성표가 있을 때 생성, ex) 4월 28일 기준 -> 금일 4월 28일 02시 ~ 24시
                log.info("start /index/mss/STEP1/{}/1", TO_DAY);
                if (!ToDayExistStep1.contains("02-24")) { // zookeeper index 존재 유무 체크
                    List<BmsDdAdTmpResSche> tmpResScheList = bmsDdAdTmpResScheMapper.selAdNprgmScheMergeForToDay(map);

                    if (tmpResScheList != null && tmpResScheList.size() > 0) {
                        tmpResScheList.stream().forEach(x -> bmsDdAdTmpResScheMapper.insertAdTmpResSche(x));
                        // 생성 후 zookeeper 저장, ex) /mss/PRGM_SCHE/STEP1/20180428/02-24
                        mssIndexStoreStep1.setIndexForDate(ToDayExistStep1, TO_DAY, "02-24");
                    } else {
                        log.info("STEP1 tmpResScheList is empty --> 1. not set index zookeeper [02-24], 2. not insert bms_dd_ad_tmp_res_sche");
                    }
                }

                // mss 의 익일 편성표가 있을 때 생성, ex) 4월 28일 기준 -> 익일 4월 29일 00시 ~ 02시
//                log.info("start /index/mss/STEP1/{}/1", DateUtil.getNextDay());
                log.info("start /index/mss/STEP1/{}/1", NEXT_DAY);
                if (NEXT_DAY.equals(latelyMap.get("lately_day")) && !NextDayExistStep1.contains("00-02")) {
                    List<BmsDdAdTmpResSche> tmpResScheList = bmsDdAdTmpResScheMapper.selAdNprgmScheMergeForNextDay(map);
                    if (tmpResScheList != null && tmpResScheList.size() > 0) {
                        tmpResScheList.stream().forEach(x -> bmsDdAdTmpResScheMapper.insertAdTmpResSche(x));
                        // 생성 후 zookeeper 저장, ex) /mss/PRGM_SCHE/STEP1/20180429/00-02
                        mssIndexStoreStep1.setIndexForDate(NextDayExistStep1, NEXT_DAY, "00-02");
                    } else {
                        log.info("STEP1 tmpResScheList is empty --> 1. not set index zookeeper [00-02], 2. not insert bms_dd_ad_tmp_res_sche");
                    }
                }
                resMap.put("result", true);
            } catch (Exception e) {
                // step1 zookeeper index clean up
                if (ToDayExistStep1.contains("02-24")) {
                    mssIndexStoreStep1.deleteIndexForDate(TO_DAY + "/" + "1");
                }
                if (NextDayExistStep1.contains("00-02")) {
                    mssIndexStoreStep1.deleteIndexForDate(NEXT_DAY + "/" + "1");
                }
                resMap.put("result", false);
                // rollback
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error("", e);
            }

            // 시간되면 -> (1-2번 녹화완료된 데이터와 4번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장)

            // 5. 긴급 광고 편성표 데이터가 있는지 체크, 존재하면
            List<BmsDdAdResSche> resScheList = new ArrayList<>();
//            int res = bmsDdAdResScheMapper.selAdminScheCheck(map);

            // 6. 5번 데이터 + 6번 데이터 매핑, 6번 데이터는 최상위로 편성될 수 있게 생성(긴급편성표 시간대의 편성표는 다 제거) 하는데
            // 이미 5번 데이터에 6번 데이터가 존재하면 스킵, 아니면 db 저장

            config = new LinkedHashMap<>();
            config.put("uri", bmsMetaProperties.getBmsMeta().get("zookeeper-info").get("mss-prgm-sche-step2-uri"));
            mssIndexStoreStep2 = context.getBean(IndexStore.class, config);

            // 편성표는 두벌 만들어야 함
            // mss 의 익일 편성표가 있는지 체크
            mssIndexStoreStep2.creatingIfNeededForDate(TO_DAY);
            mssIndexStoreStep2.creatingIfNeededForDate(NEXT_DAY);
            List<String> ToDayExistStep2 = mssIndexStoreStep2.getIndexForDate(TO_DAY);
            List<String> NextDayExistStep2 = mssIndexStoreStep2.getIndexForDate(NEXT_DAY);

            // 7. 6번 데이터 파일 생성(최종 편성표)
            Map<String, Object> args = new LinkedHashMap<>();

            try {
                log.info("start /index/mss/STEP2/{}/1", TO_DAY);
                if (ToDayExistStep1.contains("02-24") && !ToDayExistStep2.contains("02-24")) { // step1, step2 zookeeper index 존재 유무 체크
                    resScheList = bmsDdAdResScheMapper.selTmpScheNadminScheMergeForToDay(map);

                    if (resScheList != null && resScheList.size() > 0) {
                        // 생성 후 zookeeper 저장, ex) /mss/PRGM_SCHE/STEP2/20180428/02-24
                        mssIndexStoreStep2.setIndexForDate(ToDayExistStep2, TO_DAY, "02-24");

                        resScheList.stream().forEach(x -> bmsDdAdResScheMapper.insertAdResSche(x));

                        args.put("list", resScheList);
                        args.put("bmsMetaProperties", bmsMetaProperties);
                        args.put("sche_gubn", "1");
                        args.put("to_day", TO_DAY);
                        // 8. .dat, .fin 파일 생성
                        Map<String, Object> result = fileUtil.writeFileAdResSche(args);

                        log.debug("filePath: {}", result.get("filePath"));

                        // send sftp
                        String sinkFilePath = sinkHandler.send(sinkConfig, (File) result.get("filePath"));
                        String sinkFinFilePath = sinkHandler.send(sinkConfig, (File) result.get("finFilePath"));
                        log.debug("sinkFilePath: {}", sinkFilePath);
                        log.debug("sinkFinFilePath: {}", sinkFinFilePath);
                    } else {
                        log.info("STEP2 resScheList is empty --> 1. not set index zookeeper [02-24], 2. not insert bms_dd_ad_res_sche");
                    }
                }

                resScheList.clear();
                args.clear();
                // mss 의 익일 편성표가 있을 때 생성, ex) 4월 28일 기준 -> 익일 4월 29일 00시 ~ 02시
                log.info("start /index/mss/STEP2/{}/1", NEXT_DAY);
                if (NEXT_DAY.equals(latelyMap.get("lately_day")) &&
                        (NextDayExistStep1.contains("00-02") && !NextDayExistStep2.contains("00-02"))) { // 익일 편성표, step1, step2 zookeeper index 존재 유무 체크
                    resScheList = bmsDdAdResScheMapper.selTmpScheNadminScheMergeForNextDay(map);

                    if (resScheList != null && resScheList.size() > 0) {
                        // 생성 후 zookeeper 저장, ex) /mss/PRGM_SCHE/STEP2/20180429/00-02
                        mssIndexStoreStep2.setIndexForDate(NextDayExistStep2, NEXT_DAY, "00-02");

                        resScheList.stream().forEach(x -> bmsDdAdResScheMapper.insertAdResSche(x));

                        args.put("list", resScheList);
                        args.put("bmsMetaProperties", bmsMetaProperties);
                        args.put("sche_gubn", "2");
                        args.put("next_day", NEXT_DAY);
                        // 8. .dat, .fin 파일 생성
                        Map<String, Object> result = fileUtil.writeFileAdResSche(args);

                        log.debug("filePath: {}", result.get("filePath"));

                        // send sftp
                        String sinkFilePath = sinkHandler.send(sinkConfig, (File) result.get("filePath"));
                        String sinkFinFilePath = sinkHandler.send(sinkConfig, (File) result.get("finFilePath"));
                        log.debug("sinkFilePath: {}", sinkFilePath);
                        log.debug("sinkFinFilePath: {}", sinkFinFilePath);
                    } else {
                        log.info("STEP2 resScheList is empty --> 1. not set index zookeeper [00-02], 2. not insert bms_dd_ad_res_sche");
                    }
                }
                resMap.put("result", true);
            } catch (Exception e) {
                // step1, step2 zookeeper index clean up
                if (ToDayExistStep1.contains("02-24")) {
                    mssIndexStoreStep1.deleteIndexForDate(TO_DAY + "/" + "1");
                }
                if (NextDayExistStep1.contains("00-02")) {
                    mssIndexStoreStep1.deleteIndexForDate(NEXT_DAY + "/" + "1");
                }
                if (ToDayExistStep2.contains("02-24")) {
                    mssIndexStoreStep2.deleteIndexForDate(TO_DAY + "/" + "1");
                }
                if (NextDayExistStep2.contains("00-02")) {
                    mssIndexStoreStep2.deleteIndexForDate(NEXT_DAY + "/" + "1");
                }
                resMap.put("result", false);
                // rollback
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error("", e);
            }
        } catch (Exception e) {
            resMap.put("result", false);
            // rollback
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        } finally {
            CollectorUtil.quietlyClose(sinkHandler);
        }
        return resMap;
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
