package com.iflytek.cetsim.dto;

/**
 * 单词
 * Created by Administrator on 2017/5/18.
 */
public class StudyReportWordInfoDTO
{
    //单词内容
    private String wordContent;

    private int dpMessage;

    private int index;

    private int begPos;

    private int endPos;

    private int globalIndex;

    private int property;

    private double totalScore;


    public String getWordContent() {
        return wordContent;
    }

    public void setWordContent(String wordContent) {
        this.wordContent = wordContent;
    }

    public int getDpMessage() {
        return dpMessage;
    }

    public void setDpMessage(int dpMessage) {
        this.dpMessage = dpMessage;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getBegPos() {
        return begPos;
    }

    public void setBegPos(int begPos) {
        this.begPos = begPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public int getGlobalIndex() {
        return globalIndex;
    }

    public void setGlobalIndex(int globalIndex) {
        this.globalIndex = globalIndex;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}
