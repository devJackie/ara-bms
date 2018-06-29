# ara-bms
ara-bms

# production start script (daisy-ara04)
/home/daisy/ara-bms/bin/ara-bms --executeGroup=LIMIT

# production start script argument (수동 처리 날짜 입력 --executeDate=${yyyymmdd})
/home/daisy/ara-bms/bin/ara-bms --executeGroup=LIMIT --executeDate=20180629

# LOCAL TEST
1. program arguments -> --executeGroup=LIMIT 설정
2. resources -> linkServer -> .yml 을 local 맞게 수정후 BmsApplication.java main 실행
