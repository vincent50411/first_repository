package com.iflytek.cetsim.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */
public class TrainTaskRecordInfoDTO
{
    private String recordCode;

    private String studentAccount;

    private String paperTypeCode;

    private String paperCode;

    private String paperName;

    private Integer examCount;

    private Double score;

    private Double avgScore;

    private Double maxScore;

    private Double minScore;

    private Date startTime;

    private Date endTime;

    //详细记录
    private List<Object> recordInfoList;

    public List<Object> getRecordInfoList() {
        return recordInfoList;
    }

    public void setRecordInfoList(List<Object> recordInfoList) {
        this.recordInfoList = recordInfoList;
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

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Integer getExamCount() {
        return examCount;
    }

    public void setExamCount(Integer examCount) {
        this.examCount = examCount;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
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
}
