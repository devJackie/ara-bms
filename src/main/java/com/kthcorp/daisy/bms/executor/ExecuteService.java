package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.repository.BmsDdAdResScheMapper;
import com.kthcorp.daisy.bms.repository.BmsDdAdTmpResScheMapper;
import com.kthcorp.daisy.bms.repository.BmsDdRecordFilesMapper;
import com.kthcorp.daisy.bms.repository.entity.BmsDdAdTmpResSche;
import com.kthcorp.daisy.bms.repository.entity.BmsDdRecordFiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by devjackie on 2018. 5. 23..
 */
@Service
@Slf4j
public class ExecuteService {

    @Autowired
    BmsDdRecordFilesMapper bmsDdRecordFilesMapper;

    @Autowired
    BmsDdAdTmpResScheMapper bmsDdAdTmpResScheMapper;

    @Autowired
    BmsDdAdResScheMapper bmsDdAdResScheMapper;

    public void executeRecordFileProcessTask(ExecuteFileInfo executeFileInfo) throws Exception {
        // .idx 파일 리스트
        // AAAAAAAAAA-20180517053610_20180517_0109_32-201804019_180416GSPB30.MP4
        // AAAAAAAAAA-20180517053610_20180517_0109_32-201804032_180420RDLN15.MP4
        // AAAAAAAAAA-20180517053610_20180517_0109_32-201804028_180417TMSD30.MP4
        // 녹화 파일
        // AAAAAAAAAA-20180517053610_20180517_0109_32.MP4
        String[] fileArray = FilenameUtils.getBaseName(executeFileInfo.getSourceFile().getIdxRecFilePath()).split("-");
        if (fileArray != null && fileArray.length > 2) {
            // firFileInfo : [AAAAAAAAAA]
            // secFileInfo : [20180517053610, 20180517, 0109, 32]
            // thiFileInfo : [201804019, 180416GSPB30]
            List<String> firFileInfo = new ArrayList<>(Arrays.asList(fileArray[0]));
            log.debug("firFileInfo : {}", firFileInfo);

            List<Map<String, Object>> resultRecInfoFiles = new ArrayList<>();

            for (int i = 0; i < firFileInfo.size(); i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("fileId", firFileInfo.get(i));
                String temp1 = fileArray[1];
                StringTokenizer tokenizer1 = new StringTokenizer(temp1, "_");
                while (tokenizer1.hasMoreTokens()) {
                    map.put("startDt", tokenizer1.nextToken());
                    map.put("brdcstDt", tokenizer1.nextToken());
                    map.put("chId", tokenizer1.nextToken());
                    map.put("chNo", tokenizer1.nextToken());
                }
                String temp2 = fileArray[2];
                StringTokenizer tokenizer2 = new StringTokenizer(temp2, "_");
                while (tokenizer2.hasMoreTokens()) {
                    map.put("aplnFormId", tokenizer2.nextToken());
                    map.put("adId", tokenizer2.nextToken());
                }
                resultRecInfoFiles.add(map);
            }

            resultRecInfoFiles.stream().forEach(x -> {
                BmsDdRecordFiles bmsDdRecordFiles = new BmsDdRecordFiles();
                bmsDdRecordFiles.setYyyymmdd(executeFileInfo.getSourceFile().getYyyyMMdd());
                bmsDdRecordFiles.setFile_id((String) x.get("fileId"));
                bmsDdRecordFiles.setApln_form_id((String) x.get("aplnFormId"));
                bmsDdRecordFiles.setAd_id((String) x.get("adId"));
                bmsDdRecordFiles.setCh_id((String) x.get("chId"));
                bmsDdRecordFiles.setCh_no((String) x.get("chNo"));
                bmsDdRecordFiles.setStart_dt((String) x.get("startDt"));
                bmsDdRecordFiles.setRec_file_path(executeFileInfo.getSourceFile().getAbsolutePath());
                bmsDdRecordFiles.setRec_thumb_file_path(executeFileInfo.getSourceFile().getThumbAbsolutePath());
                bmsDdRecordFiles.setBrdcst_dt((String) x.get("brdcstDt"));

                bmsDdRecordFilesMapper.insertRecordFiles(bmsDdRecordFiles);
            });

            executeFileInfo.setSuccess(true);
        } else {
            log.info("The idx file line split count is not 3. idxRecFilePath -> {}", executeFileInfo.getSourceFile().getIdxRecFilePath());
        }
    }

    public void executeAtsScheCollectTask() throws Exception {

    }

    public void executeMssScheCollectTask() throws Exception {

    }

    public void executeMakeAdScheTask() throws Exception {

        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("yyyymmdd", "20180428");
        map.put("brdcst_dt", "20180428");
        map.put("from_yyyymmdd", "20180401");
        map.put("to_yyyymmdd", "20180430");
        List<BmsDdAdTmpResSche> tmpResScheList = bmsDdAdTmpResScheMapper.selAdNprgmEpgForMerge(map);
        tmpResScheList.stream().forEach(x -> bmsDdAdTmpResScheMapper.insertAdTmpResSche(x));

        // 시간되면 -> (1-2번 녹화완료된 데이터와 4번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장)

        // 5. 긴급 광고 편성표 데이터가 있는지 체크, 존재하면
        int res = bmsDdAdResScheMapper.selAdminScheCheck(map);
        if (res > 0) {
        // 6. 5번 데이터 + 6번 데이터 매핑, 6번 데이터는 최상위로 편성될 수 있게 생성하는데 이미 5번 데이터에 6번 데이터가 존재하면 스킵, 아니면 db 저장
//        List<BmsDdAdResSche> resScheList = bmsDdAdResScheMapper.selTmpScheNadminScheForMerge(map);
        }
//        resScheList.stream().forEach(x -> bmsDdAdResScheMapper.insertAdResSche(x));

        // 7. 6번 데이터 파일 생성(최종 편성표)

    }

    public void executeMediaCollectTask() throws Exception {

    }
}
