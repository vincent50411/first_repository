package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.PaperBuffer;
import com.iflytek.cetsim.model.PaperItemBuffer;

@MyBatisRepository
public interface PaperItemBufferMapper {
    int insert(PaperItemBuffer record);

    int insertSelective(PaperItemBuffer record);

    PaperItemBuffer selectByPrimaryKey(String code);

    /**
     * 获取试题数据包
     * @param examRecordCode
     * @return
     */
    PaperItemBuffer getPaperItemBuffData(String examRecordCode);
}