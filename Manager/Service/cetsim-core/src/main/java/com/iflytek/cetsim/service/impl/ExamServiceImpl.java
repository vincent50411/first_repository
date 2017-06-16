package com.iflytek.cetsim.service.impl;

import com.alibaba.fastjson.JSON;
import com.iflytek.cetsim.base.dao.BaseRecordDao;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.*;
import com.iflytek.cetsim.common.json.ExamJsonResult;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.*;
import com.iflytek.cetsim.dao.*;
import com.iflytek.cetsim.dto.EvalResultDTO;
import com.iflytek.cetsim.dto.ExamLoginDTO;
import com.iflytek.cetsim.dto.RecordDTO;
import com.iflytek.cetsim.model.*;
import com.iflytek.cetsim.service.ExamService;
import com.jfinal.core.Const;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 *
 * Created by code2life on 2017/3/10.
 */
@Service
@Transactional
public class ExamServiceImpl implements ExamService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExamRecordMapper simRecordDao;

    @Autowired
    private TrainRecordMapper trainRecordDao;

    @Autowired
    private SpecialTrainRecordMapper specialRecordDao;

    @Autowired
    private ScoreDetailMapper scoreDetailDao;

    @Autowired
    private PaperBufferMapper paperBufferDao;

    @Autowired
    private PaperItemBufferMapper itemBufferDao;

    @Autowired
    private PaperItemTypeMapper itemTypeDao;

    private static final int A_HOUR = 1000*60*60;

    @Override
    public boolean verifyToken(String recordId, String recordType, String token){
        BaseRecordDao dao = selectCorrectDao(recordType);
        Map<String, Object> param = new HashMap<>();
        param.put("recordId", recordId);
        param.put("token", token);
        List<String> result = dao.verifyToken(param);
        return result != null && result.size() > 0;
    }


    @Override
    public ExamLoginDTO login(String recordId, String recordType, String ip, String mac) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        Map<String, Object> param = new HashMap<>();
        param.put("recordId", recordId);
        List<ExamLoginDTO> dtoList = dao.examineeLogin(param);
        if(dtoList.size() > 0) {
            String token = UUID.randomUUID().toString();
            ExamLoginDTO result = dtoList.get(0);
            result.setIp(ip);
            result.setMac(mac);
            result.setToken(token);
            param.put("ip", ip);
            param.put("mac", mac);
            param.put("account", result.getAccountId());
            param.put("token", token);
            param.put("startTime",new Date());
            dao.updateIpMac(param);
            dao.updateToken(param);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void updateFlowState(String recordId, String recordType, Integer state) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("state", state);
        param.put("code", recordId);
        dao.updateFlowState(param);

        //如果状态为开始试音, 解除分组锁
        if(state == FlowStateEnum.testStart.getFlowCode() && Constants.groupLock.containsKey(recordId)) {
            Constants.groupLock.remove(recordId);
        }
    }

    @Override
    public void updateGroupState(String recordId, String recordType, Integer state) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("state", state);
        param.put("code", recordId);
        dao.updateGroupState(param);

        //如果状态为分组异常, 解除分组锁
        if(state == GroupStateEnum.GROUP_CANCEL.getCode() && Constants.groupLock.containsKey(recordId)) {
            Constants.groupLock.remove(recordId);
        }
    }

    @Override
    public void updatePaperState(String recordId, String recordType, Integer state) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("state", state);
        param.put("code", recordId);
        dao.updatePaperState(param);
    }

    @Override
    public void updateDataState(String recordId, String recordType, Integer state) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("state", state);
        param.put("code", recordId);
        param.put("startTime",new Date());
        int updateFlag = dao.updateDataState(param);
        List<RecordDTO> record = dao.getCurrentRecord(recordId);
        //如果更新为上传成功, 并且已经评测成功, 则直接将exam_state也更新为成功
        if (updateFlag > 0 && state == DataStateEnum.UPLOAD_SUCCESS.getCode() && record.size() > 0 &&
                record.get(0).getEvalState() == EvalStateEnum.SUCCESS.getCode()){
            HashMap<String, Object> args = new HashMap<>();
            args.put("examState", (short)ExamStateEnum.Exam_success.getStateCode());
            args.put("code", recordId);
            dao.updateExamState(args);
        }

    }

    @Override
    public void updateErrorCode(String recordId, String recordType, Integer errorCode) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("error", errorCode);
        param.put("code", recordId);
        dao.updateErrorCode(param);
    }

    @Override
    public void groupException(String recordId, String recordType, String partnerRecordId) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        dao.cleanPartner(recordId);
        dao.cancelGroup(partnerRecordId);

        //如果存在锁解除分组锁
        if(Constants.groupLock.containsKey(recordId)) {
            Constants.groupLock.remove(recordId);
        }
        if(Constants.groupLock.containsKey(partnerRecordId)) {
            Constants.groupLock.remove(partnerRecordId);
        }
    }

    @Override
    public void exitGroup(String recordId, String recordType) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        RecordDTO currentRecord = dao.getCurrentRecord(recordId).get(0);
        String partnerId = currentRecord.getPartnerRecordId();
        if(partnerId != null && !partnerId.equals("")) {
            dao.cancelGroup(partnerId);

            //如果存在锁解除分组锁
            if(Constants.groupLock.containsKey(partnerId)) {
                Constants.groupLock.remove(partnerId);
            }
        }
        dao.cancelGroup(recordId);

        //如果存在锁解除分组锁
        if(Constants.groupLock.containsKey(recordId)) {
            Constants.groupLock.remove(recordId);
        }
    }

    @Override
    public List<ExamLoginDTO> checkGroup(String recordId, String recordType) throws Exception {
        List<ExamLoginDTO> group = new ArrayList<>();
        BaseRecordDao dao = selectCorrectDao(recordType);
        RecordDTO currentRecord = dao.getCurrentRecord(recordId).get(0);
        Integer groupState = currentRecord.getGroupState();
        String partnerRecordId = currentRecord.getPartnerRecordId();

        //step0 每隔20个请求清理分组锁, 防止in查询爆炸
        if((++Constants.groupCheckCount) % Constants.cleanFlag == 0) {
            Long current = System.currentTimeMillis();
            Iterator<Map.Entry<String, Long>> it = Constants.groupLock.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Long> entry=it.next();
                if(current - entry.getValue() > A_HOUR)
                    it.remove();
            }
        }

        //step1 防止出现异常记录, 先判断记录是否有效
        if(groupState == GroupStateEnum.GROUP_CANCEL.getCode()) {
            logger.info("当前考试记录分组状态异常: [recordId]" + recordId);
            throw new Exception("同组考生意外退出");
        }
        //step2 更新自己的状态为请求预分组
        updateGroupState(recordId, recordType, GroupStateEnum.PRE_GROUP.getCode());
        //step3 判断是否存在partnerRecordId, 查询是否有可分组考生
        Map<String, Object> partnerQuery = new HashMap<>();
        Map<String, Object> selfQuery = new HashMap<>();

        selfQuery.put("recordId", recordId);
        partnerQuery.put("exceptSelf", recordId);
        partnerQuery.put("currentIp", currentRecord.getIp());
        partnerQuery.put("taskCode", currentRecord.getTaskId());
        partnerQuery.put("studentNo", currentRecord.getStudentNo());
        //3.1 模拟考试模式添加taskId条件只查询同一个任务的记录
        partnerQuery.put("taskId", currentRecord.getTaskId());
        //3.2 专项和自由考生模式添加paperCode条件查询同一个试卷的记录
        partnerQuery.put("paperCode", currentRecord.getPaperCode());
        //3.3 查询同伴
        boolean hasPartner = false;
        if(partnerRecordId != null && !partnerRecordId.equals("")) {
            hasPartner = true;
            partnerQuery.put("recordId", partnerRecordId);
        }
        List<ExamLoginDTO> partnerCandidates = dao.selectPartner(partnerQuery);

        //step4 如果存在可分组考生, 将两个结果添加到结果集, 并写入role和partnerId
        if(partnerCandidates !=null && partnerCandidates.size() > 0) {

            //step4.1 如果查询结果全部是不能取的值, 即除同伴外被锁住的记录, 直接返回
            if(!hasPartner) {
                Iterator<ExamLoginDTO> it = partnerCandidates.iterator();
                while(it.hasNext()) {
                    ExamLoginDTO cur = it.next();
                    if(Constants.groupLock.containsKey(cur.getRecordId())) {
                        it.remove();
                    }
                }
            }
            if(partnerCandidates.size() == 0) {
                return group;
            }

            ExamLoginDTO me = dao.examineeLogin(selfQuery).get(0);
            ExamLoginDTO other = partnerCandidates.get(0);

            //step4.2 锁住两条记录
            if(!Constants.groupLock.containsKey(me.getRecordId()))
                Constants.groupLock.put(me.getRecordId(), System.currentTimeMillis());
            if(!Constants.groupLock.containsKey(other.getRecordId()))
                Constants.groupLock.put(other.getRecordId(), System.currentTimeMillis());

            //step4.3 互相写入recordId
            selfQuery.put("partnerRecordId", other.getRecordId());
            partnerQuery.put("partnerRecordId", me.getRecordId());
            partnerQuery.put("recordId", other.getRecordId());
            //step4.4 判断设置为redis主机的客户端和考试角色
            if(other.getRecordId().compareTo(me.getRecordId()) <= 0) {
                me.setExamRole(Constants.ROLE_A);
                other.setExamRole(Constants.ROLE_B);
                me.setMaster(1);
            } else {
                me.setExamRole(Constants.ROLE_B);
                other.setExamRole(Constants.ROLE_A);
                other.setMaster(1);
            }
            selfQuery.put("examRole", me.getExamRole());
            partnerQuery.put("examRole", other.getExamRole());

            //step4.5 写入数据库
            dao.updateGroupInfo(selfQuery);
            dao.updateGroupInfo(partnerQuery);

            group.add(me);
            group.add(other);
        }
        return group;
    }

    @Override
    public Integer getPartnerGroupState(String recordId, String recordType, String partnerRecordId) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("partnerRecordId", partnerRecordId);
        param.put("code", recordId);
        short partnerGroupState = dao.getPartnerGroupState(param);
        return (int)partnerGroupState;
    }

    @Override
    public Integer getPartnerFlowState(String recordId, String recordType, String partnerRecordId) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> param = new HashMap<>();
        param.put("partnerRecordId", partnerRecordId);
        param.put("code", recordId);

        //如果发现考生抛弃了同伴另外找了个机器考试, 直接返回0, 告诉被抛弃的同伴不用等了
        if(dao.checkHasNewRecord(param) > 0)
            return 0;

        short partnerFlowState = dao.getPartnerFlowState(param);
        return (int)partnerFlowState;
    }

    @Override
    public byte[] downloadPaper(String recordType, String recordCode) {
        if (ExamModeEnum.SPECIAL_TRAIN.getCode().equals(recordType)) {
            return itemBufferDao.getPaperItemBuffData(recordCode).getBuffer();
        } else if(ExamModeEnum.FREE_TRAIN.getCode().equals(recordType)){
            return paperBufferDao.getPaperBuffData4Train(recordCode).getBuffer();
        } else {
            return paperBufferDao.getPaperBuffData(recordCode).getBuffer();
        }
    }

    @Override
    public void uploadAnswer(String recordId, String recordType, String dataPath) {
        BaseRecordDao dao = selectCorrectDao(recordType);
        HashMap<String, Object> params = new HashMap<>();
        params.put("dataPath", dataPath);
        params.put("code", recordId);
        dao.updateDataPath(params);
    }

    @Override
    public boolean evaluateItem(String recordId, String recordType, String datPath, String itemCode, String paperCode) {
        //初始化
        ScoreDetail scoreDetail = new ScoreDetail();
        scoreDetail.setRecordCode(recordId);
        scoreDetail.setPaperItemTypeCode(itemCode);
        scoreDetail.setSocre((float)0.0);
        scoreDetail.setEngineCode(EngineCodeEnum.Normal.getCode());
        scoreDetail.setEvalStatus(EvalStateEnum.DEFAULT.getCode());
        //1. insert eval_record
        scoreDetailDao.insertSelective(scoreDetail);
        //2. http req
        Map<String, String> params = new HashMap<>();
        params.put(Constants.DATFILE,datPath);
        String url = new PropertiesLoader("cetsim.properties").getProperty("evalServer");
        logger.info(String.format("评分开始[record]%s[item]%s", recordId, itemCode));
        //3. analyse json and update
        try {
            String evalResult = HttpClientThreadUtil.singlePostJSON(url, params);
            logger.info("评分结束![eval result]" + evalResult);
            if (evalResult.equals(""))
                logger.info("evalResult为空");
            EvalResultDTO evalRet = JSON.parseObject(evalResult, EvalResultDTO.class);
            if(evalRet.getSuccess()) {
                double score =  evalRet.getScore();
                scoreDetail.setSocre((float)score);
                scoreDetail.setEngineCode(Integer.parseInt(evalRet.getRet()));
                scoreDetail.setEvalStatus(EvalStateEnum.SUCCESS.getCode());
            }
            else
            {
                scoreDetail.setEvalStatus(EvalStateEnum.FAIL.getCode());
            }

            scoreDetailDao.updateScoreDetail(scoreDetail);

            return true;
        }catch (Exception ex) {
            scoreDetail.setEvalStatus(EvalStateEnum.FAIL.getCode());
            scoreDetailDao.updateScoreDetail(scoreDetail);
            logger.error("评测引擎调用失败: ", ex);
            return false;
        }
    }

    @Override
    public void evaluateFail(String recordId, String recordType, Integer engineCode, String itemCode, String paperCode) {
        //插入当前数据
        ScoreDetail scoreDetail = new ScoreDetail();
        scoreDetail.setRecordCode(recordId);
        scoreDetail.setPaperItemTypeCode(itemCode);
        scoreDetail.setSocre((float)0.0);
        //进制转换
        scoreDetail.setEngineCode(engineCode);
        scoreDetail.setEvalStatus(EvalStateEnum.FAIL.getCode());
        scoreDetailDao.insertSelective(scoreDetail);
    }

    @Override
    public void evaluateFinish(String recordId, String recordType, boolean success) {
        //获得题目信息
        List<ScoreDetail> evaluateInfo = scoreDetailDao.selectAllByRecordId(recordId);
        ScoreDetail scoreDetail = new ScoreDetail();
        if(evaluateInfo.size() == 0) {
            logger.info(String.format("当前考试记录状态异常,未找到所给: [recordId]%s 对应的数据",recordId));
            return;
        }
        //获得小题所有数目以及对应的weight等信息
        List<PaperItemType> paperItemInfo = itemTypeDao.getAllInfo();
        float totalScore = 0;

        //先判断题目类型是否为special专题训练，若是不必按照引擎码判断，直接算总分(special题目只有一题)
        if (ExamModeEnum.SPECIAL_TRAIN.getCode().equals(recordType)){
            scoreDetail = evaluateInfo.get(0);
            if (scoreDetail == null){
                logger.info(String.format("当前考试记录状态异常,未找到所给: [recordId]%s下[PaperItemTypeCode]%s 对应的数据.",recordId,scoreDetail.getPaperItemTypeCode()));
            }
            //获得所有小题weight信息
            PaperItemType paperItem = new PaperItemType();
            String itemType = scoreDetail.getPaperItemTypeCode();
            float itemWeight = 0;
            for (int i = 0;i< paperItemInfo.size();i++){
                paperItem = paperItemInfo.get(i);
                if (itemType.equals(paperItem.getCode())){
                    itemWeight = paperItem.getWeight();
                }
            }
            //计算总分，并更新
            totalScore = scoreDetail.getSocre() / 5 * itemWeight;
            UpdateEvalTotalScore(recordId, recordType, totalScore, success);
            return ;
        }

        //下面是非special题目，需要判断引擎码
        //对于五个语音全部都有非0异常返回码，则该考生总分判定为0分；
        int flag = 0;
        for (int size=0; size < evaluateInfo.size(); size++){
            scoreDetail = evaluateInfo.get(size);
            int engine = scoreDetail.getEngineCode();
            if (engine == EngineCodeEnum.Normal.getCode())
                break;
            else
                flag++;
        }
        //若5个全为非0，总分直接为0
        if (flag == evaluateInfo.size()){
            UpdateEvalTotalScore(recordId, recordType, (float)0, success);
            return ;
        }
        //遍历每道题,求总分
        for (int size=0; size < evaluateInfo.size(); size++){
            scoreDetail = evaluateInfo.get(size);
            if (scoreDetail == null){
                logger.info(String.format("当前考试记录状态异常,未找到所给: [recordId]%s 下[PaperItemTypeCode]%s 对应的数据.",recordId,scoreDetail.getPaperItemTypeCode()));
            }
            //获得所有小题weight信息
            PaperItemType paperItem;
            String itemType = scoreDetail.getPaperItemTypeCode();
            float itemWeight = 0;
            for (int i = 0;i< paperItemInfo.size();i++){
                paperItem = paperItemInfo.get(i);
                if (itemType.equals(paperItem.getCode())){
                    itemWeight = paperItem.getWeight();
                }
            }

            int engine = scoreDetail.getEngineCode();

            if(!EngineCodeEnum.IsKownCode(engine))
            {
                scoreDetail.setSocre((float)0.0);
            }
            else if(engine == EngineCodeEnum.NoScene.getCode() && scoreDetail.getSocre() > 3.0){
                scoreDetail.setSocre((float)3.0);
            }
            totalScore += scoreDetail.getSocre() / 5 * itemWeight;
        }
        //更新总分数和评测状态信息
        UpdateEvalTotalScore(recordId, recordType, totalScore, success);
    }

    private void UpdateEvalTotalScore(String recordId, String recordType, float totalScore, boolean success){
        HashMap<String, Object> params = new HashMap<>();
        params.put("totalScore", totalScore);
        params.put("code", recordId);
        if (success){
            params.put("evalState",EvalStateEnum.SUCCESS.getCode());
        }
        else{
            params.put("evalState",EvalStateEnum.FAIL.getCode());
        }
        //根据不同的类型，更新不同的表
        if(ExamModeEnum.SIM_EXAM.getCode().equals(recordType)) {
            scoreDetailDao.updateSimTotalScore(params);;
        } else if(ExamModeEnum.FREE_TRAIN.getCode().equals(recordType)) {
            scoreDetailDao.updateTrainTotalScore(params);;
        } else if(ExamModeEnum.SPECIAL_TRAIN.getCode().equals(recordType)) {
            scoreDetailDao.updateSpecialTotalScore(params);
        }
    }

    @Override
    public boolean evaluateReadChapter(String recordId, String recordType, String baseDir, String wavPath, String paperCode){
        String readChapter = "04_02";

        String pprFile = Paths.get(baseDir, Constants.PprPath, paperCode + "_" + readChapter +".ppr").toString();
        String xmlFile = Paths.get(baseDir , Constants.XMLFILE, recordId + ".xml").toString();
        //调用学习引擎
        Map<String, String> params = new HashMap<>();
        params.put(Constants.WAVFILE,wavPath);
        params.put(Constants.PPRFILE,pprFile);
        params.put(Constants.XMLFILE,xmlFile);
        String url = new PropertiesLoader("cetsim.properties").getProperty("enStudy");
        try {
            logger.info(String.format("朗读评分开始[record]%s[paper]%s", recordId, paperCode));
            String evalResult = HttpClientThreadUtil.singlePostJSON(url, params);
            logger.info("朗读学习引擎评分结束![eval result]" + evalResult);
            EvalResultDTO evalRet = JSON.parseObject(evalResult, EvalResultDTO.class);

            if (evalRet.getSuccess()) {
                //获取返回码
                String ret = evalRet.getRet();
                //成功调用学习引擎的才将xml路径写入detail字段
                Map<String, String> xmlParams = new HashMap<>();
                xmlParams.put("ret", ret);
                xmlParams.put("code",recordId);
                xmlParams.put("itemCode",readChapter);
                xmlParams.put("xmlFile",xmlFile);
                scoreDetailDao.insertXmlFile(xmlParams);
            } else {
                logger.error("学习引擎返回了失败的信息!");
            }
            return true;
        } catch (Exception ex) {
            logger.error("学习引擎调用出错: ", ex);
            return false;
        }
    }


    private BaseRecordDao selectCorrectDao(String recordType) {
        if(ExamModeEnum.SIM_EXAM.getCode().equals(recordType)) {
            return simRecordDao;
        } else if(ExamModeEnum.FREE_TRAIN.getCode().equals(recordType)) {
            return trainRecordDao;
        } else if(ExamModeEnum.SPECIAL_TRAIN.getCode().equals(recordType)) {
            return specialRecordDao;
        } else {
            //默认使用模考的数据访问对象
            return simRecordDao;
        }
    }

}
