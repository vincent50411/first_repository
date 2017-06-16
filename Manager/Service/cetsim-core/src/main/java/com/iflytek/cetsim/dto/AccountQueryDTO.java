package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.common.constants.Constants;

public class AccountQueryDTO {

    private Integer role;

    private Integer status = Constants.AVAILABLE;

    private String name;

    private Integer pageIndex = Constants.FIRST_PAGE;

    private Integer pageSize = Constants.PAGE_SIZE;

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

}
