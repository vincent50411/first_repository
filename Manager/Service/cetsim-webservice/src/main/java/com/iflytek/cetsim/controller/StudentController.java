package com.iflytek.cetsim.controller;

import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.EngineCodeEnum;
import com.iflytek.cetsim.common.enums.ExamStateEnum;
import com.iflytek.cetsim.common.exception.BusinessException;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.AppContext;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.dto.*;
import com.iflytek.cetsim.model.*;
import com.iflytek.cetsim.service.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/5/6.
 */
@Controller
@RequestMapping("/api/student")
public class StudentController extends BaseController
{
    @Autowired
    private ExamTaskReportService examTaskReportService;

    @Autowired
    private StudentTaskService studentTaskService;

    @Autowired
    private StudentsExamService studentsExamService;

    @Autowired
    private StudentMgrService studentMgrService;

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private TrainTaskService trainTaskService;

    @Autowired
    private TrainTaskRoomService trainTaskRoomService;

    /**
     * 获取学生所在测试班级列表
     * @param studentAccount
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/myClassList", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult queryMyAllTestClassList(String studentAccount)
    {
        JsonResult result = this.getJson();

        try
        {
            List<StudentClassDTO> myClasList = studentMgrService.queryMyAllTestClassList(studentAccount);

            result.setSucceed(myClasList);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取所在测试班级列表异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 任务列表
     * @param studentAccount 学生账号
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/tasklist",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult getTaskList(String studentAccount, int status, int pageIndex, int pageSize, String orderColumnName, String orderCode)
    {
        JsonResult result = this.getJson();

        try
        {
            PageModel<StudentTaskDTO_new> pageModel = new PageModel<StudentTaskDTO_new>(pageIndex,pageSize);

            List<StudentTaskDTO_new> studentTaskDTO_newsList = studentTaskService.listStudentTasks(studentAccount, status, pageIndex, pageSize, orderColumnName, orderCode);

            pageModel.setTotal(studentTaskService.StudentTasksListCount(studentAccount, status, pageIndex, pageSize));
            pageModel.setData(studentTaskDTO_newsList);

            result.setSucceed(pageModel);
        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }

    /**
     * 开始考试
     * @param studentAccount
     * @param examTaskCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value = "/beginexam")
    @ResponseBody
    public JsonResult beginExam(String studentAccount, String examTaskCode, boolean always)
    {
        JsonResult result = this.getJson();
        try {
            result.setSucceed(studentsExamService.startEcp(studentAccount, examTaskCode));
        }
        catch (Exception ex) {
            LogUtil.error("--> 开始考试出现异常：" + ex.getMessage());
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 成绩分布
     * @param examTaskCode
     * @param studentAccount
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/examrecorddistribution",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult GetExamRecordDistribution(String examTaskCode, String studentAccount){
        JsonResult result = this.getJson();
        try {
            result.setSucceed(studentTaskService.listStudentRecords(examTaskCode, studentAccount));
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


    /**
     * 获取模拟测试排名
     * @param examTaskCode 模拟测试任务代码
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/rankinfo",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult GetRankInfo(String examTaskCode)
    {
        JsonResult result = this.getJson();
        try{
            result.setSucceed(studentTaskService.listRank(examTaskCode));
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取成绩报告
     * @param examRecordCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/examresult",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult GetExamResult(String examRecordCode)
    {
        JsonResult result = this.getJson();
        try{
            result.setSucceed(studentsExamService.listExamResult(examRecordCode));
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


    /**
     * 获取试卷包信息
     * @param examRecordCode
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     * @param request
     * @return
     */
    //@IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/taskExamInfo", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getTaskExamInfoHandler(String examRecordCode, String examType,  HttpServletRequest request)
    {
        JsonResult result = this.getJson();

        //返回给前台的数据
        Map<String, Map<String, String>> examPaperInfoMap = null;

        try
        {
            //E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/
            String serverBasePath = request.getServletContext().getRealPath("");

            //替换文件分隔符
            String basePath = serverBasePath.replace('\\','/');

            //全局缓存服务器基础路径
            AppContext.getInstance().serverBasePath = serverBasePath;

            //根据考试记录，查询本次考试的试卷代码，再判断试卷是否已经被解压生成，如果已经存在，则不需要重复解压
            String paperaperCode = questionsService.getPaperCodeByExamType(examRecordCode, examType);

            String paperFileName = "sim_" + paperaperCode;

            //试卷包地址, sim_试卷代码.rpk
            String rpkFilePath = paperFileName + ".rpk";

            //只判断试卷包解压后的文件夹是否存在
            File paperDicFile = new File(basePath + Constants.PaperPath + paperFileName);

            //判断rpk是否已经存在，如果不存在，则写入文件系统
            if(!paperDicFile.exists())
            {
                File rpkFile = new File(basePath + Constants.PaperPath + rpkFilePath);

                //从数据库中获取试卷包的二进制字节数组
                PaperBuffer paperBuffer;

                //专项训练试题包
                PaperItemBuffer paperItemBuffer;

                byte[] paperRPKData;

                if(Constants.TRAIN_EXAM.equals(examType))
                {
                    paperBuffer = trainTaskService.getTrainPaperBuffData(examRecordCode);

                    paperRPKData = paperBuffer.getBuffer();
                }
                else if(Constants.SPECIAL_EXAM.equals(examType))
                {
                    //专项训练试题包
                    paperItemBuffer = questionsService.getPaperItemBuffData(examRecordCode);

                    paperRPKData = paperItemBuffer.getBuffer();
                }
                else
                {
                    paperBuffer = studentTaskService.getPaperBuffData(examRecordCode);

                    paperRPKData = paperBuffer.getBuffer();
                }

                LogUtil.debug("----------------从数据库中读取试卷包二进制字节数据, 开始-----------------");

                rpkFile.createNewFile();

                //将二进制字节数组写入文件系统
                FileUtils.writeByteArrayToFile(rpkFile, paperRPKData);

                LogUtil.debug("----------------从数据库中读取试卷包二进制字节数据, 写入文件系统结束，试卷地址:" + rpkFile.getPath() + "-----------------");
            }

            String examRPKPath = rpkFilePath;
            //String examRPKPath = studentTaskService.getPaperPath(studentTaskId);//"cet4/sim02.rpk";//studentTaskService.getPaperPath(studentTaskId);

            LogUtil.debug("----------------开始读取试卷包信息，试卷包地址:" + examRPKPath + "-----------------");

            if(examRPKPath != null && !"".equals(examRPKPath))
            {
                //读取试卷包信息(视频内容){item01:{L1:001.mpg , L2:001.mpg}...}
                examPaperInfoMap = examTaskReportService.getPaperRPKInfoDataService(paperFileName, examRPKPath, basePath);

                if(examPaperInfoMap == null)
                {
                    result.setFailMsg("试卷包文件不存在");
                }
                else
                {
                    result.setSucceed(examPaperInfoMap);
                }

                //试卷压缩包没用了，需要清理
                File exitRPKFile = new File(rpkFilePath);
                if (exitRPKFile != null)
                {
                    exitRPKFile.delete();
                }
            }

            LogUtil.debug("-------------------------------读取试卷包信息结束--------------------------");
        }
        catch (Exception ex)
        {
            //如果获取试卷包出现异常，则需要删除所有试卷包信息


            LogUtil.info("--> Exception:" + ex.getMessage());
            LogUtil.info("--> Exception:" + ex.fillInStackTrace().getMessage());
            ex.printStackTrace();
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }

    /**
     * 获取任务答题包信息
     * @param examRecordCode
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     * @param request
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/taskAnswerInfo", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getTaskReportInfoHandler(String studentAccount, String examRecordCode, String examType, HttpServletRequest request)
    {
        JsonResult result = this.getJson();

        try
        {
            //E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/
            String serverBasePath = request.getServletContext().getRealPath("");

            //替换文件分隔符
            String basePath = serverBasePath.replace('\\','/');

            //全局缓存服务器基础路径
            AppContext.getInstance().serverBasePath = serverBasePath;

            //考生答题录音包地址
            String answerPath;
            if("train".equals(examType))
            {
                //自主考试记录
                answerPath = trainTaskService.getTrainTaskAnswerPath(examRecordCode);//"cet4/1001_answer.rpk";//studentTaskService.getAnswerPath(studentTaskId);
            }
            else
            {
                //模拟考试记录
                answerPath = studentTaskService.getAnswerPath(examRecordCode);//"cet4/1001_answer.rpk";//studentTaskService.getAnswerPath(studentTaskId);
            }

            //判断考试自己的答题包是否存在，如果不存在，则返回提示信息
            if(!new File(basePath + Constants.ANSWER_PATH + "/" + answerPath).exists())
            {
                result.setFailMsg("考生答题包未上传");

                LogUtil.debug("-->考生答题包未上传, 答题包文件为：" + answerPath);
                return result;
            }

            //队友录音包地址，4级考试第一题自我介绍题型使用
            String partnerAnswerPath;
            if("train".equals(examType))
            {
                //队友自主考试，根据自己的记录号查队友的记录
                partnerAnswerPath = trainTaskService.getPartnerTrainTaskAnswerPath(examRecordCode);//"cet4/1002_answer.rpk";//studentTaskService.getPartnerAnswerPath(studentTaskId);
            }
            else
            {
                partnerAnswerPath = studentTaskService.getPartnerAnswerPath(examRecordCode);//"cet4/1002_answer.rpk";//studentTaskService.getPartnerAnswerPath(studentTaskId);
            }

            LogUtil.debug("-->开始解压考生答题录音包文件, 自己答题包地址：" + answerPath + "; 队友录音地址：" + partnerAnswerPath);

            if(answerPath != null && !"".equals(answerPath))
            {
                LogUtil.debug("-->解压自己答题包开始");

                //答题包解压在服务端生成答题结果,如果rpk已经被解压，则不能重新解压，否则会将已经合成的第四题录音文件删除
                String mediaServerPath = examTaskReportService.getTaskAnswerInfoDataService(answerPath, basePath);

                LogUtil.debug("-->解压自己答题包结束");

                //自己的帐号
                String mySelfAccount = studentAccount;

                String partnerMediaServerPath = null;

                //队友的帐号
                String partnerAccount = "";

                if(partnerAnswerPath != null && !"".equals(partnerAnswerPath))
                {
                    LogUtil.debug("-->开始解压队友答题包");
                    //(队友录音包解压后路劲)在服务端生成答题结果,客户端路径写死
                    if(new File(basePath + Constants.ANSWER_PATH + "/" + partnerAnswerPath).exists())
                    {
                        //队友rpk文件存在的情况下，才去解压rpk
                        partnerMediaServerPath = examTaskReportService.getTaskAnswerInfoDataService(partnerAnswerPath, basePath);

                        LogUtil.debug("-->解压队友答题包结束, 开始查询队友账号信息");

                        if("train".equals(examType))
                        {
                            partnerAccount = trainTaskService.getPartnerAccountByTrainCode(examRecordCode);
                        }
                        else
                        {
                            partnerAccount = studentTaskService.getPartnerAccount(examRecordCode);
                        }

                        LogUtil.debug("-->查询队友账号信息结束，队友账号:" + partnerAccount);
                    }
                    else
                    {
                        LogUtil.debug("-->队友答题包未上传，文件地址:" + partnerAnswerPath);
                    }
                }

                LogUtil.info("--> 自己的帐号:" + mySelfAccount + "; 队友的帐号:" + partnerAccount);

                //答题结果封装, 最后一题语音合成独立处理
                ExamAnswerDTO examAnswerDTO = getExamAnswerDTO(serverBasePath, mediaServerPath, partnerMediaServerPath, mySelfAccount, partnerAccount);

                //根据引擎返回码，设置每一题的温馨提示信息
                examAnswerDTO = tipMessageByEnginCode(examAnswerDTO, examRecordCode);

                //本人的录音包服务器地址
                result.setSucceed(examAnswerDTO);
            }
        }
        catch (Exception ex)
        {
            LogUtil.info("--> Exception:" + ex.getMessage());
            LogUtil.info("--> Exception:" + ex.fillInStackTrace().getMessage());
            ex.printStackTrace();
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 根据考试记录，查询每一题的评测结果，根据引擎码获取提示信息
     * @param examAnswerDTO
     * @param examRecordCode
     * @return
     */
    private ExamAnswerDTO tipMessageByEnginCode(ExamAnswerDTO examAnswerDTO, String examRecordCode)
    {
        //查询小题评测引擎返回码，对应界面展示，给每一小题提示信息
        List<EvalRecordResultDTO> evalRecordResultDTOs = studentTaskService.listEvalListByRecordCode(examRecordCode);
        for(EvalRecordResultDTO evalRecordResultDTO : evalRecordResultDTOs)
        {
            if("04_02".equals(evalRecordResultDTO.getPaperItemTypeCode()))
            {
                //根据每一题的引擎码获取对应的提示信息
                examAnswerDTO.setReadTipMesage(EngineCodeEnum.getTipMessage(evalRecordResultDTO.getEngineCode()));
            }
            else if("04_03_01".equals(evalRecordResultDTO.getPaperItemTypeCode()))
            {
                examAnswerDTO.setAnswerOneTipMesage(EngineCodeEnum.getTipMessage(evalRecordResultDTO.getEngineCode()));
            }
            else if("04_03_02".equals(evalRecordResultDTO.getPaperItemTypeCode()))
            {
                examAnswerDTO.setAnswerTwoTipMesage(EngineCodeEnum.getTipMessage(evalRecordResultDTO.getEngineCode()));
            }
            else if("04_04".equals(evalRecordResultDTO.getPaperItemTypeCode()))
            {
                examAnswerDTO.setPresTipMesage(EngineCodeEnum.getTipMessage(evalRecordResultDTO.getEngineCode()));
            }
            else if("04_05".equals(evalRecordResultDTO.getPaperItemTypeCode()))
            {
                examAnswerDTO.setPairTipMesage(EngineCodeEnum.getTipMessage(evalRecordResultDTO.getEngineCode()));
            }
        }

        return examAnswerDTO;
    }


    /**
     * 处理队友的音频文件合成操作
     * @param examRecordCode
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     * @param request
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/partnerSounder")
    @ResponseBody
    public JsonResult operatePartnerSounder(String studentAccount, String examRecordCode, String examType, HttpServletRequest request)
    {
        JsonResult result = this.getJson();

        try
        {
            //E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/
            String serverBasePath = request.getServletContext().getRealPath("");

            //替换文件分隔符
            String basePath = serverBasePath.replace('\\','/');

            //全局缓存服务器基础路径
            AppContext.getInstance().serverBasePath = serverBasePath;

            //自己的帐号
            String mySelfAccount = studentAccount;

            //考生答题录音包地址
            String answerPath;
            if("train".equals(examType))
            {
                //自主考试记录
                answerPath = trainTaskService.getTrainTaskAnswerPath(examRecordCode);//"cet4/1001_answer.rpk";//studentTaskService.getAnswerPath(studentTaskId);
            }
            else
            {
                //模拟考试记录
                answerPath = studentTaskService.getAnswerPath(examRecordCode);
            }

            String examAnswerDirPath = answerPath.substring(0, answerPath.indexOf("."));

            //自己的录音文件解压后服务器路径，队友和自己的最后一题录音文件合成后，也存放在自己的目录下
            String myselfSoundServerPath = basePath + Constants.ANSWER_PATH + "/" + examAnswerDirPath + "/";

            //CET4题型中最后一题为2人对话录音，需要将2人的对话录音独立音频文件叠加合并为一个同步音频播放
            String cet4LastItemAnsMp3Path = myselfSoundServerPath + mySelfAccount + "_pair.mp3";

            LogUtil.debug("-->自己的第四题录音文件地址:" + cet4LastItemAnsMp3Path);

            //判断自己的录音文件是否存在, 文件命名规则为 考试记录ID/考生账号_pair.mp3（如果题型发生改变，则规则需要改变）
            if(!new File(cet4LastItemAnsMp3Path).exists())
            {
                result.setFailMsg("自己的录音文件找不到");
                LogUtil.debug("-->自己的录音文件找不到，文件地址:" + cet4LastItemAnsMp3Path);
                return result;
            }

            //队友录音包地址，4级考试第一题自我介绍题型使用
            String partnerAnswerPath;
            if("train".equals(examType))
            {
                //队友自主考试，根据自己的记录号查队友的记录
                partnerAnswerPath = trainTaskService.getPartnerTrainTaskAnswerPath(examRecordCode);//"cet4/1002_answer.rpk";//studentTaskService.getPartnerAnswerPath(studentTaskId);
            }
            else
            {
                partnerAnswerPath = studentTaskService.getPartnerAnswerPath(examRecordCode);//"cet4/1002_answer.rpk";//studentTaskService.getPartnerAnswerPath(studentTaskId);
            }

            String partnerRPKSoundFilePath = basePath + Constants.ANSWER_PATH + "/" + partnerAnswerPath;

            //判断队友录音文件是否存在
            if(!new File(partnerRPKSoundFilePath).exists())
            {
                result.setFailMsg("队友答题包未上传");

                LogUtil.debug("-->队友答题包未上传，文件地址:" + partnerRPKSoundFilePath);

                return result;
            }

            String partnerMediaServerPath;

            //队友的帐号
            String partnerAccount;

            if("train".equals(examType))
            {
                partnerAccount = trainTaskService.getPartnerAccountByTrainCode(examRecordCode);
            }
            else
            {
                partnerAccount = studentTaskService.getPartnerAccount(examRecordCode);
            }

            LogUtil.debug("-->查询队友账号信息结束，队友账号:" + partnerAccount);

            LogUtil.debug("-->开始解压队友答题包");

            //队友录音包解压后路劲,如果rpk已经被解压，则不能重新解压，否则会将已经合成的第四题录音文件删除
            partnerMediaServerPath = examTaskReportService.getTaskAnswerInfoDataService(partnerAnswerPath, basePath);
            LogUtil.debug("-->解压队友答题包结束, 开始查询队友账号信息");

            //CET4最后一题2人对话的录音文件地址(自己和队友的)
            String cet4LastItemPartnerAnsMp3Path = partnerMediaServerPath + partnerAccount + "_pair.mp3";

            LogUtil.debug("-->队友的第四题录音文件地址:" + cet4LastItemPartnerAnsMp3Path);

            //最后合成的录音文件名，以自己的考试记录命名
            String togetherFileName = mySelfAccount + "_pair_together";

            String togetherMP3ServierPath = Constants.ANSWER_PATH + "/" + examAnswerDirPath + "/" + togetherFileName + ".mp3";

            try
            {
                LogUtil.debug("-------------- 开始音频文件转换 ----------------------");
                Long startTime = new Date().getTime();

                examTaskReportService.sndChannelfile(basePath, cet4LastItemAnsMp3Path, cet4LastItemPartnerAnsMp3Path, myselfSoundServerPath, togetherFileName);

                Long endTime = new Date().getTime();
                LogUtil.debug("-------------- 音频文件转换结束, 耗时：" + (endTime - startTime) + " ----------------------");

            }
            catch (BusinessException bex)
            {
                LogUtil.debug("-------------- 音频文件合并异常：" + bex.getMessage() + " ----------------------");
            }

            //本人的录音包服务器地址
            result.setSucceed(togetherMP3ServierPath);
        }
        catch (Exception ex)
        {
            LogUtil.info("--> Exception:" + ex.getMessage());
            LogUtil.info("--> Exception:" + ex.fillInStackTrace().getMessage());
            ex.printStackTrace();
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }

    /**
     * 封装答题结果
     * @param serverBasePath 服务器系统路径
     * @param mediaServerPath 系统相对路径
     * @param partnerMediaServerPath
     * @param mySelfAccount
     * @param partnerAccount
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    private ExamAnswerDTO getExamAnswerDTO(String serverBasePath, String mediaServerPath, String partnerMediaServerPath, String mySelfAccount, String partnerAccount)
    {
        //如果录音文件不存在，则录音统一为null，前台提示录音文件不存在
        ExamAnswerDTO ExamAnswerDTO = new ExamAnswerDTO();

        ExamAnswerDTO.setAccount(mySelfAccount);

        ExamAnswerDTO.setMePath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_me.mp3"));

        //队友的路劲
        if(partnerAccount != null)
        {
            ExamAnswerDTO.setPartnerAccount(partnerAccount);

            String partnerMePath = checkFileExit(serverBasePath, partnerMediaServerPath + partnerAccount + "_me.mp3");
            String partnerAnswerOnePath = checkFileExit(serverBasePath, partnerMediaServerPath + partnerAccount + "_ans1.mp3");
            String partnerAnswerTwoPath = checkFileExit(serverBasePath, partnerMediaServerPath + partnerAccount + "_ans2.mp3");
            String partnerPresPath = checkFileExit(serverBasePath, partnerMediaServerPath + partnerAccount + "_pres.mp3");

            if(partnerMePath != null)
            {
                ExamAnswerDTO.setPartnerMePath(partnerMePath);
            }
            else
            {
                LogUtil.info("--> 队友的自我介绍题录音文件不存在, 地址:" + partnerMePath);
            }
            if(partnerAnswerOnePath != null)
            {
                ExamAnswerDTO.setPartnerAnswerOnePath(partnerAnswerOnePath);
            }
            else
            {
                LogUtil.info("--> 队友的answer_1录音文件不存在, 地址:" + partnerAnswerOnePath);
            }
            if(partnerAnswerTwoPath != null)
            {
                ExamAnswerDTO.setPartnerAnswerTwoPath(partnerAnswerTwoPath);
            }
            else
            {
                LogUtil.info("--> 队友的answer_2录音文件不存在, 地址:" + partnerAnswerTwoPath);
            }
            if(partnerPresPath != null)
            {
                ExamAnswerDTO.setPartnerPresPath(partnerPresPath);
            }
            else
            {
                LogUtil.info("--> 队友的自pres录音文件不存在, 地址:" + partnerPresPath);
            }
        }
        else
        {
            ExamAnswerDTO.setPartnerMePath(null);
            ExamAnswerDTO.setPartnerPresPath(null);
            ExamAnswerDTO.setPartnerAnswerOnePath(null);
            ExamAnswerDTO.setPartnerAnswerTwoPath(null);
        }

        ExamAnswerDTO.setReadPath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_read.mp3"));

        ExamAnswerDTO.setAnswerOnePath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_ans1.mp3"));

        ExamAnswerDTO.setAnswerTwoPath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_ans2.mp3"));

        ExamAnswerDTO.setPresPath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_pres.mp3"));

        ExamAnswerDTO.setPairPath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_pair.mp3"));

        //合成录音文件地址
        //ExamAnswerDTO.setTogetherPairPath(checkFileExit(serverBasePath, mediaServerPath + mySelfAccount + "_pair_together.mp3"));

        return ExamAnswerDTO;
    }

    private String checkFileExit(String serverBasePath, String path)
    {
        //以全路径判断文件是否存在
        if(new File(serverBasePath + path).exists())
        {
            //返回相对路径
            return path;
        }

        return null;
    }


    /**
     * 查询管理员已经发布的所有专项训练试题列表, 如果管理员关闭了权限，学生可以看到，但是不能开始练习
     * @param studentAccount
     * @param paperItemQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryQuestionList")
    @ResponseBody
    public JsonResult queryQuestionList(String studentAccount, PaperItemQueryDTO paperItemQueryDTO, HttpSession session)
    {
        JsonResult result = new JsonResult();

        try
        {
            Account userInfo = (Account)session.getAttribute("loginUser");

            List<PaperItemQueryDTO> paperItemList = questionsService.selectQuestionListByAccount(userInfo.getAccount(), paperItemQueryDTO);
            int countPaperItems = questionsService.countQuestionListByAccount(userInfo.getAccount(), paperItemQueryDTO);

            PageModel<PaperItemQueryDTO> pageModel = new PageModel<PaperItemQueryDTO>(paperItemQueryDTO.getPageIndex(), paperItemQueryDTO.getPageSize());
            pageModel.setData(paperItemList);

            if(paperItemList != null && paperItemList.size() > 0)
            {
                pageModel.setTotal(countPaperItems);
            }
            else
            {
                pageModel.setTotal(0);
            }

            result.setSucceed(pageModel);
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询试题列表异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 开始专项训练
     * @param studentAccount
     * @param paperItemCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/startSpecialTrainExam")
    @ResponseBody
    public JsonResult startSpecialTrainExam(String studentAccount, String paperItemCode)
    {
        JsonResult result = new JsonResult();

        try
        {
            //先检查试题的权限，如果试题权限被关闭，则不允许开始练习
            result = questionsService.checkSpecialPaperState(paperItemCode);

            if(result.getCode() == Constants.FAIL)
            {
                return result;
            }

            SpecialTrainRecord specialTrainRecord = questionsService.startSpecialTrainExam(studentAccount, paperItemCode);

            if(specialTrainRecord != null)
            {
                result.setSucceed(specialTrainRecord);
            }
            else
            {
                result.setFailMsg("--> 开始专项训练失败");
            }

        }
        catch (Exception ex)
        {
            result.setFailMsg("--> 开始专项训练异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 查询学生的专项训练测试记录
     * @param studentAccount 学生账号
     * @param paperItemQueryDTO 查询记录
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/querySpecialRecordList")
    @ResponseBody
    public JsonResult querySpecialRecordList(String studentAccount, PaperItemQueryDTO paperItemQueryDTO)
    {
        JsonResult result = new JsonResult();

        try
        {
            List<SpecialTaskRecordDTO> studentSpecialRecordList = questionsService.querySpecialRecordList(studentAccount, paperItemQueryDTO);

            PageModel<SpecialTaskRecordDTO> pageModel = new PageModel<SpecialTaskRecordDTO>(paperItemQueryDTO.getPageIndex(), paperItemQueryDTO.getPageSize());
            pageModel.setData(studentSpecialRecordList);

            if(studentSpecialRecordList != null && studentSpecialRecordList.size() > 0)
            {
                pageModel.setTotal(studentSpecialRecordList.size());
            }
            else
            {
                pageModel.setTotal(0);
            }

            result.setSucceed(pageModel);
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询专项训练记录异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 查询专项训练报告的状态
     * @param studentAccount
     * @param specialRecordCode
     * @param request
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/querySpecialRecordState")
    @ResponseBody
    public JsonResult querySpecialRecordState(String studentAccount, String specialRecordCode, HttpServletRequest request)
    {
        JsonResult result = new JsonResult();

        try {
            short examState = questionsService.querySpecialRecordStateByAccount(studentAccount, specialRecordCode);

            //判断考试的状态
            if (examState != ExamStateEnum.Exam_success.getStateCode()) {
                result.setFailMsg("考试没有正常结束，无法正常查看报告详情~");
                return result;
            }

            result.setSucceed("可以正常查看报告了");
        }
        catch (Exception ex)
        {
            result.setSucceed("查询专项训练考试记录状态异常，无法正常查看报告详情~");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 查看专项训练记录报告
     * @param studentAccount
     * @param specialRecordCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/querySpecialRecordReport")
    @ResponseBody
    public JsonResult querySpecialRecordReport(String studentAccount, String specialRecordCode, HttpServletRequest request)
    {
        JsonResult result = new JsonResult();

        try
        {
            short examState = questionsService.querySpecialRecordStateByAccount(studentAccount, specialRecordCode);

            //判断考试的状态
            if(examState != ExamStateEnum.Exam_success.getStateCode())
            {
                result.setFailMsg("考试没有正常结束，无法正常查看报告详情~");
                return result;
            }

            SpecialTaskRecordDTO studentSpecialRecordReport = questionsService.querySpecialRecordReport(studentAccount, specialRecordCode);

            //处理答题录音文件地址
            if(studentSpecialRecordReport != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String examStartTime = sdf.format(studentSpecialRecordReport.getStartTime());

                studentSpecialRecordReport.setStartTimeStr(examStartTime);

                //学生提交的录音文件原始地址rpk
                String dataPath = studentSpecialRecordReport.getDataPath();

                //E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/
                String serverBasePath = request.getServletContext().getRealPath("");

                //替换文件分隔符
                String basePath = serverBasePath.replace('\\','/');

                //全局缓存服务器基础路径
                AppContext.getInstance().serverBasePath = serverBasePath;

                //判断考试自己的答题包是否存在，如果不存在，则返回提示信息
                if(!new File(basePath + Constants.ANSWER_PATH + "/" + dataPath).exists())
                {
                    result.setSucceed(studentSpecialRecordReport, "考生答题包未上传");

                    LogUtil.debug("-->考生答题包未上传, 答题包文件为：" + dataPath);
                    return result;
                }

                LogUtil.debug("********************* 解压自己的专项训练答题包开始 **********************");

                //答题包解压在服务端生成答题结果,如果rpk已经被解压，则不能重新解压，否则会将已经合成的第四题录音文件删除
                String mediaServerPath = examTaskReportService.getTaskAnswerInfoDataService(dataPath, basePath);

                LogUtil.debug("********************* 解压自己的专项训练答题包结束 **********************");

                String readAnswerPath = mediaServerPath + studentAccount + "_read.mp3";

                studentSpecialRecordReport.setAnswerPath(readAnswerPath);
                LogUtil.debug("********************* [" + studentAccount + "]的专项训练语音地址 **********************");

                result.setSucceed(studentSpecialRecordReport);
            }
            else
            {
                result.setFailMsg("获取专项训练报告失败");
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询专项训练成绩报告异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 查询自己的答题信息，包括自己的录音文件地址、学习引擎返回的评价地址
     * @param studentAccount
     * @param specialRecordCode
     * @param paperItemTypeCode  试题类型
     * @param examType sim:模拟考试，train:自主考试, special:专项训练
     * @return
     */
    //@IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/querySelfSpecialAnswerInfo")
    @ResponseBody
    public JsonResult querySelfSpecialAnswerInfo(String studentAccount, String specialRecordCode, String paperItemTypeCode, String examType)
    {
        JsonResult result = new JsonResult();

        result = questionsService.querySelfSpecialAnswerInfoService(studentAccount, specialRecordCode, paperItemTypeCode, examType);

        return result;
    }

    /**
     * 查询所有可用的自主考试试卷列表
     * @param paperQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryTrainTaskPaperList")
    @ResponseBody
    public JsonResult queryTrainTaskPaperList(PaperQueryDTO paperQueryDTO, HttpSession session)
    {
        JsonResult result = new JsonResult();

        try
        {
            //当前登录的用户
            Account userInfo = (Account)session.getAttribute("loginUser");

            List<PaperInfoDTO>  paperInfoDTOs = trainTaskService.queryTrainTaskPaperList(userInfo, paperQueryDTO);

            PageModel<PaperInfoDTO> pageModel = new PageModel<PaperInfoDTO>(paperQueryDTO.getPageIndex(), paperQueryDTO.getPageSize());
            pageModel.setData(paperInfoDTOs);

            if(paperInfoDTOs != null && paperInfoDTOs.size() > 0)
            {
                pageModel.setTotal(paperInfoDTOs.size());
            }
            else
            {
                pageModel.setTotal(0);
            }

            result.setSucceed(pageModel);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取自主考试试卷列表失败");
            ex.printStackTrace();
        }

        return  result;
    }

    /**
     * 查询开放的自主考试房间列表
     * @param studentAccount
     * @param trainTaskRoomQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryTrainTaskRoomList")
    @ResponseBody
    public JsonResult queryTrainTaskRoomList(String studentAccount, TrainTaskRoomQueryDTO trainTaskRoomQueryDTO)
    {
        JsonResult result = new JsonResult();

        try
        {
            //step-1 先查询自己是否已经加入房间(属于非正常退出房间事件)
            trainTaskRoomService.clearUserExitTrainRoom(studentAccount);

            //step-2 重新查询可加入到房间
            List<TrainTaskRoomInfoDTO> trainTaskRoomInfoDTOs = trainTaskRoomService.queryTrainTaskRoomList(studentAccount, trainTaskRoomQueryDTO);

            result.setSucceed(trainTaskRoomInfoDTOs);

        }
        catch (Exception ex)
        {
            result.setFailMsg("创建自主考试房间异常");
            ex.printStackTrace();
        }

        return result;
    }




    /**
     * 创建自主考试房间, 需要检查试卷的权限
     * @param studentAccount 学生账号 A角色
     * @param paperCode 试卷代码
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/createTrainTaskRoom")
    @ResponseBody
    public JsonResult createTrainTaskRoom(String studentAccount, String paperCode)
    {
        //先检查试卷的权限是否被关闭，如果被关闭，则提示无法正常组建考试
        JsonResult result = trainTaskRoomService.checkTrainPaperState(paperCode);

        if(result.getCode() == Constants.FAIL)
        {
            return result;
        }

        return trainTaskRoomService.createTrainTaskRoomService(studentAccount, paperCode);
    }

    /**
     * 加入房间
     * @param studentAccount 自己的账号
     * @param candidateACode 房主的账号
     * @param roomCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/joinTrainTaskRoom")
    @ResponseBody
    public JsonResult joinTrainTaskRoom(String studentAccount, String candidateACode, String roomCode)
    {
        JsonResult result;
        synchronized(this){
            result = trainTaskRoomService.joinTrainTaskRoomService(studentAccount, candidateACode, roomCode);
        }

        return result;
    }

    /**
     * 房主A退出房间
     * @param roomCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/candidateAQuitRoom")
    @ResponseBody
    public JsonResult candidateAQuitRoom(String roomCode)
    {
        //只是退出房间
        return trainTaskRoomService.candidateAQuitRoomService(roomCode, "quitRoom");
    }

    /**
     * 队友退出房间
     * @param roomCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/candidateBQuitRoom")
    @ResponseBody
    public JsonResult candidateBQuitRoom(String roomCode)
    {
        //房间加入失败，要重置房间状态
        AppContext.getInstance().removeRoomStatus(roomCode);

        return trainTaskRoomService.candidateBQuitRoomService(roomCode);
    }

    /**
     * 通知房间内的A/B同时开始考试，启动客户端考试机程序
     * 需要判断组队有没有成功，如果没有成功，则不允许启动考试
     * @param roomCode
     * @param candidateACode
     * @param candidateBCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/startTrainTaskExam")
    @ResponseBody
    public JsonResult startTrainTaskExam(String roomCode, String candidateACode, String candidateBCode, String paperCode)
    {
        //先检查试卷的权限是否被关闭，如果被关闭，则提示无法正常组建考试
        JsonResult result = trainTaskRoomService.checkTrainPaperState(paperCode);

        if(result.getCode() == Constants.FAIL)
        {
            return result;
        }

        TrainRoom trainRoom = trainTaskRoomService.queryTrainTaskRoomByRoomCode(roomCode);

        if(trainRoom == null)
        {
            //房间已经不存在
            result.setFailMsg("房间已经不存在，不能开始考试了~");
            return result;
        }
        else
        {
            String bAccount = trainRoom.getCandidateBCode();

            //队友账号为空时，不允许考试
            if(bAccount == null || "".equals(bAccount) || bAccount.length() == 0)
            {
                result.setFailMsg("没有队友，请等待队友加入");
                return result;
            }
        }

        return trainTaskRoomService.startTraimTaskExam(roomCode, candidateACode, candidateBCode, paperCode);

    }

    /**
     * 查询自主考试记录
     * @param trainTaskRecordQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryTrainTaskRecordList")
    @ResponseBody
    public JsonResult queryTrainTaskRecordList(TrainTaskRecordQueryDTO trainTaskRecordQueryDTO)
    {
        JsonResult result = new JsonResult();

        try
        {
            List<TrainTaskRecordInfoDTO> studentTrainTaskRecordList = trainTaskService.queryTrainTaskRecordList(trainTaskRecordQueryDTO);

            PageModel<TrainTaskRecordInfoDTO> pageModel = new PageModel<TrainTaskRecordInfoDTO>(trainTaskRecordQueryDTO.getPageIndex(), trainTaskRecordQueryDTO.getPageSize());
            pageModel.setData(studentTrainTaskRecordList);

            if(studentTrainTaskRecordList != null && studentTrainTaskRecordList.size() > 0)
            {
                pageModel.setTotal(studentTrainTaskRecordList.size());
            }
            else
            {
                pageModel.setTotal(0);
            }

            result.setSucceed(pageModel);
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询自主考试记录异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 查询学生端成绩轨迹
     * @param trainTaskRecordQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/queryTrainTaskScoreTrack")
    @ResponseBody
    public JsonResult queryTrainTaskScoreTrack(TrainTaskRecordQueryDTO trainTaskRecordQueryDTO)
    {
        JsonResult result = new JsonResult();
        try
        {
            result = trainTaskService.queryTrainTaskScoreTrack(trainTaskRecordQueryDTO);
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询学生成绩轨迹异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 获取自主考试成绩报告头部信息
     * @param trainRecordCode
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/queryTrainTaskExamResult",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult queryTrainTaskExamResult(String trainRecordCode)
    {
        JsonResult result = this.getJson();
        try{
            result.setSucceed(trainTaskService.listTrainTaskExamResult(trainRecordCode));
        }catch (Exception ex){
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


}
