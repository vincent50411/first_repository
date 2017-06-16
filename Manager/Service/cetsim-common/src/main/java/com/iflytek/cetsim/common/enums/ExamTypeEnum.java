package com.iflytek.cetsim.common.enums;

/**
 * 考试类型
 *
 * Created by code2life on 2017/3/10.
 */
public enum ExamTypeEnum {

    CET4(1), CET6(2);

    private int code;

    ExamTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
