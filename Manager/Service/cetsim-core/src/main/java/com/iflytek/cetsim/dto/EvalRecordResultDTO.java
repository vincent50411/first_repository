package com.iflytek.cetsim.dto;

/**
 * Created by Administrator on 2017/5/27.
 */
public class EvalRecordResultDTO
{
    //考试记录code
    private String recordCode;

    //小题记录code
    private String evalRecordCode;

    private String paperItemTypeCode;

    //引擎返回码
    private int engineCode;

    //单题评测状态(0:未评测,1:评测成功,2:评测失败)
    private int evalStatus;

    //专项训练、朗读题，该字段存学习引擎返回的xml文件路径
    private String detail;

    private Double socre;


    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getEvalRecordCode() {
        return evalRecordCode;
    }

    public void setEvalRecordCode(String evalRecordCode) {
        this.evalRecordCode = evalRecordCode;
    }

    public String getPaperItemTypeCode() {
        return paperItemTypeCode;
    }

    public void setPaperItemTypeCode(String paperItemTypeCode) {
        this.paperItemTypeCode = paperItemTypeCode;
    }

    public int getEngineCode() {
        return engineCode;
    }

    public void setEngineCode(int engineCode) {
        this.engineCode = engineCode;
    }

    public int getEvalStatus() {
        return evalStatus;
    }

    public void setEvalStatus(int evalStatus) {
        this.evalStatus = evalStatus;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Double getSocre() {
        return socre;
    }

    public void setSocre(Double socre) {
        this.socre = socre;
    }
}
