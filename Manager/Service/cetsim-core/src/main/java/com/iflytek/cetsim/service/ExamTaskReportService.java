package com.iflytek.cetsim.service;

import java.util.Map;

/**
 * Created by Administrator on 2017/3/24.
 */
public interface ExamTaskReportService
{
    /**
     * 从考试包rpk文件中获取考试试卷的信息
     * @param rpkFileName 试卷包名称：sim_试卷代码
     * @param examRPKPath     考试包的相对路径
     * @param serverBasePath  服务端基础路径
     * @return
     */
    Map<String, Map<String, String>> getPaperRPKInfoDataService(String rpkFileName, String examRPKPath, String serverBasePath);

    /**
     * 获取答题信息
     * @param examRPKPath
     * @param serverBasePath
     * @return
     */
    String getTaskAnswerInfoDataService(String examRPKPath, String serverBasePath);

    /**
     * 2个音频文件叠加合并为一个同步音频文件，只支持wav格式
     * @param sourceFile1
     * @param sourceFile2
     * @param destFileDicPath 生成文件的文件夹目录
     * @param destFileName 文件名称
     * @return
     */
    void sndChannelfile(String serverBasePath, String sourceFile1, String sourceFile2, String destFileDicPath, String destFileName);

}
