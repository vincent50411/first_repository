package com.iflytek.cetsim.controller;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.base.service.ServiceException;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.RoleEnum;
import com.iflytek.cetsim.common.enums.UserStatusEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;
import com.iflytek.cetsim.service.AccountService;
import com.iflytek.cetsim.service.StudentMgrService;
import com.iflytek.cetsim.service.StudentsExamService;
import com.iflytek.cetsim.service.TeacherMgrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;


/**
 * Created by pengwang on 2017/3/13.
 */

@Controller
@RequestMapping("/admin")

public class AdminController extends BaseController
{

    @Autowired
    private AccountService accountService;

    @Autowired
    private TeacherMgrService teacherMgrService;

    @Autowired
    private StudentMgrService studentMgrService;

    @Autowired
    private StudentsExamService studentsExamService;

    /**
     * 添加教师
     *
     * @param teacherAccount
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/addTeacher")
    @ResponseBody
    public JsonResult addTeacher(TeacherAccountDTO teacherAccount) {
        JsonResult json = getJson();
        try {
            if (teacherAccount.getAccount().length() > 100){
                json.setFailMsg("添加的教师账号信息过长，请重新输入！");
                return json;
            }
            if (teacherAccount.getName().length() > 100){
                json.setFailMsg("添加的教师姓名信息过长，请重新输入！");
                return json;
            }

            //设置默认状态值
            if (teacherAccount.getStatus() == null)
            {
                teacherAccount.setStatus(UserStatusEnum.Enabled.getStatusCode().shortValue());
            }

            teacherAccount.setRole(RoleEnum.Teacher.getRoleCode());
            teacherAccount.setCreateTime(new Date());
            teacherAccount.setPassword(Constants.DEFAULT_PASSWORD);

            String reStr = teacherMgrService.addTeacher(teacherAccount);

            if(reStr.equals("exists"))
            {
                json.setFailMsg("该教师信息已经在教师列表中，不能重复导入哦~");
            }
            else if("existStudent".equals(reStr))
            {
                json.setFailMsg("该教师信息已经被分配给班级学生使用，不能重复导入哦~");
            }
            else
            {
                json.setSucceedMsg("添加成功");
            }
        }
        catch (Exception ex)
        {
            json.setFailMsg("添加失败");
        }
        return json;
    }

    /**
     * 批量上传教师
     *
     * @param file
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/addTeachers")
    @ResponseBody
    public JsonResult addTeachers(@RequestParam("file") CommonsMultipartFile file)
    {
        JsonResult json = getJson();

        try {

            //先检查上传文件的类型，如果不是execl格式文件，直接返回失败并提示
            String fileName = file.getFileItem().getName();

            if(!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))
            {
                json.setFailMsg("添加的文件不符合模板格式哦，请检查后重新导入~");

                return json;
            }

            String string = teacherMgrService.addBatchTeachers(file.getInputStream());

            if (string.equals("null")) {
                json.setFailMsg("该excel没有内容，请检查后重新导入！");
            }
            else if(string.equals("wrong")) {
                json.setFailMsg("导入的教师信息中存在不是教师列表中的内容,请检查后重新导入！");
            }
            else if(string.equals("classWrong")){
                json.setFailMsg("导入表格类型失败,导入的表格不符合教师表格式,请检查后重新导入！");
            }
            else if(string.equals("tempWrong")){
                json.setFailMsg("模板不是最新的,请检查后重新导入！");
            }
            else if(string.contains("#")){
                json.setFailMsg("导入的Excel表格中有重复的账号，请检查后再导入！重复的账号有：<br>"+string.substring(1,string.length()-1));
            }
            else if(string.contains("@")){
                json.setFailMsg("导入的Excel表格中的账号中含有空格或特殊字符,请检查后重新导入！含空格或特殊字符的账号有:<br>"+string.substring(1, string.length() - 1));
            }
            else if(string.contains("$")) {
                json.setFailMsg(string.substring(1,string.length()));
            }
            else if(string.equals("OK")){
                json.setSucceedMsg("批量添加成功");
            } else
            {
                json.setFailMsg("失败");
            }
        } catch (Exception ex) {
            json.setFailMsg("导入失败");
        }

        return json;
    }

    /**
     * 下载教师模板
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/downloadTeacherTemplate")
    public void downloadTeacherTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String basePath = request.getServletContext().getRealPath("");
        String templateFile = basePath + Constants.TeacherTemplatePath;
        File file = new File(templateFile);
        //判断文件类型
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        //文件名编码，解决乱码问题
        String fileName = file.getName();
        String encodedFileName = null;
        encodedFileName = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
        //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
        response.setHeader("Content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");
        //文件下载
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(in, response.getOutputStream());
    }

    /**
     * 查询教师
     *
     * @param user
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryTeachers")
    @ResponseBody
    public JsonResult query(AccountQueryDTO user)
    {
        JsonResult json = getJson();

        try {
            AccountExample example = new AccountExample();
            /* 设置分页条件 */
            example.setLimit(user.getPageSize());
            example.setOffset((user.getPageIndex() - 1) * user.getPageSize());

