package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.DateUtils;
import com.iflytek.cetsim.dao.TrainRecordMapper;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.PaperBuffer;
import com.iflytek.cetsim.service.TrainTaskService;
import org.apache.commons.collections.FastArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/11.
 */
@Service
@Transactional
public class TrainTaskServiceImpl implements TrainTaskService
{
    @Autowired
    private TrainRecordMapper trainRecordMapper;

    /**
     *  查询允许自主考试用的试卷列表
     * @param paperQueryDTO
     * @return
     */
    public List<PaperInfoDTO> queryTrainTaskPaperList(Account userInfo, PaperQueryDTO paperQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("limit", paperQueryDTO.getPageSize());
        paramMap.put("offset", (paperQueryDTO.getPageIndex() - 1) * paperQueryDTO.getPageSize());

        if(userInfo != null)
        {
            paramMap.put("student_account", userInfo.getAccount());
        }

        if (paperQueryDTO.getPaperTypeCode() != null) {
            paramMap.put("paper_type_code", paperQueryDTO.getPaperTypeCode().toUpperCase());
        }

        if(paperQueryDTO.getUseState() != null && paperQueryDTO.getUseState() == 0)
        {
            //未练习
            paramMap.put("noneUsed", paperQueryDTO.getUseState());
        }
        else if(paperQueryDTO.getUseState() != null && paperQueryDTO.getUseState() == 1)
        {
            //已练习
            paramMap.put("used", paperQueryDTO.getUseState());
        }

        List<PaperInfoDTO>  paperInfoDTOs = trainRecordMapper.queryTrainTaskPaperList(paramMap);

        return paperInfoDTOs;
    }

    /**
     * 查询自主考试考试记录
     * @param trainTaskRecordQueryDTO
     * @return
     */
    public List<TrainTaskRecordInfoDTO> queryTrainTaskRecordList(TrainTaskRecordQueryDTO trainTaskRecordQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("limit", trainTaskRecordQueryDTO.getPageSize());
        paramMap.put("offset", (trainTaskRecordQueryDTO.getPageIndex() - 1) * trainTaskRecordQueryDTO.getPageSize());

        paramMap.put("student_account", trainTaskRecordQueryDTO.getStudentAccount());

        if(trainTaskRecordQueryDTO.getPaperTypeCode() != null)
        {
            //试题类型代码
            paramMap.put("paper_type_code", trainTaskRecordQueryDTO.getPaperTypeCode());
        }

        if(trainTaskRecordQueryDTO.getPaperName() != null)
        {
            //试卷名称
            paramMap.put("paper_name", "%" + trainTaskRecordQueryDTO.getPaperName() + "%");
        }

        //增加排序规则
        if(trainTaskRecordQueryDTO.getOrderColumnName() != null && trainTaskRecordQueryDTO.getOrderCode() != null)
        {
            paramMap.put(trainTaskRecordQueryDTO.getOrderColumnName().toUpperCase() + "_" + trainTaskRecordQueryDTO.getOrderCode().toUpperCase(), "true");
        }

        //总的统计记录，每一条统计记录还包含详细的记录明细
        List<TrainTaskRecordInfoDTO> trainTaskRecordInfoDTOs = trainRecordMapper.queryTrainTaskRecordList(paramMap);

        Map<String, Object> paramInfoMap = new HashMap<String, Object>();
        paramInfoMap.put("student_account", trainTaskRecordQueryDTO.getStudentAccount());

        //所有的明细，按照试卷代码汇总到统计记录中
        List<TrainTaskRecordInfoDTO> trainTaskRecordInfoList = trainRecordMapper.queryTrainTaskRecordInfoListByAccount(paramInfoMap);

        if(trainTaskRecordInfoDTOs != null)
        {
            for(TrainTaskRecordInfoDTO  trainTaskRecordInfoDTO:trainTaskRecordInfoDTOs)
            {
                //试卷代码
                String paperCode = trainTaskRecordInfoDTO.getPaperCode();

                if(paperCode != null && trainTaskRecordInfoList != null)
                {
                    List<Object> infoRecordList = new ArrayList();

                    for(TrainTaskRecordInfoDTO recordInfo:trainTaskRecordInfoList)
                    {
                        if(paperCode.equals(recordInfo.getPaperCode()))
                        {
                            //根据试卷代码，从学生所有训练记录中汇总明细记录
                            Map<String, Object> infoRecordMap = new HashMap<String, Object>();

                            infoRecordMap.put("studentAccount", trainTaskRecordQueryDTO.getStudentAccount());
                            infoRecordMap.put("startTime", recordInfo.getStartTime());
                            infoRecordMap.put("score", recordInfo.getScore());
                            infoRecordMap.put("recordCode", recordInfo.getRecordCode());

                            infoRecordList.add(infoRecordMap);
                        }
                    }

                    trainTaskRecordInfoDTO.setRecordInfoList(infoRecordList);
                }
            }
        }

        return trainTaskRecordInfoDTOs;
    }


