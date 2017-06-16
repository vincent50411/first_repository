package com.iflytek.cetsim.controller.exHandler;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.json.JsonResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局Exception处理, controller层异常处理
 * Created by Administrator on 2017/4/24.
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler
{

    /**
     * 通用的异常捕获，可以针对不同的异常做特定的错误处理
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response)
    {
        StringBuffer resultMessage = new StringBuffer();
        if (ex instanceof org.springframework.web.multipart.MaxUploadSizeExceededException)
        {
            long maxSize = ((MaxUploadSizeExceededException)ex).getMaxUploadSize()/1000000;

            resultMessage.append("文件太大，最多不能超过" + maxSize + "M");
        }
        else
        {
            resultMessage.append("未定义的异常");
        }

        try
        {
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().println("{\"code\":\"" + Constants.FAIL + "\", \"msg\":\"" + resultMessage.toString() + "\", \"data\":null}");
            response.getWriter().flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
