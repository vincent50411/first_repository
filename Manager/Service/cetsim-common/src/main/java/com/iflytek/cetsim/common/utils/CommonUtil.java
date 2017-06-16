package com.iflytek.cetsim.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

/**
 * Created by Administrator on 2017/4/13.
 */
public class CommonUtil
{

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir)
    {
        if(dir.exists() && dir.isDirectory())
        {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for(int i=0; i<children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }

        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 得分规则计算
     * @param scoreValue
     * @return
     */
    public static String scoreFormatter(double scoreValue)
    {
        Double result = null;

        if(scoreValue > 0 && scoreValue < 1)
        {
            result = Math.ceil(scoreValue);
        }
        else if(scoreValue >= 1 && scoreValue < 19)
        {
            result = Math.rint(scoreValue);
        }
        else if(scoreValue >= 19 && scoreValue < 20)
        {
            result = Math.floor(scoreValue);
        }
        else
        {
            result = new Double(0);
        }

        return String.format("%.0f",result);
    }

    public static String GUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取客户端IP地址
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

}
