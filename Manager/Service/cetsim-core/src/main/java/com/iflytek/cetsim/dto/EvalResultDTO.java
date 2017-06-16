package com.iflytek.cetsim.dto;

/**
 * 服务端评测结果
 *
 * Created by qxb-810 on 2017/3/18.
 */
public class EvalResultDTO {

    private Boolean success;

    private String msg;

    private String ret;

    private Double score;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
