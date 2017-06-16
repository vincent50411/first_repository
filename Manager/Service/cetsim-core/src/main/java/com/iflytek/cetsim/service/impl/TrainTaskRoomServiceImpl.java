package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.TrainRoomState;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.AppContext;
import com.iflytek.cetsim.common.utils.CommonUtil;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.dao.TrainRecordMapper;
import com.iflytek.cetsim.dao.TrainRoomMapper;
import com.iflytek.cetsim.dto.TrainTaskRoomMessage;
import com.iflytek.cetsim.dto.TrainTaskRoomQueryDTO;
import com.iflytek.cetsim.model.*;
import com.iflytek.cetsim.service.AccountService;
import com.iflytek.cetsim.service.PaperService;
import com.iflytek.cetsim.service.TrainTaskRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自主考试房间服务
 * Created by Administrator on 2017/5/11.
 */
@Service
@Transactional
public class TrainTaskRoomServiceImpl implements TrainTaskRoomService
{
    @Autowired
    private SocketServerManager socketServerManager;

    @Autowired
    private TrainRoomMapper trainRoomMapper;

    @Autowired
    private PaperService paperService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TrainRecordMapper trainRecordMapper;


    public JsonResult checkTrainPaperState(String paperCode)
    {
        JsonResult result = new JsonResult();
        //先检查试卷的权限是否被关闭，如果被关闭，则提示无法正常组建考试
        Paper paper = paperService.selectPaperByCode(paperCode);

        if(paper == null)
        {
            result.setFailMsg("该试卷已经不存在，无法组建自主考试了~");
            return result;
        }

        if(paper != null && !paper.getAllowFreeUseage())
        {
            result.setFailMsg("该试卷权限已经被管理员关闭，无法组建自主考试了~");
            return result;
        }

        result.setSucceed("成功");

        return result;
    }


    /**
     * 根据自己的账号，查询是否已经加入其他房间，如果有，则需要先退出
     * @param account
     */
    public void clearExitJoinRoom(String account)
    {
        ////step-1 加入自主考试房间之前，先检查该用户是否已经加入过房间，如果有，则需要先删除
        TrainRoom exitTrainRoom = selectExitJoinRoomByAccount(account);
        if(exitTrainRoom != null)
        {
            LogUtil.info("--> 该学生[" + account + "]已经加入了房间[" + exitTrainRoom.getCode() + "], 现在开始初始化房间信息");

            //先修改该房间初始状态，将队友信息移除
            exitTrainRoom.setCandidateBCode("");

            //未分组
            exitTrainRoom.setState(TrainRoomState.UN_GROUP.getRoomStateCode());

            //修改房间信息
            int delResult = updateTrainTaskRoom(exitTrainRoom);

            if(delResult > 0)
            {
                LogUtil.info("--> 初始化房间成功");

                TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
                trainTaskRoomMessage.setRoomCode(exitTrainRoom.getCode());
                trainTaskRoomMessage.setCandidateACode(exitTrainRoom.getCandidateACode());
                trainTaskRoomMessage.setCandidateBCode(exitTrainRoom.getCandidateBCode());

                //初始化房间成功后，通知房主A:B已经退出该房间
                socketServerManager.sendMessageToClient(Constants.CANDIDATE_B_QUIT_TASK_ROOM_EVENT, trainTaskRoomMessage);
            }
            else
            {
                LogUtil.info("--> 初始化房间失败");
            }
        }

    }

    /**
     * 清理用户已经存在的房间，可能是加入、也可能是创建的
     * @param studentAccount
     */
    public void clearUserExitTrainRoom(String studentAccount)
    {
        //先清理已经创建的房间，发送事件给客户端
        clearExitCreateRomm(studentAccount);

        //清理已经加入过的房间，发送事件给客户端
        clearExitJoinRoom(studentAccount);
    }

    private void clearExitCreateRomm(String studentAccount)
    {
        TrainRoom exitTrainRoom = queryTrainTaskRoomByAccount(studentAccount);
        if(exitTrainRoom != null)
        {
            LogUtil.info("--> 该学生[" + studentAccount + "]已经创建了房间[" + exitTrainRoom.getCode() + "], 现在开始删除");
            //先删除该房间
            int delResult = deleteExitTrainTaskRoom(exitTrainRoom.getCode());

            if(delResult > 0)
            {
                LogUtil.info("--> 删除旧房间成功");

                TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
                trainTaskRoomMessage.setRoomCode(exitTrainRoom.getCode());

                //删除旧房间成功后，通知所有客户端
                socketServerManager.sendMessageToClient(Constants.CANDIDATE_A_QUIT_TASK_ROOM_EVENT, trainTaskRoomMessage);
            }
            else
            {
                LogUtil.info("--> 删除房间失败");
            }
        }
    }

