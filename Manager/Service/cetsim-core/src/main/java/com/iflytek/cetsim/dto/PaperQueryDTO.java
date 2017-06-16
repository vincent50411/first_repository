package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.common.constants.Constants;

/**
 * Created by pengwang on 2017/3/22.
 */
public class PaperQueryDTO {

    public Integer type;
    public Integer status;
    public Integer allowPlanUseage;
    public Integer allowFreeUseage;
    public String name;

    //试卷类型
    private String paperTypeCode;

    //使用次数, 最为查询条件用，0：未练习 1：已练习
    private Integer useState;

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

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode;
    }

    public Integer getUseState() {
        return useState;
    }

    public void setUseState(Integer useState) {
        this.useState = useState;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
