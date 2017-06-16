package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.ScoreDetail;
import com.iflytek.cetsim.model.ScoreDetailKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface ScoreDetailMapper {
    int insert(ScoreDetail record);

    int insertSelective(ScoreDetail record);

    ScoreDetail selectByPrimaryKey(ScoreDetailKey key);

    void updateScoreDetail(ScoreDetail scoreDetail);

    List<ScoreDetail> selectAllByRecordId(String recordCode);

    void updateSimTotalScore(Map<String, Object> params);

    void updateTrainTotalScore(Map<String, Object> params);

    void updateSpecialTotalScore(Map<String, Object> params);

    void insertXmlFile(Map<String, String> params);
}