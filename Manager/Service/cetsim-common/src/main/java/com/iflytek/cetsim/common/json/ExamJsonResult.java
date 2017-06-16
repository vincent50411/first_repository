package com.iflytek.cetsim.common.json;

import com.iflytek.cetsim.common.constants.Constants;

import java.io.Serializable;

/**
 * 用作CET考试机json返回结果
 *
 * Created by code2life on 2017/3/10.
 */
public class ExamJsonResult implements Serializable {

    private static final long serialVersionUID = -6209802555693381640L;

    /**
     * 返回码值,默认值Const.FAIL
     */
    private int flag = Constants.FAIL;
    /**
     * 返回码值解析
     */
    private String flagMsg;

    /**
     * 返回对象
     */
    private Object data;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getFlagMsg() {
        return flagMsg;
    }

    public void setFlagMsg(String msg) {
        this.flagMsg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 设置成功值
     * @param data  设置对象
     * @param msg  设置码值解析
     */
    public void setSucceed(Object data,String msg){
        this.setFlagMsg(msg);
        this.setSucceed(data);
    }
    /**
     * 设置成功值
     * @param data 设置对象
     */
    public void setSucceed(Object data){
        this.data = data;
        this.setFlag(Constants.SUCCEED);
    }
    /**
     * 设置成功值
     * @param msg 返回码值解析
     */
    public void setSucceedMsg(String msg){
        this.setFlag(Constants.SUCCEED);
        this.setFlagMsg(msg);
    }
    /**
     * 设置失败值
     * @param msg 返回码值解析
     */
    public void setFailMsg(String msg){
        this.data = null;
        this.setFlag(Constants.FAIL);
        this.setFlagMsg(msg);
    }

    @Override
    public String toString() {
        return "FormatJson [flag=" + flag + ", flagMsg=" + flagMsg + ", data=" + data + "]";
    }
}
