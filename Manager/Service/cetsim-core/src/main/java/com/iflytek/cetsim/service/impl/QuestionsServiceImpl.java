package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.PaperAllowFreeUsageEnum;
import com.iflytek.cetsim.common.enums.PaperAllowPlanUsageEnum;
import com.iflytek.cetsim.common.enums.PaperStatusEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.CommonUtil;
import com.iflytek.cetsim.common.utils.DateUtils;
import com.iflytek.cetsim.common.utils.Dom4jUtils;
import com.iflytek.cetsim.common.utils.Zip4jUtils;
import com.iflytek.cetsim.dao.*;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.PaperItem;
import com.iflytek.cetsim.model.PaperItemBuffer;
import com.iflytek.cetsim.model.SpecialTrainRecord;
import com.iflytek.cetsim.service.QuestionsService;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2017/5/10.
 */
@Service
@Transactional
public class QuestionsServiceImpl implements QuestionsService
{
    @Autowired
    private PaperItemMapper paperItemMapper;

    @Autowired
    private PaperBufferMapper paperBufferMapper = null;

    @Autowired
    private PaperItemBufferMapper paperItemBufferMapper;

    @Autowired
    private SpecialTrainRecordMapper specialTrainRecordMapper;

    @Autowired
    private TrainRecordMapper trainRecordMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;


    /**
     * 导入专项训练试卷包
     *
     * @return
     */
    public JsonResult importPaper(String srcFile, String baseDir)throws SQLException,IOException
    {
        JsonResult result = new JsonResult();
        //文件名和试卷号对应关系
        HashMap<String, String> rpkNameDict = new HashMap<>();

        //解压后的文件系统路径，由于试卷压缩包已经写入数据库，该解压文件在所有流程结束后，应该删除
        String unZipDir = Paths.get(baseDir, UUID.randomUUID().toString()).toString();

        Zip4jUtils.extractAllFiles(srcFile, unZipDir);

        //2.解析paper.xml，获取试卷信息, 需要检查xml文件是否存在，如果不存在，直接返回错误信息
        String paperXmlFile = Paths.get(unZipDir, Constants.PAPER_MODEL).toString();

        Document doc = Dom4jUtils.getXMLByFilePath(paperXmlFile);

        if (doc == null)
        {
            //退出程序前，删除所有临时文件
            CommonUtil.deleteDir(new File(unZipDir));

            result.setFailMsg("试卷包中丢失试卷信息描述文件paper.xml或试卷包目录结构不合法");

            //如果doc为null，表示xml源文件不存在
            return result;
        }

        List<Element> paperEls = Dom4jUtils.getChildElements(doc.getRootElement());

        //4.导入试卷
        for (int i = 0; i < paperEls.size(); i++)
        {
            Element element = paperEls.get(i);
            String paperRPKFilePath = element.attribute("file").getValue();

            //需要检查试卷文件是否存在，如果不存在直接返回异常, 保持事务一致性
            String srcRpkFile = Paths.get(unZipDir, paperRPKFilePath).toString();

            //判断导入的试题包是否合法，mode="special"
            if(element.attribute("mode") == null || !"special".equals(element.attribute("mode").getValue()))
            {
                result.setFailMsg("导入的文件不符合专项训练用试题包格式");

                return result;
            }

            //封装paperItem实体类
            PaperItem paperItem = getPaperItem(element);

            HashMap<String, Object> paramMap = new HashMap<>();

            //code是主键唯一性
            paramMap.put("code", paperItem.getCode());
            //paramMap.put("paperItemTypeCode", paperItem.getPaperItemTypeCode());

            //判断试卷是否存在
            int paperNum = paperItemMapper.selectPaperItemByCode(paramMap);

            //已经存在试卷名称相同的记录
            if (paperNum > 0) {
                //退出程序前，删除所有临时文件
                CommonUtil.deleteDir(new File(unZipDir));

                result.setFailMsg("已经存在试卷代码为:[" + paperItem.getCode() + "]的试卷");

                //返回试卷名称
                return result;
            }

            paperItemMapper.insertSelective(paperItem);
            rpkNameDict.put(paperRPKFilePath, paperItem.getCode());
            try
            {
                //向试卷数据表增加记录
                byte[] b = FileUtils.readFileToByteArray(new File(srcRpkFile));

                PaperItemBuffer paperItemBuffer = new PaperItemBuffer();
                paperItemBuffer.setCode(CommonUtil.GUID());
                paperItemBuffer.setBuffer(b);
                //测试用
                paperItemBuffer.setMd5("123456789");
                paperItemBuffer.setPaperItemCode(paperItem.getCode());

                //表引擎为InnoDb类型支持事务操作，但是blob字段大小受限制，只能把引擎设置为MyISAM类型
                paperItemBufferMapper.insertSelective(paperItemBuffer);
            }
            catch (IOException ex)
            {
                //如果抛出异常，则手动删除保存的记录（paper表无法应用事务，原因是表达引擎不是InnoDb类型mysql）
                setCallBack(paperItem, unZipDir);

                ex.printStackTrace();

                result.setFailMsg("试卷包中缺失试卷文件[" + srcRpkFile + "],请检查后重新上传");

                //事务无法自定捕获，只能手动控制返回值
                return result;
            }
            catch (Exception ex)
            {
                //如果抛出异常，则手动删除保存的记录（paper表无法应用事务，原因是表达引擎不是InnoDb类型mysql）
                setCallBack(paperItem, unZipDir);

                ex.printStackTrace();

                result.setFailMsg("保存试卷异常");

                //事务无法自定捕获，只能手动控制返回值
                return result;
            }
        }
        String pprResult = PaperServiceImpl.copyPPRFiles(unZipDir, baseDir, rpkNameDict);
        if (pprResult.equals("fail")){
            result.setFailMsg("PPR文件复制异常");
            return result;
        }


        //退出程序前，删除所有临时文件
        CommonUtil.deleteDir(new File(unZipDir));

        result.setSucceedMsg("导入成功");

        return result;
    }

