package com.iflytek.cetsim.dto;

import java.util.List;

/**
 * 学习引擎返回的报告结果封装
 * Created by Administrator on 2017/5/18.
 */
public class StudyReportChapterInfoDTO
{
    //记录代码
    private String recordCode;

    //试卷或者试题代码
    private String paperCode;

    //题型代码
    private String papertItemTypeCode;

    //标准朗读文件地址
    private String standardMp3Path;

    //短文内容
    private String chapterContent;

    private double accuracyScore;

    //学习引擎返回码，可根据不同返回码，界面显示不同提示信息
    private int exceptInfo;

    //流畅度分
    private double fluencyScore;

    //完整度分
    private double integrityScore;

    //发音标准度分
    private double standardScore;

    private int wordCount;

    private int index;

    private int begPos;

    private int endPos;

    private double totalScore;

    private boolean isRejected;

    private int wordCDount;

    //句子列表
    List<StudyReportSentenceInfoDTO> studyReportSentenceInfoDTOs;


    public String getStandardMp3Path() {
        return standardMp3Path;
    }

    public void setStandardMp3Path(String standardMp3Path) {
        this.standardMp3Path = standardMp3Path;
    }

    public int getWordCDount() {
        return wordCDount;
    }

    public void setWordCDount(int wordCDount) {
        this.wordCDount = wordCDount;
    }

    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public String getPapertItemTypeCode() {
        return papertItemTypeCode;
    }

    public void setPapertItemTypeCode(String papertItemTypeCode) {
        this.papertItemTypeCode = papertItemTypeCode;
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public double getAccuracyScore() {
        return accuracyScore;
    }

    public void setAccuracyScore(double accuracyScore) {
        this.accuracyScore = accuracyScore;
    }

    public int getExceptInfo() {
        return exceptInfo;
    }

    public void setExceptInfo(int exceptInfo) {
        this.exceptInfo = exceptInfo;
    }

    public double getFluencyScore() {
        return fluencyScore;
    }

    public void setFluencyScore(double fluencyScore) {
        this.fluencyScore = fluencyScore;
    }

    public double getIntegrityScore() {
        return integrityScore;
    }

    public void setIntegrityScore(double integrityScore) {
        this.integrityScore = integrityScore;
    }

    public double getStandardScore() {
        return standardScore;
    }

    public void setStandardScore(double standardScore) {
        this.standardScore = standardScore;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
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

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }

    public List<StudyReportSentenceInfoDTO> getStudyReportSentenceInfoDTOs() {
        return studyReportSentenceInfoDTOs;
    }

    public void setStudyReportSentenceInfoDTOs(List<StudyReportSentenceInfoDTO> studyReportSentenceInfoDTOs) {
        this.studyReportSentenceInfoDTOs = studyReportSentenceInfoDTOs;
    }
}
