package com.iflytek.cetsim.common.enums;

/**
 * 考试状态
 *
 * Created by qxb-810 on 2017/3/14.
 */
public enum ExamStateEnum {

    NO_exam(0, "未考试"),

    Exam_success(1, "考试成功"),

    Exam_failure(2, "考试失败");

    private int stateCode;

    private String stateName;

    ExamStateEnum(int code, String name) {
        this.stateCode = code;
        this.stateName = name;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
