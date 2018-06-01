package com.kthcorp.daisy.bms.indexstore;


import com.kthcorp.daisy.bms.ZkClient;
import com.kthcorp.daisy.bms.util.CollectorUtil;
import org.json.JSONArray;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devjackie on 2018. 5. 9..
 */
public class ZkIndexStore implements IndexStore {

    private ZkClient zkClient;

    private String indexPath;

    public ZkIndexStore(ZkClient zkClient, String indexPath) throws Exception {
        this.zkClient = zkClient;
        this.indexPath = indexPath;
        zkClient.creatingParentsIfNeeded(indexPath);
    }

    @Override
    public String getIndex() throws Exception {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        JSONArray jsonArray = new JSONArray(zkClient.getData(indexPath));
        for (int i = 0, n = jsonArray.length(); i < n; i++) {
            jsonArrayBuilder.add((String) jsonArray.get(i));
        }
        for (String node : zkClient.getChildren(indexPath)) {
            jsonArray = new JSONArray(zkClient.getData(indexPath + "/" + node));
            for (int i = 0, n = jsonArray.length(); i < n; i++) {
                jsonArrayBuilder.add((String) jsonArray.get(i));
            }
        }
        return jsonArrayBuilder.build().toString();
    }

    @Override
    public void creatingIfNeededForDate(String index) throws Exception {
        zkClient.creatingIfNeeded(indexPath + "/" + index);
    }

    @Override
    public List<String> getIndexForDate(String index) throws Exception {
        List<String> indexList = new ArrayList<>();

        for (String node : zkClient.getChildren(indexPath + "/" + index)) {
            JSONArray jsonArray = new JSONArray(zkClient.getData(indexPath + "/" + index + "/" + node));
            for (int i = 0, n = jsonArray.length(); i < n; i++) {
                indexList.add((String) jsonArray.get(i));
            }
        }
        return indexList;
    }

    @Override
    public List<String> getIndexAsList() throws Exception {
        List<String> indexList = new ArrayList<>();
        String zNodeString;

        zNodeString = zkClient.getData(indexPath);
        if (zNodeString != null && !zNodeString.isEmpty()) {
            if(zNodeString.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(zNodeString);
                for (int i = 0, n = jsonArray.length(); i < n; i++) {
                    indexList.add((String) jsonArray.get(i));
                }
            } else {
                indexList.add(zNodeString);
            }
        }

        for (String node : zkClient.getChildren(indexPath)) {
            try {
                zNodeString = zkClient.getData(indexPath + "/" + node);
                if (zNodeString != null && !zNodeString.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(zNodeString);
                    for (int i = 0, n = jsonArray.length(); i < n; i++) {
                        indexList.add((String) jsonArray.get(i));
                    }
                }
            } catch (Exception e) {
                System.out.println(zNodeString);
                throw e;
            }
        }
        return indexList;
    }

    @Override
    public void setIndex(String indexString) throws Exception {
        List<String> indexes = new ArrayList<>();
        indexes.add(indexString);
        setIndex(indexes);
    }

    @Override
    public void setIndex(List<String> indexes) throws Exception {
        for (String node : zkClient.getChildren(indexPath)) {
            zkClient.delete(indexPath + "/" + node);
        }

        List<String> subIndexes = new ArrayList<>();
        String indexSubPath;
        int cnt = 0;
        for (String node : indexes) {
            if (cnt % 1000 == 0) {
                indexSubPath = indexPath + "/" + (cnt / 1000);
                zkClient.creatingParentsIfNeeded(indexSubPath);
                zkClient.setData(indexSubPath, CollectorUtil.createJsonArray(subIndexes));
                subIndexes.clear();
            }
            subIndexes.add(node);
            cnt++;
        }

        if (subIndexes.size() != 0) {
            indexSubPath = indexPath + "/" + ((cnt / 1000) + 1);
            zkClient.creatingParentsIfNeeded(indexSubPath);
            zkClient.setData(indexSubPath, CollectorUtil.createJsonArray(subIndexes));
            subIndexes.clear();
        }
    }

    @Override
    public void setIndexForDate(List<String> indexes, String date, String index) throws Exception {

        indexes.add(index);

        List<String> subIndexes = new ArrayList<>();
        String indexSubPath;
        int cnt = 0;
        for (String node : indexes) {
            if (cnt % 1000 == 0) {
                indexSubPath = indexPath + "/" + date + "/" + (cnt / 1000);
                zkClient.creatingParentsIfNeeded(indexSubPath);
                zkClient.setData(indexSubPath, CollectorUtil.createJsonArray(subIndexes));
                subIndexes.clear();
            }
            subIndexes.add(node);
            cnt++;
        }

        if (subIndexes.size() != 0) {
            indexSubPath = indexPath + "/" + date + "/" + ((cnt / 1000) + 1);
            zkClient.creatingParentsIfNeeded(indexSubPath);
            zkClient.setData(indexSubPath, CollectorUtil.createJsonArray(subIndexes));
            subIndexes.clear();
        }
    }
}
