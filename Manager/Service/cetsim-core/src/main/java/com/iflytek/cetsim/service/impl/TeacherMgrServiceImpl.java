package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.base.service.BaseService;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.RoleEnum;
import com.iflytek.cetsim.common.enums.UserStatusEnum;
import com.iflytek.cetsim.dao.AccountMapper;
import com.iflytek.cetsim.dao.ClassStudentMapper;
import com.iflytek.cetsim.dao.ExamTaskMapper;
import com.iflytek.cetsim.dao.TeacherPieceMapper;
import com.iflytek.cetsim.dto.StudentDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;
import com.iflytek.cetsim.model.ClassStudentExample;
import com.iflytek.cetsim.model.TeacherPiece;
import com.iflytek.cetsim.service.TeacherMgrService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pengwang on 2017/3/13.
 */
@Service
@Transactional
public class TeacherMgrServiceImpl extends BaseService implements TeacherMgrService {
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TeacherPieceMapper teacherPieceMapper;

    @Autowired
    private ExamTaskMapper examTaskMapper;

    @Autowired
    private ClassStudentMapper classStudentMapper;

    @Override
    public String addTeacher(TeacherAccountDTO teacherAccount) {
        AccountExample example = new AccountExample();
        AccountExample.Criteria criteria = example.createCriteria();
        criteria.andAccountEqualTo(teacherAccount.getAccount());
        int count = accountMapper.countByExample(example);
        if(count>0)
        {
            return "exists";
        }
        else
        {
            //查询账号是否被加入虚拟班级, 保持账号的唯一性
            ClassStudentExample classStudentExample = new ClassStudentExample();
            ClassStudentExample.Criteria studentCriteria = classStudentExample.createCriteria();
            studentCriteria.andStudentAccountEqualTo(teacherAccount.getAccount());

            int studentClassCount = classStudentMapper.countByExample(classStudentExample);

            if(studentClassCount > 0)
            {
                //账号被教师提前分配给班级使用
                return "existStudent";
            }

            //往账号表增加账号记录
            accountMapper.insertSelective(baseAccountDTOToAccount(teacherAccount));

            //往教师信息表增加教师信息
            teacherPieceMapper.insertSelective(teacherAccountDTOToTeacherPiece(teacherAccount));

            return "new";
        }
    }

    /**
     * 任务下所有学生的基本信息和测试成绩
     * @param paramMap
     * @return
     */
    public List<StudentDTO> listTaskStudent(Map<String, Object> paramMap)
    {
        return examTaskMapper.teacherQueryTaskStudentList(paramMap);
    }

