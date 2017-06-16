package com.iflytek.cetsim.service;

import com.iflytek.cetsim.dto.EvalRecordResultDTO;
import com.iflytek.cetsim.dto.ExamRecordDTO_new;
import com.iflytek.cetsim.dto.RankDTO;
import com.iflytek.cetsim.dto.StudentTaskDTO_new;
import com.iflytek.cetsim.model.PaperBuffer;

import java.util.List;

public interface StudentTaskService {

    /**
     * 获取任务试卷路径
     * @param examRecordCode 模拟考试记录代码
     * @return
     */
    PaperBuffer getPaperBuffData(String examRecordCode);

    String getPaperCodeByRecordCode(String examRecordCode);

    /**
     * 获取答案包路径
     * @param examRecordCode 模拟考试记录代码
     * @return
     */
    String getAnswerPath(String examRecordCode);

    String getPartnerAnswerPath(String examRecordCode);

    /**
     * 获取队友账号
     * @param examRecordCode
     * @return
     */
    String getPartnerAccount(String examRecordCode);

    int StudentTasksListCount(String studentAccount, int status, int pageIndex, int pageSize);

    /**
     * 获取学生的任务
     * @param studentAccount
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<StudentTaskDTO_new> listStudentTasks(String studentAccount, int status, int pageIndex, int pageSize, String orderColumnName, String orderCode);

    /**
     * 统计学生任务总数
     * @param studentAccount
     * @return
     */
    int countStudentTask(String studentAccount, int status);

    /**
     * 一条测试任务下的排名列表
     * @param examTaskCode
     * @return
     */
    List<RankDTO> listRank(String examTaskCode);

    /**
     * 学生成绩分布
     * @param examTaskCode
     * @param studentAccount
     * @return
     */
    List<ExamRecordDTO_new> listStudentRecords(String examTaskCode, String studentAccount);


    List<EvalRecordResultDTO> listEvalListByRecordCode(String recordCode);


}
