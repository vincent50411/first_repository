package com.iflytek.cetsim.common.enums;

public enum GroupStateEnum {
    NO_GROUP(0,"未分组"),
    PRE_GROUP(1,"请求预分组"),
    PRE_GROUP_OK(2,"预分组成功"),
    GROUP_OK(3,"分组成功"),
    GROUP_CANCEL(4,"分组取消或失败");

    private int code;

    private String name;

    GroupStateEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
