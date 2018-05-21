package com.kthcorp.daisy.bms;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Component
@Lazy
public class ZkClient {

    private final static int baseSleepTimeMs = 1000;
    private final static int maxRetries = 3;

    private CuratorFramework client;

    private Map<String, InterProcessMutex> mutexMap;

    @Value("${daisy.collector.zk-host}")
    private String zkHost;

    @PostConstruct
    private void init() throws Exception {
        URI zkUri = new URI(zkHost);

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        client = CuratorFrameworkFactory.newClient(zkUri.getAuthority(), retryPolicy);
        client.start();
        if (client.checkExists().forPath(zkUri.getPath()) == null) {
            client.create().creatingParentsIfNeeded().forPath(zkUri.getPath(), "".getBytes());
        }
        mutexMap = new HashMap<>();
    }

    public boolean acquire(String path) throws Exception {
        String zkPath = new URI(path).getPath();
        InterProcessMutex mutex;
        if (mutexMap.containsKey(zkPath)) {
            mutex = mutexMap.get(zkPath);
        } else {
            mutex = new InterProcessMutex(client, zkPath);
            mutexMap.put(zkPath, mutex);
        }
        return mutex.acquire(1, TimeUnit.SECONDS);
    }

    public void acquireRelease(String path) throws Exception {
        String zkPath = new URI(path).getPath();
        InterProcessMutex mutex;
        if (mutexMap.containsKey(zkPath)) {
            mutex = mutexMap.get(zkPath);
        } else {
            mutex = new InterProcessMutex(client, zkPath);
            mutexMap.put(zkPath, mutex);
        }
        if (mutex.isAcquiredInThisProcess()) {
            mutex.release();
        }
    }

    public void creatingParentsIfNeeded(String path) throws Exception {
        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().forPath(path, "".getBytes());
        }
    }

    public String getData(String path) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            return new String(client.getData().forPath(path));
        } else {
            return null;
        }
    }

    public void setData(String path, String data) throws Exception {
        client.setData().forPath(path, data.getBytes());
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public void delete(String path) throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    @PreDestroy
    public void close() {
        CloseableUtils.closeQuietly(client);
    }
}
