package com.iflytek.cetsim.dto;

import com.iflytek.cetsim.common.constants.Constants;

/**
 * Created by Administrator on 2017/5/12.
 */
public class TrainTaskRoomQueryDTO
{

    //房间代码
    private String roomCode;

    //试卷名称
    private String paperName;

    //试卷类型
    private String paperTypeCode;





    public Integer pageIndex = Constants.FIRST_PAGE;
    public Integer pageSize = Constants.PAGE_SIZE;


    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperTypeCode() {
        return paperTypeCode;
    }

    public void setPaperTypeCode(String paperTypeCode) {
        this.paperTypeCode = paperTypeCode;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