    public PageModel<Account> queryPage(AccountExample example)
    {
        int offset = example.getOffset();
        int size = example.getLimit();
        PageModel<Account> model = new PageModel<>(offset/size, size);
        model.setData(accountMapper.selectByExample(example));
        model.setTotal(accountMapper.countByExample(example));

        return model;
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
                    String getAccount = null;
                    if (account != null)
                        getAccount = getValue(account);
                    //Excel表格用delete键删除后获取值不为空，为空白，需要提前判断
                    if (account == null) {
                        continue;
                    }
                    //判断账号中间是否含有空格
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

    public String judgeExcelRepTeachers(XSSFWorkbook xssfWorkbook) throws IOException {
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
                    String getAccount = null;
                    if (account != null)
                        getAccount = getValue(account);
                    //Excel表格用delete键删除后获取值不为空，为空白，需要提前判断
                    if (account == null) {
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

    public String judgeRepeatTeachers(XSSFWorkbook xssfWorkbook) throws IOException {
        String string = "$";
        List<String> reList = new ArrayList<String>();
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
                    XSSFCell name = xssfRow.getCell(1);
                    XSSFCell gender = xssfRow.getCell(2);
                    XSSFCell phoneNumber = xssfRow.getCell(3);
                    XSSFCell eMail = xssfRow.getCell(4);
                    //如果必填项都为空则默认该行为空，这是针对Excel表格用delete键删除多行做的处理
                    if(account == null && name == null && phoneNumber == null && gender==null && (eMail!=null && getValue(eMail).equals("")))
                    {
                        continue;
                    }
                    //如果该行为空,则继续处理下一行
                    if(account == null && name == null && phoneNumber == null && gender==null && eMail == null )
                    {
                        continue;
                    }
                    //必填列如果有一个为null，则该条记录导入失败
                    if (account == null || name == null || phoneNumber == null || gender == null) {
                        //返回组合错误信息
                        string += "第[" + (numSheet + 1) + "]个sheet第[" + (rowNum + 1) + "]行,账号、姓名、联系方式、邮箱、性别列中，有值为空!<br>";
                    }
                    //用户的账号或姓名信息太长，需报错
                    if((account != null && getValue(account).length()>100) || (name != null && getValue(name).length()>100))
                    {
                        string += "第[" +(numSheet+1)+"]个sheet第["+ (rowNum+1) + "]行,账号或姓名的信息过长，请更改后重新输入!<br>";
                    }
                    //性别中出现了不正确的值（除男女之外的值）
                    if (gender != null && !getValue(gender).equals("男") && !getValue(gender).equals("女")){
                        string += "第[" +(numSheet+1)+"]个sheet第["+ (rowNum+1) + "]行中性别列出现了不正确的值（除男女之外的值）！<br>";
                    }
                    if (account != null)
                        record.setAccount(getValue(account));

                    //判断用户account是否存在
                    if (account != null && accountMapper.selectByPrimaryKey(record.getAccount()) != null)
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
            string += "已经在教师列表中，不能重复导入哦~";
        }
        return  string;
    }

    @Override
    public String addBatchTeachers(InputStream is) throws IOException{
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
            if (nullNum != 0 && xssfSheet.getRow(0) != null) {
                //以Excel表格的第一行的列数来判断导入的表格是教师的还是学生的（学生的列数为8）
                if (xssfSheet.getRow(0).getPhysicalNumberOfCells() > 7) {
                    return "classWrong";
                }

                //教师最新模板有5列
                System.out.print(xssfSheet.getRow(0).getLastCellNum());
                if (xssfSheet.getRow(0).getLastCellNum() != 5) {
                    return "tempWrong";
                }

                //判断教师表格的第六列是否有值，有值的话是多余的
                if (xssfSheet.getRow(0).getCell(5) != null) {
                    return "wrong";
                }
            }
        }
        if (nullNum == 0){
            return "null";
        }
        //判断导入的Excel表在数据库中是否有存在的用户
        String repeatAll = judgeRepeatTeachers(xssfWorkbook);
        if (!repeatAll.equals("$")){
            return  repeatAll;
        }
        //判断导入的Excel表中是否有重复（两个或以上一样的）的用户
        String reExcelAccount = judgeExcelRepTeachers(xssfWorkbook);
        if(!reExcelAccount.equals("#")){
            return  reExcelAccount;
        }
        //判断账号中间是否有空格和特殊字符
        String accountSpace = accountIncludeSpace(xssfWorkbook);
        if (!accountSpace.equals("@")){
            return accountSpace;
        }

        //经过上面之后，没有错误，则进行添加用户，返回成功
        List<Account> list = new ArrayList<Account>();

        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null)
                {
                    TeacherAccountDTO teacherAccount = new TeacherAccountDTO();

                    //下面是必填选项
                    XSSFCell account = xssfRow.getCell(0);
                    XSSFCell name = xssfRow.getCell(1);
                    XSSFCell gender = xssfRow.getCell(2);
                    XSSFCell telephone = xssfRow.getCell(3);
                    XSSFCell emailAddrCell = xssfRow.getCell(4);
                    //如果必填项都为空则默认该行为空，这是针对Excel表格用delete键删除多行做的处理
                    if(account == null && name == null && telephone == null && gender==null && (emailAddrCell!=null && getValue(emailAddrCell).equals("")))
                    {
                        continue;
                    }
                    //非必填项如果列对象cell为null，也需要做异常处理

                    if(telephone != null)
                    {
                        teacherAccount.setTelephone(getValue(telephone));
                    }

                    //新增邮箱地址允许为空

                    if(emailAddrCell != null)
                    {
                        teacherAccount.setEmail(getValue(emailAddrCell));
                    }

                    teacherAccount.setAccount(getValue(account));
                    teacherAccount.setName(getValue(name));
                    teacherAccount.setPassword(Constants.DEFAULT_PASSWORD);
                    teacherAccount.setStatus(UserStatusEnum.Enabled.getStatusCode().shortValue());

                    Integer genderValue = getValue(gender).equals("男") ? 0 : 1;
                    teacherAccount.setGender(genderValue.shortValue());
                    teacherAccount.setCreateTime(new Date());
                    teacherAccount.setRole(RoleEnum.Teacher.getRoleCode());

                    String s = addTeacher(teacherAccount);

                }
            }
        }
        return "OK";
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        }else if(xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC){
            DecimalFormat df = new DecimalFormat("0");
            return df.format(xssfRow.getNumericCellValue());
        }
        else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }



}
