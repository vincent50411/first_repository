package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.PaperBuffer;

@MyBatisRepository
public interface PaperBufferMapper {
    int insert(PaperBuffer record);

    int insertSelective(PaperBuffer record);

    PaperBuffer selectByPrimaryKey(String code);


    /**
     * 获取试题数据包
     * @param examRecordCode
     * @return
     */
    PaperBuffer getPaperBuffData(String examRecordCode);

    PaperBuffer getPaperBuffData4Train(String examRecordCode);

    String getPaperCodeByRecordCode(String examRecordCode);


}