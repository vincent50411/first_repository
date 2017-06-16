package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.dao.ConfigurationMapper;
import com.iflytek.cetsim.model.Configuration;
import com.iflytek.cetsim.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/5/18.
 */
@Service
@Transactional
public class EmailServiceImpl implements EmailService
{
    @Autowired
    private ConfigurationMapper configurationMapper;


    /**
     * 保存全局邮件属性值
     */
    private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    private JavaMailSenderImpl mailSender = null;

    @PostConstruct
    public void initEmailService()
    {
        List<Configuration> configurations = configurationMapper.selectAllConfig();

        for (Configuration configuration : configurations)
        {
            if(Constants.IS_EMAIL_VALIDATE.equals(configuration.getConfigKey()))
            {
                map.put(Constants.IS_EMAIL_VALIDATE, configuration.getConfigValue());
            }

            if(Constants.EMAIL_HOST.equals(configuration.getConfigKey()))
            {
                map.put(Constants.EMAIL_HOST, configuration.getConfigValue());
            }

            if(Constants.EMAIL_PORT.equals(configuration.getConfigKey()))
            {
                map.put(Constants.EMAIL_PORT, configuration.getConfigValue());
            }

            if(Constants.EMAIL_USER_NAME.equals(configuration.getConfigKey()))
            {
                map.put(Constants.EMAIL_USER_NAME, configuration.getConfigValue());
            }

            if(Constants.EMAIL_PASSWORD.equals(configuration.getConfigKey()))
            {
                map.put(Constants.EMAIL_PASSWORD, configuration.getConfigValue());
            }
        }

        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(map.get(Constants.EMAIL_HOST));
        mailSender.setPort(Integer.valueOf(map.get(Constants.EMAIL_PORT)));
        mailSender.setUsername(map.get(Constants.EMAIL_USER_NAME));
        mailSender.setPassword(map.get(Constants.EMAIL_PASSWORD));
        mailSender.setProtocol("smtp");
    }


    /**
     * 发送简单邮件服务
     * @param to
     * @param subject
     * @param content
     * @return
     */
    public boolean sendSimpleEmail(String to, String subject, String content)
    {
        try
        {
            //SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //创建多元化邮件
            MimeMessage mimeMessage = ((JavaMailSenderImpl)mailSender).createMimeMessage();

            //创建 mimeMessage 帮助类，用于封装信息至 mimeMessage
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(map.get(Constants.EMAIL_USER_NAME));
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);

            //html格式的邮件文本内容，true
            mimeMessageHelper.setText("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <title>邮箱验证码</title>\n" +
                    "    \n" +
                    "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n" +
                    "    \n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "\t<div>\n" +
                    "\t\t<h3>亲爱的用户：</h3>\n" +
                    "\t\t<div style=\"text-indent:2em;\">\n" +
                    "\t\t\t<p>您好！感谢您使用大学英语四六级口语智能考试系统服务。您正在进行邮箱验证，请在验证码输入框中输入此次验证码<b style=\"font-size:18px;\">" + content + "</b>，以完成验证。<p>\n" +
                    "\t\t</div>\n" +
                    "\t\t\n" +
                    "\t\t<p>注：此验证码有效期为20分钟，过期未验证将失效。如非本人操作，请忽略此邮件，由此给您带来的不便请谅解。</p>\n" +
                    "\n" +
                    "\t\t<p>如果有疑问，请联系校管理员。</p>\n" +
                    "\n" +
                    "\t<div>\n" +
                    "\n" +
                    "  </body>\n" +
                    "</html>", true);

            ((JavaMailSender)mailSender).send(mimeMessage);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }

        return true;
    }


}
