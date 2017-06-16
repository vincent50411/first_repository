package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.StudentDTO;
import com.iflytek.cetsim.model.ExamTask;
import com.iflytek.cetsim.model.ExamTaskExample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface ExamTaskMapper {
    int countByExample(ExamTaskExample example);

    int deleteByPrimaryKey(String code);

    int insert(ExamTask record);

    int insertSelective(ExamTask record);

    List<ExamTask> selectByExample(ExamTaskExample example);

    ExamTask selectByPrimaryKey(String code);

    int updateByExampleSelective(@Param("record") ExamTask record, @Param("example") ExamTaskExample example);

    int updateByExample(@Param("record") ExamTask record, @Param("example") ExamTaskExample example);

    int updateByPrimaryKeySelective(ExamTask record);

    int updateByPrimaryKey(ExamTask record);

    PaperInfoDTO queryPaperInfoByTaskCode(Map<String, Object> paramMap);

    /**
     * 教师查看测试计划详情，导出学生考试的所有记录(报告所有的测试任务)
     * @param paramMap
     * @return
     */
    List<StudentDTO> teacherQueryTaskStudentList(Map<String, Object> paramMap);

    /**
     * 教师根据测试计划导出学生统计记录，报告考试次数（一个学生参加一个测试计划下的多个测试任务）、最高成绩、平均成绩
     * @param paramMap
     * @return
     */
    List<StudentDTO> teacherExportTaskStudentList(Map<String, Object> paramMap);


    int countScoreDistribution(HashMap<String,Object> map);

    int deleteByPlanCode(String code);

    int countTaskStudent(Map paramMap);
}