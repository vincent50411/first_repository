package com.iflytek.cetsim.common.enums;

/**
 * 使用状态
 * Created by Administrator on 2017/5/11.
 */
public enum UseStateEnum
{
    NONEUSED(0, "未练习"), USED(1, "已练习");

    private int useStateCode;
    private String useStateDisplay;

    UseStateEnum(int useStateCode, String useStateDisplay)
    {
        this.useStateCode = useStateCode;
        this.useStateDisplay = useStateDisplay;
    }

    public int getUseStateCode() {
        return useStateCode;
    }

    public String getUseStateDisplay() {
        return useStateDisplay;
    }
}
