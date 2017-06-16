package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.base.service.BaseService;
import com.iflytek.cetsim.dao.ExamRecordMapper;
import com.iflytek.cetsim.dao.PaperBufferMapper;
import com.iflytek.cetsim.dto.EvalRecordResultDTO;
import com.iflytek.cetsim.dto.ExamRecordDTO_new;
import com.iflytek.cetsim.dto.RankDTO;
import com.iflytek.cetsim.dto.StudentTaskDTO_new;
import com.iflytek.cetsim.model.*;
import com.iflytek.cetsim.service.StudentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StudentTaskServiceImpl extends BaseService implements StudentTaskService
{
    @Autowired
    private PaperBufferMapper paperBufferMapper = null;

    @Autowired
    private ExamRecordMapper examRecordMapper = null;

    public PaperBuffer getPaperBuffData(String examRecordCode) {
        return paperBufferMapper.getPaperBuffData(examRecordCode);
    }

    public String getPaperCodeByRecordCode(String examRecordCode)
    {
        return paperBufferMapper.getPaperCodeByRecordCode(examRecordCode);
    }

    public String getAnswerPath(String examRecordCode) {
        return examRecordMapper.getAnswerPath(examRecordCode);
    }

    public String getPartnerAnswerPath(String examRecordCode)
    {
        return examRecordMapper.getPartnerAnswerPath(examRecordCode);
    }

    public String getPartnerAccount(String examRecordCode)
    {
        return examRecordMapper.getPartnerAccount(examRecordCode);
    }

    /**
     * 统计学生任务总数
     * @param studentAccount
     * @return
     */
    public int countStudentTask(String studentAccount, int status)
    {

        return 0;
    }

    /**
     * 获取学生的任务
     * @param studentAccount
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<StudentTaskDTO_new> listStudentTasks(String studentAccount, int status, int pageIndex, int pageSize, String orderColumnName, String orderCode)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("studentAccount", studentAccount);

        Date serverDate = new Date();
        Map<String, Object> dateParam = new HashMap<String, Object>();
        dateParam.put("currentTime", serverDate);

        //根据输入的状态查询
        if(status == 0)
        {
            //进行中
            paramMap.put("examing", dateParam);
        }
        else if(status == 2)
        {
            //未开始
            paramMap.put("unStart", dateParam);
        }
        else if(status == 1)
        {
            //已完成
            paramMap.put("examed", dateParam);
        }
        else if(status == -1)
        {
            //已过期
            paramMap.put("examOver", dateParam);
        }

        paramMap.put("limit", pageSize);
        paramMap.put("offset", (pageIndex - 1) * pageSize);

        //增加排序规则
        if(orderColumnName != null && orderCode != null)
        {
            paramMap.put(orderColumnName.toUpperCase() + "_" + orderCode.toUpperCase(), "true");
        }
        else
        {
            paramMap.put("order_condition_default", "true");
        }

        List<StudentTaskDTO_new> studentTaskDTO_newsList = examRecordMapper.selectStudentTaskList(paramMap);

        for(StudentTaskDTO_new studentTaskDTO_new:studentTaskDTO_newsList)
        {
            HashMap<String,Object> map = new HashMap<>();
            map.put("task_code", studentTaskDTO_new.getTaskCode());
            map.put("record_code", studentTaskDTO_new.getRecordCode());

            //排名是按照任务的总人数（包含没有参加考试的）
            RankDTO rank = examRecordMapper.selectRank(map);

            if(rank != null)
            {
                studentTaskDTO_new.setRank(rank.getRank());
                studentTaskDTO_new.setTotal(rank.getTotal());
            }
        }

        return studentTaskDTO_newsList;
    }

    /**
     * 统计总数
     * @param studentAccount
     * @param status
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public int StudentTasksListCount(String studentAccount, int status, int pageIndex, int pageSize)
    {
        Map<String, Object> sqlParamMap = new HashMap<String, Object>();

        sqlParamMap.put("studentAccount", studentAccount);

        Date serverDate = new Date();
        Map<String, Object> dateParam = new HashMap<String, Object>();
        dateParam.put("currentTime", serverDate);

        //根据输入的状态查询
        if(status == 0)
        {
            //进行中
            sqlParamMap.put("examing", dateParam);
        }
        else if(status == 1)
        {
            //已完成
            sqlParamMap.put("examed", dateParam);
        }
        else if(status == -1)
        {
            //已过期
            sqlParamMap.put("examOver", dateParam);
        }

        return  examRecordMapper.selectStudentTaskListCount(sqlParamMap);
    }

    /**
     * 一条测试任务下的排名列表
     * @param examTaskCode
     * @return
     */
    public List<RankDTO> listRank(String examTaskCode)
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("task_code", examTaskCode);
        return examRecordMapper.selectStudentExamTaskRecordRanks(map);
    }

    /**
     * 学生成绩分布
     * @param examTaskCode
     * @param studentAccount
     * @return
     */
    public List<ExamRecordDTO_new> listStudentRecords(String examTaskCode, String studentAccount)
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("exam_task_code", examTaskCode);
        map.put("student_account", studentAccount);
        return examRecordMapper.selectPagedStudentRecords(map);
    }

    public List<EvalRecordResultDTO> listEvalListByRecordCode(String recordCode)
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("recordCode", recordCode);

        return examRecordMapper.listEvalListByRecordCode(map);

    }


}