    /**
     * 查询试题列表
     * @param paperItemQueryDTO
     * @return
     */
    public List<PaperItemQueryDTO> selectQuestionList(PaperItemQueryDTO paperItemQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("limit", paperItemQueryDTO.getPageSize());
        paramMap.put("offset", (paperItemQueryDTO.getPageIndex() - 1) * paperItemQueryDTO.getPageSize());

        if(paperItemQueryDTO.getPaperItemTypeCode() != null)
        {
            paramMap.put("paper_item_type_code", paperItemQueryDTO.getPaperItemTypeCode());
        }

        if(paperItemQueryDTO.getStatus() != null)
        {
            paramMap.put("status", paperItemQueryDTO.getStatus());
        }

        if(paperItemQueryDTO.getName() != null && !"".equals(paperItemQueryDTO.getName()))
        {
            paramMap.put("name", "%" + paperItemQueryDTO.getName() + "%");
        }

        //增加排序规则
        if(paperItemQueryDTO.getOrderColumnName() != null && paperItemQueryDTO.getOrderCode() != null)
        {
            paramMap.put(paperItemQueryDTO.getOrderColumnName().toUpperCase() + "_" + paperItemQueryDTO.getOrderCode().toUpperCase(), "true");
        }
        else
        {
            paramMap.put("order_condition_default", "true");
        }

        return paperItemMapper.selectQuestionList(paramMap);
    }

    public int countQuestions(PaperItemQueryDTO paperItemQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        if(paperItemQueryDTO.getPaperItemTypeCode() != null)
        {
            paramMap.put("paper_item_type_code", paperItemQueryDTO.getPaperItemTypeCode());
        }

        if(paperItemQueryDTO.getStatus() != null)
        {
            paramMap.put("status", paperItemQueryDTO.getStatus());
        }

        if(paperItemQueryDTO.getName() != null && !"".equals(paperItemQueryDTO.getName()))
        {
            paramMap.put("name", "%" + paperItemQueryDTO.getName() + "%");
        }

        return paperItemMapper.countQuestions(paramMap);
    }





