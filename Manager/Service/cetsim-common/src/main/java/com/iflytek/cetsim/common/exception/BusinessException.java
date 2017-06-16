package com.iflytek.cetsim.common.exception;

/**
 * <b>类 名：</b>BusinessException<br/>
 * <b>类描述：</b>Service层统一异常封装<br/>
 * <b>创建人：</b>longzhao<br/>
 * <b>创建时间：</b>2015-8-12 下午2:12:17<br/>
 * <b>修改人：</b>longzhao<br/>
 * <b>修改时间：</b>2015-8-12 下午2:12:17<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 * 
 */
public class BusinessException extends RuntimeException
{
    /**
     * serialVersionUID
     * 
     * @since 1.0.0
     */
    private static final long serialVersionUID = -4629228321556997314L;

    /**
     * 创建一个新的实例 BusinessException.
     * 
     * @param throwable 异常
     */
    public BusinessException(Throwable throwable)
    {
        super(throwable);
    }

    /**
     * 
     * 创建一个新的实例 BusinessException.
     * 
     * @param errMsg 异常消息内容
     */
    public BusinessException(String errMsg)
    {
        super(errMsg);
    }
}
