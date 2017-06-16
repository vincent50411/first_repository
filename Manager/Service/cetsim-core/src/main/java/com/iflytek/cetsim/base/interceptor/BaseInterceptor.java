package com.iflytek.cetsim.base.interceptor;

import com.iflytek.cetsim.common.constants.Constants;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基础拦截器
 * Created by Administrator on 2017/4/6.
 */
public abstract class BaseInterceptor extends HandlerInterceptorAdapter
{
    /**
     * 拦截器类型判断
     * @param handler
     * @return
     */
    public boolean isIflytekHandler(Object handler)
    {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        IflytekInterceptor interceptor = handlerMethod.getMethodAnnotation(IflytekInterceptor.class);

        if (interceptor == null)
        {
            return false;
        }

        if (!interceptor.name().equals(this.getClass().getSimpleName()) &&
                !interceptor.name().equals(this.getClass().getName()))
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (isIflytekHandler(handler))
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            IflytekInterceptor interceptor = handlerMethod.getMethodAnnotation(IflytekInterceptor.class);


            //业务拦截器返回拦截结果
            boolean isInterceptor = runHandler(request, response, interceptor.paramName());

            //统一执行业务拦截器拦截结果
            return runHandler(response, isInterceptor);
        }

        return super.preHandle(request, response, handler);
    }

    /**
     * 业务拦截器具体实现
     * @param request
     * @param response
     * @return
     */
    public abstract boolean runHandler(HttpServletRequest request, HttpServletResponse response, String paramName);

    /**
     * 运行结果
     *
     * @param response
     * @param isInterceptor
     *            是否拦截,true拦截,跳转向failed指向页面,false:不拦截,跳转向success指向页面;
     *            success和failed为空时不做任何操作
     * @return
     * @throws Exception
     */
    public boolean runHandler(HttpServletResponse response, boolean isInterceptor) throws Exception
    {
        return isInterceptor;
    }

    protected void writeJSONToResponse(HttpServletResponse response, String message) throws IOException
    {
        response.setContentType("text/json");
                /*设置字符集为'UTF-8'*/
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(message);
    }


}
