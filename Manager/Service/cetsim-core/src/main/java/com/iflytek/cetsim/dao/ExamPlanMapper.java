package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.TeacherTaskDTO;
import com.iflytek.cetsim.model.CourseClass;
import com.iflytek.cetsim.model.ExamPlan;
import com.iflytek.cetsim.model.ExamPlanExample;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface ExamPlanMapper {
    int countByExample(ExamPlanExample example);

    int insert(ExamPlan record);

    int insertSelective(ExamPlan record);

    List<ExamPlan> selectByExample(ExamPlanExample example);

    ExamPlan selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(ExamPlan record);

    int updateByPrimaryKey(ExamPlan record);

    int countDetailByExample(Map paramMap);

    List<TeacherTaskDTO> queryPlanDetail(Map paramMap);

    List<CourseClass> getPlanClassList(Map<String, Object> map);

    int deleteByPlanCode(String plan_code);
}