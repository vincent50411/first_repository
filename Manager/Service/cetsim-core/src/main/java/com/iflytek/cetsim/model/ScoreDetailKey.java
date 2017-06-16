package com.iflytek.cetsim.model;

public class ScoreDetailKey {
    private String recordCode;

    private String paperItemTypeCode;

    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode == null ? null : recordCode.trim();
    }

    public String getPaperItemTypeCode() {
        return paperItemTypeCode;
    }

    public void setPaperItemTypeCode(String paperItemTypeCode) {
        this.paperItemTypeCode = paperItemTypeCode == null ? null : paperItemTypeCode.trim();
    }
}