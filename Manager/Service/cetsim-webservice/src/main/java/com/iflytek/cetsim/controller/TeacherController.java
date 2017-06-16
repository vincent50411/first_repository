package com.iflytek.cetsim.controller;

import com.alibaba.fastjson.JSON;
import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.RoleEnum;
import com.iflytek.cetsim.common.enums.UserStatusEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.DateUtils;
import com.iflytek.cetsim.dao.*;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.*;
import com.iflytek.cetsim.service.CourseClassMgrService;
import com.iflytek.cetsim.service.StudentMgrService;
import com.iflytek.cetsim.service.TaskService;
import com.iflytek.cetsim.service.TeacherMgrService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.tomcat.util.bcel.Const;
import org.nutz.mvc.annotation.At;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
 * Created by Administrator on 2017/5/6.
 */
@Controller
@RequestMapping("/api/teacher")
public class TeacherController extends BaseController
{

    @Autowired
    private CourseClassMapper courseClassDao;
    @Autowired
    private CourseClassMgrService courseClassMgrService;
    @Autowired
    private AccountMapper accountDao;
    @Autowired
    private ClassStudentMapper classStudentDao;
    @Autowired
    private StudentMgrService studentMgrService;
    @Autowired
    private ExamPlanMapper planDao;
    @Autowired
    private PaperMapper paperDao;
    @Autowired
    private ExamTaskMapper examTaskMapper;
    @Autowired
    private TaskService taskService;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private TeacherMgrService teacherMgrService;