    /**
     * 查询学生的试题列表
     * @param studentAccount
     * @param paperItemQueryDTO
     * @return
     */
    public List<PaperItemQueryDTO> selectQuestionListByAccount(String studentAccount, PaperItemQueryDTO paperItemQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("limit", paperItemQueryDTO.getPageSize());
        paramMap.put("offset", (paperItemQueryDTO.getPageIndex() - 1) * paperItemQueryDTO.getPageSize());

        paramMap.put("student_account", studentAccount);

        if(paperItemQueryDTO.getPaperItemTypeCode() != null)
        {
            paramMap.put("paper_item_type_code", paperItemQueryDTO.getPaperItemTypeCode());
        }

        if(paperItemQueryDTO.getUseState() != null && paperItemQueryDTO.getUseState() == 0)
        {
            //未练习
            paramMap.put("noneUsed", paperItemQueryDTO.getUseState());
        }
        else if(paperItemQueryDTO.getUseState() != null && paperItemQueryDTO.getUseState() == 1)
        {
            //已练习
            paramMap.put("used", paperItemQueryDTO.getUseState());
        }

        if(paperItemQueryDTO.getName() != null)
        {
            paramMap.put("name", paperItemQueryDTO.getName());
        }


        return paperItemMapper.selectQuestionListByAccount(paramMap);

    }


    /**
     * 统计学生的试题数目
     * @param studentAccount
     * @param paperItemQueryDTO
     * @return
     */
    public int countQuestionListByAccount(String studentAccount, PaperItemQueryDTO paperItemQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();


        paramMap.put("student_account", studentAccount);

        if(paperItemQueryDTO.getPaperItemTypeCode() != null)
        {
            paramMap.put("paper_item_type_code", paperItemQueryDTO.getPaperItemTypeCode());
        }

        if(paperItemQueryDTO.getUseState() != null && paperItemQueryDTO.getUseState() == 0)
        {
            //未练习
            paramMap.put("noneUsed", paperItemQueryDTO.getUseState());
        }
        else if(paperItemQueryDTO.getUseState() != null && paperItemQueryDTO.getUseState() == 1)
        {
            //已练习
            paramMap.put("used", paperItemQueryDTO.getUseState());
        }

        if(paperItemQueryDTO.getName() != null)
        {
            paramMap.put("name", paperItemQueryDTO.getName());
        }


        return paperItemMapper.selectQuestionListByAccount(paramMap).size();

    }




    private void setCallBack(PaperItem paperItem, String unZipDir)
    {
        //如果抛出异常，则手动删除保存的记录（paper表无法应用事务，原因是表达引擎不是InnoDb类型mysql）
        paperItemMapper.deleteByPrimaryKey(paperItem.getCode());

        //退出程序前，删除所有临时文件
        CommonUtil.deleteDir(new File(unZipDir));
    }

    private PaperItem getPaperItem(Element el) {
        PaperItem paperItem = new PaperItem();
        paperItem.setName(el.attribute("title").getValue());

        paperItem.setCode(el.attribute("code").getValue());

        //试题难度
        paperItem.setDifficulty(new Float(1.0));

        //试卷导入时间取应用服务器当前时间
        paperItem.setCreateTime(DateUtils.parseDate(DateUtils.getDateTime()));

        //试题的类型，关联试题类型表
        paperItem.setPaperItemTypeCode(el.attribute("itemType").getValue().toLowerCase());

        paperItem.setStatus((short) PaperStatusEnum.ENABLED.getStatusCode());
        paperItem.setAllowFreeUseage(PaperAllowFreeUsageEnum.ENABLED.getAllowFreeUsageCode());
        paperItem.setAllowPlanUseage(PaperAllowPlanUsageEnum.ENABLED.getAllowPlanUsageCode());

        return paperItem;
    }

    /**
     * 修改试题状态
     * @param paperItemCode
     * @param status
     * @return
     */
    public int setQuestionStatus(String paperItemCode, int status)
    {
        PaperItem paperItem = new PaperItem();

        paperItem.setCode(paperItemCode);
        paperItem.setStatus((short)status);

        return paperItemMapper.updateByPrimaryKeySelective(paperItem);
    }

