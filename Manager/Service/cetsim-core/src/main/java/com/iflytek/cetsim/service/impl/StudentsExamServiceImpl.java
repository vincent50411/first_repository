package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.base.service.BaseService;
import com.iflytek.cetsim.common.utils.CommonUtil;
import com.iflytek.cetsim.dao.ExamRecordMapper;
import com.iflytek.cetsim.dto.ExamRecordDTO_new;
import com.iflytek.cetsim.dto.KVStringDTO;
import com.iflytek.cetsim.model.ExamRecord;
import com.iflytek.cetsim.service.StudentsExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/8.
 */
@Service
@Transactional
public class StudentsExamServiceImpl extends BaseService implements StudentsExamService
{
    @Autowired
    private ExamRecordMapper examRecordMapper;

    /**
     * 开始考试，往exam_record表中加入一条初始考试记录
     * @param studentAccount 学生账号
     * @param examTaskCode  模拟考试任务代码
     * @return
     */
    public ExamRecord startEcp(String studentAccount, String examTaskCode)
    {

        //全新的记录
        ExamRecord examRecord = new ExamRecord();
        examRecord.setCode(CommonUtil.GUID());
        examRecord.setStudentNo(studentAccount);
        examRecord.setExamTaskCode(examTaskCode);

        //考试状态(0:未考试,1:考试成功,2:考试失败)
        examRecord.setExamState((short)0);
        //考试过程状态(0:未登录,1:已登录,2:开始试音,3:试音完成,5:正在答题,6:答题完成)
        examRecord.setFlowState((short)0);
        examRecord.setScore(new Float(0.0));
        //所有小题评测状态(0:未评测,1:评测完成,2:评测失败)
        examRecord.setEvalState((short)0);

        //分组状态(0:未分组,1:请求预分组,2:预分组成功,3:分组成功,4:分组失败/取消)
        examRecord.setGroupState((short)0);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        examRecord.setPaperState((short)0);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        examRecord.setDataState((short)0);

        //错误码(0:无错误,1:故障申报,2:网络故障,3:系统故障,4:同组失败,5:试音失败)
        examRecord.setErrorCode((short)0);

        examRecordMapper.insert(examRecord);

        return examRecord;
    }


    /**
     * 查询成绩报告信息
     * @param examRecordCode 考试记录代码
     * @return
     */
    public List<KVStringDTO> listExamResult(String examRecordCode)
    {
        return examRecordMapper.queryStudentExamResult(examRecordCode);
    }

    /**
     * 学生全部考试记录，有效的
     * @param studentAccount
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public PageModel<ExamRecordDTO_new> listStudentAllRecords(String studentAccount, int pageIndex, int pageSize)
    {
        PageModel<ExamRecordDTO_new> model = new PageModel<>(pageIndex,pageSize);
        try
        {
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("limit", pageSize);
            paramMap.put("offset", (pageIndex - 1) * pageSize);

            paramMap.put("studentAccount", studentAccount);

            List<ExamRecordDTO_new> examRecordDTO_newList = examRecordMapper.selectAllStudentRecords(paramMap);

            model.setData(examRecordDTO_newList);
            model.setTotal(examRecordMapper.selectAllStudentRecordsCount(paramMap));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }




        return model;
    }


}
