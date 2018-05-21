package com.kthcorp.daisy.bms.test;

import com.kthcorp.daisy.bms.repository.entity.RecFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@RunWith(SpringRunner.class)
@Slf4j
public class SplitTest {

    @Test
    public void stringTokenizerTest() {
        String str = "20180428022500_17_0122#201804019-180416GSPB30_201804032-180420RDLN15_201804028-180417TMSD30.MP4";
//        List<String> carNames = Arrays.asList(str.split(","));
//        List<Person> newPersonList = carNames.stream().map(x -> new Person(x, x.2)).collect(Collectors.toList());

//        String str = "20180428022500_17_0122#201804019-180416GSPB30_201804032-180420RDLN15.MP4";
        String[] fileArray = FilenameUtils.getBaseName(str).split("#");
        if (fileArray.length > 1) {
            List<String> firFileInfo = new ArrayList<>(Arrays.asList(fileArray[0].split("_", 3)));
            List<String> secFileInfo = new ArrayList<>(Arrays.asList(fileArray[1].split("_")));

            log.debug("firFileInfo : {}", firFileInfo);
            log.debug("secFileInfo : {}", secFileInfo);

            List<Map<String, Object>> resultRecInfoFiles = new ArrayList<>();

//            Map<String, Object> tMap1 = new LinkedHashMap<>();

            for (int i = 0; i < fileArray.length - 1; i++) {
                for (int j = 0; j < secFileInfo.size(); j++) {
                    Map<String, Object> tMap1 = new LinkedHashMap<>();
                    String temp1 = fileArray[0];
                    StringTokenizer tokenizer1 = new StringTokenizer(temp1, "_");
                    while (tokenizer1.hasMoreTokens()) {
                        tMap1.put("startDt", tokenizer1.nextToken());
                        tMap1.put("chNo", tokenizer1.nextToken());
                        tMap1.put("chId", tokenizer1.nextToken());
                    }
//                    tMap2 = new LinkedHashMap<>();
                    String temp2 = secFileInfo.get(j);
                    StringTokenizer tokenizer2 = new StringTokenizer(temp2, "-");
                    while (tokenizer2.hasMoreTokens()) {
                        tMap1.put("aplnFormId", tokenizer2.nextToken());
                        tMap1.put("adId", tokenizer2.nextToken());
                    }
                    resultRecInfoFiles.add(tMap1);
                }
//                tMapList.add(tMap1);
            }

//            for (String key : tMap1.keySet()) {
//                System.out.println(String.format("키 : %s, 값 : %s", key, tMap1.get(key)));
//            }
//
//            Map<String, Object> tMap2;
//            for (int j = 0; j < secFileInfo.size(); j++) {
//                tMap2 = new LinkedHashMap<>();
//                String temp = secFileInfo.get(j);
//                StringTokenizer tokenizer1 = new StringTokenizer(temp, "-");
//                while (tokenizer1.hasMoreTokens()) {
//                    tMap2.put("aplnFormId", tokenizer1.nextToken());
//                    tMap2.put("adId", tokenizer1.nextToken());
//                }
//                tMapList.add(tMap2);
//            }

            for (Map<String, Object> map : resultRecInfoFiles) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    log.debug("key : {},  value : {}", entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Test
    public void tempSplitTest() {

//        String str = "20180428022500_17_0122#201804019-180416GSPB30_201804032-180420RDLN15_201804028-180417TMSD30.MP4";
        String str = "20180428022500_17_0122#201804019-180416GSPB30_201804032-180420RDLN15.MP4";
        StringTokenizer tokenizer = new StringTokenizer(FilenameUtils.getBaseName(str), "#");

        while (tokenizer.hasMoreTokens()) {
            System.out.println(tokenizer.nextToken());
        }

        List<String> items = Arrays.asList(FilenameUtils.getBaseName(str).split("\\s*&\\s*"));
        log.debug("{}", items);

//        List<String> items1 = items.stream().map(x1 -> {
//            List<String> item = Arrays.asList(FilenameUtils.getBaseName(x1).split("\\s*_\\s*"));
//            return item;
//        });


//        String str1 = "name~peter-add~mumbai-md~v-refNo~";
//        Map<String,String> map = Pattern.compile("\\s*-\\s*")
//                .splitAsStream(str1.trim())
//                .map(s -> s.split("~", 2))
//                .collect(Collectors.toMap(a -> a[0], a -> a.length>1? a[1]: ""));
//
//        log.debug(map.toString());


        String[] fileArray = FilenameUtils.getBaseName(str).split("#");
        if (fileArray.length > 1) {
            List<String> firFileInfo = Arrays.asList(fileArray[0].split("_", 3));
            List<String> secFileInfo = Arrays.asList(fileArray[1].split("_"));

            log.debug("firFileInfo : {}", firFileInfo);
            log.debug("secFileInfo : {}", secFileInfo);

            List<Map<String, Object>> tMapList = new ArrayList<>();

            Map<String, Object> tMap1 = new LinkedHashMap<>();
            for (int i = 0; i < fileArray[0].length(); i++) {
//                tMap1 = new LinkedHashMap<>();
                String temp = fileArray[0];
                StringTokenizer tokenizer1 = new StringTokenizer(temp, "_");
                while (tokenizer1.hasMoreTokens()) {
                    tMap1.put("startDt", tokenizer1.nextToken());
                    tMap1.put("chNo", tokenizer1.nextToken());
                    tMap1.put("chId", tokenizer1.nextToken());
                }
//                tMapList.add(tMap1);
            }

            for( String key : tMap1.keySet() ){
                System.out.println( String.format("키 : %s, 값 : %s", key, tMap1.get(key)) );
            }

            Map<String, Object> tMap2;
            for (int j = 0; j < secFileInfo.size(); j++) {
                tMap2 = new LinkedHashMap<>();
                String temp = secFileInfo.get(j);
                StringTokenizer tokenizer1 = new StringTokenizer(temp, "-");
                while (tokenizer1.hasMoreTokens()) {
                    tMap2.put("aplnFormId", tokenizer1.nextToken());
                    tMap2.put("adId", tokenizer1.nextToken());
                }
                tMapList.add(tMap2);
            }



            for (Map<String, Object> map : tMapList) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    log.debug("key : {},  value : {}", entry.getKey(), entry.getValue());
                }
            }

//            for( String key : tMap.keySet() ){
//                System.out.println( String.format("키 : %s, 값 : %s", key, tMap.get(key)) );
//            }

//            Map<String, String> tMap1 = new LinkedHashMap<>();
//            tMap.put("aplnFormId", "");
//            tMap.put("adId", "");
//            tMap.put("startDt", "");
//            tMap.put("chNo", "");
//            tMap.put("chId", "");
//            log.debug(tMap1.toString());

//            Map<String, Object> map = firFileInfo.stream().collect(Collectors.toMap(Function.identity(), s -> s));
//            map.forEach((x, y) -> System.out.println("Key: " + x +", value: "+ y));

            // firFileInfo : [20180428022500, 17, 0122]
            // secFileInfo : [201804019-180416GSPB30, 201804032-180420RDLN15, 201804028-180417TMSD30]


            // resultRecInfos : [[201804019, 180416GSPB30, 20180428022500, 17, 0122], [201804032, 180420RDLN15, 20180428022500, 17, 0122], [201804028, 180417TMSD30, 20180428022500, 17, 0122]]
            List<List<String>> resultRecInfoFiles = secFileInfo.stream().map(x -> {
                List<String> list = new ArrayList<>();
                String[] adInfos = x.split("-");
                for (String adInfo : adInfos) {
                    list.add(adInfo);
                }
                firFileInfo.stream().forEach(s -> list.add(s));
                return list;
            }).collect(Collectors.toList());


            resultRecInfoFiles.stream().forEach(x -> {
                RecFileInfo recFileInfo = new RecFileInfo();
                x.stream().forEach(list -> {
                    log.debug(list);
                });
            });


//            resultRecInfoFiles.stream().forEach(x -> {
//                List<Info> aa = new ArrayList<>();
//                aa.add(new Info(x));
//                });

//                    recFileInfo.setStartDt(list);
//                    recFileInfo.setAplnFormId(splits[1]);
//                    recFileInfo.setAdNo(splits[2]);
//                    recFileInfo.setOtvChNo(splits[3]);
//                    recFileInfo.setChId(splits[4]);

//            });

            log.debug("resultRecInfoFiles : {}", resultRecInfoFiles);
        } else {
            log.info("The idx file line split count is not 5. recFilePath -> {}", str);
        }


    }

    @Test
    public void splitTest() {

        String str = "20180428022500_17_0122#201804019-180416GSPB30_201804032-180420RDLN15_201804028-180417TMSD30.MP4";
//        String str = "20180428022500_17_0122&201804019-180416GSPB30_201804032.MP4";
        StringTokenizer tokenizer = new StringTokenizer(FilenameUtils.getBaseName(str), "&");

        while (tokenizer.hasMoreTokens()) {
            System.out.println(tokenizer.nextToken());
        }

        List<String> items = Arrays.asList(FilenameUtils.getBaseName(str).split("\\s*&\\s*"));
        log.debug("{}", items);

//        List<String> items1 = items.stream().map(x1 -> {
//            List<String> item = Arrays.asList(FilenameUtils.getBaseName(x1).split("\\s*_\\s*"));
//            return item;
//        });


        String[] fileArray = FilenameUtils.getBaseName(str).split("#");
        if (fileArray.length > 1) {
            List<String> firFileInfo = Arrays.asList(fileArray[0].split("_", 3));
            List<String> secFileInfo = Arrays.asList(fileArray[1].split("_"));

//            Map<String, Object> map = firFileInfo.stream().collect(Collectors.toMap(Function.identity(), s -> s));
//            map.forEach((x, y) -> System.out.println("Key: " + x +", value: "+ y));

            // firFileInfo : [20180428022500, 17, 0122]
            // secFileInfo : [201804019-180416GSPB30, 201804032-180420RDLN15, 201804028-180417TMSD30]
            log.debug("firFileInfo : {}", firFileInfo);
            log.debug("secFileInfo : {}", secFileInfo);

            // resultRecInfos : [[201804019, 180416GSPB30, 20180428022500, 17, 0122], [201804032, 180420RDLN15, 20180428022500, 17, 0122], [201804028, 180417TMSD30, 20180428022500, 17, 0122]]
            List<List<String>> resultRecInfos = secFileInfo.stream().map(x -> {
                List<String> list = new ArrayList<>();
                String[] adInfos = x.split("-");
                for (String adInfo : adInfos) {
                    list.add(adInfo);
                }
                firFileInfo.stream().forEach(s -> list.add(s));
                return list;
            }).collect(Collectors.toList());

            log.debug("resultRecInfos : {}", resultRecInfos);
        } else {
            log.info("The idx file line split count is not 5. recFilePath -> {}", str);
        }


    }

    @Test
    public void SplitFncTest() {
//        String path = "";
        String path = "20180508101134_201804135_180201CHAM5_17_0122.MP4";
        String[] splitPath = path.split("\\.");
//        List<String> splitArray = Arrays.asList(splitPath[0].split("_", 5));
        String[] splits = splitPath[0].split("_", 5);

        if (splits.length == 5) {
            for (int i = 0; i < splitPath.length - 1; i++) {
                log.debug(splits[0]);
                log.debug(splits[1]);
                log.debug(splits[2]);
                log.debug(splits[3]);
                log.debug(splits[4]);
            }
        } else {
            log.info("The idx file line split count is not 5. -> {}", path);
        }

    }
//    RecFileInfo recInfo;
//            for (int i = 0; i < splits.length; i++) {
//        recInfo = new RecFileInfo();
//        recInfo.setStartDt(splits[0]);
//        recInfo.setAplnFormId(splits[1]);
//        recInfo.setAdNo(splits[2]);
//        recInfo.setOtvChNo(splits[3]);
//        recInfo.setChId(splits[4]);
//    }
}
