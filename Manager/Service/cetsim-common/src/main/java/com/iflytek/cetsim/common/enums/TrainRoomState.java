package com.iflytek.cetsim.common.enums;

/**
 * Created by Administrator on 2017/5/11.
 */
public enum TrainRoomState
{
    UN_GROUP((short)0, "未组队"),  GROUPE_SUCC((short)1, "组队成功");


    private Short roomStateCode;

    private String roomStateDisplay;

    TrainRoomState(Short roomStateCode, String roomStateDisplay)
    {
        this.roomStateCode = roomStateCode;
        this.roomStateDisplay = roomStateDisplay;
    }

    public Short getRoomStateCode() {
        return roomStateCode;
    }

    public String getRoomStateDisplay() {
        return roomStateDisplay;
    }

}
