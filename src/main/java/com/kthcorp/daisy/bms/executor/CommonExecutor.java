package com.kthcorp.daisy.bms.executor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 8..
 */
public interface CommonExecutor {

    CompletableFuture<Map<String, Object>> executeCollect();

    CompletableFuture<Map<String, Object>> executeWorkFlow();
}
