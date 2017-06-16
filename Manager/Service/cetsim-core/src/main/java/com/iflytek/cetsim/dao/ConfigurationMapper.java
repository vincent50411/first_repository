package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.Configuration;

import java.util.List;

@MyBatisRepository
public interface ConfigurationMapper {
    int insert(Configuration record);

    int insertSelective(Configuration record);

    Configuration selectByPrimaryKey(Integer id);

    Configuration selectByConfigName(String configName);

    List<Configuration> selectAllConfig();

    int updateByPrimaryKey(Configuration record);

}