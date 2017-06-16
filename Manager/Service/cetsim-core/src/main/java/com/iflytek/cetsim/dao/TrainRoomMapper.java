package com.iflytek.cetsim.dao;

import com.iflytek.cetsim.common.annotation.MyBatisRepository;
import com.iflytek.cetsim.model.TrainRoom;
import com.iflytek.cetsim.model.TrainTaskRoomInfoDTO;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface TrainRoomMapper {
    int insert(TrainRoom record);

    int insertSelective(TrainRoom record);

    TrainRoom selectByPrimaryKey(String code);

    /**
     * 查询未分组的
     * @param code
     * @return
     */
    TrainRoom selectUnGroupRoom(String code);

    /**
     * 根据房主A的账号，查询A有没有创建过的房间，状态为未组队或者已经组队的
     * @param candidateACode
     * @return
     */
    TrainRoom selectByAccount(String candidateACode);

    /**
     * 查询是否已经加入过房间
     * @param candidateBCode
     * @return
     */
    TrainRoom selectExitJoinRoomByAccount(String candidateBCode);

    /**
     * 查询是否已经创建过房间
     * @param candidateBCode
     * @return
     */
    TrainRoom selectExitCreateRoomByAccount(String candidateBCode);


    int updateByExampleSelective(TrainRoom record);

    int deleteExitTrainTaskRoom(String code);

    /**
     * 删除state为0和1的所有房间
     * @param paramMap
     * @return
     */
    int deleteExitTrainTaskRoomByState(Map<String, Object> paramMap);

    List<TrainTaskRoomInfoDTO> queryTrainTaskRoomList(Map<String, Object> paramMap);



}