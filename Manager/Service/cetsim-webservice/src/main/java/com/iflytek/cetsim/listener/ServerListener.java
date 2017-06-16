package com.iflytek.cetsim.listener;

import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.service.impl.SocketServerManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Administrator on 2017/5/31.
 */
public class ServerListener implements ServletContextListener
{

    public void contextDestroyed(ServletContextEvent arg0)
    {
        //当tomcat服务正常关闭后，需要同时关闭socket，防止出现内存泄露
        SocketServerManager.serverSocket.stop();

        LogUtil.info("********************************************************");
        LogUtil.info("tomcat关闭了..........");
        LogUtil.info("********************************************************");
    }


    public void contextInitialized(ServletContextEvent arg0)
    {
        //System.out.println("tomcate启动了..............");
    }


}
