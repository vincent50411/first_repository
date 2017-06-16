package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.PaperItemQueryDTO;
import com.iflytek.cetsim.model.PaperItem;
import com.iflytek.cetsim.model.PaperItemExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface PaperItemMapper {
    int deleteByExample(PaperItemExample example);

    int deleteByPrimaryKey(String code);

    int insert(PaperItem record);

    int insertSelective(PaperItem record);

    List<PaperItem> selectByExampleWithBLOBs(PaperItemExample example);

    List<PaperItem> selectByExample(PaperItemExample example);

    PaperItem selectByPrimaryKey(String code);

    PaperItem selectNotBlobByPrimaryKey(String code);

    int updateByExampleSelective(@Param("record") PaperItem record, @Param("example") PaperItemExample example);

    int updateByExampleWithBLOBs(@Param("record") PaperItem record, @Param("example") PaperItemExample example);

    int updateByExample(@Param("record") PaperItem record, @Param("example") PaperItemExample example);

    int updateByPrimaryKeySelective(PaperItem record);

    int updateByPrimaryKeyWithBLOBs(PaperItem record);

    int updateByPrimaryKey(PaperItem record);

    String getSpecialPaperItemCodeByRecordCode(String examRecordCode);


    int selectPaperItemByCode(Map<String, Object> paramMap);

    /**
     * 查询试题列表
     * @param paramMap
     * @return
     */
    List<PaperItemQueryDTO> selectQuestionList(Map<String, Object> paramMap);

    int countQuestions(Map<String, Object> paramMap);

    /**
     * 查询学生的试题列表
     * @param paramMap
     * @return
     */
    List<PaperItemQueryDTO> selectQuestionListByAccount(Map<String, Object> paramMap);


}