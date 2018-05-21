package com.kthcorp.daisy.bms.service;

import com.kthcorp.daisy.bms.repository.BmsUserMapper;
import com.kthcorp.daisy.bms.repository.entity.BmsUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Service
@Slf4j
public class BmsUserService {

    @Autowired
    private BmsUserMapper bmsUserMapper;

    @Async
    public CompletableFuture<List<BmsUser>> findByUserName(String user) throws InterruptedException {
        log.info("Looking up {}", user);
        List<BmsUser> results = bmsUserMapper.findByUserName("test");
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(results);
    }
}
