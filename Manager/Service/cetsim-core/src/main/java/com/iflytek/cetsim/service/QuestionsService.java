package com.iflytek.cetsim.service;

import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.PaperItemQueryDTO;
import com.iflytek.cetsim.dto.SpecialTaskRecordDTO;
import com.iflytek.cetsim.model.PaperItemBuffer;
import com.iflytek.cetsim.model.SpecialTrainRecord;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */
public interface QuestionsService
{
    /**
     * 导入专项训练试卷包
     *
     * @return
     */
    JsonResult importPaper(String srcFile, String desDir)throws SQLException,IOException;

    /**
     * 查询试题列表
     * @param paperItemQueryDTO
     * @return
     */
    List<PaperItemQueryDTO> selectQuestionList(PaperItemQueryDTO paperItemQueryDTO);


    int countQuestions(PaperItemQueryDTO paperItemQueryDTO);

    /**
     * 查询学生的试题列表
     * @param studentAccount
     * @param paperItemQueryDTO
     * @return
     */
    List<PaperItemQueryDTO> selectQuestionListByAccount(String studentAccount, PaperItemQueryDTO paperItemQueryDTO);


    int countQuestionListByAccount(String studentAccount, PaperItemQueryDTO paperItemQueryDTO);

    /**
     * 修改试题状态
     * @param paperItemCode
     * @param status
     * @return
     */
    int setQuestionStatus(String paperItemCode, int status);

    /**
     * 查询学生的专项训练测试记录
     * @param studentAccount
     * @param paperItemQueryDTO
     * @return
     */
    List<SpecialTaskRecordDTO> querySpecialRecordList(String studentAccount, PaperItemQueryDTO paperItemQueryDTO);


    /**
     * 查看学生的专项训练成绩报告
     * @param studentAccount 学生账号
     * @param specialRecordCode 专项训练记录代码
     * @return
     */
    SpecialTaskRecordDTO querySpecialRecordReport(String studentAccount, String specialRecordCode);

    /**
     * 查询专项训练考试状态
     * @param studentAccount
     * @param specialRecordCode
     * @return
     */
    short querySpecialRecordStateByAccount(String studentAccount, String specialRecordCode);

    /**
     * 开始专项训练
     * @param studentAccount
     * @param paperItemCode
     * @return
     */
    SpecialTrainRecord startSpecialTrainExam(String studentAccount, String paperItemCode);

    /**
     * 检查专项训练试卷的权限状态
     * @param paperCode
     * @return
     */
    JsonResult checkSpecialPaperState(String paperCode);


    /**
     * 查看专项训练报告服务
     * @param studentAccount
     * @param specialRecordCode
     * @param paperItemTypeCode
     * @return
     */
    JsonResult querySelfSpecialAnswerInfoService(String studentAccount, String specialRecordCode, String paperItemTypeCode, String examType);

    String getSpecialPaperItemCodeByRecordCode(String examRecordCode);

    PaperItemBuffer getPaperItemBuffData(String examRecordCode);

    String getPaperCodeByExamType(String examRecordCode, String examType);

}
