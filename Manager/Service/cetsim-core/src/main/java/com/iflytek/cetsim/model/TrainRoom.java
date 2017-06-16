package com.iflytek.cetsim.model;

public class TrainRoom {
    private String code;

    private Integer id;

    private String paperCode;

    private String candidateACode;
    private String candidateAPhoto;

    private String candidateBPhoto;

    private String candidateBCode;

    private Short state;

    private String roomPassword;


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

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode == null ? null : paperCode.trim();
    }

    public String getCandidateACode() {
        return candidateACode;
    }

    public void setCandidateACode(String candidateACode) {
        this.candidateACode = candidateACode == null ? null : candidateACode.trim();
    }

    public String getCandidateBCode() {
        return candidateBCode;
    }

    public void setCandidateBCode(String candidateBCode) {
        this.candidateBCode = candidateBCode == null ? null : candidateBCode.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword == null ? null : roomPassword.trim();
    }
}