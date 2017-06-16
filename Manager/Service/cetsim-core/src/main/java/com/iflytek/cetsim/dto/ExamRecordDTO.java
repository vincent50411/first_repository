package com.iflytek.cetsim.dto;

import java.util.Date;

public class ExamRecordDTO {
    private Long id;

    private Long studentTaskId;

    private Integer partnerId;

    private String clientMac;

    private String clientIp;

    private Integer examState;

    private Integer flowState;

    private Integer evalState;

    private String dataPath;

    private Double totalScore;

    private Date startTime;

    private Date endTime;

    private String paperName;

    private String level;

    private String rank;

    private String name;

    private String account;



    private Long partnerRecord;

    private String examRole;

    private String token;

    private Integer enable;

    private Integer examPaper;

    private Integer examTask;

    private Integer examPlan;

    //试卷类型(0:CET4,1:CET6)
    private Integer type;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentTaskId() {
        return studentTaskId;
    }

    public void setStudentTaskId(Long studentTaskId) {
        this.studentTaskId = studentTaskId;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public String getClientMac() {
        return clientMac;
    }

    public void setClientMac(String clientMac) {
        this.clientMac = clientMac;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getExamState() {
        return examState;
    }

    public void setExamState(Integer examState) {
        this.examState = examState;
    }

    public Integer getFlowState() {
        return flowState;
    }

    public void setFlowState(Integer flowState) {
        this.flowState = flowState;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getEvalState() {
        return evalState;
    }

    public void setEvalState(Integer evalState) {
        this.evalState = evalState;
    }


    public Long getPartnerRecord() {
        return partnerRecord;
    }

    public void setPartnerRecord(Long partnerRecord) {
        this.partnerRecord = partnerRecord;
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

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getExamPaper() {
        return examPaper;
    }

    public void setExamPaper(Integer examPaper) {
        this.examPaper = examPaper;
    }

    public Integer getExamTask() {
        return examTask;
    }

    public void setExamTask(Integer examTask) {
        this.examTask = examTask;
    }

    public Integer getExamPlan() {
        return examPlan;
    }

    public void setExamPlan(Integer examPlan) {
        this.examPlan = examPlan;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
