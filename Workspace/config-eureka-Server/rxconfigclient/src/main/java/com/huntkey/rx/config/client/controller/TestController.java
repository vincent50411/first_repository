package com.huntkey.rx.config.client.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@Component
@RestController
public class TestController {
	//@Value("${ivr.jdbc.user}")
	private String jdbcUrl;
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@RequestMapping("/appMsg")
	public Map<String,Object> appMsg(){
		Map<String,Object> msg = new HashMap<String,Object>();
		//msg.put("ivr_jdbc_url", jdbcUrl);
		msg.put("applicationName", applicationName);
	
		return msg;
	}
}
