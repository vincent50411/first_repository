package com.iflytek.cetsim.model;

import java.util.Date;

public class SpecialTrainRecord {
    private String code;

    private Integer id;

    private String studentNo;

    private String paperItemCode;

    private String clientMac;

    private String clientIp;

    private Short examState;

    private Short flowState;

    private Short groupState;

    private Short paperState;

    private Short evalState;

    private Short dataState;

    private String dataPath;

    private Short errorCode;

    private String examRole;

    private String partnerSpecialTrainRecordCode;

    private Float score;

    private String token;

    private Date startTime;

    private Date endTime;

    private String reserved1;

    private String reserved2;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo == null ? null : studentNo.trim();
    }

    public String getPaperItemCode() {
        return paperItemCode;
    }

    public void setPaperItemCode(String paperItemCode) {
        this.paperItemCode = paperItemCode == null ? null : paperItemCode.trim();
    }

    public String getClientMac() {
        return clientMac;
    }

    public void setClientMac(String clientMac) {
        this.clientMac = clientMac == null ? null : clientMac.trim();
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp == null ? null : clientIp.trim();
    }

    public Short getExamState() {
        return examState;
    }

    public void setExamState(Short examState) {
        this.examState = examState;
    }

    public Short getFlowState() {
        return flowState;
    }

    public void setFlowState(Short flowState) {
        this.flowState = flowState;
    }

    public Short getGroupState() {
        return groupState;
    }

    public void setGroupState(Short groupState) {
        this.groupState = groupState;
    }

    public Short getPaperState() {
        return paperState;
    }

    public void setPaperState(Short paperState) {
        this.paperState = paperState;
    }

    public Short getEvalState() {
        return evalState;
    }

    public void setEvalState(Short evalState) {
        this.evalState = evalState;
    }

    public Short getDataState() {
        return dataState;
    }

    public void setDataState(Short dataState) {
        this.dataState = dataState;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath == null ? null : dataPath.trim();
    }

    public Short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Short errorCode) {
        this.errorCode = errorCode;
    }

    public String getExamRole() {
        return examRole;
    }

    public void setExamRole(String examRole) {
        this.examRole = examRole;
    }

    public String getPartnerSpecialTrainRecordCode() {
        return partnerSpecialTrainRecordCode;
    }

    public void setPartnerSpecialTrainRecordCode(String partnerSpecialTrainRecordCode) {
        this.partnerSpecialTrainRecordCode = partnerSpecialTrainRecordCode == null ? null : partnerSpecialTrainRecordCode.trim();
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2 == null ? null : reserved2.trim();
    }
}