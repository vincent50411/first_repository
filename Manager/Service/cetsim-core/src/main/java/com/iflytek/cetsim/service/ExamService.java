package com.iflytek.cetsim.service;

import com.iflytek.cetsim.common.json.ExamJsonResult;
import com.iflytek.cetsim.dto.ExamLoginDTO;

import java.util.List;

/**
 * 考试机考前接口
 *
 * Created by code2life on 2017/3/10.
 */
public interface ExamService {

    boolean verifyToken(String recordId, String recordType, String token);

    ExamLoginDTO login(String ip, String mac, String recordId, String recordType);

    void updateFlowState(String recordId, String recordType, Integer state);

    void updateGroupState(String recordId, String recordType, Integer state);

    void updatePaperState(String recordId, String recordType, Integer state);

    void updateDataState(String recordId, String recordType, Integer state);

    void updateErrorCode(String recordId, String recordType, Integer errorCode);

    void groupException(String recordId, String recordType, String partnerRecordId);

    void exitGroup(String recordId, String recordType);

    List<ExamLoginDTO> checkGroup(String recordId, String recordType) throws Exception;

    Integer getPartnerGroupState(String recordId, String recordType, String partnerRecordId);

    Integer getPartnerFlowState(String recordId, String recordType, String partnerRecordId);

    byte[] downloadPaper(String recordType, String recordCode);

    void uploadAnswer(String recordId, String recordType, String dataPath);

    boolean evaluateItem(String recordId, String recordType, String datPath, String itemCode, String paperCode);

    void evaluateFail(String recordId, String recordType, Integer engineCode, String itemCode, String paperCode);

    void evaluateFinish(String recordId, String recordType, boolean success);

    boolean evaluateReadChapter(String recordId, String recordType, String baseDir, String wavPath, String paperCode);
}
