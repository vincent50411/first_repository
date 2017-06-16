package com.iflytek.cetsim.dto;

import java.util.List;

/**
 * 句子
 * Created by Administrator on 2017/5/18.
 */
public class StudyReportSentenceInfoDTO
{

    private String sentenceMp3Path;

    //句子内容
    private String sentenceContent;

    private double accuracyScore;

    private double fluencyScore;

    private double standardScore;

    private int wordCount;

    private int index;

    private int begPos;

    private int endPos;

    private double totalScore;

    private int wordCDount;

    //单词列表
    private List<StudyReportWordInfoDTO> studyReportWordInfoDTOs;

    public String getSentenceMp3Path() {
        return sentenceMp3Path;
    }

    public void setSentenceMp3Path(String sentenceMp3Path) {
        this.sentenceMp3Path = sentenceMp3Path;
    }

    public int getWordCDount() {
        return wordCDount;
    }

    public void setWordCDount(int wordCDount) {
        this.wordCDount = wordCDount;
    }

    public String getSentenceContent() {
        return sentenceContent;
    }

    public void setSentenceContent(String sentenceContent) {
        this.sentenceContent = sentenceContent;
    }

    public double getAccuracyScore() {
        return accuracyScore;
    }

    public void setAccuracyScore(double accuracyScore) {
        this.accuracyScore = accuracyScore;
    }

    public double getFluencyScore() {
        return fluencyScore;
    }

    public void setFluencyScore(double fluencyScore) {
        this.fluencyScore = fluencyScore;
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

    public List<StudyReportWordInfoDTO> getStudyReportWordInfoDTOs() {
        return studyReportWordInfoDTOs;
    }

    public void setStudyReportWordInfoDTOs(List<StudyReportWordInfoDTO> studyReportWordInfoDTOs) {
        this.studyReportWordInfoDTOs = studyReportWordInfoDTOs;
    }
}
