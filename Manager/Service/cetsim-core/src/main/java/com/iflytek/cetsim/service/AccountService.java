package com.iflytek.cetsim.service;

import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;

/**
 * 用户服务接口
 *
 * @author wbyang3
 * */
public interface AccountService
{
    /**
     * 用户登录
     *
     * @param account 账户
     * @param password 密码
     * */
    Account login(String account, String password);

    /**
     * 根据用户账号查找用户
     *
     * @param account 账户
     * */
    Account findUserByAccount(String account);

    /**
     * Account基础表信息修改
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Account record);

    /**
     * 教师信息修改
     * @param teacherAccountDTO
     * @return
     */
    int updateTeacherByAccount(TeacherAccountDTO teacherAccountDTO);

    /**
     * 学生信息修改
     * @param studentAccountDTO
     * @return
     */
    int updateStudentByAccount(StudentAccountDTO studentAccountDTO);

    /**
     * 查询管理员基本信息
     * @param account
     * @return
     */
    Account findUserInfoByAccount(String account);

    /**
     * 修改图像
     * @param account
     * @param photo
     */
    void uploadPhoto(String account,String photo);

    /**
     * 根据学生账号查询学生全部信息
     * @param account
     * @return
     */
    StudentAccountDTO findStudentByAccount(String account);

    /**
     * 根据教师账号查询教师全部信息
     * @param account
     * @return
     */
    TeacherAccountDTO findTeacherByAccount(String account);


    /**
     * 学生注册
     * @param studentAccountDTO
     * @return
     */
    int registerStudent(StudentAccountDTO studentAccountDTO);



    int judgePasswordByUserId(String account, String oldPass);

    /**
     * 更新account用户的密码
     * @param account
     */
    void updateAccountPassword(Account account);

    /**
     * 重置密码
     * @param account
     */
    void resetPassword(Account account);

    /**
     * 设置用户状态，启用和禁用
     * @param account
     */
    void setUserStatus(Account account);

}
