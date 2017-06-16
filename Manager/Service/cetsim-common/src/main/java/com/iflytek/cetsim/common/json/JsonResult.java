package com.iflytek.cetsim.common.json;

import java.io.Serializable;

import com.iflytek.cetsim.common.constants.Constants;

/**
 * <b>类 名：</b>JsonResult<br/>
 * <b>类描述：</b>定义json数据格式<br/>
 * <b>创建人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>创建时间：</b>2016年8月29日 下午1:59:20<br/>
 * <b>修改人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>修改时间：</b>2016年8月29日 下午1:59:20<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 *
 */
public class JsonResult implements Serializable {
	
	private static final long serialVersionUID = -6209802555693381640L;
	
	/**
	 * 返回码值,默认值Const.FAIL
	 */
	private int code = Constants.FAIL;
	/**
	 * 返回码值解析
	 */
	private String msg;
	/**
	 * 返回对象
	 */
	private Object data;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * 设置没有权限返回值
	 * @param auth 原值返回
	 * @return
	 */
	public boolean setNoAuth(boolean auth){
		if(!auth){
			this.data = null;
			this.setCode(Constants.NO_AUTHORIZED);
			this.setMsg(Constants.NO_AUTHORIZED_MSG);
		}
		return auth;
	}
	
	/**
	 * 设置成功值
	 * @param data  设置对象  
	 * @param msg  设置码值解析
	 */
	public void setSucceed(Object data,String msg){
		this.setMsg(msg);
		this.setSucceed(data);
	}
	/**
	 * 设置成功值
	 * @param data 设置对象    
	 */
	public void setSucceed(Object data){
		this.data = data;
		this.setCode(Constants.SUCCEED);
	}
	/**
	 * 设置成功值
	 * @param msg 返回码值解析
	 */
	public void setSucceedMsg(String msg){
		this.setCode(Constants.SUCCEED);
		this.setMsg(msg);
	}
	/**
	 * 设置失败值
	 * @param msg 返回码值解析
	 */
	public void setFailMsg(String msg){
		this.data = null;
		this.setCode(Constants.FAIL);
		this.setMsg(msg);
	}
	
	@Override
	public String toString() {
		return "FormatJson [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}	
}
