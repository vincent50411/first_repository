package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.PaperQueryDTO;
import com.iflytek.cetsim.model.Paper;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface PaperMapper {
    int deleteByPrimaryKey(String code);

    int insert(Paper paper);

    int insertSelective(Paper paper);

    int updateByPrimaryKeySelective(Paper paper);

    int updateAllowUseage(Paper paper);

    Paper selectByPrimaryKey(String code);

    Paper selectByPrimaryKeyNotBlob(String code);



    int selectPaperByNameAndType(Map paramMap);


    List<PaperInfoDTO> selectPaperByExample(Map<String, Object> paramMap);


    int countByExample(Map<String, Object> paramMap);

    List<PaperInfoDTO> getPapersByHashMap(Map<String, Object> map);

    int getPapersCountByHashMap(Map<String, Object> map);

    List<Paper> getPapersByCodeList(Map<String, Object> map);
}