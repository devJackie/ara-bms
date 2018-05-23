package com.kthcorp.daisy.bms.executor;

import com.kthcorp.daisy.bms.fao.SourceHandler;
import com.kthcorp.daisy.bms.fileio.FileIO;
import com.kthcorp.daisy.bms.indexstore.IndexStore;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
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
    ExecuteService executeService;

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
                        // business logic flow
                        // 1. 아메바 .idx 파일 존재 체크, .FIN 가 있을 경우는 주기마다 체크 하지 않도록 구현
                        // 1-1. .mp4, .jpg 파일 존재 체크 (zookeeper index 에 파일 .mp4 저장)
                        // 1-2. 1 과 1-1 를 fileId 로 매핑 후 merge 하고 녹화된 파일정보 db 저장
                        // 1-3. .idx 가 없을 경우 프로세스 종료, 매분 또는 일정 주기마다 .idx 가 있는지 체크 (30분마다 또는 1시간마다 배치 실행할 예정 또는 sping boot 의 schedule 또는 cron 사용)
                        executeService.executeRecordFileProcessTask(executeFileInfo);
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
            // 2. 선천 epg 수집, 모듈 개발
            executeService.executeAtsScheCollectTask();

            // 3. mss 프로그램 epg 수집, 모듈 개발
            executeService.executeMssScheCollectTask();

            // 4. 상위 30개 채널 쿼리 get +  선천 광고 익일 epg 테이블 + 녹화파일 테이블 매핑, 녹화파일이 있으면 녹화파일은 제외
            // 4-1. mss 프로그램 epg 테이블 + 4번 선천 광고 익일 epg 테이블 매핑 (검증 필요)
            // (프로그램 종료시간 -15분을 start_dt, 15분후를 end_dt 로 기준 정함, 광고 epg 생성 되게 쿼리 생성 후 epg 데이터 db 저장)
            executeService.executeMakeAdScheTask();

            // 5. skylife 에서 제공하는 미디어 서버 광고 파일 수집 및 db 저장 (분당 ara 서버에 put 방식으로 진행 예정)
            executeService.executeMediaCollectTask();

            // Artificial delay of 1s for demonstration purposes
            Thread.sleep(1000L);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            CollectorUtil.quietlyClose(sourceHandler);
        }
        return CompletableFuture.completedFuture(result);
    }
}
