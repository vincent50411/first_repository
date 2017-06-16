package com.iflytek.cetsim.dto;

/**
 * 自主考试组建房间消息
 */
public class TrainTaskRoomMessage
{
    //房间代码
    private String roomCode;

    private String candidateAName;

    //房主代码
    private String candidateACode;

    private String candidateAPhoto;

    private String paperCode;

    private String paperName;

    private String paperTypeCode;

    private String candidateBName;

    //队友账号
    private String candidateBCode;

    private String candidateBPhoto;

    //A的考试记录代码
    private String candidateARecord;

    //B的考试记录代码
    private String candidateBRecord;

    //传递的消息
    private String message;

    private String messageType;

    public TrainTaskRoomMessage() {
    }

    public TrainTaskRoomMessage(String candidateACode, String message) {
        super();
        this.candidateACode = candidateACode;
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCandidateAName() {
        return candidateAName;
    }

    public void setCandidateAName(String candidateAName) {
        this.candidateAName = candidateAName;
    }

    public String getCandidateBName() {
        return candidateBName;
    }

    public void setCandidateBName(String candidateBName) {
        this.candidateBName = candidateBName;
    }

    public String getCandidateARecord() {
        return candidateARecord;
    }

    public void setCandidateARecord(String candidateARecord) {
        this.candidateARecord = candidateARecord;
    }

    public String getCandidateBRecord() {
        return candidateBRecord;
    }

    public void setCandidateBRecord(String candidateBRecord) {
        this.candidateBRecord = candidateBRecord;
    }

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode;
    }

    public String getCandidateAPhoto() {
        return candidateAPhoto;
    }

    public void setCandidateAPhoto(String candidateAPhoto) {
        this.candidateAPhoto = candidateAPhoto;
    }

    public String getCandidateACode() {
        return candidateACode;
    }

    public void setCandidateACode(String candidateACode) {
        this.candidateACode = candidateACode;
    }

    public String getCandidateBCode() {
        return candidateBCode;
    }

    public void setCandidateBCode(String candidateBCode) {
        this.candidateBCode = candidateBCode;
    }

    public String getCandidateBPhoto() {
        return candidateBPhoto;
    }

    public void setCandidateBPhoto(String candidateBPhoto) {
        this.candidateBPhoto = candidateBPhoto;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }



    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