    /**
     * 创建房间
     * @param studentAccount
     * @param paperCode
     * @return
     */
    public JsonResult createTrainTaskRoomService(String studentAccount, String paperCode)
    {
        JsonResult result = new JsonResult();

        try
        {
            ////step-1 创建一条自主考试房间之前，先检查该用户是否已经创建过房间，如果有，则需要先删除
            clearExitCreateRomm(studentAccount);

            //step-3 创建一条自主考试房间记录
            TrainRoom trainRoom = insertTrainTaskRoomService(studentAccount, paperCode);

            //查询试卷的信息
            Paper paper = paperService.selectPaperByCode(trainRoom.getPaperCode());

            Account userInfo = accountService.findUserByAccount(studentAccount);

            TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
            trainTaskRoomMessage.setRoomCode(trainRoom.getCode());
            trainTaskRoomMessage.setPaperCode(trainRoom.getPaperCode());
            if(paper != null)
            {
                trainTaskRoomMessage.setPaperName(paper.getName());
                trainTaskRoomMessage.setPaperCode(paper.getCode());
                trainTaskRoomMessage.setPaperTypeCode(paper.getPaperTypeCode());
            }
            trainTaskRoomMessage.setCandidateACode(trainRoom.getCandidateACode());

            if(userInfo != null)
            {
                trainTaskRoomMessage.setCandidateAName(userInfo.getName());
                //图像地址
                trainTaskRoomMessage.setCandidateAPhoto(userInfo.getReserved1());
            }

            //socketServerManager.roomMap.put(trainTaskRoomMessage.getRoomCode(), trainRoom);

            result.setSucceed(trainTaskRoomMessage);
        }
        catch (Exception ex)
        {
            result.setFailMsg("创建自主考试房间异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 加入房间, 需要判断待加入到房间是否已经被人提前加入
     * @param studentAccount
     * @param candidateACode
     * @param roomCode
     * @return
     */
    public JsonResult joinTrainTaskRoomService(String studentAccount, String candidateACode, String roomCode)
    {
        JsonResult result = new JsonResult();

        if(AppContext.getInstance().getRoomMap().containsKey(roomCode) && AppContext.getInstance().getRoomMap().get(roomCode))
        {
            //检查加入到房间数据库记录是否被其他人占用（有一种可能是其他人加入房间后，刷新界面或者非正常退出）
            TrainRoom exitRoomInfo = trainRoomMapper.selectUnGroupRoom(roomCode);

            if(exitRoomInfo == null)
            {
                result.setFailMsg("房间已满，请选择其他房间加入");
                return result;
            }
        }

        //将房间状态修改为已满,在房间退出 或者加入异常时，重置状态
        AppContext.getInstance().getRoomMap().put(roomCode, true);

        try
        {
            ////step-1 加入自主考试房间之前，先检查该用户是否已经加入过房间，如果有，则需要先删除
            clearExitJoinRoom(studentAccount);

            //step-2 查询待加入的房间信息,必须是未分组的
            TrainRoom newRoom = trainRoomMapper.selectUnGroupRoom(roomCode);

            if(newRoom == null)
            {
                result.setFailMsg("房间不存在，请刷新界面重新加入");
                return result;
            }

            newRoom.setCandidateBCode(studentAccount);
            newRoom.setState(TrainRoomState.GROUPE_SUCC.getRoomStateCode());

            //step-3 将队友信息更新到房间信息中
            int joinResult = updateTrainTaskRoom(newRoom);

            if(joinResult > 0)
            {
                //查询试卷的信息
                Paper paper = paperService.selectPaperByCode(newRoom.getPaperCode());

                //A的基本信息
                Account candidateAInfo = accountService.findUserByAccount(candidateACode);

                //B的基本信息
                Account candidateBInfo = accountService.findUserByAccount(studentAccount);

                TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
                trainTaskRoomMessage.setRoomCode(newRoom.getCode());
                trainTaskRoomMessage.setPaperCode(newRoom.getPaperCode());
                trainTaskRoomMessage.setPaperTypeCode(paper.getPaperTypeCode());

                if(paper != null)
                {
                    trainTaskRoomMessage.setPaperName(paper.getName());
                }

                trainTaskRoomMessage.setCandidateBCode(studentAccount);

                if(candidateBInfo != null)
                {
                    trainTaskRoomMessage.setCandidateBName(candidateBInfo.getName());
                    //自己的图像
                    trainTaskRoomMessage.setCandidateBPhoto(candidateBInfo.getReserved1());
                }

                trainTaskRoomMessage.setCandidateACode(candidateACode);
                if(candidateAInfo != null)
                {
                    trainTaskRoomMessage.setCandidateAName(candidateAInfo.getName());
                    //房主的图像
                    trainTaskRoomMessage.setCandidateAPhoto(candidateAInfo.getReserved1());
                }

                //通知客户端，有队友加入房间
                socketServerManager.sendMessageToClient(Constants.CANDIDATE_B_JION_TASK_ROOM_EVENT, trainTaskRoomMessage);

                //通知所有客户端，有房间组队成功的事件
                socketServerManager.sendMessageToClient(Constants.TRAIN_TASK_ROOM_GROUPED_EVENT, trainTaskRoomMessage);

                result.setSucceed(trainTaskRoomMessage);
            }
            else
            {
                result.setFailMsg("加入房间失败");

                //房间加入失败，要重置房间状态
                AppContext.getInstance().removeRoomStatus(roomCode);
            }
        }
        catch (Exception ex)
        {
            //房间加入失败，要重置房间状态
            AppContext.getInstance().removeRoomStatus(roomCode);

            result.setFailMsg("加入房间失败");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * A主动退出房间
     * @param roomCode
     * @return
     */
    public JsonResult candidateAQuitRoomService(String roomCode, String type)
    {
        JsonResult result = new JsonResult();
        try
        {
            int delResult = trainRoomMapper.deleteExitTrainTaskRoom(roomCode);

            if(delResult > 0)
            {
                result.setSucceed("退出房间成功");
            }
            else
            {
                result.setFailMsg("退出房间失败");
            }

            //发送房主A撤销房间的通知事件
            TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
            trainTaskRoomMessage.setRoomCode(roomCode);

            if("startExam".equals(type))
            {
                trainTaskRoomMessage.setMessageType(type);
                //如果是房间开始考试撤销房间，则增加一个标示位，让前端可以个性化提示信息
                trainTaskRoomMessage.setMessage("自主考试已经开始，房间被销毁，请进入考试机进行考试~");
            }

            socketServerManager.sendMessageToClient(Constants.CANDIDATE_A_QUIT_TASK_ROOM_EVENT, trainTaskRoomMessage);

        }
        catch (Exception ex)
        {
            result.setFailMsg("退出房间异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * B主动退出房间
     * @param roomCode
     * @return
     */
    public JsonResult candidateBQuitRoomService(String roomCode)
    {
        JsonResult result = new JsonResult();
        try
        {
            TrainRoom trainRoom = new TrainRoom();
            trainRoom.setCode(roomCode);
            trainRoom.setCandidateBCode("");
            trainRoom.setState(TrainRoomState.UN_GROUP.getRoomStateCode());

            int updateResult = trainRoomMapper.updateByExampleSelective(trainRoom);

            if(updateResult > 0)
            {
                result.setSucceed("退出房间成功");
            }
            else
            {
                result.setFailMsg("退出房间失败");
            }

            //发送房主A撤销房间的通知事件
            TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
            trainTaskRoomMessage.setRoomCode(roomCode);

            socketServerManager.sendMessageToClient(Constants.CANDIDATE_B_QUIT_TASK_ROOM_EVENT, trainTaskRoomMessage);

        }
        catch (Exception ex)
        {
            result.setFailMsg("退出房间异常");
            ex.printStackTrace();
        }

        return result;
    }


    /**
     * 通知房间内的A/B同时开始考试，启动客户端考试机程序, 考试机器一旦触发启动，则强制退出AB
     * @param roomCode
     * @param candidateACode
     * @param candidateBCode
     * @return
     */
    public JsonResult startTraimTaskExam(String roomCode, String candidateACode, String candidateBCode, String paperCode)
    {
        JsonResult result = new JsonResult();
        try
        {
            //step-1 在自主考试记录表中增加2条考试记录
            String canditateBRecordCode = CommonUtil.GUID();
            String candidateARecordCode = saveCandidateARecord(candidateACode, paperCode, canditateBRecordCode);

            if(candidateARecordCode != null)
            {
                int bRs = saveCandidateBRecord(canditateBRecordCode, candidateBCode, paperCode, candidateARecordCode);

                if(bRs > 0)
                {
                    Paper paper = paperService.selectPaperByCode(paperCode);

                    //通知房间开始考试
                    TrainTaskRoomMessage trainTaskRoomMessage = new TrainTaskRoomMessage();
                    trainTaskRoomMessage.setRoomCode(roomCode);
                    trainTaskRoomMessage.setCandidateACode(candidateACode);
                    trainTaskRoomMessage.setCandidateBCode(candidateBCode);

                    if(paper != null)
                    {
                        //试卷类型代码
                        trainTaskRoomMessage.setPaperTypeCode(paper.getPaperTypeCode());
                    }

                    trainTaskRoomMessage.setCandidateARecord(candidateARecordCode);
                    trainTaskRoomMessage.setCandidateBRecord(canditateBRecordCode);

                    TrainRecord trainRecord = new TrainRecord();
                    trainRecord.setCode(candidateARecordCode);

                    socketServerManager.sendMessageToClient(Constants.START_TASK_EXAM_EVENT, trainTaskRoomMessage);

                    //强制A退出房间
                    candidateAQuitRoomService(roomCode, "startExam");

                    result.setSucceed(trainRecord);

                }
                else
                {
                    //开始考试失败，因为创建A的考试记录失败
                    result.setFailMsg("启动考试失败，B创建考试记录失败");

                    return result;
                }
            }
            else
            {
                //开始考试失败，因为创建A的考试记录失败
                result.setFailMsg("启动考试失败，A创建考试记录失败");

                return result;
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("开始考试异常");
            ex.printStackTrace();
        }

        return result;
    }





    /**
     * 查询房间列表
     * @param studentAccount
     * @param trainTaskRoomQueryDTO
     * @return
     */
    public List<TrainTaskRoomInfoDTO> queryTrainTaskRoomList(String studentAccount, TrainTaskRoomQueryDTO trainTaskRoomQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        //paramMap.put("limit", trainTaskRoomQueryDTO.getPageSize());
        //paramMap.put("offset", (trainTaskRoomQueryDTO.getPageIndex() - 1) * trainTaskRoomQueryDTO.getPageSize());

        paramMap.put("student_account", studentAccount);


        if(trainTaskRoomQueryDTO.getPaperTypeCode() != null)
        {
            paramMap.put("paper_type_code", trainTaskRoomQueryDTO.getPaperTypeCode());
        }

        if(trainTaskRoomQueryDTO.getPaperName() != null)
        {
            paramMap.put("paper_name", "%" + trainTaskRoomQueryDTO.getPaperName() + "%");
        }

        List<TrainTaskRoomInfoDTO> trainTaskRoomInfoDTOs = trainRoomMapper.queryTrainTaskRoomList(paramMap);

        return trainTaskRoomInfoDTOs;
    }


    /**
     * 创建自主考试房间
     * @param studentAccount
     * @param paperCode
     * @return
     */
    public TrainRoom insertTrainTaskRoomService(String studentAccount, String paperCode)
    {
        TrainRoom trainRoom = new TrainRoom();

        trainRoom.setCode(CommonUtil.GUID());
        trainRoom.setCandidateACode(studentAccount);
        trainRoom.setPaperCode(paperCode);
        trainRoom.setCandidateBCode("");

        //未组队
        trainRoom.setState(TrainRoomState.UN_GROUP.getRoomStateCode());

        int result = trainRoomMapper.insertSelective(trainRoom);
        if(result > 0)
        {
            return trainRoom;
        }

        return null;
    }

    public int updateTrainTaskRoom(String roomCode, String candidateBCode, Short state)
    {
        TrainRoom trainRoom = new TrainRoom();
        trainRoom.setCode(roomCode);
        trainRoom.setCandidateBCode(candidateBCode);
        trainRoom.setState(state);

        return trainRoomMapper.updateByExampleSelective(trainRoom);
    }

    public TrainRoom queryTrainTaskRoomByAccount(String studentAccount)
    {
        return trainRoomMapper.selectExitCreateRoomByAccount(studentAccount);
    }

    /**
     * 查询是否已经加入过房间
     * @param candidateBCode
     * @return
     */
    public TrainRoom selectExitJoinRoomByAccount(String candidateBCode)
    {
        return trainRoomMapper.selectExitJoinRoomByAccount(candidateBCode);
    }

    public int deleteExitTrainTaskRoom(String code)
    {
        return trainRoomMapper.deleteExitTrainTaskRoom(code);
    }

    public int updateTrainTaskRoom(TrainRoom record)
    {
        return trainRoomMapper.updateByExampleSelective(record);
    }

    public TrainRoom queryTrainTaskRoomByRoomCode(String roomCode)
    {
        return trainRoomMapper.selectByPrimaryKey(roomCode);
    }


    private String saveCandidateARecord(String candidateACode, String paperCode, String canditateBRecordCode)
    {
        //全新的记录
        TrainRecord examRecord = new TrainRecord();
        examRecord.setCode(CommonUtil.GUID());
        examRecord.setPaperCode(paperCode);
        examRecord.setStudentNo(candidateACode);

        examRecord.setPartnerTrainRecordCode(canditateBRecordCode);

        examRecord.setStartTime(new Date());

        //考试状态(0:未考试,1:考试成功,2:考试失败)
        examRecord.setExamState((short)0);
        //考试过程状态(0:未登录,1:已登录,2:开始试音,3:试音完成,5:正在答题,6:答题完成)
        examRecord.setFlowState((short)0);
        examRecord.setScore(new Float(0.0));
        //所有小题评测状态(0:未评测,1:评测完成,2:评测失败)
        examRecord.setEvalState((short)0);

        //分组状态(0:未分组,1:请求预分组,2:预分组成功,3:分组成功,4:分组失败/取消)
        examRecord.setGroupState((short)2);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        examRecord.setPaperState((short)0);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        examRecord.setDataState((short)0);

        //错误码(0:无错误,1:故障申报,2:网络故障,3:系统故障,4:同组失败,5:试音失败)
        examRecord.setErrorCode((short)0);

        int result = trainRecordMapper.insertSelective(examRecord);

        if(result > 0)
        {
            return examRecord.getCode();
        }
        else
        {
            return null;
        }
    }

    public int updatePartnerTrainRecordCode(String recordCode, String canditateBRecordCode)
    {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("code", recordCode);
        param.put("partner_train_record_code", canditateBRecordCode);

        int result = trainRecordMapper.updatePartnerTrainRecordCodeByPrimaryKey(param);

        return result;
    }

    private int saveCandidateBRecord(String canditateBRecordCode, String candidateBCode, String paperCode, String candidateARecordCode)
    {
        //全新的记录
        TrainRecord examRecord = new TrainRecord();

        //提前生成好
        examRecord.setCode(canditateBRecordCode);
        examRecord.setPaperCode(paperCode);
        examRecord.setStudentNo(candidateBCode);

        examRecord.setPartnerTrainRecordCode(candidateARecordCode);

        examRecord.setStartTime(new Date());

        //考试状态(0:未考试,1:考试成功,2:考试失败)
        examRecord.setExamState((short)0);
        //考试过程状态(0:未登录,1:已登录,2:开始试音,3:试音完成,5:正在答题,6:答题完成)
        examRecord.setFlowState((short)0);
        examRecord.setScore(new Float(0.0));
        //所有小题评测状态(0:未评测,1:评测完成,2:评测失败)
        examRecord.setEvalState((short)0);

        //分组状态(0:未分组,1:请求预分组,2:预分组成功,3:分组成功,4:分组失败/取消)
        examRecord.setGroupState((short)2);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        examRecord.setPaperState((short)0);

        //答卷上传状态(0:未上传,1:上传成功,2:上传失败)
        examRecord.setDataState((short)0);

        //错误码(0:无错误,1:故障申报,2:网络故障,3:系统故障,4:同组失败,5:试音失败)
        examRecord.setErrorCode((short)0);

        int result = trainRecordMapper.insertSelective(examRecord);

        return result;
    }




}
