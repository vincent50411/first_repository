package com.iflytek.cetsim.common.exception;

/**
 * Created by tangjian on 2016/10/18.
 */
public class NoLoginException extends RuntimeException {
    /**
     * serialVersionUID
     *
     * @since 1.0.0
     */
    private static final long serialVersionUID=4629228321556997314L;

    /**
     * 创建一个新的实例 BusinessException.
     *
     * @param throwable 异常
     */
    public NoLoginException(Throwable throwable)
    {
        super(throwable);
    }

    /**
     *
     * 创建一个新的实例 BusinessException.
     *
     * @param errMsg 异常消息内容
     */
    public NoLoginException(String errMsg)
    {
        super(errMsg);
    }
}
