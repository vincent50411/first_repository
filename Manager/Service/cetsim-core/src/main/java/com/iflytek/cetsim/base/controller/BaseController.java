package com.iflytek.cetsim.base.controller;

import com.iflytek.cetsim.common.json.ExamJsonResult;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.DateUtils;
import com.iflytek.cetsim.common.exception.NoLoginException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <b>类 名：</b>BaseController<br/>
 * <b>类描述：</b>Controller层基础实现，添加控制器常用的方法<br/>
 * <b>创建人：</b>longzhao<br/>
 * <b>创建时间：</b>2016-1-27 上午11:53:28<br/>
 * <b>修改人：</b>haoshen<br/>
 * <b>修改时间：</b>2016-9-21 下午11:36:56<br/>
 * <b>修改备注：</b><br/>
 *
 */
public class BaseController {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取json对象
	 *
	 * @param
	 * @return JsonResult
	 */
    protected JsonResult getJson(){
		return new JsonResult();
	}

	protected ExamJsonResult getJson4Exam(){
		return new ExamJsonResult();
	}

	/**
	 *
	 * exception(画面通用异常处理)
	 *
	 * @param request
     * @param response
	 * @param e
	 */
	@ExceptionHandler
	@ResponseBody
	public JsonResult exception(HttpServletRequest request,HttpServletResponse response, Exception e) {
        JsonResult jsonResult = getJson();
         if(e instanceof UnauthorizedException){
            jsonResult.setNoAuth(Boolean.FALSE);
            return jsonResult;
        }
        else if(e instanceof  NoLoginException){
			 jsonResult.setCode(405);//未登陆异常
			 return jsonResult;
		 }
         return null;
	}

	/**
	 * 初始化数据绑定:1.将所有传递进来的String进行HTML编码，防止XSS攻击; 2.将字段中Date类型转换为String类型
	 *
	 * @param  binder
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
//				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
				setValue(text == null ? null : text.trim());
			}
			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
		});
	}

	/**
     *
     * getFirstErrorMsg(获取校验错误信息中的第一个)
     *
     * @param bindingResult
     * @return
     * @exception
     * @since 1.0
     * @author huhuang@iflytek.com
     */
    public String getFirstErrorMsg(BindingResult bindingResult){
        List<ObjectError> errList = bindingResult.getAllErrors();
        String errStr = "";
        if(errList != null && errList.size()>0){
            errStr = errList.get(0).getDefaultMessage();
        }
        return errStr;
    }

	protected String getFormateDate() {
		Date date = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
		return time.format(date);
	}

	private boolean writeToFile(InputStream is, String file) {

		boolean result = false;
		OutputStream os = null;
		try {

			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
			os.close();
			result = true;
		} catch (Exception e) {
			logger.error("writeToFile {},error:{}", file, e.getMessage());
			result = false;
		}
		return result;
	}

}
