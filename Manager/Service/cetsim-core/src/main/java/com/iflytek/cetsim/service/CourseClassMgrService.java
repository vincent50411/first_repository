package com.iflytek.cetsim.service;

import com.iflytek.cetsim.dto.CourseClassDTO;
import com.iflytek.cetsim.model.CourseClass;

import java.util.List;

/**
 * Created by pengwang on 2017/3/15.
 */
public interface CourseClassMgrService {
    /*
    *查询教师创建的班级
     */
    List<CourseClassDTO> listTeacherCourseClass(String account, int pageIndex, int pageSize);

    /*
    *查询教师创建班级的数量
     */
    int countTeacherCourseClass(String account);
    /**
     * 获取学生加入的班级
     * @param account
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<CourseClassDTO> listStudentCourseClass(String account, int pageIndex, int pageSize);

    /**
     * 统计学生加入的班级
     * @param account
     * @return
     */
    int countStudentCourseClass(String account);

    /**
     * 判断CourseClass中code是否重复, 同一个教师下不能重复班级名称
     * @param courseClass
     * @return
     */
    CourseClass judgeCourseClassCode(CourseClass courseClass);

    /**
     * 查找CourseClass中code的最大值
     * @return
     */
    String getMaxClassCode();
}
