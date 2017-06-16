package com.iflytek.cetsim.service;

/**
 * Created by Administrator on 2017/5/18.
 */
public interface EmailService
{
    boolean sendSimpleEmail(String to, String subject, String content);

}
