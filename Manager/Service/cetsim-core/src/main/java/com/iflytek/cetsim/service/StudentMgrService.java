package com.iflytek.cetsim.service;

import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.StudentClassDTO;
import com.iflytek.cetsim.dto.StudentQueryDTO;
import com.iflytek.cetsim.model.Account;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by pengwang on 2017/3/15.
 */
public interface StudentMgrService
{

    /**
     * 班级批量添加学生
     * @param classCode 班级代码
     * @param is
     * @throws IOException
     */
    String addBatchStudentsToCourseClass(String classCode, InputStream is) throws IOException;

    /**
     * 管理员查询全校学生
     * @param studentQueryDTO
     * @return
     */
    PageModel<StudentAccountDTO> queryStudents(StudentQueryDTO studentQueryDTO);

    String addStudentToCourseClass(String classCode, Account account);

    /**
     * 导出学生测试详情
     * @param studentQueryDTO
     */
    void exportExamDetail(StudentQueryDTO studentQueryDTO, String path) throws IOException;

    /**
     * 获取自己所在的所有测试班级列表
     * @param studentAccount
     * @return
     */
    List<StudentClassDTO> queryMyAllTestClassList(String studentAccount);

}
