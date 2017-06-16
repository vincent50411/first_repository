package com.iflytek.cetsim.model;

import java.util.Date;

public class Paper {
    private String code;

    private Integer id;

    private String name;

    private String paperTypeCode;

    private Short status;

    private Boolean allowPlanUseage;

    private Boolean allowFreeUseage;

    private String summary;

    private Date createTime;

    private String reserved1;

    private String reserved2;

    private byte[] thumb;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode == null ? null : paperTypeCode.trim();
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Boolean getAllowPlanUseage() {
        return allowPlanUseage;
    }

    public void setAllowPlanUseage(Boolean allowPlanUseage) {
        this.allowPlanUseage = allowPlanUseage;
    }

    public Boolean getAllowFreeUseage() {
        return allowFreeUseage;
    }

    public void setAllowFreeUseage(Boolean allowFreeUseage) {
        this.allowFreeUseage = allowFreeUseage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public byte[] getThumb() {
        return thumb;
    }

    public void setThumb(byte[] thumb) {
        this.thumb = thumb;
    }
}