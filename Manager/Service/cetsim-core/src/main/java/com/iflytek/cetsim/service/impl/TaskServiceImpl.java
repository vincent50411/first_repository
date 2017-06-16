package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.enums.ExamPlanStatus;
import com.iflytek.cetsim.common.enums.PaperAllowPlanUsageEnum;
import com.iflytek.cetsim.common.enums.PaperStatusEnum;
import com.iflytek.cetsim.common.utils.CommonUtil;
import com.iflytek.cetsim.dao.*;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.StudentDTO;
import com.iflytek.cetsim.model.*;
import com.iflytek.cetsim.service.TaskService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.util.*;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ExamPlanMapper planDao;
    @Autowired
    private PaperMapper paperDao;
    @Autowired
    private ExamTaskMapper examTaskMapper;
    @Autowired
    private CourseClassMapper courseClassDao;


    @Override
    public void publishTask(ExamPlan plan, String[] rooms, String[] papers) throws Exception {
        if (plan.getCode() != null) { // 更新plan的操作
            planDao.updateByPrimaryKeySelective(plan);
        } else {    // 新增一条记录plan
            plan.setCreateTime(new Date());
            plan.setCode(UUID.randomUUID().toString());
            plan.setStatus(ExamPlanStatus.ENABLED.getCode());
            planDao.insert(plan);
        }


        if (plan.getCode() != null) { // 如果是更新plan操作, 删除原来已有的与plan关联的task, 然后重新创建task记录
            examTaskMapper.deleteByPlanCode(plan.getCode());
        }

        Map<String, Object> map = new HashedMap();
        map.put("codeList", papers);
        List<Paper> paperList = paperDao.getPapersByCodeList(map);

        CourseClassExample courseClassExample = new CourseClassExample();
        courseClassExample.or().andCodeIn(Arrays.asList(rooms));
        List<CourseClass> courseClasses = courseClassDao.selectByExample(courseClassExample);


        for (Paper paper : paperList) {
            if(paper.getStatus() == PaperStatusEnum.DISABLED.getStatusCode()
                    || paper.getAllowPlanUseage() != null && paper.getAllowPlanUseage() == PaperAllowPlanUsageEnum.DISABLED.getAllowPlanUsageCode()){
                throw new Exception("试卷被禁用或没有权限使用！");
            }
        }

        for (Paper paper : paperList) {
            for (CourseClass courseClass : courseClasses) {
                ExamTask task = new ExamTask();
                task.setCode(UUID.randomUUID().toString());
                task.setName(paper.getName());
                task.setPlanCode(plan.getCode());
                task.setClassCode(courseClass.getCode());
                task.setPaperCode(paper.getCode());
                examTaskMapper.insert(task);
            }
        }
    }

    /**
     * 导出任务的成绩
     * @param planCode 模拟测试计划代码
     * @param path
     */
    public void exportTask(String planCode, String path)
    {
        try
        {
            Map<String, Object> queryPaperMap = new HashMap<String, Object>();
            queryPaperMap.put("planCode", planCode);

            PaperInfoDTO paperInfoDTO = examTaskMapper.queryPaperInfoByTaskCode(queryPaperMap);

            String paperType = null;
            if(paperInfoDTO != null)
            {
                paperType = paperInfoDTO.getPaperTypeCode();
            }

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("planCode", planCode);
            paramMap.put("examState", 1);

            //根据模拟测试任务代码查询所有学生列表
            List<StudentDTO> studentDTOs = examTaskMapper.teacherQueryTaskStudentList(paramMap);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("考生成绩");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("学号");
            row.createCell(1).setCellValue("姓名");
            row.createCell(2).setCellValue("性别");
            row.createCell(3).setCellValue("年级");
            row.createCell(4).setCellValue("专业");
            row.createCell(5).setCellValue("学院");
            row.createCell(6).setCellValue("班级");
            row.createCell(7).setCellValue("联系方式");
            row.createCell(8).setCellValue("试卷名称");
            row.createCell(9).setCellValue("测试次数");
            row.createCell(10).setCellValue("最高分");
            row.createCell(11).setCellValue("平均分");
            for (int j = 0; j < studentDTOs.size(); j++) {
                StudentDTO student = studentDTOs.get(j);
                row = sheet.createRow(j + 1);
                row.createCell(0).setCellValue(student.getAccount() == null ? "-" : student.getAccount());
                row.createCell(1).setCellValue(student.getName() == null ? "-" : student.getName());
                if(student.getGender() != null)
                {
                    row.createCell(2).setCellValue(student.getGender() == 0 ? "男" :"女");
                }
                else
                {
                    row.createCell(2).setCellValue("-");
                }

                row.createCell(3).setCellValue(student.getGrade() == null ? "-" : student.getGrade());
                row.createCell(4).setCellValue(student.getMajor() == null ? "-" : student.getMajor());
                row.createCell(5).setCellValue(student.getInstitution() == null ? "-" : student.getInstitution());
                row.createCell(6).setCellValue(student.getClassName() == null ? "-" : student.getClassName());
                row.createCell(7).setCellValue(student.getTelephone() == null ? "-" : student.getTelephone());
                row.createCell(8).setCellValue(student.getPaperName() == null ? "-" : student.getPaperName());
                row.createCell(9).setCellValue(student.getExam_count());
                if(student.getExam_count() > 0) {
                    if(student.getMax_score() > 0)
                    {
                        row.createCell(10).setCellValue(CommonUtil.scoreFormatter(student.getMax_score()));
                    }
                    else
                    {
                        row.createCell(10).setCellValue("0");
                    }
                    if(student.getAverage_score() > 0)
                    {
                        row.createCell(11).setCellValue(CommonUtil.scoreFormatter(student.getAverage_score()));
                    }
                    else
                    {
                        row.createCell(11).setCellValue("0");
                    }
                }
                else {
                    row.createCell(10).setCellValue("未考");
                    row.createCell(11).setCellValue("未考");
                }
            }
            FileOutputStream output = new FileOutputStream(path);
            workbook.write(output);
            output.flush();
            output.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 根据测试任务查询试卷信息
     * @param plantCode
     * @return
     */
    public PaperInfoDTO queryPaperInfoByTaskCode(String plantCode)
    {
        Map<String, Object> queryPaperMap = new HashMap<String, Object>();
        queryPaperMap.put("plantCode", plantCode);

        PaperInfoDTO paperInfoDTO = examTaskMapper.queryPaperInfoByTaskCode(queryPaperMap);

        return paperInfoDTO;
    }

    /**
     * 导出模拟测试任务完成情况
     * @param planCode
     * @param path
     */
    public void exportFinish(String planCode, String path)
    {
        try
        {
            PaperInfoDTO paperInfoDTO = queryPaperInfoByTaskCode(planCode);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("planCode", planCode);
            paramMap.put("examState", 1);

            //根据模拟测试任务代码查询所有学生列表
            List<StudentDTO> studentDTOs = examTaskMapper.teacherExportTaskStudentList(paramMap);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("导出成绩列表");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("学号");
            row.createCell(1).setCellValue("姓名");
            row.createCell(2).setCellValue("性别");
            row.createCell(3).setCellValue("学院");
            row.createCell(4).setCellValue("专业");
            row.createCell(5).setCellValue("年级");
            row.createCell(6).setCellValue("班级");
            row.createCell(7).setCellValue("测试班级名称");
            row.createCell(8).setCellValue("联系方式");
            row.createCell(9).setCellValue("测试次数");
            //row.createCell(10).setCellValue("平均成绩");
            row.createCell(10).setCellValue("最高成绩");

            for (int j = 0; j < studentDTOs.size(); j++) {
                StudentDTO student = studentDTOs.get(j);
                row = sheet.createRow(j + 1);
                row.createCell(0).setCellValue(student.getAccount() == null ? "-" : student.getAccount());
                row.createCell(1).setCellValue(student.getName() == null ? "-" : student.getName());
                if(student.getGender() != null)
                {
                    row.createCell(2).setCellValue(student.getGender() == 0 ? "男":"女");
                }
                else
                {
                    row.createCell(2).setCellValue("-");
                }

                row.createCell(3).setCellValue(student.getInstitution() == null ? "-" : student.getInstitution());
                row.createCell(4).setCellValue(student.getMajor() == null ? "-" : student.getMajor());
                row.createCell(5).setCellValue(student.getGrade() == null ? "-" : student.getGrade());
                row.createCell(6).setCellValue(student.getClassName() == null ? "-" : student.getClassName());
                row.createCell(7).setCellValue(student.getTaskClassName());
                row.createCell(8).setCellValue(student.getTelephone() == null ? "-" : student.getTelephone());

                row.createCell(9).setCellValue(student.getExam_count());

                if(student.getExam_count() > 0)
                {
                    if(student.getMax_score() > 0)
                    {
                        row.createCell(10).setCellValue(CommonUtil.scoreFormatter(student.getMax_score()));
                    }
                    else
                    {
                        row.createCell(10).setCellValue("0");
                    }
                }
                else
                {
                    row.createCell(10).setCellValue("未考");
                }
            }

            FileOutputStream output = new FileOutputStream(path);
            workbook.write(output);
            output.flush();
            output.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /**
     * 根据任务代码和分数区段，统计一个成绩区段内的考生人数
     * @param map
     * @return
     */
    public int countScoreDistribution(HashMap<String,Object> map)
    {
        return examTaskMapper.countScoreDistribution(map);
    }



}
