package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.base.dao.BaseRecordDao;
import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.ExamLoginDTO;
import com.iflytek.cetsim.dto.RecordDTO;
import com.iflytek.cetsim.dto.SpecialTaskRecordDTO;
import com.iflytek.cetsim.model.SpecialTrainRecord;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface SpecialTrainRecordMapper extends BaseRecordDao {
    int insert(SpecialTrainRecord record);

    int insertSelective(SpecialTrainRecord record);

    SpecialTrainRecord selectByPrimaryKey(String code);

    /**
     * 汇总统计学生的专项训练考试记录
     * @param param
     * @return
     */
    List<SpecialTaskRecordDTO> querySpecialRecordListByAccount(Map<String, Object> param);


    /**
     * 根据专项训练的代码和账号查询记录详情
     * @param param
     * @return
     */
    List<SpecialTaskRecordDTO> querySpecialRecordInfoListByAccount(Map<String, Object> param);

    /**
     * 查看学生的专项训练成绩报告
     * @param param
     * @return
     */
    SpecialTaskRecordDTO querySpecialRecordReportByAccount(Map<String, Object> param);

    short querySpecialRecordStateByAccount(Map<String, Object> param);


    /**
     * 查询学习引擎返回的结果地址
     * @param param
     * @return
     */
    String querySpecialStudyResultByRecordCode(Map<String, Object> param);



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
}