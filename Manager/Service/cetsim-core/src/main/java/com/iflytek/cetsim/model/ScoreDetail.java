package com.iflytek.cetsim.model;

public class ScoreDetail extends ScoreDetailKey {
    private Integer id;

    private Float socre;

    private Integer engineCode;

    private Integer evalStatus;

    private String detail;

    private String reserved1;

    private String reserved2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getSocre() {
        return socre;
    }

    public void setSocre(Float socre) {
        this.socre = socre;
    }

    public Integer getEngineCode() {
        return engineCode;
    }

    public void setEngineCode(Integer engineCode) {
        this.engineCode = engineCode;
    }

    public Integer getEvalStatus() {
        return evalStatus;
    }

    public void setEvalStatus(Integer evalStatus) {
        this.evalStatus = evalStatus;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2 == null ? null : reserved2.trim();
    }
}