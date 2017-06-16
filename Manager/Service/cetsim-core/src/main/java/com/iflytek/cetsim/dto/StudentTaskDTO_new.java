package com.iflytek.cetsim.dto;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/8.
 */
public class StudentTaskDTO_new
{
    private String studentAccount;

    private String taskClassCode;

    private String taskClassName;

    //发布教师
    private String publisher;

    private String recordCode;

    private String taskCode;

    private String taskName;

    private String planCode;

    private String planName;

    private Date planCreateTime;

    private Date planStartTime;

    private Date planEndTime;

    private String paperName;

    private String paperType;

    private String requirement;

    private Integer examCount;

    private Short examState;

    private double maxScore;
    private double minScore;
    private double averageScore;
    private Integer rank;
    private Integer total;

    //试卷的总状态
    private Short paperState;

    //是否允许发布测试计划，1允许，0 允许
    private Short paperAllowPlanUseage;

    //是否允许自主考试用，1允许，0 允许
    private Short paperAllowFreeUseage;


    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTaskClassCode() {
        return taskClassCode;
    }

    public void setTaskClassCode(String taskClassCode) {
        this.taskClassCode = taskClassCode;
    }

    public String getTaskClassName() {
        return taskClassName;
    }

    public void setTaskClassName(String taskClassName) {
        this.taskClassName = taskClassName;
    }

    public Short getPaperState() {
        return paperState;
    }

    public void setPaperState(Short paperState) {
        this.paperState = paperState;
    }

    public Short getPaperAllowPlanUseage() {
        return paperAllowPlanUseage;
    }

    public void setPaperAllowPlanUseage(Short paperAllowPlanUseage) {
        this.paperAllowPlanUseage = paperAllowPlanUseage;
    }

    public Short getPaperAllowFreeUseage() {
        return paperAllowFreeUseage;
    }

    public void setPaperAllowFreeUseage(Short paperAllowFreeUseage) {
        this.paperAllowFreeUseage = paperAllowFreeUseage;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Date getPlanCreateTime() {
        return planCreateTime;
    }

    public void setPlanCreateTime(Date planCreateTime) {
        this.planCreateTime = planCreateTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public Integer getExamCount() {
        return examCount;
    }

    public void setExamCount(Integer examCount) {
        this.examCount = examCount;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Short getExamState() {
        return examState;
    }

    public void setExamState(Short examState) {
        this.examState = examState;
    }
}
