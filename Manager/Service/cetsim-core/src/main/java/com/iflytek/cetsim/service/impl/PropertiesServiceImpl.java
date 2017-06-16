package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.dao.PropertiesMapper;
import com.iflytek.cetsim.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/6/14.
 */
@Service
@Transactional
public class PropertiesServiceImpl implements PropertiesService
{
    @Autowired
    private PropertiesMapper ropertiesMapper;



}
