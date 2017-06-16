package com.iflytek.cetsim.common.enums;

/**
 * 考试流程枚举类
 *
 * Created by code2life on 2017/3/10.
 */
public enum FlowStateEnum {
    //未登录
    unLogin(0,"未登录"),
    //已登录
    login(1,"已登录"),
    //开始试音
    testStart(2,"开始试音"),
    //试音完成
    testSuccess(3,"试音完成"),
    //开始答题
    examStart(4,"开始答题"),
    //答题结束
    examSuccess(5,"答题结束");

    private int flowCode;

    private String flowName;

    FlowStateEnum(int code, String name) {
        this.flowCode = code;
        this.flowName = name;
    }

    public int getFlowCode() {
        return flowCode;
    }

    public String getFlowName() {
        return flowName;
    }

}
