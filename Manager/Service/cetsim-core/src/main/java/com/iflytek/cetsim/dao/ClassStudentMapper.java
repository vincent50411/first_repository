package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.StudentClassDTO;
import com.iflytek.cetsim.dto.StudentDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.ClassStudent;
import com.iflytek.cetsim.model.ClassStudentExample;
import com.iflytek.cetsim.model.ClassStudentKey;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface ClassStudentMapper {
    int countByExample(ClassStudentExample example);

    int countByCondition(Map<String, Object> paramMap);

    int deleteByExample(ClassStudentExample example);

    int deleteByPrimaryKey(ClassStudentKey key);

    int insert(ClassStudent record);

    int insertSelective(ClassStudent record);

    List<ClassStudent> selectByExample(ClassStudentExample example);

    List<Account> selectClassStudents(ClassStudentExample example);

    List<StudentClassDTO> queryMyAllTestClassList(Map<String, Object> paramMap);

    List<StudentDTO> selectClassStudentsDetailInfo(Map<String, Object> paramMap);
}