package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.StudentExamDetailDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface AccountMapper {
    int countByExample(AccountExample example);

    int deleteByExample(AccountExample example);

    int deleteByPrimaryKey(String account);

    int insert(Account record);

    int insertSelective(Account record);

    List<Account> selectByExample(AccountExample example);

    Account selectByPrimaryKey(String account);

    StudentAccountDTO selectStudentInfoByPrimaryKey(String account);

    TeacherAccountDTO selectTeacherInfoByPrimaryKey(String account);

    Account selectByIdAndPwd(Account account);

    int updateUserPassByAccount(Account account);

    int updateByExampleSelective(@Param("record") Account record, @Param("example") AccountExample example);

    int updateByExample(@Param("record") Account record, @Param("example") AccountExample example);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);

    Account login(Account dto);

    List<StudentExamDetailDTO> selectStudentExamDetailByExampleByMap(HashMap<String,Object> map);



}