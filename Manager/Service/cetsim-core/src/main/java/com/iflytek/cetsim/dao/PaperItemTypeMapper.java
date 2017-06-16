package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.PaperItemType;

import java.util.List;

@MyBatisRepository
public interface PaperItemTypeMapper {
    PaperItemType selectByPrimaryKey(String code);

    List<PaperItemType> getAllInfo();
}