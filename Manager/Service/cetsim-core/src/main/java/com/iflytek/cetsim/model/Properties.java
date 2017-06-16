package com.iflytek.cetsim.model;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/14.
 */
public class Properties
{
    private Integer id;

    private String displayName;

    private Integer displayLevel;

    private Integer parentId;

    private Date createTime;

    private String reserved1;

    private String reserved2;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getDisplayLevel() {
        return displayLevel;
    }

    public void setDisplayLevel(Integer displayLevel) {
        this.displayLevel = displayLevel;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }
}
