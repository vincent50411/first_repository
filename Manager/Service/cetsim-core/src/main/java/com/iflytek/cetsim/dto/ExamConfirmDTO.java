package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.model.PaperItem;

import java.util.List;

public class ExamConfirmDTO {

    private String paperId;

    private List<PaperItem> paperSchema;

    private Integer remainTimeToBeginExam;

    private Integer answerTime;

    private String paperType;

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public List<PaperItem> getPaperSchema() {
        return paperSchema;
    }

    public void setPaperSchema(List<PaperItem> paperSchema) {
        this.paperSchema = paperSchema;
    }

    public Integer getRemainTimeToBeginExam() {
        return remainTimeToBeginExam;
    }

    public void setRemainTimeToBeginExam(Integer remainTimeToBeginExam) {
        this.remainTimeToBeginExam = remainTimeToBeginExam;
    }

    public Integer getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Integer answerTime) {
        this.answerTime = answerTime;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }
}
