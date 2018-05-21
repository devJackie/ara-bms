package com.kthcorp.daisy.bms.repository;


import com.kthcorp.daisy.bms.repository.entity.BmsUser;
import com.kthcorp.daisy.bms.repository.support.BmsSchema;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@BmsSchema
public interface BmsUserMapper {

    public List<BmsUser> findAll();

    public List<BmsUser> findByUserName(@Param("userName") String userName);

    public BmsUser findOne(Long id);

    public Boolean exists(Long id);

    public void save(BmsUser users);

    public void update(BmsUser users);

    public void delete(BmsUser users);
}
