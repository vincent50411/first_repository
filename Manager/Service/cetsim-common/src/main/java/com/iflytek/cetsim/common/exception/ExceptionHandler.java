package com.iflytek.cetsim.common.exception;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataAccessException;

import com.iflytek.cetsim.common.utils.logger.LogUtil;

/**
 * <b>类 名：</b>ExceptionHandler<br/>
 * <b>类描述：</b>Service异常统一处理<br/>
 * <b>创建人：</b>longzhao<br/>
 * <b>创建时间：</b>2015-8-12 下午2:07:13<br/>
 * <b>修改人：</b>longzhao<br/>
 * <b>修改时间：</b>2015-8-12 下午2:07:13<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 * 
 */
public class ExceptionHandler implements ThrowsAdvice
{
    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) throws Throwable
    {
        LogUtil.error("错误发生：" + String.valueOf(target) + "  方法名：" + method.getName());
        // 根据不同异常返回不同信息
        String errMsg;
        if (DataAccessException.class.equals(ex.getClass()))
        {
            errMsg = "数据库访问失败";
        }
        else if (SQLException.class.equals(ex.getClass()))
        {
            errMsg = "数据库操作失败";
        }
        else if (NullPointerException.class.equals(ex.getClass()))
        {
            errMsg = "使用了未经初值化的对象或对象不存在";
        }
        else if (ClassNotFoundException.class.equals(ex.getClass()))
        {
            errMsg = "使用的类不存在";
        }
        else if (NoSuchMethodException.class.equals(ex.getClass()))
        {
            errMsg = "调用的方法不存在";
        }
        else if (IOException.class.equals(ex.getClass()))
        {
            errMsg = "IO异常";
        }
        else if (ArithmeticException.class.equals(ex.getClass()))
        {
            errMsg = "数字运算异常";
        }
        else if (IllegalArgumentException.class.equals(ex.getClass()))
        {
            errMsg = "方法参数异常";
        }
        else if (ClassCastException.class.equals(ex.getClass()))
        {
            errMsg = "强制类型转换异常";
        }
        else if (ArrayIndexOutOfBoundsException.class.equals(ex.getClass()))
        {
            errMsg = "数组越界异常";
        }
        else
        {
            errMsg = "程序出现异常：" + ex.getMessage();
        }
        LogUtil.error(errMsg);
        throw new BusinessException(ex);
    }
}
