package com.iflytek.cetsim.dto;

import java.util.Date;

/**
 * 通用的考试记录数据对象
 *
 * Created by wbyang3 on 2017/5/9.
 */

public class RecordDTO {

    private String recordId;

    /* 仅在模考模式下有值 */
    private String taskId;

    /* 仅在自由训练和专项训练模式下有值 */
    private String paperCode;

    private String studentNo;

    private String ip;

    private Integer examState;

    private Integer flowState;

    private Integer groupState;

    private Integer paperState;

    private Integer dataState;

    private Integer evalState;

    private Integer errorCode;

    private String examRole;

    private String partnerRecordId;

    private Double score;

    private Date startTime;

    private Date endTime;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
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

    public Integer getGroupState() {
        return groupState;
    }

    public void setGroupState(Integer groupState) {
        this.groupState = groupState;
    }

    public Integer getPaperState() {
        return paperState;
    }

    public void setPaperState(Integer paperState) {
        this.paperState = paperState;
    }

    public Integer getDataState() {
        return dataState;
    }

    public void setDataState(Integer dataState) {
        this.dataState = dataState;
    }

    public Integer getEvalState() {
        return evalState;
    }

    public void setEvalState(Integer evalState) {
        this.evalState = evalState;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getExamRole() {
        return examRole;
    }

    public void setExamRole(String examRole) {
        this.examRole = examRole;
    }

    public String getPartnerRecordId() {
        return partnerRecordId;
    }

    public void setPartnerRecordId(String partnerRecordId) {
        this.partnerRecordId = partnerRecordId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
