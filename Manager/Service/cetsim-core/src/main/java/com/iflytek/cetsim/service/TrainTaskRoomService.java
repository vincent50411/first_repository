package com.iflytek.cetsim.service;

import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.TrainTaskRoomQueryDTO;
import com.iflytek.cetsim.model.TrainRoom;
import com.iflytek.cetsim.model.TrainTaskRoomInfoDTO;

import java.util.List;

/**
 * 自主考试房间服务
 * Created by Administrator on 2017/5/11.
 */
public interface TrainTaskRoomService
{

    /**
     * 检查试卷的自主考试权限
     * @param paperCode
     * @return
     */
    JsonResult checkTrainPaperState(String paperCode);

    /**
     * 清理用户已经存在的房间，可能是加入、也可能是创建的
     * @param studentAccount
     */
    public void clearUserExitTrainRoom(String studentAccount);

    /**
     * 创建自主考试房间
     * @param studentAccount
     * @param paperCode
     * @return
     */
    JsonResult createTrainTaskRoomService(String studentAccount, String paperCode);

    /**
     * 加入房间
     * @param studentAccount
     * @param roomCode
     * @return
     */
    JsonResult joinTrainTaskRoomService(String studentAccount, String candidateACode, String roomCode);

    JsonResult candidateAQuitRoomService(String roomCode, String type);

    JsonResult candidateBQuitRoomService(String roomCode);

    /**
     * 通知房间内的A/B同时开始考试，启动客户端考试机程序
     * @param roomCode
     * @param candidateACode
     * @param candidateBCode
     * @return
     */
    JsonResult startTraimTaskExam(String roomCode, String candidateACode, String candidateBCode, String paperCode);


    TrainRoom insertTrainTaskRoomService(String studentAccount, String paperCode);


    int updateTrainTaskRoom(String roomCode, String candidateBCode, Short state);

    List<TrainTaskRoomInfoDTO> queryTrainTaskRoomList(String studentAccount, TrainTaskRoomQueryDTO trainTaskRoomQueryDTO);

    int deleteExitTrainTaskRoom(String code);

    TrainRoom queryTrainTaskRoomByAccount(String studentAccount);

    TrainRoom queryTrainTaskRoomByRoomCode(String roomCode);

    /**
     * 查询是否已经加入过房间
     * @param candidateBCode
     * @return
     */
    TrainRoom selectExitJoinRoomByAccount(String candidateBCode);

    int updateTrainTaskRoom(TrainRoom record);





}
