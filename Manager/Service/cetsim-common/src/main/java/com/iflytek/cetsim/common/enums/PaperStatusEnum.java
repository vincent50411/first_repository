package com.iflytek.cetsim.common.enums;

/**
 * Created by pengwang on 2017/3/23.
 */
public enum PaperStatusEnum {
    ENABLED(1,"启用"),DISABLED(0,"禁用");

    private int statusCode;

    private String statusName;

    PaperStatusEnum(int statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }
    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusName() {
        return statusName;
    }
}
