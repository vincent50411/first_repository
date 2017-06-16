package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.dao.ClassStudentMapper;
import com.iflytek.cetsim.dao.CourseClassMapper;
import com.iflytek.cetsim.dto.CourseClassDTO;
import com.iflytek.cetsim.model.ClassStudentExample;
import com.iflytek.cetsim.model.CourseClass;
import com.iflytek.cetsim.model.CourseClassExample;
import com.iflytek.cetsim.service.CourseClassMgrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengwang on 2017/3/15.
 */
@Service
@Transactional
public class CourseClassMgrServiceImpl implements CourseClassMgrService {
    @Autowired
    private CourseClassMapper courseClassDao;

    @Autowired
    private ClassStudentMapper clsStudentDao;

    /*
  *查询教师创建的班级
   */
    @Override
    public List<CourseClassDTO> listTeacherCourseClass(String account, int pageIndex, int pageSize){
        List<CourseClassDTO> courseClassDTOs = new ArrayList<CourseClassDTO>();
        CourseClassExample example = new CourseClassExample();
        example.setLimit(pageSize);
        example.setOffset((pageIndex-1)*pageSize);
        CourseClassExample.Criteria criteria = example.createCriteria();
        criteria.andTeacherAccountEqualTo(account);
        return courseClassDao.selectDetailByExample(example);
    }

    /*
    *查询教师创建班级的数量
     */
    @Override
    public int countTeacherCourseClass(String account){
        CourseClassExample example = new CourseClassExample();
        CourseClassExample.Criteria criteria = example.createCriteria();
        criteria.andTeacherIdEqualTo(account);
        return courseClassDao.countByExample(example);
    }

    @Override
    public List<CourseClassDTO> listStudentCourseClass(String account, int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public int countStudentCourseClass(String account) {
        return 0;
    }

    @Override
    public CourseClass judgeCourseClassCode(CourseClass courseClass) {
        return null;
    }

    @Override
    public String getMaxClassCode() {
        CourseClass courseClass = courseClassDao.getMaxIdCourseClass();

        return courseClass == null ? null : courseClass.getCode();
    }

}
