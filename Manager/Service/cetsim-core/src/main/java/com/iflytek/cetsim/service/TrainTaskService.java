package com.iflytek.cetsim.service;

import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.PaperBuffer;

import java.util.List;

/**
 * 自主考试服务接口
 * Created by Administrator on 2017/5/11.
 */
public interface TrainTaskService
{

    /**
     *  查询允许自主考试用的试卷列表
     * @param paperQueryDTO
     * @return
     */
    List<PaperInfoDTO> queryTrainTaskPaperList(Account userInfo, PaperQueryDTO paperQueryDTO);


    List<TrainTaskRecordInfoDTO> queryTrainTaskRecordList(TrainTaskRecordQueryDTO trainTaskRecordQueryDTO);

    /**
     * 学生成绩轨迹
     * @param trainTaskRecordQueryDTO
     * @return
     */
    JsonResult queryTrainTaskScoreTrack(TrainTaskRecordQueryDTO trainTaskRecordQueryDTO);

    List<KVStringDTO> listTrainTaskExamResult(String trainRecordCode);

    String getTrainTaskAnswerPath(String examRecordCode);

    String getPartnerTrainTaskAnswerPath(String examRecordCode);

    /**
     * 根据自己的考试记录找到队友的账号
     * @param examRecordCode
     * @return
     */
    String getPartnerAccountByTrainCode(String examRecordCode);


    String getTrainPaperCodeByRecordCode(String examRecordCode);

    PaperBuffer getTrainPaperBuffData(String examRecordCode);

}