    /**
     * 获取班级列表 (用于发布测试的班级,班级人数不能为空)
     * @param account
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/roomlist",method= RequestMethod.GET)
    @ResponseBody
    public JsonResult GetClassList(String account)
    {
        JsonResult result = this.getJson();
        CourseClassExample example = new CourseClassExample();
        example.or().andTeacherAccountEqualTo(account);

        result.setSucceed(courseClassDao.selectClassExistStudentsOfTeacher(account));
        return result;
    }


    /*
  查询班级
   */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/queryCourseClass")
    @ResponseBody
    public JsonResult queryCourseClass(String teacherCode, int pageIndex, int pageSize) {
        JsonResult result = this.getJson();
        try {
            List<CourseClassDTO> courseClassDTOs = courseClassMgrService.listTeacherCourseClass(teacherCode, pageIndex, pageSize);
            PageModel<CourseClassDTO> model = new PageModel<>(pageIndex,pageSize);
            int total = courseClassMgrService.countTeacherCourseClass(teacherCode);
            model.setData(courseClassDTOs);
            model.setTotal(total);
            result.setSucceed(model);
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


    /*
 创建班级
  */
    //TODO
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/createCourseClass")
    @ResponseBody
    public JsonResult createCourseClass(CourseClass courseClass) {
        JsonResult result = this.getJson();
        try {
            //先判断所创建的班级名称是否存在, 一个教师下班级名称不能重复, 不同教师创建的班级名称可以重复
            CourseClassExample courseClassExample = new CourseClassExample();
            courseClassExample.or().andNameEqualTo(courseClass.getName()).andCreaterEqualTo(courseClass.getCreater());
            List<CourseClass> list = courseClassDao.selectByExample(courseClassExample);
            if (list.size() > 0) {
                result.setFailMsg("该班级名称已存在，请重新填写班级名称！");
                return result;
            }

            courseClass.setCreateTime(new Date());
            //获取当前时间的年份
            int year = Calendar.getInstance().get(Calendar.YEAR);
            //在数据库中查询最大的班级号code
            String maxCode = courseClassMgrService.getMaxClassCode();
            //maxCode不为空的话并且班级号中的年份符合当前年份
            if ((maxCode != null) && (maxCode.substring(1,5).contains((""+year))))
            {
                String subS = maxCode.substring(1,maxCode.length());
                subS = "C"+(Integer.parseInt(subS)+1);
                courseClass.setCode(subS);
            }
            else
            {
                //maxCode为空的话表示还没有班级code被创建，或获取的maxCode中的年份不符合当前年份
                courseClass.setCode("C"+year+"0001");
            }
            courseClassDao.insertSelective(courseClass);
            result.setSucceedMsg("创建成功");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result.setFailMsg("创建班级异常，请稍后重试~");
        }
        return result;
    }

    /*
    查询班级学生
    */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/queryCourseClassStudents")
    @ResponseBody
    public JsonResult queryCourseClassStudents(String classCode, String conditionValue, Integer pageIndex, Integer pageSize) {
        JsonResult result = this.getJson();
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("class_code", classCode);
            paramMap.put("limit", pageSize);
            paramMap.put("offset", (pageIndex - 1) * pageSize);

            if(conditionValue != null && !"".equals(conditionValue))
            {
                paramMap.put("like_value", "%" + conditionValue + "%");
            }

            //增加排序，以新加入班级的id降序排列，最新加入到学生放在最前面排列
            //example.setOrderByClause("sim_class_student.id desc");

            List<StudentDTO> stus = classStudentDao.selectClassStudentsDetailInfo(paramMap);

            int total = classStudentDao.countByCondition(paramMap);
            PageModel<StudentDTO> model = new PageModel<StudentDTO>(pageIndex,pageSize);
            model.setData(stus);
            model.setPageIndex(pageIndex);
            model.setPageSize(pageSize);
            model.setTotal(total);
            result.setSucceed(model);
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


    @IflytekInterceptor(name="WebInterceptor", paramName = "studentAccount")
    @RequestMapping("/selectStudentByAccount")
    @ResponseBody
    public JsonResult selectStudentBystudentAccount(String studentAccount) {
        JsonResult result = new JsonResult();
        try {
            Account studentInfo =  accountDao.selectByPrimaryKey(studentAccount);
            result.setSucceed(studentInfo);
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /*
    下载模板
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/downloadStuTemplate")
    public void downloadStuTempalte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String basePath = request.getServletContext().getRealPath("");
        String templateFile = basePath+Constants.StudentTemplatePath;
        File file = new File(templateFile);
        //判断文件类型
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        //设置文件响应大小
//        response.setContentLengthLong(file.length());
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

    /*
    添加学生
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/addStudent")
    @ResponseBody
    public JsonResult addStudent(String classCode, Account account) {
        JsonResult result = this.getJson();
        try {
            if (account.getAccount().length() > 100){
                result.setFailMsg("添加的学生账号信息过长，请重新输入！");
                return result;
            }
            if (account.getName().length() > 100){
                result.setFailMsg("添加的学生姓名信息过长，请重新输入！");
                return result;
            }
            account.setCreateTime(new Date());
            account.setRole(RoleEnum.Student.getRoleCode());
            account.setStatus(UserStatusEnum.Enabled.getStatusCode());
            account.setPassword(Constants.DEFAULT_PASSWORD);
            String reStr = studentMgrService.addStudentToCourseClass(classCode, account);
            if(reStr.equals("exists"))
            {
                result.setFailMsg("该学生信息已添加到此班级中，不需要再次添加了喔~");
            }
            else if("used".equals(reStr))
            {
                result.setFailMsg("该学生账号已经被其他人使用，不要再次添加了喔~");
            }
            else if(reStr.equals("null")){
                result.setSucceedMsg("添加成功");
            }else{
                result.setSucceedMsg("失败了");
            }
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 从班级中移除学生
     * @param classCode
     * @param account
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/deleteStudentFromCourseClass")
    @ResponseBody
    public JsonResult deleteStudentFromCourseClass(String classCode,String account){
        JsonResult result = new JsonResult();
        try{
            ClassStudentExample example = new ClassStudentExample();
            ClassStudentExample.Criteria criteria = example.createCriteria();
            criteria.andClassCodeEqualTo(classCode);
            criteria.andStudentAccountEqualTo(account);
            classStudentDao.deleteByExample(example);
            result.setSucceedMsg("");
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取教师的任务分页列表
     * @param account
     * @param pageIndex
     * @param pageSize
     * @param paperType 试卷类型
     * @param name 试卷名称（模糊查询）
     * @param beginTime 计划开始时间  2017-05-06 12：12：12
     * @param endTime   计划结束时间
     * @param status    计划执行状态
     * @param orderColumnName
     * @param orderCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/tasklist",method= RequestMethod.GET)
    @ResponseBody
    public JsonResult GetTaskList(String account,int pageIndex,int pageSize, String paperType, String name, String beginTime, String endTime,
                                  String status, String orderColumnName, String orderCode)
    {
        JsonResult result = this.getJson();

        PageModel<TeacherTaskDTO> pageModel = new PageModel<>(pageIndex,pageSize);

        //服务器当前时间
        Date currentServerDate = new Date();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if(paperType != null && !"".equals(paperType))
        {
            paramMap.put("paperType", paperType);
        }

        if(name != null && !"".equals(name))
        {
            paramMap.put("name", "%" + name + "%");
        }

        if(beginTime != null && !"".equals(beginTime))
        {
            paramMap.put("beginTime", DateUtils.parseDate(beginTime));
        }

        if(endTime != null && !"".equals(endTime))
        {
            paramMap.put("endTime", DateUtils.parseDate(endTime));
        }

        paramMap.put("publisher", account);

        Map<String, Object> dateParam = new HashMap<String, Object>();
        dateParam.put("currentTime", currentServerDate);

        //根据输入的状态查询
        if("0".equals(status))
        {
            //未开始
            paramMap.put("unStart", dateParam);
        }
        else if("1".equals(status))
        {
            //进行中, 当前服务器时间处于计划开始时间和计划结束时间之间
            paramMap.put("examing", dateParam);
        }
        else if("2".equals(status))
        {
            //已结束, 当前服务器时间已经超过计划结束时间
            paramMap.put("examEnd", dateParam);
        }

        pageModel.setTotal(planDao.countDetailByExample(paramMap));

        paramMap.put("limit", pageSize);
        paramMap.put("offset", (pageIndex - 1) * pageSize);

        if(orderColumnName != null && orderCode != null)
        {
            paramMap.put(orderColumnName.toUpperCase() + "_" + orderCode.toUpperCase(), "true");
        }
        else
        {
            paramMap.put("order_condition_default", "true");
        }

        pageModel.setData(planDao.queryPlanDetail(paramMap));
        result.setSucceed(pageModel);
        return result;
    }


    /**
     * 根据plan的code获取已发布的测试的对应的班级列表
     * @param plan_code
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/getPlanClassList")
    @ResponseBody
    public JsonResult getPlanClassList(String plan_code)
    {
        JsonResult result = this.getJson();

        Map<String, Object> map = new HashedMap();
        map.put("plan_code", plan_code);

        result.setSucceed(planDao.getPlanClassList(map));

        return result;
    }


    /**
     * 获取试卷分页列表，用于选择发布计划的试卷
     * @param pageIndex
     * @param pageSize
     * @param paperTypeCode  cet4  cet6
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/paperlist",method= RequestMethod.GET)
    @ResponseBody
    public JsonResult GetPaperList(String paperName, int pageIndex,int pageSize, String paperTypeCode)
    {
        JsonResult result = this.getJson();
        Map<String, Object> map = new HashMap<>();
        map.put("offset", (pageIndex - 1) * pageSize);
        map.put("limit", pageSize);

        map.put("allowPlanUseage", true);

        if(paperName != null && !"".equals(paperName))
        {
            map.put("paperName", "%" + paperName + "%");
        }

        if(paperTypeCode != null && !"".equals(paperTypeCode))
        {
            map.put("paperTypeCode", paperTypeCode);
        }

        Map<String, Object> countMap = new HashedMap();
        countMap.put("allowPlanUseage", true);

        PageModel<PaperInfoDTO> pageModel = new PageModel<PaperInfoDTO>(pageIndex,pageSize);

        pageModel.setPageIndex(pageIndex);
        pageModel.setPageSize(pageSize);

        List<PaperInfoDTO> paperList = paperDao.getPapersByHashMap(map);
        pageModel.setData(paperList);
        pageModel.setTotal(paperDao.getPapersCountByHashMap(map));

        result.setSucceed(pageModel);
        return result;
    }

    /**
     * 发布计划
     * @param _plan
     * @param _papers
     * @param _rooms
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/publish",method= RequestMethod.POST)
    @ResponseBody
    @Transactional
    public JsonResult PublishExamPlan(String _plan, String _papers, String _rooms)
    {
        JsonResult result = this.getJson();
        try {
            ExamPlan plan = JSON.parseObject(_plan, ExamPlan.class);
            String[] papers = JSON.parseObject(_papers, String[].class);
            String[] rooms = JSON.parseObject(_rooms, String[].class);
            if(plan.getEndTime().getTime() < plan.getStartTime().getTime())
            {
                result.setFailMsg("考试计划结束时间必须在开始时间之前！");
                return result;
            }
            if (papers.length == 0) {
                result.setFailMsg("未选择试卷！");
                return result;
            }
            if (rooms.length == 0) {
                result.setFailMsg("未选择班级！");
                return result;
            }
            taskService.publishTask(plan, rooms, papers);
            result.setSucceed(true);
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 删除计划
     * @param plan_code
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/deletePlan")
    @ResponseBody
    @Transactional
    public JsonResult deletePlan(String plan_code)
    {
        JsonResult result = this.getJson();
        try {
            planDao.deleteByPlanCode(plan_code);

            examTaskMapper.deleteByPlanCode(plan_code);
            result.setSucceed(null);
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }



    /**
     * 批量上传学生
     * @param classCode 班级代码
     * @param file
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/addStudents")
    @ResponseBody
    public JsonResult addBatchStudents(String classCode, @RequestParam("file") CommonsMultipartFile file) {
        JsonResult result = this.getJson();
        try {
            //先检查上传文件的类型，如果不是execl格式文件，直接返回失败并提示
            String fileName = file.getFileItem().getName();

            if(!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))
            {
                result.setFailMsg("添加的文件不符合模板格式哦，请检查后重新导入~");

                return result;
            }

            String string = studentMgrService.addBatchStudentsToCourseClass(classCode, file.getInputStream());
            if (string.equals("null")) {
                result.setFailMsg("该excel没有内容，请检查后重新导入！");
            }
            else if(string.equals("wrong")) {
                result.setFailMsg("导入的学生信息中存在不是学生列表中的内容,请检查后重新导入！");
            }
            else if(string.equals("classWrong")){
                result.setFailMsg("导入的学生的模板格式不符合模板规则，请重新下载模板或者导入正确的文件！");
            }
            else if(string.equals("oldTable")){
                result.setFailMsg("导入的学生模版是之前的老模板（不含班级），请下载最新的模板哦~");
            }
            else if(string.contains("#")) {
                result.setFailMsg("导入的Excel表格中有重复的账号，请检查后再导入！重复的账号有：<br>" + string.substring(1, string.length() - 1));
            }
            else if(string.contains("@")){
                result.setFailMsg("导入的Excel表格中的账号中含有空格或特殊字符,请检查后重新导入！含空格或特殊字符的账号有:<br>"+string.substring(1, string.length() - 1));
            }
            else if(string.contains("$")) {
                result.setFailMsg(string.substring(1,string.length()));
            }
            else if(string.equals("OK")){
                result.setSucceedMsg("批量添加成功");
            }
            else {
                result.setSucceedMsg("失败");
            }
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
            result.setFailMsg("导入学生异常～");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            result.setFailMsg("导入学生异常～");
        }
        return result;
    }





    /**
     * 导出班级学生成绩
     * @param planCode
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/exportscore", method = RequestMethod.GET,produces = "application/octet-stream;charset=UTF-8")
    public void exportScore(String planCode, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{

        String path = Paths.get(request.getServletContext().getRealPath(""), UUID.randomUUID().toString()+".xlsx").toString();
        try
        {
            //判断session是否超时
            if(session == null || session.getAttribute(Constants.LOGIN_USER_INFO) == null)
            {
                //重定向到首页重新登录
                response.sendRedirect(Constants.REDIRECT_HOME);
                return;
            }

            taskService.exportTask(planCode, path);

            File file = new File(path);
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            //设置文件响应大小
            //response.setContentLengthLong(file.length());
            //文件名编码，解决乱码问题
            String encodedFileName = null;
            encodedFileName = URLEncoder.encode("导出成绩列表.xlsx", "utf-8").replaceAll("\\+", "%20");
            //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");
            //文件下载
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(in, response.getOutputStream());
        } catch (Exception ex) {
            logger.error("接口出错!", ex);
        }
    }

    /**
     * 导出模拟测试任务完成情况
     * @param planCode
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/exportfinish", method = RequestMethod.GET,produces = "application/octet-stream;charset=UTF-8")
    public void exportFinish(String planCode, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{

        String path = Paths.get(request.getServletContext().getRealPath(""), UUID.randomUUID().toString()+".xlsx").toString();
        try
        {
            //判断session是否超时
            if(session == null || session.getAttribute(Constants.LOGIN_USER_INFO) == null)
            {
                //重定向到首页重新登录
                response.sendRedirect(Constants.REDIRECT_HOME);
                return;
            }

            taskService.exportFinish(planCode, path);

            File file = new File(path);
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            //设置文件响应大小
            //response.setContentLengthLong(file.length());
            //文件名编码，解决乱码问题
            String encodedFileName = null;
            encodedFileName = URLEncoder.encode("导出成绩列表.xlsx", "utf-8").replaceAll("\\+", "%20");
            //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");
            //文件下载
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(in, response.getOutputStream());
        } catch (Exception ex) {
            logger.error("接口出错!", ex);
        }
    }

    /**
     * 获取任务的信息
     * @param planCode 计划代码
     * @returnstudentmonitor
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/taskinfo",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult GetTaskInfo(String planCode, String taskCode){
        JsonResult result = this.getJson();
        try
        {
            Map<String, String> map = new HashMap<>();
            map.put("planCode", planCode);
            map.put("taskCode", taskCode);
            result.setSucceed(examRecordMapper.listExamTaskInfo(map));
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取测试任务详情信息异常");
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 获取分数分布图(饼状图)
     * @param plantCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/scoredistribution",method=RequestMethod.GET)
    @ResponseBody
    public JsonResult getScoreDistribution(String plantCode)
    {
        JsonResult result = this.getJson();

        try
        {
            PaperInfoDTO paperInfoDTO = taskService.queryPaperInfoByTaskCode(plantCode);

            List<ScoreLevel> levels = ScoreLevelUtil.getLevels(paperInfoDTO.getPaperTypeCode());
            ArrayList<KVDTO> list = new ArrayList<>();
            for (ScoreLevel level : levels)
            {
                HashMap<String, Object> map = new HashMap<>();

                if(-999 == level.getMin())
                {
                    //未考的
                    map.put("nullScore", "is null");
                }
                else
                {
                    map.put("min", level.getMin());
                    map.put("max", level.getMax());
                }

                map.put("plantCode", plantCode);
                KVDTO kvdto = new KVDTO();
                kvdto.setK(level.getName());

                kvdto.setV(taskService.countScoreDistribution(map));

                list.add(kvdto);
            }

            result.setSucceed(list);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取成绩分布图异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 测试任务详情
     * @param planCode 老师查看测试计划报告，包含多个测试任务
     * @param name 模糊查询
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/taskdetail")
    @ResponseBody
    public JsonResult getTaskDetail(String planCode, String name, Integer pageIndex, Integer pageSize)
    {
        JsonResult result = this.getJson();

        try
        {
            Map paramMap = new HashMap();

            paramMap.put("planCode", planCode);

            if (name != null && !"".equals(name))
            {
                paramMap.put("name", "%" + name + "%");
            }

            int stusCount = examTaskMapper.countTaskStudent(paramMap);

            paramMap.put("limit", pageSize);
            paramMap.put("offset", (pageIndex - 1) * pageSize);

            List<StudentDTO> stus = teacherMgrService.listTaskStudent(paramMap);

            PageModel<StudentDTO> model = new PageModel<>(pageIndex,pageSize);
            if(stus != null && stus.size() > 0)
            {
                model.setTotal(stusCount);
            }
            else
            {
                model.setTotal(0);
            }

            model.setData(stus);
            result.setSucceed(model);
        }
        catch(Exception ex)
        {
            result.setFailMsg("获取所有学生成绩列表异常");
            ex.printStackTrace();
        }

        return result;
    }
}
