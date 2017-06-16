package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.CourseClassDTO;
import com.iflytek.cetsim.model.CourseClass;
import com.iflytek.cetsim.model.CourseClassExample;
import java.util.List;

@MyBatisRepository
public interface CourseClassMapper {
    int countByExample(CourseClassExample example);

    int deleteByPrimaryKey(String code);

    int insert(CourseClass record);

    int insertSelective(CourseClass record);

    List<CourseClass> selectByExample(CourseClassExample example);

    CourseClass selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(CourseClass record);

    int updateByPrimaryKey(CourseClass record);

    List<CourseClassDTO> selectDetailByExample(CourseClassExample example);

    CourseClass getMaxIdCourseClass();

    /**
     * 获取某个老师的包含学生的班级列表
     * @param account
     * @return
     */
    List<CourseClass> selectClassExistStudentsOfTeacher(String account);
}