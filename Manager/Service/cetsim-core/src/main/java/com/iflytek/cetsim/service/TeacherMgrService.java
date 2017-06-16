package com.iflytek.cetsim.service;
import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.dto.StudentDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pengwang on 2017/3/13.
 */
public interface TeacherMgrService {
    /**
     * 添加教师
     * @param teacherAccount
     */
    String addTeacher(TeacherAccountDTO teacherAccount);

    /***
     * 批量上传教师
     * @param is
     * @throws IOException
     */
    String addBatchTeachers(InputStream is) throws IOException;

    /**
     * 分页查询教师
     * @param example
     * @return
     */
    PageModel<Account> queryPage(AccountExample example);

    /**
     * 任务下所有学生的基本信息和测试成绩
     * @param paramMap
     * @return
     */
    List<StudentDTO> listTaskStudent(Map<String, Object> paramMap);

}
