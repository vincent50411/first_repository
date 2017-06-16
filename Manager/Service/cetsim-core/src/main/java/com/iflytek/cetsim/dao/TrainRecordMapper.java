package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.base.dao.BaseRecordDao;
import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.PaperBuffer;
import com.iflytek.cetsim.model.TrainRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface TrainRecordMapper extends BaseRecordDao {
    int insert(TrainRecord record);

    int insertSelective(TrainRecord record);

    TrainRecord selectByPrimaryKey(String code);

    List<PaperInfoDTO> queryTrainTaskPaperList(Map<String, Object> param);

    int updatePartnerTrainRecordCodeByPrimaryKey(Map<String, Object> param);

    List<TrainTaskRecordInfoDTO> queryTrainTaskRecordList(Map<String, Object> param);

    /**
     * 查记录明细
     * @param param
     * @return
     */
    List<TrainTaskRecordInfoDTO> queryTrainTaskRecordInfoListByAccount(Map<String, Object> param);

    /**
     * 学生成绩轨迹
     * @param param
     * @return
     */
    List<TrainTaskRecordInfoDTO> queryTrainTaskScoreTrackByAccount(Map<String, Object> param);

    List<KVStringDTO> queryStudentTrainTaskExamResult(String examRecordCode);

    /**
     * 获取自己的答题路径
     * @param examRecordCode
     * @return
     */
    String getTrainTaskAnswerPath(String examRecordCode);

    /**
     * 根据自己的考试记录找到队友的答题路径
     * @param examRecordCode
     * @return
     */
    String getPartnerTrainTaskAnswerPath(String examRecordCode);

    /**
     * 根据自己的考试记录找到队友的账号
     * @param examRecordCode
     * @return
     */
    String getPartnerAccountByTrainCode(String examRecordCode);


    PaperBuffer getTrainPaperBuffDataByAccount(String trainRecordCode);

    String getTrainPaperCodeByRecordCode(String trainRecordCode);

    /**
     * 查询学习引擎返回的结果地址
     * @param param
     * @return
     */
    String queryTrainExamStudyResultByRecordCode(Map<String, Object> param);




    /**
     * 此处分割线, 以下是考试机数据层接口
     * */

    @Override
    List<ExamLoginDTO> examineeLogin(Map<String, Object> param);

    @Override
    List<RecordDTO> getCurrentRecord(String recordId);

    @Override
    List<ExamLoginDTO> selectPartner(Map<String, Object> param);

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
    void cancelGroup(String recordId);

    @Override
    void cleanPartner(String recordId);

    @Override
    void updateGroupInfo(Map<String,Object> param);

    @Override
    List<String> verifyToken(Map<String, Object> param);

    @Override
    void updateToken(Map<String, Object> param);

    @Override
    Integer checkHasNewRecord(Map<String, Object> param);
}