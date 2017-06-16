package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.model.StudentPiece;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface StudentPieceMapper {
    int insert(StudentPiece record);

    int insertSelective(StudentPiece record);

    StudentPiece selectByPrimaryKey(String studentAccount);

    int selectStudentAcountByCondition(Map<String, Object> paramMap);

    List<StudentAccountDTO> selectStudentByCondition(Map<String, Object> paramMap);

    int updateByPrimaryKeySelective(StudentPiece record);


}