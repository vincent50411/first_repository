package com.iflytek.cetsim.interceptors;

import com.iflytek.cetsim.base.interceptor.BaseInterceptor;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.RoleEnum;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.model.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * 拦截网站发送到所有请求
 * Created by Administrator on 2017/4/6.
 */
public class WebInterceptor extends BaseInterceptor
{
    @Override
    public boolean runHandler(HttpServletRequest request, HttpServletResponse response, String paramName)
    {
        //确保session能够被正常创建
        HttpSession session = request.getSession(true);

        Account loginUserInfo = (Account) session.getAttribute("loginUser");

        //如果session登录信息为空，则认为session过期
        if(loginUserInfo == null)
        {
            LogUtil.info("--> Session 已经过期，请求路劲为：" + request.getRequestURI());

            try
            {
                writeJSONToResponse(response, "{\"code\":\"" + Constants.SESSION_INVALID + "\", \"msg\":\"session已经过期，请重新登录\", \"data\":null}");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return false;
        }
        else
            {
            //做权限拦截, 学生权限拦截
            int roleValue =  loginUserInfo.getRole();

            //学生权限拦截,判断学生提交的修改个人信息是否是本人发送, 参数名称必须有值
            if(roleValue == RoleEnum.Student.getRoleCode() && !"".equals(paramName))
            {
                //判断是否是,paramName参数是业务方法上增加的注解接受的参数(关键参数)，和请求携带的参数名称一致
                boolean isValidatorQueryRequest = checkUserRequestValidate(request, loginUserInfo, paramName);

                //如果请求是非法的，则直接拒绝
                if(!isValidatorQueryRequest)
                {
                    try
                    {
                        writeJSONToResponse(response, "{\"code\":\"" + Constants.FAIL + "\", \"msg\":\"非法的请求\", \"data\":null}");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 检查请求携带的参数体是否是登录用户本人发送的，需要根据关键参数来和登录用户的id进行验证
     * @param request
     * @param loginUserInfo
     * @param paramNameStr
     * @return
     */
    private boolean checkUserRequestValidate(HttpServletRequest request, Account loginUserInfo, String paramNameStr)
    {
        boolean result = false;

        Map<String, String[]> paramsMap = request.getParameterMap();

        //判断是否是studentId
        String[] paramsValue = paramsMap.get(paramNameStr);

        String paramValue = "";

        if(paramsValue != null && paramsValue.length > 0)
        {
            try {
                paramValue = paramsValue[0];

                //学生角色发送的关键请求携带的id必须和登录的id保持一致才是合法的请求
                if ("userAccount".equals(paramNameStr)) {
                    if(loginUserInfo.getAccount() == null || !loginUserInfo.getAccount().equals(paramValue))
                    {
                        //请求不合法
                        result = false;
                    }
                    else
                    {
                        //请求合法
                        result = true;
                    }
                } else {
                    if(loginUserInfo.getId() != new Integer(paramValue))
                    {
                        //请求不合法
                        result = false;
                    }
                    else
                    {
                        //请求合法
                        result = true;
                    }
                }
            }
            catch (Exception ex)
            {
                //数值转换异常, 请求不合法
                ex.printStackTrace();
            }
        }

        return result;
    }


}
