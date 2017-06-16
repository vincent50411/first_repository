package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.Role;

@MyBatisRepository
public interface RoleMapper {
    Role selectByPrimaryKey(Integer id);
}