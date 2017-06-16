package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.common.constants.Constants;

import java.util.Date;

/**
 * 试题查询实体类
 * Created by Administrator on 2017/5/10.
 */
public class PaperItemQueryDTO
{
    //试题代码
    private String code;

    //试题名称
    private String name;

    //试题类型名称
    private String paperItemTypeName;

    //试题类型代码
    private String paperItemTypeCode;

    //试题状态0禁用，1可用
    private Short status;

    private Short allowPlanUseage;

    private Short allowFreeUseage;

    private String summary;

    //试题缩略图
    private byte[] thumb;

    private Float difficulty;

    private Date createTime;

    private String knowledge;

    private String reserved2;

    private String reserved1;

    //使用次数
    private Integer userCount;

    //使用次数, 最为查询条件用，0：未练习 1：已练习
    private Integer useState;

    private Double maxScore;

    private Double avgScore;

    private Double score;

    //练习开始时间-查询的开始时间
    private Date startTime;

    //练习结束时间-查询的结束时间
    private Date endTime;

    public Integer pageIndex = Constants.FIRST_PAGE;
    public Integer pageSize = Constants.PAGE_SIZE;

    //排序字段名称
    private String orderColumnName;

    //排序规则 ASC，DESC
    private String orderCode;

    public String getOrderColumnName() {
        return orderColumnName;
    }

    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getUseState() {
        return useState;
    }

    public void setUseState(Integer useState) {
        this.useState = useState;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaperItemTypeCode() {
        return paperItemTypeCode;
    }

    public void setPaperItemTypeCode(String paperItemTypeCode) {
        this.paperItemTypeCode = paperItemTypeCode;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Short getAllowPlanUseage() {
        return allowPlanUseage;
    }

    public void setAllowPlanUseage(Short allowPlanUseage) {
        this.allowPlanUseage = allowPlanUseage;
    }

    public Short getAllowFreeUseage() {
        return allowFreeUseage;
    }

    public void setAllowFreeUseage(Short allowFreeUseage) {
        this.allowFreeUseage = allowFreeUseage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public byte[] getThumb() {
        return thumb;
    }

    public void setThumb(byte[] thumb) {
        this.thumb = thumb;
    }

    public Float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Float difficulty) {
        this.difficulty = difficulty;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPaperItemTypeName() {
        return paperItemTypeName;
    }

    public void setPaperItemTypeName(String paperItemTypeName) {
        this.paperItemTypeName = paperItemTypeName;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }
}
