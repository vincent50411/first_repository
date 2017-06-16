package com.iflytek.cetsim.controller;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.cetsim.base.controller.BaseController;

import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.base.service.ServiceException;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.context.SessionManagerContext;
import com.iflytek.cetsim.common.enums.RoleEnum;
import com.iflytek.cetsim.common.enums.UserStatusEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.StringUtils;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.dao.AccountMapper;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.Configuration;
import com.iflytek.cetsim.service.AccountService;
import com.iflytek.cetsim.service.ConfigurationService;
import com.iflytek.cetsim.service.EmailService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api/user")
public class AccountController extends BaseController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigurationService configurationService;

    private static final String EMAIL_CODE = "EMAIL_CODE";

    private static final String UPDATE_PASSWORD_USER_ACCOUNT = "UPDATE_PASSWORD_USER_ACCOUNT";

    /**
     * @param account 用户信息
     * @param password 页码
     * */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult login(String account, String password, HttpSession session)
    {
        //每次登录验证sessionID的一致性，如果sessionID不一致，则保留最后一次的，删除上一次的session并使之失效
        String loginSessionID = session.getId();

        JsonResult json = getJson();
        logger.info(String.format("开始登陆:[username]%s", account));
        try {
            if(StringUtils.isNullOrEmpty(account) || StringUtils.isNullOrEmpty(password)) {
                json.setFailMsg("用户名或密码为空.");
                logger.info("用户登录失败, 用户名或密码为空");
            }
            else {
                Account userInfo = accountService.login(account, password);
                if (userInfo == null)
                {
                    //新增一个查询条件，检查学生账号是否已经
                    Account eixtUser = accountService.findUserByAccount(account);

                    if(eixtUser != null)
                    {
                        json.setFailMsg("输入的密码错误");
                        logger.info("输入的密码错误");
                    }
                    else
                    {
                        json.setFailMsg("该账号不存在，请先注册");
                        logger.info("用户不存在");
                    }
                }
                else if(userInfo.getStatus() > 0)
                {
                    //每次登录验证sessionID的一致性，如果sessionID不一致，则保留最后一次的，删除上一次的session并使之失效（首次登陆）
                    if(userInfo.getSessionID() != null)
                    {
                        HttpSession oldSession = SessionManagerContext.getInstance().getSession(userInfo.getSessionID());

                        //如果是管理员或者教师角色，可以多账户登录
                        if(oldSession != null && userInfo.getRole() == RoleEnum.Student.getRoleCode())
                        {
                            //使session失效
                            oldSession.invalidate();
                        }
                    }

                    Account user_update = new Account();

                    user_update.setAccount(account);
                    user_update.setLastLogin(new Date());

                    //保存最新的sessionID
                    user_update.setSessionID(loginSessionID);

                    accountService.updateByPrimaryKeySelective(user_update);

                    userInfo.setLastLogin(user_update.getLastLogin());
                    json.setSucceed(userInfo, "登陆成功!");

                    if(session.getAttribute(Constants.LOGIN_USER_INFO) != null)
                    {
                        session.removeAttribute(Constants.LOGIN_USER_INFO);
                    }

                    session.setAttribute(Constants.LOGIN_USER_INFO, userInfo);
                    logger.info("登陆成功,获取用户信息:" + JSONObject.toJSONString(userInfo));
                }
                else if(userInfo.getStatus() == 0)
                {
                    json.setFailMsg("您的账号已被禁用，请联系管理员");
                    logger.info("用户登录失败, 账号已被禁用");
                }

            }
        } catch (ServiceException ex) {
            json.setFailMsg(ex.getMessage());
            return json;
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause() instanceof TooManyResultsException) {
                logger.error("列表中存在相同账号的用户,请联系管理员修复.相同账号:" + account, ex);
                json.setFailMsg("列表中存在相同账号的用户,请联系管理员修复.相同账号:" + account);
            } else {
                logger.error("获取用户列表信息报错!", ex);
                json.setFailMsg("获取用户列表信息报错!");
            }
        }
        return json;
    }

    /**
     * 每次用户登录成功后，检查用户信息必填项电话和邮箱是否有值，如果没有值，前台提示用户补充信息
     * @param account 用户信息
     * */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/checkUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult checkUserInfo(String account)
    {
        JsonResult json = getJson();

        Account userInfo = accountService.findUserInfoByAccount(account);

        if((userInfo.getTelephone() == null || userInfo.getTelephone().length() == 0 ) || (userInfo.getEmail() == null|| userInfo.getEmail().length() == 0))
        {
            json.setFailMsg("用户首次登录，请完善个人基本信息");
        }
        else
        {
            json.setSucceed("用户信息完整");
        }



        return json;
    }


    /**
     * 上传照片,
     * 图片存储规则：账号+
     * @param file
     * @param account
     * @param request
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/uploadphoto", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult uploadPhoto(@RequestParam("file") CommonsMultipartFile file, String account, HttpServletRequest request) {
        JsonResult json = getJson();
        try
        {
            String fileName = file.getOriginalFilename();

            String suffixList="jpg,png,gif,bmp,jpeg";
            //获取文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

            if(!suffixList.contains(suffix.trim().toLowerCase()))
            {
                //判断上传的文件后缀是否是图片类型，如果不是图片类型，提示文件不合法
                json.setFailMsg("上传文件类型不是图片类型，不能上传~");
                return json;
            }

            //上传到图片字节大于1M的，不允许上传
            if(file.getSize() > 1048576)
            {
                json.setFailMsg("上传的照片内容大于1M了，不能上传~");
                return json;
            }

            File path = Paths.get(request.getServletContext().getRealPath(Constants.PhotoPath)).toFile();
            if(!path.exists()) {
                path.mkdir();
            }

            //图像名称只用账号存储
            String photo = account + file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
            File destFile = Paths.get(request.getServletContext().getRealPath(Constants.PhotoPath),
                    photo).toFile();
            file.transferTo(destFile);
            try {
                BufferedImage image= ImageIO.read(destFile);
                if (image == null) {
                    json.setFailMsg("上传的照片不是图片格式！");
                    destFile.delete();
                    return json;
                }
            } catch(IOException ex) {
                json.setFailMsg("上传的照片不是图片格式！");
                return json;
            }

            accountService.uploadPhoto(account, photo);

            json.setSucceed(photo);
        } catch (Exception ex) {
            json.setFailMsg("接口调用失败!");
        }
        return json;
    }

    /**
     * 用户注销
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult logout(HttpServletRequest request) {
        JsonResult json = getJson();
        try {
            HttpSession session = request.getSession(true);
            Account loginUserInfo = (Account) session.getAttribute("loginUser");
            if(loginUserInfo != null) {
                session.setAttribute("loginUser", null);
                session.invalidate();
            }
        } catch (Exception ex) {
            json.setFailMsg("接口调用失败!");
        }
        return json;
    }

    /**
     * 查看照片
     * @param path, {account}.png
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/photo", method = RequestMethod.GET)
    @ResponseBody
    public void showPhoto(String path, HttpServletRequest request,HttpServletResponse response) {

        HttpHeaders headers = new HttpHeaders();
        try {
            File file = Paths.get(request.getServletContext().getRealPath(Constants.PhotoPath),
                    path).toFile();
            if(!file.isFile() || !file.exists()){
                file = Paths.get(request.getServletContext().getRealPath(Constants.PhotoPath),
                        "default.png").toFile();
            }
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());;
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(in, response.getOutputStream());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 查询用户详细信息，新需求需要根据不同角色返回不同的信息量
     * @param account
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/selectUserByAccount")
    @ResponseBody
    public JsonResult selectUserByAccount(String account, int role)
    {
        JsonResult result = new JsonResult();
        try
        {
            if(RoleEnum.Admin.getRoleCode() == role)
            {
                Account accountInfo =  accountService.findUserInfoByAccount(account);
                result.setSucceed(accountInfo);
            }
            else if(RoleEnum.Student.getRoleCode() == role)
            {
                StudentAccountDTO studentAccountDTO = accountService.findStudentByAccount(account);
                result.setSucceed(studentAccountDTO);
            }
            else if(RoleEnum.Teacher.getRoleCode() == role)
            {
                TeacherAccountDTO teacherAccountDTO = accountService.findTeacherByAccount(account);
                result.setSucceed(teacherAccountDTO);
            }
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 更新管理员信息
     * @param user
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/updateAdminByAccount")
    @ResponseBody
    public JsonResult updateAdminByAccount(Account user, String emailCode)
    {
        JsonResult result = new JsonResult();
        try
        {
            accountService.updateByPrimaryKeySelective(user);
            result.setSucceedMsg("更新成功");
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }

    /**
     * 更新教师信息
     * @param teacherAccountDTO 学生信息
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/updateTeacherByAccount")
    @ResponseBody
    public JsonResult updateTeacherByAccount(TeacherAccountDTO teacherAccountDTO)
    {
        JsonResult result = new JsonResult();

        try
        {
            accountService.updateTeacherByAccount(teacherAccountDTO);
            result.setSucceedMsg("更新成功");
        }
        catch (Exception ex)
        {
            result.setFailMsg("修改教师个人信息异常");
            ex.printStackTrace();
        }

        return result;
    }


    /**
     * 更新学生信息
     * @param studentAccountDTO 学生信息
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/updateStudentByAccount")
    @ResponseBody
    public JsonResult updateStudentByAccount(StudentAccountDTO studentAccountDTO)
    {
        JsonResult result = new JsonResult();

        try
        {
            int updateIndex = accountService.updateStudentByAccount(studentAccountDTO);
            if (updateIndex == 2)
                result.setFailMsg("修改学生个人信息中行政班级不应该为空格和特殊字符（除下划线_之外）");
            else if (updateIndex == 3)
                result.setFailMsg("修改的学生信息中的姓名不应该含有空格或特殊字符！");
            else
                result.setSucceedMsg("更新成功");
        }
        catch (Exception ex)
        {
            result.setFailMsg("修改学生个人信息异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 学生注册接口, 需要携带邮箱收到的验证码校验邮箱的合法性
     * @param studentAccountDTO
     * @emailCode 邮箱携带的验证码
     * @return
     */
    @RequestMapping("/studentRegister")
    @ResponseBody
    public JsonResult registerStudent(StudentAccountDTO studentAccountDTO, String emailCode, HttpSession session)
    {
        JsonResult result = new JsonResult();

        Configuration configuration = configurationService.selectByConfigName(Constants.IS_EMAIL_VALIDATE);
        //邮箱验证启用
        if(configuration != null && "1".equals(configuration.getConfigValue()))
        {
            //邮箱验证码进行验证
            if(emailCode == null || "".equals(emailCode))
            {
                result.setFailMsg("邮箱验证码不能为空~");
                return result;
            }

            String exitEmailCode = (String)session.getAttribute(EMAIL_CODE);

            if(!emailCode.equals(exitEmailCode))
            {
                result.setFailMsg("验证码不正确~");

                return result;
            }
        }

        //设置默认值
        studentAccountDTO.setRole(RoleEnum.Student.getRoleCode());
        studentAccountDTO.setStatus(UserStatusEnum.Enabled.getStatusCode().shortValue());
        studentAccountDTO.setCreateTime(new Date());

        try
        {
            int res = accountService.registerStudent(studentAccountDTO);

            if(res > 0)
            {
                result.setFailMsg("该学生账号已经存在，不能重复注册~");
            }
            else
            {
                result.setSucceedMsg("注册学生账号成功~");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            result.setFailMsg("注册学生账号失败");
        }
        finally {
            //需要清除缓存
            session.removeAttribute(EMAIL_CODE);
        }

        return result;
    }

    /**
     * 更新用户密码，根据所给的用户ID以及原密码和新密码
     *
     * @param userAccount
     * @param oldPass
     * @param newPass
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor", paramName = "userAccount")
    @RequestMapping(value = "/updatePassword")
    @ResponseBody
    public JsonResult updatePasswordByOldPassword(String userAccount, String oldPass, String newPass)
    {
        JsonResult result = new JsonResult();
        try {
            //判断用户以及原密码是否正确，用户ID不存在返回0，用户原密码错误返回1，正确返回2
            int passIsOrNotTrue = accountService.judgePasswordByUserId(userAccount, oldPass);
            Account account = new Account();
            //根据passIsOrNotTrue返回值设置不同的信息
            switch (passIsOrNotTrue)
            {
                case 0:
                    result.setFailMsg("所给用户的ID不存在！");
                    break;
                case 1:
                    result.setFailMsg("所给ID用户的原密码错误！");
                    break;
                case 2:
                    account.setPassword(newPass);
                    account.setAccount(userAccount);

                    accountService.updateAccountPassword(account);

                    result.setSucceedMsg("修改成功！");
                    break;
                default:
                    result.setFailMsg("变量passIsOrNotTrue返回值错误！");
            }
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }



    /**
     * 邮箱验证码验证来修改密码, 只有密码修改成功后，验证码才失效
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/updatePasswordByEmail")
    @ResponseBody
    public JsonResult updatePasswordByEmail(String userAccount, String newPass, String emailCode, HttpSession session)
    {
        JsonResult result = new JsonResult();
        try
        {
            Configuration configuration = configurationService.selectByConfigName(Constants.IS_EMAIL_VALIDATE);
            //邮箱验证启用
            if(configuration != null && "1".equals(configuration.getConfigValue()))
            {
                //如果没有设置邮箱启用状态或者不启用邮箱验证
                if(emailCode == null || "".equals(emailCode))
                {
                    //邮箱验证码进行验证
                    result.setFailMsg("邮箱验证码不能为空");
                    return result;
                }

                String exitEmailCode = (String)session.getAttribute(EMAIL_CODE);

                //修改用户密码的账号，需要和提交的账号对比
                String updatePasswordUserAccount = (String)session.getAttribute(UPDATE_PASSWORD_USER_ACCOUNT);

                if(!userAccount.equals(updatePasswordUserAccount))
                {
                    result.setFailMsg("用户账号输入不一致~");

                    return result;
                }

                if(!emailCode.equals(exitEmailCode))
                {
                    result.setFailMsg("验证码不正确");

                    return result;
                }
            }

            Account account = new Account();
            account.setPassword(newPass);
            account.setAccount(userAccount);

            accountService.updateAccountPassword(account);

            result.setSucceedMsg("修改成功！");
        }
        catch (Exception ex)
        {
            result.setFailMsg("通过邮箱修改密码异常");
            ex.printStackTrace();
        }
        finally
        {
            //需要清除缓存
            session.removeAttribute(EMAIL_CODE);

            session.removeAttribute(UPDATE_PASSWORD_USER_ACCOUNT);
        }

        return result;
    }

    /**
     * 获取验证码, 在修改个人信息和密码时
     * @param account
     * @param email
     * @param session
     * @return
     */
    @RequestMapping(value="/getValidateCodeByEmail",method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getValidateCodeByEmail(String account, String email, HttpSession session)
    {
        JsonResult result = this.getJson();

        try
        {
            if(email == null || "".equals(email))
            {
                result.setFailMsg("邮箱地址不能为空");

                return  result;
            }

            Account userInfo = accountService.findUserByAccount(account);

            if(userInfo == null)
            {
                result.setFailMsg("该账号不存在");

                return  result;
            }

            if(!email.equals(userInfo.getEmail()))
            {
                //如果输入的邮箱地址和注册时的邮箱地址不一致，则不能接受验证码
                result.setFailMsg("邮箱地址和注册时的邮箱地址不一致~");

                return  result;
            }

            String code = getRandNum(1, 999999).toString();

            //正则表达式的模式
            String ruleEmail = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
            Pattern p = Pattern.compile(ruleEmail);
            //正则表达式的匹配器
            Matcher m = p.matcher(email);
            //进行正则匹配
            if(!m.matches())
            {
                result.setFailMsg("邮箱地址不合法");
                return  result;
            }

            boolean rs = emailService.sendSimpleEmail(email, "邮箱验证码", code);

            if(rs)
            {
                //发送完成，session保存发送的验证码
                session.setAttribute(EMAIL_CODE, code);

                session.setAttribute(UPDATE_PASSWORD_USER_ACCOUNT, account);

                result.setSucceed("验证码发送成功");

                //设置定时器，20分钟后验证码失效
                emailInValidateTimer(session, "找回密码或者修改密码界面");

            }
            else
            {
                result.setFailMsg("发送邮件异常");
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("发送邮件异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 该接口不需要验证邮箱的一致性,只用来修改邮箱地址  (以及注册页面获取验证码)
     * @param email
     * @param session
     * @return
     */
//    @IflytekInterceptor(name="WebInterceptor", paramName = "userAccount")  不要拦截, 因为注册页面也要调用此接口获取验证码
    @RequestMapping(value="/getValidateCodeForUpdateEmail",method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getValidateCodeForUpdateEmail(String email, HttpSession session)
    {
        JsonResult result = this.getJson();

        try
        {
            if(email == null || "".equals(email))
            {
                result.setFailMsg("邮箱地址不能为空");

                return  result;
            }

            String code = getRandNum(1, 999999).toString();

            //正则表达式的模式
            String ruleEmail = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
            Pattern p = Pattern.compile(ruleEmail);
            //正则表达式的匹配器
            Matcher m = p.matcher(email);
            //进行正则匹配
            if(!m.matches())
            {
                result.setFailMsg("邮箱地址不合法");
                return  result;
            }

            boolean rs = emailService.sendSimpleEmail(email, "邮箱验证码", code);

            if(rs)
            {
                //发送完成，session保存发送的验证码
                session.setAttribute(EMAIL_CODE, code);

                result.setSucceed("验证码发送成功");

                //设置定时器，20分钟后验证码失效
                emailInValidateTimer(session, "注册界面或者个人信息修改界面");
            }
            else
            {
                result.setFailMsg("发送邮件异常");
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("发送邮件异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 验证邮箱有效性
     * @param account
     * @param emailCode
     * @param session
     * @return
     */
    @RequestMapping(value="/validateEmail",method = RequestMethod.GET)
    @ResponseBody
    public JsonResult validateEmail(String account, String emailCode, HttpSession session)
    {
        JsonResult result = new JsonResult();

        try
        {
            Configuration configuration = configurationService.selectByConfigName(Constants.IS_EMAIL_VALIDATE);
            //邮箱验证启用
            if(configuration != null && "1".equals(configuration.getConfigValue()))
            {
                //邮箱验证码进行验证
                if(emailCode == null || "".equals(emailCode))
                {
                    result.setFailMsg("邮箱验证码不能为空");
                    return result;
                }

                String exitEmailCode = (String)session.getAttribute(EMAIL_CODE);

                if(!emailCode.equals(exitEmailCode))
                {
                    result.setFailMsg("验证码不正确");

                    return result;
                }

                result.setSucceedMsg("邮箱验证成功");
            }

            result.setSucceedMsg("不启用邮箱验证");
        }
        catch (Exception ex)
        {
            result.setFailMsg("邮箱验证异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 获取邮箱验证状态，1：启用邮箱验证，0：不启用
     * @return
     */
    @RequestMapping(value = "/getEmailValidateState")
    @ResponseBody
    public JsonResult getEmailValidateState()
    {
        JsonResult result = new JsonResult();

        try
        {
            Configuration configuration = configurationService.selectByConfigName(Constants.IS_EMAIL_VALIDATE);

            if(configuration != null && "1".equals(configuration.getConfigValue()))
            {
                result.setSucceed("1");
            }
            else {
                result.setSucceed("0");
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取邮箱验证状态异常");
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 验证码为6位
     * @param min
     * @param max
     * @return
     */
    private Integer getRandNum(int min, int max)
    {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));

        String randNumStr = String.valueOf(randNum);

        if(randNumStr.length() < 6)
        {
            LogUtil.debug("--> errro email code :" + randNumStr);
            randNum = getRandNum(1, 999999);
        }

        return randNum;
    }

    /**
     * 邮箱验证码失效定时器
     * @param session
     */
    private void emailInValidateTimer(HttpSession session, String message)
    {

        Timer timer = new Timer();

        //20分钟延迟后执行任务
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                //需要清除缓存
                session.removeAttribute(EMAIL_CODE);

                session.removeAttribute(UPDATE_PASSWORD_USER_ACCOUNT);

                LogUtil.info("--> 邮箱验证码已经失效 [" + message + "]");
            }
        }, 1000 * 60 * 20);
    }

}
