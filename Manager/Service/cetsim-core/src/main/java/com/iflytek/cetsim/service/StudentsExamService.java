package com.iflytek.cetsim.service;

import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.dto.ExamRecordDTO_new;
import com.iflytek.cetsim.dto.KVStringDTO;
import com.iflytek.cetsim.model.ExamRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/5/8.
 */
public interface StudentsExamService
{
    /**
     * 开始考试，往exam_record表中加入一条初始考试记录
     * @param examTaskCode  模拟考试任务代码
     * @return
     */
    ExamRecord startEcp(String studentAccount, String examTaskCode);


    /**
     * 查询成绩报告信息
     * @param examRecordCode 考试记录代码
     * @return
     */
    List<KVStringDTO> listExamResult(String examRecordCode);

    /**
     * 学生全部考试记录，有效的
     * @param studentAccount
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageModel<ExamRecordDTO_new> listStudentAllRecords(String studentAccount, int pageIndex, int pageSize);


}
