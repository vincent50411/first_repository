package com.iflytek.cetsim.common.enums;

/**
 * Created by pengwang on 2017/3/16.
 */
public enum UserStatusEnum {
    Disabled((short)0,"非激活"), Enabled((short)1,"激活");

    private Short statusCode;

    private String statusName;

    UserStatusEnum(short statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    public Short getStatusCode() {
        return statusCode;
    }

    public String getStatusName() {
        return statusName;
    }
}
