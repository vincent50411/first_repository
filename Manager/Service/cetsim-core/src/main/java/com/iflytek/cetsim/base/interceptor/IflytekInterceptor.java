package com.iflytek.cetsim.base.interceptor;

import java.lang.annotation.*;

/**
 * 自定义拦截器注释
 * Created by Administrator on 2017/4/6.
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface IflytekInterceptor
{
    /**
     * 业务拦截器具体实现类名称
     * @return
     */
    public String name();

    /**
     * 业务拦截器接受的参数名称
     * 该参数是请求携带参数中的主键参数，根据该参数来进行请求合法性验证
     * @return
     */
    public String paramName() default "";

}
