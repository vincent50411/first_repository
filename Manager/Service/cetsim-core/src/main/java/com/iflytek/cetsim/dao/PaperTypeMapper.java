package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.PaperType;
import com.iflytek.cetsim.model.PaperTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface PaperTypeMapper {
    List<PaperType> selectByExample(PaperTypeExample example);

    PaperType selectByPrimaryKey(String code);

    int updateByExampleSelective(@Param("record") PaperType record, @Param("example") PaperTypeExample example);

    int updateByExample(@Param("record") PaperType record, @Param("example") PaperTypeExample example);

    int updateByPrimaryKeySelective(PaperType record);

    int updateByPrimaryKey(PaperType record);
}