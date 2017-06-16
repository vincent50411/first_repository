package com.iflytek.cetsim.dto;

import java.util.Date;

/**
 * Created by pengwang on 2017/3/22.
 */
public class PaperInfoDTO {
    private Integer id;

    private String code;

    private String name;

    private String paperTypeCode;

    private Date createTime;

    private String summary;

    //总权限
    private Integer status;

    //是否允许模拟考试
    private Integer allowPlanUseage;

    //是否允许自主考试考试
    private Integer allowFreeUseage;

    private Double maxScore;

    private Double averageScore;

    private Double trainAverageScore;

    private Double minScore;

    private int useCount;

    private int trainUseCount;

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
        this.name = name;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getAllowPlanUseage() {
        return allowPlanUseage;
    }

    public void setAllowPlanUseage(Integer allowPlanUseage) {
        this.allowPlanUseage = allowPlanUseage;
    }

    public Integer getAllowFreeUseage() {
        return allowFreeUseage;
    }

    public void setAllowFreeUseage(Integer allowFreeUseage) {
        this.allowFreeUseage = allowFreeUseage;
    }

    public Double getTrainAverageScore() {
        return trainAverageScore;
    }

    public void setTrainAverageScore(Double trainAverageScore) {
        this.trainAverageScore = trainAverageScore;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getTrainUseCount() {
        return trainUseCount;
    }

    public void setTrainUseCount(int trainUseCount) {
        this.trainUseCount = trainUseCount;
    }
}
