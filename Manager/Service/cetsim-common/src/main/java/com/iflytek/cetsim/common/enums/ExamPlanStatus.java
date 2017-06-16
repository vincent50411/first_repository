package com.iflytek.cetsim.common.enums;

/**
 * Created by hejie on 2017/5/9.
 */
public enum ExamPlanStatus {
    ENABLED(1, "启用"),
    DISABLED(0, "禁用");

    private short code;
    private String info;


    ExamPlanStatus(int code, String info) {
        this.code = (short) code;
        this.info = info;
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
