package com.iflytek.cetsim.common.utils;

import javax.servlet.ServletContext;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

/**
 * <b>类 名：</b>SpringContextHolder<br/>
 * <b>类描述：</b>以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext<br/>
 * <b>创建人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>创建时间：</b>2016年8月25日 下午2:54:29<br/>
 * <b>修改人：</b>mailto:haoshen3@iflytek.com<br/>
 * <b>修改时间：</b>2016年8月25日 下午2:54:29<br/>
 * <b>修改备注：</b><br/>
 * 
 * @version 1.0<br/>
 *
 */
@Service
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, ServletContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null; // Spring应用上下文环境
	private static ServletContext servletContext = null;
	private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);
	
	/**
	 * @Description:   实现ApplicationContextAware接口的回调方法，设置上下文环境
	 * @param:         applicationContext 
	 * @throws:        BeansException
	 * @Author:        <a href="mailto:unitysl@hotmail.com"/>沈浩</a>
	 * @CreateDate:    2016年6月26日
	 */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * @Description:   取得存储在静态变量中的ApplicationContext(单例)
     * @return:        ApplicationContext  
     * @Author:        <a href="mailto:unitysl@hotmail.com"/>沈浩</a>
     * @CreateDate:    2016年6月26日
     */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * @Description:   从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 * @param:         name
	 * @return:        T 一个以所给名字注册的bean的实例
	 * @throws:        BeansException   
	 * @Author:        <a href="mailto:unitysl@hotmail.com"/>沈浩</a>
	 * @CreateDate:    2016年6月26日
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * @Description:   从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 * @param:         requiredType
	 * @return:        T  
	 * @throws:        BeansException   
	 * @Author:        <a href="mailto:unitysl@hotmail.com"/>沈浩</a>
	 * @CreateDate:    2016年6月26日
	 */
	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}
	
	/**
	 * @Description:   如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
	 * @param:         name
	 * @return:        boolean  
	 * @Author:        <a href="mailto:unitysl@hotmail.com"/>沈浩</a>
	 * @CreateDate:    2016年6月26日
	 */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }
    
    /**
     * @Description:   判断以给定名字注册的bean定义是一个singleton还是一个prototype
     * @param:         name
     * @return:        boolean  
     * @throws:        NoSuchBeanDefinitionException   
     * @Author:        <a href="mailto:unitysl@hotmail.com"/>沈浩</a>
     * @CreateDate:    2016年6月26日
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		if (logger.isDebugEnabled()){
			logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		applicationContext = null;
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		Validate.validState(applicationContext != null, "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		SpringContextHolder.servletContext = servletContext;
	}
	
	public static ServletContext getServletContext(){
		return servletContext;
	}
	
	public static String getServletContextRealPath(){
		return servletContext.getRealPath("/");
	}
}