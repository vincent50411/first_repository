package com.iflytek.cetsim.service;

import com.iflytek.cetsim.model.Configuration;

/**
 * Created by Administrator on 2017/5/25.
 */
public interface ConfigurationService
{
    /**
     * 根据配置名称查询配置项, 名称需要唯一
     * @param configName
     * @return
     */
    Configuration selectByConfigName(String configName);

}
