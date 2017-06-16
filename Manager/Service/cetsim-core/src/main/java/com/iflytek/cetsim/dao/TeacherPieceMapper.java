package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.dto.StudentDTO;
import com.iflytek.cetsim.model.TeacherPiece;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface TeacherPieceMapper {
    int insert(TeacherPiece record);

    int insertSelective(TeacherPiece record);

    TeacherPiece selectByPrimaryKey(String employeeAccount);

    int updateByPrimaryKeySelective(TeacherPiece record);

    List<StudentDTO> listTaskStudent(Map<String, Object> paramMap);

}