    /**
     * 学生成绩轨迹
     * @param trainTaskRecordQueryDTO
     * @return
     */
    public JsonResult queryTrainTaskScoreTrack(TrainTaskRecordQueryDTO trainTaskRecordQueryDTO)
    {
        JsonResult result = new JsonResult();

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("student_no", trainTaskRecordQueryDTO.getStudentAccount());

        if(trainTaskRecordQueryDTO.getPaperTypeCode() != null)
        {
            //试题类型代码
            paramMap.put("paper_type_code", trainTaskRecordQueryDTO.getPaperTypeCode());
        }

        if(trainTaskRecordQueryDTO.getBeginTime() != null && trainTaskRecordQueryDTO.getEndTime() != null)
        {
            paramMap.put("begin_time", DateUtils.parseDate(trainTaskRecordQueryDTO.getBeginTime()));
            paramMap.put("end_time", DateUtils.parseDate(trainTaskRecordQueryDTO.getEndTime()));
        }

        List<TrainTaskRecordInfoDTO> trainTaskRecordInfoDTOs = trainRecordMapper.queryTrainTaskScoreTrackByAccount(paramMap);

        //处理前端界面图表展示需要的数据结构
        if(trainTaskRecordInfoDTOs != null)
        {
            Map<String, Object> dataMap = new HashMap();

            List<Map> pointList = new ArrayList<>();

            String recordCode = null;

            for(TrainTaskRecordInfoDTO trainTaskRecordInfoDTO : trainTaskRecordInfoDTOs)
            {
                //点对象
                Map<String, Object> pointMap = new HashMap();

                recordCode = trainTaskRecordInfoDTO.getRecordCode();

                Double scoreValue = trainTaskRecordInfoDTO.getScore();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String examStartTime = sdf.format(trainTaskRecordInfoDTO.getStartTime());

                //成绩列表
                pointMap.put("score", scoreValue);

                //时间列表
                pointMap.put("time", examStartTime);

                //试卷名称
                pointMap.put("paperName", trainTaskRecordInfoDTO.getPaperName());

                //记录代码
                pointMap.put("recordCode", trainTaskRecordInfoDTO.getRecordCode());

                pointList.add(pointMap);
            }

            dataMap.put("studentAccount", trainTaskRecordQueryDTO.getStudentAccount());

            //所有成绩的点列表
            dataMap.put("pointList", pointList);

            result.setSucceed(dataMap);
        }
        else
        {
            result.setFailMsg("没有查询到成绩记录");
        }

        return result;
    }


    /**
     * 查询自主考试成绩报告信息
     * @param trainRecordCode 考试记录代码
     * @return
     */
    public List<KVStringDTO> listTrainTaskExamResult(String trainRecordCode)
    {
        return trainRecordMapper.queryStudentTrainTaskExamResult(trainRecordCode);
    }

    /**
     * 自主考试答题地址
     * @param examRecordCode
     * @return
     */
    public String getTrainTaskAnswerPath(String examRecordCode) {
        return trainRecordMapper.getTrainTaskAnswerPath(examRecordCode);
    }

    /**
     * 队友自主考试答题地址
     * @param examRecordCode
     * @return
     */
    public String getPartnerTrainTaskAnswerPath(String examRecordCode)
    {
        return trainRecordMapper.getPartnerTrainTaskAnswerPath(examRecordCode);
    }
    /**
     * 根据自己的考试记录找到队友的账号
     * @param examRecordCode
     * @return
     */
    public String getPartnerAccountByTrainCode(String examRecordCode)
    {
        return trainRecordMapper.getPartnerAccountByTrainCode(examRecordCode);
    }

    public PaperBuffer getTrainPaperBuffData(String examRecordCode)
    {
        return trainRecordMapper.getTrainPaperBuffDataByAccount(examRecordCode);
    }

    public String getTrainPaperCodeByRecordCode(String examRecordCode)
    {
        return trainRecordMapper.getTrainPaperCodeByRecordCode(examRecordCode);
    }


}
