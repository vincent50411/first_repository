package com.iflytek.cetsim.dto;

/**
 * Created by Administrator on 2017/5/8.
 */
public class StudentClassDTO {
    private String studentAccount;

    private String className;

    private String classCode;

    private String teacherCode;

    private String teacherName;

    private String teacherTelephone;

    private String teacherEmail;

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherTelephone() {
        return teacherTelephone;
    }

    public void setTeacherTelephone(String teacherTelephone) {
        this.teacherTelephone = teacherTelephone;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }
}
