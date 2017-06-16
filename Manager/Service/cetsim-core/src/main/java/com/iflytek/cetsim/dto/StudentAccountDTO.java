package com.iflytek.cetsim.dto;

/**
 * 学生账号DTO
 * Created by Administrator on 2017/5/6.
 */
public class StudentAccountDTO extends BaseAccountDTO {

    //学院
    private String institution;

    //专业
    private String major;

    //年级
    private String grade;

    //学校
    private  String school;

    //自然班级
    private String naturalClass;

    //模拟测试次数
    private Integer examTaskTestCount;

    public Integer getExamTaskTestCount() {
        return examTaskTestCount;
    }

    public void setExamTaskTestCount(Integer examTaskTestCount) {
        this.examTaskTestCount = examTaskTestCount;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getNaturalClass() {
        return naturalClass;
    }

    public void setNaturalClass(String naturalClass) {
        this.naturalClass = naturalClass;
    }
}