    /**
     * 查询学生的专项训练测试记录
     * @param studentAccount
     * @param paperItemQueryDTO
     * @return
     */
    public List<SpecialTaskRecordDTO> querySpecialRecordList(String studentAccount, PaperItemQueryDTO paperItemQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("limit", paperItemQueryDTO.getPageSize());
        paramMap.put("offset", (paperItemQueryDTO.getPageIndex() - 1) * paperItemQueryDTO.getPageSize());

        paramMap.put("student_account", studentAccount);

        if(paperItemQueryDTO.getPaperItemTypeCode() != null)
        {
            //试题类型代码
            paramMap.put("paper_item_type_code", paperItemQueryDTO.getPaperItemTypeCode());
        }

        if(paperItemQueryDTO.getStatus() != null)
        {
            //试题状态
            paramMap.put("status", paperItemQueryDTO.getStatus());
        }

        if(paperItemQueryDTO.getName() != null)
        {
            //试题名称, 支持模糊查询
            paramMap.put("name", "%" +  paperItemQueryDTO.getName() + "%");
        }

        if(paperItemQueryDTO.getStartTime() != null)
        {
            //开始时间查询条件
            paramMap.put("start_time", paperItemQueryDTO.getStartTime());
        }

        if(paperItemQueryDTO.getEndTime() != null)
        {
            //开始时间查询条件
            paramMap.put("end_time", paperItemQueryDTO.getEndTime());
        }

        //增加排序规则
        if(paperItemQueryDTO.getOrderColumnName() != null && paperItemQueryDTO.getOrderCode() != null)
        {
            paramMap.put(paperItemQueryDTO.getOrderColumnName().toUpperCase() + "_" + paperItemQueryDTO.getOrderCode().toUpperCase(), "true");
        }
        else
        {
            paramMap.put("order_condition_default", "true");
        }

        //学生专项训练统计汇总列表，每条统计记录都有详细的记录列表，需要单独处理
        List<SpecialTaskRecordDTO> studentSpecialRecordList = specialTrainRecordMapper.querySpecialRecordListByAccount(paramMap);

        Map<String, Object> paramInfoMap = new HashMap<String, Object>();
        paramInfoMap.put("student_account", studentAccount);

        if(paperItemQueryDTO.getOrderColumnName() != null && paperItemQueryDTO.getOrderCode() != null)
        {
            paramInfoMap.put(paperItemQueryDTO.getOrderColumnName().toUpperCase() + "_" + paperItemQueryDTO.getOrderCode().toUpperCase(), "true");
        }

        List<SpecialTaskRecordDTO> studentSpecialInfoRecordList = specialTrainRecordMapper.querySpecialRecordInfoListByAccount(paramInfoMap);

        if(studentSpecialRecordList != null)
        {
            for(SpecialTaskRecordDTO countRecord : studentSpecialRecordList)
            {
                //为每一条统计记录添加明细记录
                String paperItemCode = countRecord.getPaperItmeCode();

                if(paperItemCode != null)
                {
                    List<Object> infoRecordList = new ArrayList();

                    for(SpecialTaskRecordDTO specialTaskInfoRecord : studentSpecialInfoRecordList)
                    {
                        String infoRecordPaperItemCode = specialTaskInfoRecord.getPaperItmeCode();

                        if(paperItemCode.equals(infoRecordPaperItemCode))
                        {
                            //根据专项训练代码，从学生所有训练记录中汇总明细记录
                            Map<String, Object> infoRecordMap = new HashMap<String, Object>();

                            infoRecordMap.put("startTime", specialTaskInfoRecord.getStartTime());
                            infoRecordMap.put("score", specialTaskInfoRecord.getScore());
                            infoRecordMap.put("code", specialTaskInfoRecord.getPaperItmeCode());
                            infoRecordMap.put("paperItemTypeCode", specialTaskInfoRecord.getPaperItemTypeCode());

                            infoRecordMap.put("specialRecordCode", specialTaskInfoRecord.getSpecialRecordCode());

                            infoRecordList.add(infoRecordMap);
                        }
                    }

                    //将明细记录添加到汇总记录中
                    countRecord.setInfoRecordList(infoRecordList);
                }
            }
        }

        return studentSpecialRecordList;
    }

