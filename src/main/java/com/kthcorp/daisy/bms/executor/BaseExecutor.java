package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.SourceHandler;
import com.kthcorp.daisy.bms.fileio.FileIO;
import com.kthcorp.daisy.bms.indexstore.IndexStore;
import com.kthcorp.daisy.bms.repository.BmsDdRecordFilesMapper;
import com.kthcorp.daisy.bms.repository.entity.BmsDdRecordFiles;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Slf4j
public abstract class BaseExecutor implements CommonExecutor {

    protected ApplicationContext context;
    protected IndexStore indexStore;
    final SourceHandler sourceHandler;
    final FileIO fileIO;
    private static final String INDEX_CONFIG = "indexConfig";
    private static final String SOURCE_CONFIG = "sourceConfig";
    private static final String FILE_IO_CONFIG = "fileIOConfig";

    BaseExecutor(ApplicationContext context, Map<String, Object> config) {
        this.context = context;
        IndexStore indexStore = context.getBean(IndexStore.class, config.get(INDEX_CONFIG));
        this.indexStore = indexStore;
        SourceHandler sourceHandler = context.getBean(SourceHandler.class, config.get(SOURCE_CONFIG));
        this.sourceHandler = sourceHandler;
        FileIO fileIO = context.getBean(FileIO.class, config.get(FILE_IO_CONFIG));
        this.fileIO = fileIO;
    }

    abstract List<ExecuteFileInfo> getExecuteFileInfos() throws Exception;

    abstract void setIndex(ExecuteFileInfo executeFileInfo) throws Exception;

    @Autowired
    BmsDdRecordFilesMapper bmsDdRecordFilesMapper;

    @Transactional
    @Async
    public CompletableFuture<String> execute() {
        String result = null;
        try {
            log.debug("start {}", "execute");

            List<ExecuteFileInfo> executeFileInfos = getExecuteFileInfos();

            Collections.sort(executeFileInfos, Comparator.comparing(ExecuteFileInfo::getSourceFile));

            long totalTaskCount = executeFileInfos.stream().filter(x -> !x.isFinished()).count();
            log.info("Task count -> {}", totalTaskCount);
            for (ExecuteFileInfo executeFileInfo : executeFileInfos) {

                if (!executeFileInfo.isFinished()) {

                    try {
                        log.info("RecFile: {}", executeFileInfo.getSourceFile().getFileName());
                        executeTask(executeFileInfo);
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        executeFileInfo.setFinished(true);
                    }
                    setIndex(executeFileInfo);

                    log.info("{} end", executeFileInfo.getSourceFile().getAbsolutePath());
                } else {
                    log.debug("{} is not new file", executeFileInfo.getSourceFile());
                }
            }
            // Artificial delay of 1s for demonstration purposes
            Thread.sleep(1000L);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            CollectorUtil.quietlyClose(sourceHandler);
        }
        return CompletableFuture.completedFuture(result);
    }

    public void executeTask(ExecuteFileInfo executeFileInfo) throws Exception {
        // business logic flow
        // 1. 아메바 .idx 파일 존재 체크, .idx 가 있을 경우는 주기마다 체크 하지 않도록 zookeeper index 저장
        // 1-1. .mp4, .jpg 파일 존재 체크
        // 1-2. 1 과 1-1 를 fileId 로 매핑 후 merge 하고 녹화된 파일정보 db 저장
        // 1-3. .idx 가 없을 경우 프로세스 종료, 매분 또는 일정 주기마다 .idx 가 있는지 체크 (30분마다 또는 1시간마다 배치 실행할 예정 또는 sping boot 의 schedule 또는 cron 사용)

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

            resultRecInfoFiles.parallelStream().forEach(x -> {
                BmsDdRecordFiles bmsDdRecordFiles = new BmsDdRecordFiles();
                bmsDdRecordFiles.setYyyyMMdd(executeFileInfo.getSourceFile().getYyyyMMdd());
                bmsDdRecordFiles.setFileId((String) x.get("fileId"));
                bmsDdRecordFiles.setAplnFormId((String) x.get("aplnFormId"));
                bmsDdRecordFiles.setAdId((String) x.get("adId"));
                bmsDdRecordFiles.setChId((String) x.get("chId"));
                bmsDdRecordFiles.setChNo((String) x.get("chNo"));
                bmsDdRecordFiles.setStartDt((String) x.get("startDt"));
                bmsDdRecordFiles.setRecFilePath(executeFileInfo.getSourceFile().getAbsolutePath());
                bmsDdRecordFiles.setRecThumbFilePath(executeFileInfo.getSourceFile().getThumbAbsolutePath());
                bmsDdRecordFiles.setBrdcstDt((String) x.get("brdcstDt"));

                bmsDdRecordFilesMapper.insertRecordFiles(bmsDdRecordFiles);
            });

            // 2. mss 프로그램 epg 수집

            // 3. 선천 epg 수집

            // 4. 상위 30개 채널 쿼리 get +  선천 광고 익일 epg 테이블 + 녹화파일 테이블 매핑, 녹화파일이 있으면 녹화파일은 제외
            // 4-1. mss 프로그램 epg 테이블 + 4번 선천 광고 익일 epg 테이블 매핑
            // (프로그램 종료시간 -15분을 start_dt, 15분후를 end_dt 로 기준 정함, 광고 epg 생성 되게 쿼리 생성 후 epg 데이터 db 저장)

            // 5. 1-2번 녹화완료된 데이터와 4번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장

            // 6. 긴급 광고 편성표 데이터가 있는지 체크

            // 7. 5번 데이터 + 6번 데이터 매핑, 6번 데이터는 최상위로 편성될 수 있게 생성하는데 이미 5번 데이터에 6번 데이터가 존재하면 스킵, 아니면 db 저장

            // 8. skylife 에서 제공하는 미디어 서버 광고 파일 저장 (분당 ara 서버에 put 방식으로 진행 예정)

            executeFileInfo.setSuccess(true);
        } else {
            log.info("The idx file line split count is not 3. idxRecFilePath -> {}", executeFileInfo.getSourceFile().getIdxRecFilePath());
        }
    }
}
