\set next_day1 '''p'''||to_char(CURRENT_TIMESTAMP + '''1 day''', '''yyyyMMdd'''))
\set next_day2 to_char(CURRENT_TIMESTAMP + '''1 day''', '''yyyyMMdd''')
ALTER TABLE bms_dd_ad_tmp_res_sche ADD PARTITION :next_day1 VALUES(:'next_day2');


\set next_day1 '''p'''||to_char(CURRENT_TIMESTAMP + '''1 day''', '''yyyyMMdd''')::text

ALTER TABLE bms_dd_ad_tmp_res_sche ADD PARTITION :next_day1 VALUES(:'next_day2');