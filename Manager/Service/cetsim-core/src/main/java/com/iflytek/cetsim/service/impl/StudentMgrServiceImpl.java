package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.common.enums.RoleEnum;
import com.iflytek.cetsim.common.utils.DateUtils;
import com.iflytek.cetsim.dao.AccountMapper;
import com.iflytek.cetsim.dao.ClassStudentMapper;
import com.iflytek.cetsim.dao.StudentPieceMapper;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;
import com.iflytek.cetsim.model.ClassStudent;
import com.iflytek.cetsim.model.ClassStudentExample;
import com.iflytek.cetsim.service.StudentMgrService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/5/6.
 */
@Service
@Transactional
public class StudentMgrServiceImpl implements StudentMgrService
{

    @Autowired
    private StudentPieceMapper studentPieceMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ClassStudentMapper classStudentMapper;

    /**
     * 获取自己所在的所有测试班级列表
     * @param studentAccount
     * @return
     */
    public List<StudentClassDTO> queryMyAllTestClassList(String studentAccount)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("studentAccount", studentAccount);

        return classStudentMapper.queryMyAllTestClassList(paramMap);
    }

    /**
     * 管理员查询全校学生
     *
     * @param studentQueryDTO
     * @return
     */
    @Override
    public PageModel<StudentAccountDTO> queryStudents(StudentQueryDTO studentQueryDTO)
    {
        PageModel<StudentAccountDTO> model = null;
        try
        {
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("limit", studentQueryDTO.getPageSize());
            paramMap.put("offset", (studentQueryDTO.getPageIndex() - 1) * studentQueryDTO.getPageSize());

            paramMap.put("role", RoleEnum.Student.getRoleCode());

            paramMap.put("status", studentQueryDTO.getStatus());
            paramMap.put("grade", studentQueryDTO.getGrade());
            paramMap.put("institution", studentQueryDTO.getInstitution());

            if(studentQueryDTO.getName() != null)
            {
                paramMap.put("likeValue", "%" + studentQueryDTO.getName() + "%");
            }


            //自定义查询条件
            List<StudentAccountDTO> data = studentPieceMapper.selectStudentByCondition(paramMap);

            int total = studentPieceMapper.selectStudentAcountByCondition(paramMap);

            model = new PageModel<>(studentQueryDTO.getPageIndex(), studentQueryDTO.getPageSize());
            model.setData(data);
            model.setTotal(total);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return model;
    }


    /**
     * 班级添加学生
     *
     * @param classCode
     * @param account
     */
    @Override
    public String addStudentToCourseClass(String classCode, Account account)
    {
        String string = "null";

        //判断该账号是否已经被其他用户（教师或者管理员或者学生）占用
        AccountExample example = new AccountExample();
        AccountExample.Criteria criteria = example.createCriteria();
        criteria.andAccountEqualTo(account.getAccount());
        criteria.andRoleNotEqualTo(RoleEnum.Student.getRoleCode());

        //除教师和管理员外
        int userCount = accountMapper.countByExample(example);

        if(userCount > 0)
        {
            //账号已经被使用
            return "used";
        }

        ClassStudentExample example2 = new ClassStudentExample();
        ClassStudentExample.Criteria criteria2 = example2.createCriteria();
        criteria2.andClassCodeEqualTo(classCode);
        criteria2.andStudentAccountEqualTo(account.getAccount());
        int count = classStudentMapper.countByExample(example2);

        //判断该账号是否存在班级中，如果不存在，则绑定到班级
        if (count == 0)
        {
            ClassStudent classStudent = new ClassStudent();
            classStudent.setClassCode(classCode);
            classStudent.setStudentAccount(account.getAccount());
            classStudent.setCreateTime(new Date());
            //将学生添加到班级信息中
            classStudentMapper.insertSelective(classStudent);
        }else
        {
            string = "exists";
        }

        return string;
    }

    /**
     * 班级批量添加学生
     * @param classCode 班级代码
     * @param is
     * @throws IOException
     */
    public String addBatchStudentsToCourseClass(String classCode, InputStream is) throws IOException
    {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

        //通过nullNum的值来判断Excel是否为空（nullNum>0不为空）
        int nullNum = 0;
        //先判断导入的表格类型以及表本身是否有错误
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            nullNum += xssfSheet.getLastRowNum();
            XSSFRow xssfRow = xssfSheet.getRow(0);
            if (nullNum != 0 && xssfRow != null) {
                //学生绑定班级只需要账号，以Excel表格的第一行的列数来判断导入的表格是教师的还是学生的（教师的列数为4）
                if (xssfRow.getPhysicalNumberOfCells() != 1){
                    return "classWrong";
                }
            }
        }
        if(nullNum == 0){
            return "null";
        }
        //判断导入的Excel表中是否有重复（两个或以上一样的）的用户
        String reExcelAccount = judgeExcelRepStudents(xssfWorkbook);
        if(!reExcelAccount.equals("#")){
            return  reExcelAccount;
        }
        //判断账号中间是否有空格
        String accountSpace = accountIncludeSpace(xssfWorkbook);
        if (!accountSpace.equals("@")){
            return accountSpace;
        }
        //判断是否有存在的用户:最新需求，只需要判断用户是否存在测试班级中
        String repeatAll = judgeRepeatStudents(classCode, xssfWorkbook);
        if (!repeatAll.equals("$")){
            return  repeatAll;
        }

        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    Account record = new Account();
                    //下面是必填选项
                    XSSFCell account = xssfRow.getCell(0);

                    //如果必填项都为空则默认该行为空，这是针对Excel表格用delete键删除多行做的处理
                    if(getValue(account).equals(""))
                    {
                        continue;
                    }

                    record.setAccount(getValue(account));

                    String s = addStudentToCourseClass(classCode, record);
                }
            }
        }

        return "OK";
    }

    /**
     * 导出测试详情
     *
     * @param studentQueryDTO
     */
    @Override
    public void exportExamDetail(StudentQueryDTO studentQueryDTO, String path) throws IOException
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("role", RoleEnum.Student.getRoleCode());
        //map.put("exam_state", 1);

        //当一个学生id都没有的时候，默认查询所有学生
        if(studentQueryDTO.getStudentIdList() != null && studentQueryDTO.getStudentIdList().length > 0)
        {
            map.put("students_account_list", studentQueryDTO.getStudentIdList());
        }

        List<StudentExamDetailDTO> studentExamDetailDTOs = accountMapper.selectStudentExamDetailByExampleByMap(map);

        XSSFWorkbook workbook = new XSSFWorkbook();

        try
        {
            XSSFSheet sheet = workbook.createSheet("测试记录");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("学号");
            row.createCell(1).setCellValue("姓名");
            row.createCell(2).setCellValue("性别");
            row.createCell(3).setCellValue("年级");
            row.createCell(4).setCellValue("专业");
            row.createCell(5).setCellValue("学院");
            row.createCell(6).setCellValue("测试次数");


            //row.createCell(6).setCellValue("试卷名称");
            //row.createCell(7).setCellValue("总分");
            //成绩等级隐藏
            //row.createCell(8).setCellValue("等级");
            //row.createCell(8).setCellValue("测试时间");
            for (int j = 0; j < studentExamDetailDTOs.size(); j++) {
                StudentExamDetailDTO student = studentExamDetailDTOs.get(j);
                row = sheet.createRow(j + 1);

                //给单元格设置数据时，需要处理数据为null时的默认值，否则会出现空指针异常
                row.createCell(0).setCellValue(student.getAccount() == null ? "" : student.getAccount());
                row.createCell(1).setCellValue(student.getName() == null ? "-" : student.getName());
                row.createCell(2).setCellValue(student.getGender() == 0 ? "男" : "女");
                row.createCell(3).setCellValue(student.getGrade() == null ? "-" : student.getGrade());
                row.createCell(4).setCellValue(student.getMajor() == null ? "-" : student.getMajor());
                row.createCell(5).setCellValue(student.getInstitution() == null ? "-" : student.getInstitution());
                //row.createCell(6).setCellValue(student.getPaperName() == null ? "-" : student.getPaperName());

                if(student.getExamCount() == null)
                {
                    row.createCell(6).setCellValue("未考");
                }
                else
                {
                    row.createCell(6).setCellValue(student.getExamCount());
                }

                Double totalScoreSDoub = student.getTotalScore();

                if(totalScoreSDoub == null)
                {
                    //如果没有开始考试时间，则认为没有参加考试
                    //row.createCell(7).setCellValue("未考");
                }
                else
                {
                    if(totalScoreSDoub < 1)
                    {
                        //如果总分小于1，则向上取整数
                        //row.createCell(7).setCellValue(Math.ceil(totalScoreSDoub));
                    }
                    else if(totalScoreSDoub < 20 && totalScoreSDoub > 19)
                    {
                        //如果在19分和20分之间的成绩，舍弃小数位取整数
                        //row.createCell(7).setCellValue(Math.floor(totalScoreSDoub));
                    }
                    else
                    {
                        //其他成绩段的分数，采用四舍五入q取整数
                        //row.createCell(7).setCellValue(Math.rint(totalScoreSDoub));
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //开始时间必须有数据, 如果没有值 提供默认值
                if(student.getStartTime() != null)
                {
                    //row.createCell(8).setCellValue(sdf.format(student.getStartTime()));
                }
                else
                {
                    //row.createCell(8).setCellValue("-");
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

    public String accountIncludeSpace(XSSFWorkbook xssfWorkbook) throws IOException {
        String accountSpace = "@";
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    XSSFCell account = xssfRow.getCell(0);
                    String getAccount = getValue(account);
                    //Excel表格用delete键删除后获取值不为空，为空白，需要提前判断
                    if (getAccount.equals("")) {
                        continue;
                    }
                    //判断账号中是否含有空格
                    if (getAccount.lastIndexOf(" ") !=-1) {
                        accountSpace += getAccount + ",";
                    }
                    //判断账号中是否有允许字符意外的字符
                    if(!includeSpecialString(getAccount)){
                        accountSpace += getAccount + ",";
                    }
                }
            }
        }
        return accountSpace;
    }

    public boolean includeSpecialString(String string)
    {
        //允许出现的字符，非特殊字符
        String regEx = "^[()0-9a-zA-Z_-]+$";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(string);
        if (matcher.find()){
            return true;
        }
        return false;
    }

    public String judgeExcelRepStudents(XSSFWorkbook xssfWorkbook) throws IOException {
        String strAccount = "";
        String reAccount = "#";

        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    XSSFCell account = xssfRow.getCell(0);
                    String getAccount = getValue(account);
                    //Excel表格用delete键删除后获取值不为空，为空白，需要提前判断
                    if (getAccount.equals("")) {
                        continue;
                    }

                    //判断是否有重复账号
                    if (strAccount.contains(getAccount)){
                        if (!reAccount.contains(getAccount))
                            reAccount += getAccount+",";
                    }else{
                        strAccount += getAccount+",";
                    }
                }
            }
        }

        return reAccount;
    }

    public String judgeRepeatStudents(String classCode, XSSFWorkbook xssfWorkbook) throws IOException
    {
        String string = "$";
        List<String> reList = new ArrayList<>();
        int listFlag = 0;
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    Account record = new Account();
                    XSSFCell account = xssfRow.getCell(0);

                    record.setAccount(getValue(account));

                    //最新需求，只需要判断学生账号是否存在于班级中
                    ClassStudentExample example2 = new ClassStudentExample();
                    ClassStudentExample.Criteria criteria2 = example2.createCriteria();
                    criteria2.andClassCodeEqualTo(classCode);
                    criteria2.andStudentAccountEqualTo(record.getAccount());

                    int count = classStudentMapper.countByExample(example2);

                    //判断是否存在于添加的班级中
                    if (count > 0)
                    {
                        listFlag = 1;
                        reList.add(getValue(account));
                    }
                }
            }
        }
        if (listFlag > 0){
            Iterator it = reList.iterator();
            while (it.hasNext()){
                String str = (String) it.next();
                string += str+",";
            }
            string = string.substring(0,string.length()-1);
            string += "已经在该班级中，不能重复导入哦~";
        }
        return  string;
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            DecimalFormat df = new DecimalFormat("0");
            return df.format(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

}
