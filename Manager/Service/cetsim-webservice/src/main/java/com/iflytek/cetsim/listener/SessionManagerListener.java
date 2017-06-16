package com.iflytek.cetsim.listener;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.context.SessionManagerContext;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.service.TrainTaskRoomService;
import com.iflytek.cetsim.service.impl.TrainTaskRoomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * session监听器
 * Created by Administrator on 2017/6/2.
 */
public class SessionManagerListener implements HttpSessionListener
{
    private SessionManagerContext sessionManagerContext = SessionManagerContext.getInstance();

    /**
     * 监听session被创建时，缓存
     * @param httpSessionEvent
     */
    public void sessionCreated(HttpSessionEvent httpSessionEvent)
    {
        sessionManagerContext.addSession(httpSessionEvent.getSession());
    }

    /**
     * session失效时，移除缓存的session对象
     * @param httpSessionEvent
     */
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent)
    {
        LogUtil.info("--> 开始session过期处理流程");

        HttpSession session = httpSessionEvent.getSession();

        //获取spring的bean
        ApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());

        //获取spring注入的service层bean对象
        TrainTaskRoomService trainTaskRoomService = springCtx.getBean(TrainTaskRoomServiceImpl.class);

        //session失效，需要移除个人所创建的自主考试房间
        Account userInfo = (Account)session.getAttribute(Constants.LOGIN_USER_INFO);

        if(userInfo != null)
        {
            String userAccount = userInfo.getAccount();

            LogUtil.info("--> 登录用户[" + userAccount + "] session已经失效，清理创建的自主考试房间");

            //将个人创建的自主考试删除
            trainTaskRoomService.clearUserExitTrainRoom(userAccount);
        }

        //删除session
        sessionManagerContext.delSession(session);

    }




}
