package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.common.constants.Constants;

/**
 * Created by pengwang on 2017/3/17.
 */
public class StudentQueryDTO {

    private String name;

    private Integer role;

    private Short status;

    private String grade;

    private String major;

    private String institution;

    private Integer pageIndex = Constants.FIRST_PAGE;

    private Integer pageSize = Constants.PAGE_SIZE;

    //学生ID列表
    private String[] studentIdList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String[] getStudentIdList() {
        return studentIdList;
    }

    public void setStudentIdList(String[] studentIdList) {
        this.studentIdList = studentIdList;
    }
}
