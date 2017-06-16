package com.iflytek.cetsim.service;

import com.iflytek.cetsim.dto.KVStringDTO;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.RankDTO;
import com.iflytek.cetsim.dto.StudentTaskDTO;
import com.iflytek.cetsim.model.ExamPlan;
import com.iflytek.cetsim.model.Paper;

import java.util.HashMap;
import java.util.List;

public interface TaskService {
    /**
     * 发布计划
     */
    void publishTask(ExamPlan plan, String[] rooms, String[] papers) throws Exception;


    /**
     * 导出任务的成绩
     * @param examTaskCode
     * @param path
     */
    void exportTask(String examTaskCode, String path);

    /**
     * 导出模拟测试任务完成情况
     * @param examTaskCode
     * @param path
     */
    void exportFinish(String examTaskCode, String path);

    /**
     * 根据测试任务查询试卷信息
     * @param plantCode
     * @return
     */
    PaperInfoDTO queryPaperInfoByTaskCode(String plantCode);

    /**
     * 根据任务代码和分数区段，统计一个成绩区段内的考生人数
     * @param map
     * @return
     */
    int countScoreDistribution(HashMap<String,Object> map);

}
