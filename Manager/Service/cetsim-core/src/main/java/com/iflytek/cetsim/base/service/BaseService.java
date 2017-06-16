package com.iflytek.cetsim.base.service;

import com.iflytek.cetsim.base.dao.BaseDao;
import com.iflytek.cetsim.dto.BaseAccountDTO;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.StudentPiece;
import com.iflytek.cetsim.model.TeacherPiece;

/**
 * 基础服务父接口，允许直接获取Dao对象
 *
 * @author wbyang3
 * */
public class BaseService
{
    protected Account baseAccountDTOToAccount(BaseAccountDTO baseAccountDTO)
    {
        Account account = new Account();
        account.setAccount(baseAccountDTO.getAccount());
        account.setPassword(baseAccountDTO.getPassword());
        account.setName(baseAccountDTO.getName());
        account.setGender(baseAccountDTO.getGender());
        account.setRole(baseAccountDTO.getRole());
        account.setStatus(baseAccountDTO.getStatus());
        account.setAddress(baseAccountDTO.getAddress());
        account.setEmail(baseAccountDTO.getEmail());
        account.setTelephone(baseAccountDTO.getTelephone());
        account.setLastLogin(baseAccountDTO.getLastLogin());
        account.setCreateTime(baseAccountDTO.getCreateTime());

        return account;
    }

    protected TeacherPiece teacherAccountDTOToTeacherPiece(TeacherAccountDTO teacherAccount)
    {
        TeacherPiece teacherPiece = new TeacherPiece();
        teacherPiece.setEmployeeAccount(teacherAccount.getAccount());
        teacherPiece.setJobTitle(teacherAccount.getJobTitle());

        return teacherPiece;
    }


    protected StudentPiece studentAccountDTOToStudentPiece(StudentAccountDTO studentAccountDTO)
    {
        StudentPiece studentPiece = new StudentPiece();
        studentPiece.setStudentAccount(studentAccountDTO.getAccount());
        studentPiece.setGrade(studentAccountDTO.getGrade());
        studentPiece.setInstitution(studentAccountDTO.getInstitution());
        studentPiece.setMajor(studentAccountDTO.getMajor());
        studentPiece.setSchool(studentAccountDTO.getSchool());
        studentPiece.setClass(studentAccountDTO.getNaturalClass());

        return studentPiece;
    }


}