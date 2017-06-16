package com.iflytek.cetsim.dto;

public class ExamLoginDTO {

    private String accountId;

    private String examineeId;

    private String studentName;

    private String photo;

    private String gender;

    private String stuTaskId;

    private String recordId;

    private String ip;

    private String mac;

    private Integer master;

    private String examRole;

    private String token;

    private String paperCode;

    public String getExamineeId() {
        return examineeId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStuTaskId() {
        return stuTaskId;
    }

    public void setStuTaskId(String stuTaskId) {
        this.stuTaskId = stuTaskId;
    }

    public void setExamineeId(String examineeId) {
        this.examineeId = examineeId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getMaster() {
        return master;
    }

    public void setMaster(Integer master) {
        this.master = master;
    }

    public String getExamRole() {
        return examRole;
    }

    public void setExamRole(String examRole) {
        this.examRole = examRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }
}