            /* 添加搜索过滤条件 */
            AccountExample.Criteria criteria1 = example.createCriteria();
            AccountExample.Criteria criteria2 = example.createCriteria();
            criteria1.andRoleEqualTo(RoleEnum.Teacher.getRoleCode());
            criteria2.andRoleEqualTo(RoleEnum.Teacher.getRoleCode());
            /* 根据DTO内容动态添加过滤条件 */
            if (user.getStatus() != null && user.getStatus() >= 0)
            {
                criteria1.andStatusEqualTo(user.getStatus().shortValue());
                criteria2.andStatusEqualTo(user.getStatus().shortValue());
            }
            if (user.getName() != null) {
                String name = user.getName().replace("%","/%").replace("_","/_");
                criteria1.andAccountLike("%" + name + "%");
                criteria2.andNameLike("%" + name + "%");
            }
            example.or(criteria2);

            //按照账号规则升序排列 教师账号规则：T+年份+自然数
            example.setOrderByClause("account asc");

            /* 调用service层接口 */
            PageModel<Account> pagedUsers = teacherMgrService.queryPage(example);

            json.setSucceed(pagedUsers, "操作成功!");
            logger.info("获取教师表户信息:" + JSONObject.toJSONString(pagedUsers));
        } catch (ServiceException ex) {
            json.setFailMsg(ex.getMessage());
            return json;
        } catch (Exception ex) {
            logger.error("获取教师表户信息报错!", ex);
            json.setFailMsg("获取教师表户信息报错!");
        }

        return json;
    }

    /**
     * 重置密码
     * @param account
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/resetPassword")
    @ResponseBody
    public JsonResult resetPassword(String account)
    {
        JsonResult result = new JsonResult();

        try
        {
            Account user = new Account();
            user.setPassword(Constants.DEFAULT_PASSWORD);
            user.setAccount(account);

            accountService.resetPassword(user);

            result.setSucceedMsg("");
        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }

    /**
     * 修改账号状态
     * @param account
     * @param status
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/setUserStatus")
    @ResponseBody
    public JsonResult setUserStatus(String account, Integer status)
    {
        JsonResult result = new JsonResult();

        try
        {
            Account user = new Account();
            user.setAccount(account);
            user.setStatus(status.shortValue());

            accountService.setUserStatus(user);

            result.setSucceedMsg("");
        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }


        return result;
    }

    /**
     * 添加学生
     *
     * @param account
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/addStudent")
    @ResponseBody
    public JsonResult addStudent(BaseAccountDTO account) {
        JsonResult json = getJson();

        return json;
    }

    /**
     * 批量添加学生
     *
     * @param file
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/addStudents")
    @ResponseBody
    public JsonResult addStudents(@RequestParam("file") CommonsMultipartFile file) {
        JsonResult json = getJson();

        return json;
    }

    /**
     * g管理员查询学生
     * @param studentQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryStudents")
    @ResponseBody
    public JsonResult queryStudents(StudentQueryDTO studentQueryDTO)
    {
        JsonResult result = new JsonResult();

        try
        {
            PageModel<StudentAccountDTO> model = studentMgrService.queryStudents(studentQueryDTO);

            result.setSucceed(model);
        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }

    /**
     * 学生所有考试记录
     * @param studentAccount
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/examAllRecordList")
    @ResponseBody
    public JsonResult GetExamAllRecord(String studentAccount, int pageIndex, int pageSize)
    {
        JsonResult result = this.getJson();
        try
        {
            PageModel<ExamRecordDTO_new> model = studentsExamService.listStudentAllRecords(studentAccount, pageIndex, pageSize);

            result.setSucceed(model);
        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


    /**
     * 导出测试详情
     * @param studentQueryDTO
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/exportExportDetail",produces = "application/octet-stream;charset=UTF-8")
    public void exportExamDetail(StudentQueryDTO studentQueryDTO, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException
    {
        String path = Paths.get(request.getServletContext().getRealPath(""), Constants.REPORT_PATH, UUID.randomUUID().toString()+".xlsx").toString();

        try
        {
            //判断session是否超时
            if(session == null || session.getAttribute(Constants.LOGIN_USER_INFO) == null)
            {
                //重定向到首页重新登录
                response.sendRedirect(Constants.REDIRECT_HOME);
                return;
            }

            //创建文件夹
            String dirPath = Paths.get(request.getServletContext().getRealPath(""), Constants.REPORT_PATH).toString();;
            File dirFile = new File(dirPath);
            if(!dirFile.exists())
            {
                dirFile.mkdir();
            }

            //生成待下载的execl文件
            studentMgrService.exportExamDetail(studentQueryDTO, path);

            File file = new File(path);
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null)
            {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            //设置文件响应大小
            //response.setContentLengthLong(file.length());
            //文件名编码，解决乱码问题
            String encodedFileName = null;
            encodedFileName = URLEncoder.encode("测试详情.xlsx", "utf-8").replaceAll("\\+", "%20");
            //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");
            //文件下载
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(in, response.getOutputStream());
        } catch (Exception ex) {
            logger.error("接口出错!", ex);
        }




    }

    @RequestMapping(value="/gradelist")
    @ResponseBody
    public JsonResult GetGradeList(){
        JsonResult result = this.getJson();

        return result;
    }

    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/getemptyclass")
    @ResponseBody
    public JsonResult GetEmptyClassList(@RequestParam("codeList[]")String[] codeList){
        JsonResult result = this.getJson();

        // TODO
        result.setSucceed(new ArrayList<>());
        return result;
    }

    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/getAdminInfo")
    @ResponseBody
    public JsonResult getAdminInfo()
    {
        JsonResult result = this.getJson();

        try
        {
            Account adminInfo = accountService.findUserByAccount("admin");

            result.setSucceed(adminInfo);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取帮助信息异常");
            ex.printStackTrace();
        }

        return result;
    }
}
