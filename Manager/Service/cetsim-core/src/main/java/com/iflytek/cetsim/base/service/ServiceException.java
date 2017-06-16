package com.iflytek.cetsim.base.service;

/**
 * <b>类 名：</b>ServiceException<br/>
 * <b>类描述：</b>Service层公用的Exception, 从由Spring管理事务的函数中抛出时会触发事务回滚.<br/>
 * <b>创建人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>创建时间：</b>2016年8月24日 下午4:45:58<br/>
 * <b>修改人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>修改时间：</b>2016年8月24日 下午4:45:58<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 *
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
