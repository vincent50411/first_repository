package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.dao.AccountMapper;
import com.iflytek.cetsim.dao.TeacherPieceMapper;
import com.iflytek.cetsim.dto.StudentAccountDTO;
import com.iflytek.cetsim.dto.TeacherAccountDTO;
import com.iflytek.cetsim.model.Account;
import com.iflytek.cetsim.model.AccountExample;
import com.iflytek.cetsim.model.StudentPiece;
import com.iflytek.cetsim.model.TeacherPiece;
import com.iflytek.cetsim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/5/6.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService
{
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TeacherPieceMapper teacherPieceMapper;


}
