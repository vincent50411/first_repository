package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.base.service.BaseService;
import com.iflytek.cetsim.dao.AccountMapper;
import com.iflytek.cetsim.dao.StudentPieceMapper;
import com.iflytek.cetsim.dao.TeacherPieceMapper;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;
import com.iflytek.cetsim.model.StudentPiece;
import com.iflytek.cetsim.model.TeacherPiece;
import com.iflytek.cetsim.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户服务接口实现，基本增删改查接口已经实现，不需要在此处实现
 * 任何Impl结尾的方法都会被切面注入，添加事务控制
 *
 * @author wbyang3
 * */
@Service
@Transactional
public class AccountServiceImpl extends BaseService implements AccountService
{
    @Autowired
    private AccountMapper accountMapper = null;

    @Autowired
    private StudentPieceMapper studentPieceMapper = null;

    @Autowired
    private TeacherPieceMapper teacherPieceMapper = null;

    /**
     * 用户登录
     *
     * @param account 账户
     * @param password 密码
     * */
    public Account login(String account, String password)
    {
        Account dto = new Account();
        dto.setAccount(account);
        dto.setPassword(password);
        Account user = accountMapper.login(dto);
        return user;
    }

    /**
     * 根据用户账号查找用户
     *
     * @param account 账户
     * */
    public Account findUserByAccount(String account)
    {
        return accountMapper.selectByPrimaryKey(account);
    }

    /**
     * 修改图像
     * @param account
     * @param photo
     */
    public void uploadPhoto(String account,String photo)
    {
        AccountExample example = new AccountExample();
        example.createCriteria().andAccountEqualTo(account);
        List<Account> accounts = accountMapper.selectByExample(example);
        if(accounts.size() > 0)
        {
            Account user = new Account();

            user.setAccount(account);
            //利用保留字段做图像路劲存储
            user.setReserved1(photo);
            accountMapper.updateByPrimaryKey(user);
        }
    }

    public int registerStudent(StudentAccountDTO studentAccountDTO)
    {
        Account user = baseAccountDTOToAccount(studentAccountDTO);

        AccountExample example = new AccountExample();
        AccountExample.Criteria criteria = example.createCriteria();
        criteria.andAccountEqualTo(user.getAccount());

        //判断是否已经存在相同账号
        int count = accountMapper.countByExample(example);
        if(count > 0)
        {
            return 1;
        }
        else
        {
            StudentPiece studentPiece = studentAccountDTOToStudentPiece(studentAccountDTO);

            //往账号表增加账号记录
            accountMapper.insertSelective(user);

            studentPieceMapper.insertSelective(studentPiece);
            return 0;
        }
    }

    public int updateByPrimaryKeySelective(Account record)
    {
        return accountMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 教师信息修改
     * @param teacherAccountDTO
     * @return
     */
    public int updateTeacherByAccount(TeacherAccountDTO teacherAccountDTO)
    {
        Account  account  = baseAccountDTOToAccount(teacherAccountDTO);
        TeacherPiece teacherPiece = teacherAccountDTOToTeacherPiece(teacherAccountDTO);

        int accountSuccRs = accountMapper.updateByPrimaryKeySelective(account);

        int teacherSuccRs = teacherPieceMapper.updateByPrimaryKeySelective(teacherPiece);

        if(accountSuccRs == 1 && teacherSuccRs == 1)
        {
            return 1;
        }

        return 0;
    }

    /**
     * 学生信息修改
     * @param studentAccountDTO
     * @return
     */
    public int updateStudentByAccount(StudentAccountDTO studentAccountDTO)
    {
        //判断行政班级中间是否含有空格和特殊字符
        String naturalClass = studentAccountDTO.getNaturalClass();
        if (includeSpecialString(naturalClass)) {
            return 2;
        }
        //判断姓名中是否含有空格和特殊字符
        String studentName = studentAccountDTO.getName();
        if (includeSpecialString(studentName)){
            return 3;
        }

        StudentPiece studentPiece = studentAccountDTOToStudentPiece(studentAccountDTO);

        Account  account  = baseAccountDTOToAccount(studentAccountDTO);

        int accountSuccRs = accountMapper.updateByPrimaryKey(account);

        int studentSuccRs = studentPieceMapper.updateByPrimaryKeySelective(studentPiece);

        if(accountSuccRs == 1 && studentSuccRs == 1)
        {
            return 1;
        }

        return 0;
    }

    public boolean includeSpecialString(String string)
    {
        //特殊字符
        String regEx = "[ `~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(string);
        if (string.lastIndexOf(" ") != -1 || matcher.find()){
            return true;
        }
        return false;
    }

    public Account findUserInfoByAccount(String account)
    {
        return accountMapper.selectByPrimaryKey(account);
    }

    /**
     * 根据学生账号查询学生全部信息
     * @param account
     * @return
     */
    public StudentAccountDTO findStudentByAccount(String account)
    {
        return  accountMapper.selectStudentInfoByPrimaryKey(account);
    }

    /**
     * 根据教师账号查询教师全部信息
     * @param account
     * @return
     */
    public TeacherAccountDTO findTeacherByAccount(String account)
    {
        return  accountMapper.selectTeacherInfoByPrimaryKey(account);
    }

    public int judgePasswordByUserId(String userAccount, String oldPass)
    {
        Account account = new Account();
        account.setAccount(userAccount);

        account = accountMapper.selectByPrimaryKey(userAccount);

        //先判断该用户ID是否存在
        if(account == null)
            return 0;
        account.setPassword(oldPass);
        //再判断该用户的原密码是否正确
        if(accountMapper.selectByIdAndPwd(account) == null)
            return 1;
        else
            return 2;
    }

    /**
     * 更新account用户的密码
     * @param account
     */
    public void updateAccountPassword(Account account)
    {
        accountMapper.updateUserPassByAccount(account);
    }

    public void resetPassword(Account account)
    {
        accountMapper.updateByPrimaryKeySelective(account);
    }

    /**
     * 设置用户状态，启用和禁用
     * @param account
     */
    public void setUserStatus(Account account)
    {
        accountMapper.updateByPrimaryKeySelective(account);
    }


}