    /**
     * 查看学生的专项训练成绩报告
     * @param studentAccount 学生账号
     * @param specialRecordCode 专项训练记录代码
     * @return
     */
    public SpecialTaskRecordDTO querySpecialRecordReport(String studentAccount, String specialRecordCode)
    {
        Map<String, Object> paramInfoMap = new HashMap<String, Object>();
        paramInfoMap.put("student_account", studentAccount);
        paramInfoMap.put("record_code", specialRecordCode);

        SpecialTaskRecordDTO specialTaskRecordDTO = specialTrainRecordMapper.querySpecialRecordReportByAccount(paramInfoMap);

        return specialTaskRecordDTO;
    }

    /**
     * 查询专项训练考试状态
     * @param studentAccount
     * @param specialRecordCode
     * @return
     */
    public short querySpecialRecordStateByAccount(String studentAccount, String specialRecordCode)
    {
        Map<String, Object> paramInfoMap = new HashMap<String, Object>();
        paramInfoMap.put("student_account", studentAccount);
        paramInfoMap.put("record_code", specialRecordCode);

        return specialTrainRecordMapper.querySpecialRecordStateByAccount(paramInfoMap);
    }

    /**
     * 检查专项训练试卷的权限状态
     * @param paperCode
     * @return
     */
    public JsonResult checkSpecialPaperState(String paperCode)
    {
        JsonResult result = new JsonResult();

        try
        {
            PaperItem paperItem = paperItemMapper.selectNotBlobByPrimaryKey(paperCode);

            if(paperItem == null)
            {
                result.setFailMsg("该专项训练题目已经不存在，不能开始训练了~");
                return result;
            }

            if(paperItem.getStatus() == (short)0)
            {
                result.setFailMsg("该专项训练题目权限已经被管理员关闭，不能开始训练了~");
                return result;
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询专项训练试题权限执行异常了~");
            ex.printStackTrace();
        }

        result.setSucceed("成功");

        return  result;
    }


    /**
     * 开始专项训练
     * @param studentAccount
     * @param paperItemCode
     * @return
     */
    public SpecialTrainRecord startSpecialTrainExam(String studentAccount, String paperItemCode)
    {
        //全新的记录
        SpecialTrainRecord specialTrainRecord = new SpecialTrainRecord();
        specialTrainRecord.setCode(CommonUtil.GUID());
        specialTrainRecord.setPaperItemCode(paperItemCode);
        specialTrainRecord.setStudentNo(studentAccount);

        specialTrainRecord.setStartTime(new Date());

        //考试状态(0:未考试,1:考试成功,2:考试失败)
        specialTrainRecord.setExamState((short)0);
        //考试过程状态(0:未登录,1:已登录,2:开始试音,3:试音完成,5:正在答题,6:答题完成)
        specialTrainRecord.setFlowState((short)0);
        specialTrainRecord.setScore(new Float(0.0));
        //所有小题评测状态(0:未评测,1:评测完成,2:评测失败)
        specialTrainRecord.setEvalState((short)0);

        //分组状态(0:未分组,1:请求预分组,2:预分组成功,3:分组成功,4:分组失败/取消)
        specialTrainRecord.setGroupState((short)0);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        specialTrainRecord.setPaperState((short)0);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        specialTrainRecord.setDataState((short)0);

        //错误码(0:无错误,1:故障申报,2:网络故障,3:系统故障,4:同组失败,5:试音失败)
        specialTrainRecord.setErrorCode((short)0);

        int result = specialTrainRecordMapper.insertSelective(specialTrainRecord);

        if(result > 0)
        {
            return specialTrainRecord;
        }
        else
        {
            return null;
        }
    }

    public String getSpecialPaperItemCodeByRecordCode(String examRecordCode)
    {
        return paperItemMapper.getSpecialPaperItemCodeByRecordCode(examRecordCode);
    }

    public PaperItemBuffer getPaperItemBuffData(String examRecordCode)
    {
        return paperItemBufferMapper.getPaperItemBuffData(examRecordCode);
    }

    public String getPaperCodeByExamType(String examRecordCode, String examType)
    {
        //查询试题包代码
        String paperCode;

        if(Constants.TRAIN_EXAM.equals(examType))
        {
            paperCode = trainRecordMapper.getTrainPaperCodeByRecordCode(examRecordCode);
        }
        else if(Constants.SPECIAL_EXAM.equals(examType))
        {
            //专项训练查询试题代码
            paperCode = paperItemMapper.getSpecialPaperItemCodeByRecordCode(examRecordCode);
        }
        else
        {
            paperCode = paperBufferMapper.getPaperCodeByRecordCode(examRecordCode);
        }

        return paperCode;
    }


    /**
     * 查看专项训练报告服务
     * @param studentAccount
     * @param examRecordCode
     * @param paperItemTypeCode 试题类型代码
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     * @return
     */
    public JsonResult querySelfSpecialAnswerInfoService(String studentAccount, String examRecordCode, String paperItemTypeCode, String examType)
    {
        JsonResult result = new JsonResult();

        if(studentAccount != null && examRecordCode != null && paperItemTypeCode != null)
        {
            Map<String, Object> paramInfoMap = new HashMap<String, Object>();
            paramInfoMap.put("student_account", studentAccount);
            paramInfoMap.put("paper_item_type_code", paperItemTypeCode);
            paramInfoMap.put("record_code", examRecordCode);

            try
            {
                //查询试题包代码, 区分不同考试类型
                String paperCode = getPaperCodeByExamType(examRecordCode, examType);

                String paperFileName = "sim_" + paperCode;

                //朗读题题标准语音文件基础路径, 前台访问时，拼装服务器访问路劲 \paper\sim_690002\item02\standard_answer
                String standardAnswerItem02BasePath = (Constants.PaperPath + paperFileName + Constants.Standard_Answer_ITEM_02).replace('\\','/');

                //先读取学习引擎xml结果，解析xml内容
                String studyResultPath;

                if(Constants.TRAIN_EXAM.equals(examType))
                {
                    studyResultPath = trainRecordMapper.queryTrainExamStudyResultByRecordCode(paramInfoMap);
                }
                else if(Constants.SPECIAL_EXAM.equals(examType))
                {
                    //专项训练
                    studyResultPath = specialTrainRecordMapper.querySpecialStudyResultByRecordCode(paramInfoMap);
                }
                else
                {
                    studyResultPath = examRecordMapper.queryExamStudyResultByRecordCode(paramInfoMap);
                }

                if(studyResultPath != null)
                {
                    //"E:\\10 workspace_svn\\Manager\\Service\\cetsim-webservice\\target\\cetsim\\study_report\\2_read.xml"
                    String detailXMLPath = studyResultPath;

                    if(new File(detailXMLPath).exists())
                    {
                        Document doc = Dom4jUtils.getXMLByFilePath(detailXMLPath);

                        if(doc != null)
                        {
                            Element rootElement = doc.getRootElement();

                            Element readChapterElement = rootElement.element("read_chapter").element("rec_paper").element("read_chapter");

                            if(readChapterElement != null)
                            {
                                StudyReportChapterInfoDTO studyReportChapterInfoDTO = new StudyReportChapterInfoDTO();

                                //范文音频文件地址
                                studyReportChapterInfoDTO.setStandardMp3Path(standardAnswerItem02BasePath + "standard_read_chapter.mp3");

                                //先解析短文整体的得分情况
                                Double accuracyScore = readChapterElement.attribute("accuracy_score") == null ? 0.0 : Double.valueOf(readChapterElement.attribute("accuracy_score").getValue());
                                studyReportChapterInfoDTO.setAccuracyScore(accuracyScore);

                                Integer begPos = readChapterElement.attribute("beg_pos") == null ? 0 : Integer.valueOf(readChapterElement.attribute("beg_pos").getValue());
                                studyReportChapterInfoDTO.setBegPos(begPos);

                                String content = readChapterElement.attribute("content") == null ? "" : readChapterElement.attribute("content").getValue();
                                studyReportChapterInfoDTO.setChapterContent(content);

                                Integer endPos = readChapterElement.attribute("end_pos") == null ? 0 : Integer.valueOf(readChapterElement.attribute("end_pos").getValue());
                                studyReportChapterInfoDTO.setEndPos(endPos);

                                Integer exceptInfo = readChapterElement.attribute("except_info") == null ? 0 : Integer.valueOf(Integer.valueOf(readChapterElement.attribute("except_info").getValue()));
                                studyReportChapterInfoDTO.setExceptInfo(exceptInfo);

                                Double fluencyScore = readChapterElement.attribute("fluency_score") == null ? 0.0 : Double.valueOf(readChapterElement.attribute("fluency_score").getValue());
                                studyReportChapterInfoDTO.setFluencyScore(fluencyScore);

                                Double integrityScore = readChapterElement.attribute("integrity_score") == null ? 0.0 : Double.valueOf(readChapterElement.attribute("integrity_score").getValue());
                                studyReportChapterInfoDTO.setIntegrityScore(integrityScore);

                                boolean isRejected = "true".equals(readChapterElement.attribute("is_rejected") == null ? "false" : readChapterElement.attribute("is_rejected").getValue()) == true ? true : false;
                                studyReportChapterInfoDTO.setRejected(isRejected);

                                Double standardScore = readChapterElement.attribute("standard_score") == null ? 0.0 : Double.valueOf(readChapterElement.attribute("standard_score").getValue());
                                studyReportChapterInfoDTO.setStandardScore(standardScore);

                                Double totalScore = readChapterElement.attribute("total_score") == null ? 0.0 : Double.valueOf(readChapterElement.attribute("total_score").getValue());
                                studyReportChapterInfoDTO.setTotalScore(totalScore);

                                Integer wordCount = readChapterElement.attribute("word_count") == null ? 0 : Integer.valueOf(readChapterElement.attribute("word_count").getValue());
                                studyReportChapterInfoDTO.setWordCount(wordCount);

                                //封装句子
                                List<Element> sentencesElement = readChapterElement.elements("sentence");

                                List<StudyReportSentenceInfoDTO> studyReportSentenceInfoDTOList = new ArrayList<>();

                                for (Element sentenceElement : sentencesElement)
                                {
                                    StudyReportSentenceInfoDTO studyReportSentenceInfoDTO = new StudyReportSentenceInfoDTO();

                                    Double sentenceAccuracyScore = sentenceElement.attribute("accuracy_score") == null ? 0.0 : Double.valueOf(sentenceElement.attribute("accuracy_score").getValue());
                                    studyReportSentenceInfoDTO.setAccuracyScore(sentenceAccuracyScore);

                                    Integer sentenceBegPos = sentenceElement.attribute("beg_pos") == null ? 0 : Integer.valueOf(sentenceElement.attribute("beg_pos").getValue());
                                    studyReportSentenceInfoDTO.setBegPos(sentenceBegPos);

                                    String sentenceContent = sentenceElement.attribute("content") == null ? "" : sentenceElement.attribute("content").getValue();
                                    studyReportSentenceInfoDTO.setSentenceContent(sentenceContent);

                                    Integer sentenceEndPos = sentenceElement.attribute("end_pos") == null ? 0 : Integer.valueOf(sentenceElement.attribute("end_pos").getValue());
                                    studyReportSentenceInfoDTO.setEndPos(sentenceEndPos);

                                    Double sentenceFluencyScore = sentenceElement.attribute("fluency_score") == null ? 0.0 : Double.valueOf(sentenceElement.attribute("fluency_score").getValue());
                                    studyReportSentenceInfoDTO.setFluencyScore(sentenceFluencyScore);

                                    Integer sentenceIndex = sentenceElement.attribute("index") == null ? 0 : Integer.valueOf(sentenceElement.attribute("index").getValue());
                                    studyReportSentenceInfoDTO.setIndex(sentenceIndex);

                                    //句子示范音频文件地址 基础路径/standard_read_sentence_index
                                    studyReportSentenceInfoDTO.setSentenceMp3Path(standardAnswerItem02BasePath + "standard_read_sentence_" + sentenceIndex + ".mp3");

                                    Double sentenceStandardScore = sentenceElement.attribute("standard_score") == null ? 0.0 : Double.valueOf(sentenceElement.attribute("standard_score").getValue());
                                    studyReportSentenceInfoDTO.setStandardScore(sentenceStandardScore);

                                    Double sentenceTotalScore = sentenceElement.attribute("total_score") == null ? 0.0 : Double.valueOf(sentenceElement.attribute("total_score").getValue());
                                    studyReportSentenceInfoDTO.setTotalScore(sentenceTotalScore);

                                    Integer sentenceWordCount = sentenceElement.attribute("word_count") == null ? 0 : Integer.valueOf(sentenceElement.attribute("word_count").getValue());
                                    studyReportSentenceInfoDTO.setWordCount(sentenceWordCount);

                                    //3、封装句子中的单词
                                    List<Element> wordElements = sentenceElement.elements("word");
                                    List<StudyReportWordInfoDTO> studyReportWordInfoDTOList = new ArrayList<>();

                                    for (Element wordElement : wordElements)
                                    {
                                        StudyReportWordInfoDTO studyReportWordInfoDTO = new StudyReportWordInfoDTO();

                                        Integer wordDPMessage = wordElement.attribute("dp_message") == null ? 0 : Integer.valueOf(wordElement.attribute("dp_message").getValue());
                                        studyReportWordInfoDTO.setDpMessage(wordDPMessage);

                                        Integer wordBegPos = wordElement.attribute("beg_pos") == null ? 0 : Integer.valueOf(wordElement.attribute("beg_pos").getValue());
                                        studyReportWordInfoDTO.setBegPos(wordBegPos);

                                        String wordContent = wordElement.attribute("content") == null ? "" : wordElement.attribute("content").getValue();
                                        studyReportWordInfoDTO.setWordContent(wordContent);

                                        Integer wordEndPos = wordElement.attribute("end_pos") == null ? 0 : Integer.valueOf(wordElement.attribute("end_pos").getValue());
                                        studyReportWordInfoDTO.setEndPos(wordEndPos);

                                        Integer wordGlobalIndex = wordElement.attribute("global_index") == null ? 0 : Integer.valueOf(wordElement.attribute("global_index").getValue());
                                        studyReportWordInfoDTO.setGlobalIndex(wordGlobalIndex);

                                        Integer wordIndex = wordElement.attribute("index") == null ? 0 : Integer.valueOf(wordElement.attribute("index").getValue());
                                        studyReportWordInfoDTO.setIndex(wordIndex);

                                        Integer wordProperty = wordElement.attribute("property") == null ? 0 : Integer.valueOf(wordElement.attribute("property").getValue());
                                        studyReportWordInfoDTO.setProperty(wordProperty);

                                        Double wordTotalScore = wordElement.attribute("total_score") == null ? 0.0 : Double.valueOf(wordElement.attribute("total_score").getValue());
                                        studyReportWordInfoDTO.setTotalScore(wordTotalScore);

                                        //句子中的所有单词的评测结果
                                        studyReportWordInfoDTOList.add(studyReportWordInfoDTO);
                                    }

                                    studyReportSentenceInfoDTO.setStudyReportWordInfoDTOs(studyReportWordInfoDTOList);



                                    //加入所有句子
                                    studyReportSentenceInfoDTOList.add(studyReportSentenceInfoDTO);
                                }

                                studyReportChapterInfoDTO.setStudyReportSentenceInfoDTOs(studyReportSentenceInfoDTOList);

                                result.setSucceed(studyReportChapterInfoDTO);
                            }
                        }
                    }
                    else
                    {
                        result.setFailMsg("评分过程中出了点小问题，暂时看不了报告<br>" +
                                "                ~~~~(>_<)~~~~");
                    }
                }
                else
                {
                    result.setFailMsg("评分过程中出了点小问题，暂时看不了报告<br>" +
                            "                ~~~~(>_<)~~~~");
                }
            }
            catch (Exception ex)
            {
                result.setFailMsg("获取学生引擎返回报告异常");
                ex.printStackTrace();
            }
        }

        return result;
    }









}
