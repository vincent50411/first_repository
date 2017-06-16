package com.iflytek.cetsim.common.enums;

public enum EvalStateEnum {
    DEFAULT(0,"未评测"),
    SUCCESS(1,"评测成功"),
    FAIL(2,"评测失败");

    private int code;

    private String name;

    EvalStateEnum(int code, String name) {
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
