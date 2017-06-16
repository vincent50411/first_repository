package com.iflytek.cetsim.common.enums;

public enum ExamModeEnum {

    SPECIAL_TRAIN("special","专项训练"),
    FREE_TRAIN("train","自主考试"),
    SIM_EXAM("sim","模拟考试");

    private String code;

    private String name;

    ExamModeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
