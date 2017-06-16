package com.iflytek.cetsim.base.dao;

import com.iflytek.cetsim.dto.ExamLoginDTO;
import com.iflytek.cetsim.dto.RecordDTO;
import com.iflytek.cetsim.model.ScoreDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试记录数据访问层接口
 *
 * Created by wbyang3 on 2017/5/8.
 */
public interface BaseRecordDao {

    List<ExamLoginDTO> examineeLogin(Map<String, Object> param);

    List<RecordDTO> getCurrentRecord(String recordId);

    List<ExamLoginDTO> selectPartner(Map<String, Object> param);

    void updateGroupInfo(Map<String, Object> param);

    void updateIpMac(Map<String, Object> param);

    void updateFlowState(Map<String,Object> param);

    void updateGroupState(Map<String, Object> param);

    void updatePaperState(Map<String, Object> param);

    int updateDataState(Map<String, Object> param);

    void updateExamState(Map<String, Object> args);

    void updateErrorCode(Map<String, Object> param);

    Short getPartnerGroupState(Map<String, Object> param);

    Short getPartnerFlowState(Map<String, Object> param);

    void updateDataPath(Map<String, Object> params);

    Integer getEngineCode(String itemCode);

    void cancelGroup(String recordId);

    void cleanPartner(String recordId);

    List<String> verifyToken(Map<String, Object> param);

    void updateToken(Map<String, Object> param);

    Integer checkHasNewRecord(Map<String, Object> param);
}
