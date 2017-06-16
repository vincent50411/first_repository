package com.iflytek.cetsim.common.context;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/6/2.
 */
public class SessionManagerContext
{
    private static SessionManagerContext instance;

    private ConcurrentHashMap<String, HttpSession> sessionMap = null;

    public SessionManagerContext()
    {
        sessionMap = new ConcurrentHashMap<String, HttpSession>();
    }

    public static SessionManagerContext getInstance()
    {
        if(instance == null)
        {
            instance = new SessionManagerContext();
        }

        return instance;
    }

    public void addSession(HttpSession session)
    {
        if(session != null)
        {
            sessionMap.put(session.getId(), session);
        }
    }

    public void delSession(HttpSession session)
    {
        if(session != null)
        {
            sessionMap.remove(session.getId());
        }
    }

    public HttpSession getSession(String sessionID)
    {
        if(sessionID == null)
        {
            return null;
        }

        return sessionMap.get(sessionID);
    }




}
