package com.iflytek.cetsim.interceptors;

import com.iflytek.cetsim.base.interceptor.BaseInterceptor;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 考试机端请求拦截器，继承自自定义拦截器基础类BaseInterceptor，实现runHandler抽象方法，添加具体业务流程验证
 * 在Controller内需要拦截的URL请求方法上增加自定义注释@IflytekInterceptor（name="具体实现类", check=是否拦截）
 * Created by Administrator on 2017/4/6.
 */
public class ClientExamInterceptor extends BaseInterceptor
{
    @Autowired
    private ExamService examService;

    @Override
    public boolean runHandler(HttpServletRequest httpServletRequest, HttpServletResponse response, String paramName)
    {
        boolean result = true;

        try
        {
            //param:Long recordId, String token
            Map<String, String[]> paramMap = (Map<String, String[]>) httpServletRequest.getParameterMap();

            if(paramMap == null)
            {
                return true;
            }

            //如果请求携带的参数为空，不做验证
            Object recordIdObj = paramMap.get("recordId") == null ? "" : paramMap.get("recordId");
            Object recordTypeObj = paramMap.get("recordType") == null ? "" : paramMap.get("recordType");
            Object tokenObj = paramMap.get("token") == null ? "" : paramMap.get("token");

            String requestPath = httpServletRequest.getRequestURI();

            if("".equals(recordIdObj) || "".equals(tokenObj))
            {
                LogUtil.info("--> requestPath:" + requestPath + "; recordId:" + recordIdObj.toString() + "; token:" + tokenObj.toString());
                return true;
            }

            String recordId = ((String[])recordIdObj)[0];
            String recordType = ((String[])recordTypeObj)[0];
            String token = ((String[])tokenObj)[0];

            //验证会话是否过期
            result = examService.verifyToken(recordId, recordType, token);

            if(!result)
            {
                LogUtil.info("--> 考试机会话已经过期，请求路劲为：" + requestPath + "; recordId:" + recordId + "; token:" + token);

                response.setContentType("text/json");
                /*设置字符集为'UTF-8'*/
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"flag\":\"" + Constants.SESSION_INVALID + "\", \"msg\":\"session已经过期，请重新登录\", \"data\":null}");

                LogUtil.info("--> 考试端验证会话过期, 请求被拦截");
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return result;
    }
}
