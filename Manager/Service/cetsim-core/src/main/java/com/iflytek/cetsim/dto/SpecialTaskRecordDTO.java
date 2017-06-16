package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.common.constants.Constants;

import java.util.Date;
import java.util.List;

/**
 * 专项训练测试记录
 * Created by Administrator on 2017/5/11.
 */
public class SpecialTaskRecordDTO
{
    //记录code
    private String specialRecordCode;

    //学生账号
    private String studentAccount;

    //试题代码
    private String paperItmeCode;

    //试题名称
    private String paperItemName;

    //试题类型名称
    private String paperItemTypeName;

    //试题类型代码
    private String paperItemTypeCode;

    //学习引擎返回的结果xml地址
    private String detail;

    //试题状态0禁用，1可用
    private Short status;

    private Short allowPlanUseage;

    private Short allowFreeUseage;

    private String summary;

    //考试机提交的答题包路径，此路劲不能直接在前端使用，需要处理，前端使用answerPath
    private String dataPath;

    //答题录音文件相对地址
    private String answerPath;

    //试题缩略图
    private byte[] thumb;

    private Float difficulty;

    private Date createTime;

    private String knowledge;

    private String reserved2;

    private String reserved1;

    //使用次数,可用作界面筛选条件（未练习和已练习）
    private Integer userCount;

    private Double maxScore;

    private Double avgScore;

    private Double score;

    //练习开始时间
    private Date startTime;

    //yyyy-MM-dd HH:mm:ss
    private String startTimeStr;

    //练习结束时间
    private Date endTime;

    //明细记录列表
    private List<Object> infoRecordList;

    public Integer pageIndex = Constants.FIRST_PAGE;
    public Integer pageSize = Constants.PAGE_SIZE;


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getAnswerPath() {
        return answerPath;
    }

    public void setAnswerPath(String answerPath) {
        this.answerPath = answerPath;
    }

    public String getSpecialRecordCode() {
        return specialRecordCode;
    }

    public void setSpecialRecordCode(String specialRecordCode) {
        this.specialRecordCode = specialRecordCode;
    }

    public List<Object> getInfoRecordList() {
        return infoRecordList;
    }

    public void setInfoRecordList(List<Object> infoRecordList) {
        this.infoRecordList = infoRecordList;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
    }

    public String getPaperItmeCode() {
        return paperItmeCode;
    }

    public void setPaperItmeCode(String paperItmeCode) {
        this.paperItmeCode = paperItmeCode;
    }

    public String getPaperItemName() {
        return paperItemName;
    }

    public void setPaperItemName(String paperItemName) {
        this.paperItemName = paperItemName;
    }

    public String getPaperItemTypeName() {
        return paperItemTypeName;
    }

    public void setPaperItemTypeName(String paperItemTypeName) {
        this.paperItemTypeName = paperItemTypeName;
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

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
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
}
