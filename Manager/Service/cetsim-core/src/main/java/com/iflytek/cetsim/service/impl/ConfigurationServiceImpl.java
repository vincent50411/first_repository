package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.dao.ConfigurationMapper;
import com.iflytek.cetsim.model.Configuration;
import com.iflytek.cetsim.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/5/25.
 */
@Service
@Transactional
public class ConfigurationServiceImpl implements ConfigurationService
{
    @Autowired
    private ConfigurationMapper configurationMapper;

    public Configuration selectByConfigName(String configName)
    {
        return configurationMapper.selectByConfigName(configName);

    }

}
