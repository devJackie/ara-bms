# ara-bms

production start script (daisy-ara04)

```/home/daisy/ara-bms/bin/ara-bms --executeGroup=LIMIT```

production start script argument (수동 처리 날짜 입력 --executeDate=${yyyymmdd})
익일자 편성표 생성시 현재날짜 + 1 로 설정

```/home/daisy/ara-bms/bin/ara-bms --executeGroup=LIMIT --executeDate=20180629```

# LOCAL TEST
1. program arguments -> --executeGroup=LIMIT 설정
2. resources -> linkServer -> .yml 을 local 에 맞게 수정후 BmsApplication.java main 실행

# 수동 처리 날짜 실행전 삭제 목록
# e.g) 특정날짜 편성표를 삭제
1. zookeeper index 삭제
    + rmr /bms/RES_SCHE/STEP1/20180702/1
    + rmr /bms/RES_SCHE/STEP2/20180702/1
    + rmr /bms/RES_SCHE/STEP1/20180703/1
    + rmr /bms/RES_SCHE/STEP2/20180703/1

2. table 삭제
    + bms_dd_ad_tmp_res_sche brdcst_dt 가 20180702 인 데이터 삭제
    + bms_dd_ad_res_sche 가 20180702 인 데이터 삭제

---

# e.g) 특정날짜 02-24 편성표만 삭제하고 싶을 때 
* (00-02도 같은 방식으로 진행 단, 20180702 02-24, 20180703 00-02 가 한 세트임, 00-02 는 익일 날짜임을 주의)

1. zookeeper index 삭제 
    + 02-24 있는지 확인
      + get /bms/RES_SCHE/STEP1/20180702/1 ["00-02","02-24"]
      + get /bms/RES_SCHE/STEP2/20180702/1 ["00-02","02-24"]
      + 02-24 제외하고 업데이트
      + set /bms/RES_SCHE/STEP1/20180702/1 ["00-02"]
      + set /bms/RES_SCHE/STEP2/20180702/1 ["00-02"]

    + 00-02 있는지 확인
      + get /bms/RES_SCHE/STEP1/20180703/1 ["00-02","02-24"]
      + get /bms/RES_SCHE/STEP2/20180703/1 ["00-02","02-24"]
      + 00-02 제외하고 업데이트
      + set /bms/RES_SCHE/STEP1/20180703/1 ["02-24"]
      + set /bms/RES_SCHE/STEP2/20180703/1 ["02-24"]

2. table 삭제 (00-02 sche_gubn 은 "2")
    + 기준 02-24 인 경우
      + bms_dd_ad_tmp_res_sche brdcst_dt 가 20180702 이고 sche_gubn 이 "1"인 데이터만 삭제
      + bms_dd_ad_res_sche 가 20180702 이고 sche_gubn 이 "1"인 데이터만 삭제

    + 기준 00-02 인 경우
      + bms_dd_ad_tmp_res_sche brdcst_dt 가 20180702 이고 sche_gubn 이 "2"인 데이터만 삭제
      + bms_dd_ad_res_sche 가 20180702 이고 sche_gubn 이 "2"인 데이터만 삭제

