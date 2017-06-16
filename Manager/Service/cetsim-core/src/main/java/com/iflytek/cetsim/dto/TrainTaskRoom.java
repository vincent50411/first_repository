package com.iflytek.cetsim.dto;

/**
 * Created by Administrator on 2017/5/15.
 */
public class TrainTaskRoom
{
    private String socketSessionID;

    private String roomCode;

    private String roomRole;

    //房主代码
    private String candidateACode;

    private String candidateBCode;


    public String getSocketSessionID() {
        return socketSessionID;
    }

    public void setSocketSessionID(String socketSessionID) {
        this.socketSessionID = socketSessionID;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomRole() {
        return roomRole;
    }

    public void setRoomRole(String roomRole) {
        this.roomRole = roomRole;
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
}
