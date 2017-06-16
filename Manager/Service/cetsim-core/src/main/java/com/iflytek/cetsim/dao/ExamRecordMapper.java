package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.base.dao.BaseRecordDao;
import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.ExamRecord;
import com.iflytek.cetsim.model.ScoreDetail;
import org.nutz.dao.entity.annotation.Id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@MyBatisRepository
public interface ExamRecordMapper  extends BaseRecordDao {
    int insert(ExamRecord record);

    int insertSelective(ExamRecord record);

    ExamRecord selectByPrimaryKey(String code);

    String getAnswerPath(String examRecordCode);

    String getPartnerAnswerPath(String examRecordCode);

    String getPartnerAccount(String examRecordCode);

    List<StudentTaskDTO_new> selectStudentTaskList(Map<String, Object> paramMap);

    int selectStudentTaskListCount(Map<String, Object> paramMap);

    RankDTO selectRank(HashMap<String,Object> map);

    List<KVStringDTO> queryStudentExamResult(String examRecordCode);

    List<ExamRecordDTO_new> selectAllStudentRecords(Map<String, Object> paramMap);

    int selectAllStudentRecordsCount(Map<String, Object> paramMap);

    List<RankDTO> selectStudentExamTaskRecordRanks(Map<String, Object> paramMap);

    List<ExamRecordDTO_new> selectPagedStudentRecords(Map<String, Object> paramMap);

    List<KVDTO> listExamTaskInfo(Map<String, String> paramMap);

    /**
     * 查询学习引擎返回的结果地址
     * @param paramMap
     * @return
     */
    String queryExamStudyResultByRecordCode(Map<String, Object> paramMap);


    List<EvalRecordResultDTO> listEvalListByRecordCode(Map<String, Object> paramMap);





    /**
     * 此处分割线, 以下是考试机数据层接口
     * */

    @Override
    List<ExamLoginDTO> examineeLogin(Map<String,Object> param);

    @Override
    List<RecordDTO> getCurrentRecord(String recordId);

    @Override
    List<ExamLoginDTO> selectPartner(Map<String, Object> param);

    @Override
    void updateGroupInfo(Map<String,Object> param);

    @Override
    void updateIpMac(Map<String,Object> param);

    @Override
    void updateFlowState(Map<String,Object> param);

    @Override
    void updateGroupState(Map<String, Object> param);

    @Override
    void updatePaperState(Map<String, Object> param);

    @Override
    int updateDataState(Map<String, Object> param);

    @Override
    void updateExamState(Map<String, Object> args);

    @Override
    void updateErrorCode(Map<String, Object> param);

    @Override
    Short getPartnerGroupState(Map<String, Object> param);

    @Override
    Short getPartnerFlowState(Map<String, Object> param);

    @Override
    void updateDataPath(Map<String, Object> params);

    @Override
    Integer getEngineCode(String itemCode);

    @Override
    void cancelGroup(String recordId);

    @Override
    void cleanPartner(String recordId);

    @Override
    List<String> verifyToken(Map<String, Object> param);

    @Override
    void updateToken(Map<String, Object> param);

    @Override
    Integer checkHasNewRecord(Map<String, Object> param);
}