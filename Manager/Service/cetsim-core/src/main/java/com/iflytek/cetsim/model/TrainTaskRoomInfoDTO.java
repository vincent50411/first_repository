package com.iflytek.cetsim.model;


/**
 * Created by Administrator on 2017/5/12.
 */
public class TrainTaskRoomInfoDTO
{
    //房间状态，0：未分组，1已分组
    private Short roomState;

    //房间代码
    private String roomCode;

    //试卷名称
    private String paperName;

    //试卷类型
    private String paperTypeCode;

    //角色A账号
    private String candidateACode;

    private String candidateAName;

    private String candidateAPhoto;

    //角色B账号
    private String candidateBCode;

    private String candidateBName;

    private String candidateBPhoto;

    //在线状态
    private Integer onlineState;

    public Short getRoomState() {
        return roomState;
    }

    public void setRoomState(Short roomState) {
        this.roomState = roomState;
    }

    public String getCandidateAPhoto() {
        return candidateAPhoto;
    }

    public void setCandidateAPhoto(String candidateAPhoto) {
        this.candidateAPhoto = candidateAPhoto;
    }

    public String getCandidateBPhoto() {
        return candidateBPhoto;
    }

    public void setCandidateBPhoto(String candidateBPhoto) {
        this.candidateBPhoto = candidateBPhoto;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode;
    }

    public String getCandidateACode() {
        return candidateACode;
    }

    public void setCandidateACode(String candidateACode) {
        this.candidateACode = candidateACode;
    }

    public String getCandidateAName() {
        return candidateAName;
    }

    public void setCandidateAName(String candidateAName) {
        this.candidateAName = candidateAName;
    }

    public String getCandidateBCode() {
        return candidateBCode;
    }

    public void setCandidateBCode(String candidateBCode) {
        this.candidateBCode = candidateBCode;
    }

    public String getCandidateBName() {
        return candidateBName;
    }

    public void setCandidateBName(String candidateBName) {
        this.candidateBName = candidateBName;
    }

    public Integer getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(Integer onlineState) {
        this.onlineState = onlineState;
    }
}
