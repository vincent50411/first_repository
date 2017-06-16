package com.iflytek.cetsim.service;

import com.iflytek.cetsim.common.utils.AppContext;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

/**
 * JNA调用dll接口，动态链接库
 * sndUtil.dll放置在工程目录下，sndUtil依赖lame.exe文件对音频文件操作
 * lame.exe文件需要放到jdk/bin目录下
 *
 * 可能出现的问题及解决办法：
 * 动态库是32位的，而JDK是64位的，所以：
 *      如果要调用32位的JDK必须是32位的；
 *      相应地，如果动态库是64位的，必须使用64位JDK。
 *
 * Created by Administrator on 2017/3/31.
 */
public interface SndUtilDllService extends Library
{
    //动态链接库dll文件
    SndUtilDllService instance = (SndUtilDllService) Native.loadLibrary(AppContext.getInstance().serverBasePath + "\\sndUtil", SndUtilDllService.class);

    /**
     * 将2个音频文件同步叠加为一个音频文件
     * @param sourceFile1 源文件地址1，携带文件后缀名
     * @param sourceFile2 源文件地址2，携带文件后缀名
     * @param destFile    合成后的文件地址，携带文件后缀名
     * @return
     */
    int sndChannelfile(WString sourceFile1, WString sourceFile2, WString destFile);

    /**
     * 将音频文件格式转换
     * @param sourceFile 音频文件地址，携带文件后缀名
     * @param destFile 转出后的音频文件地址，携带文件后缀名
     * @return
     */
    int sndNormalizefile(WString sourceFile, WString destFile);

}
