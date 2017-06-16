package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.exception.BusinessException;
import com.iflytek.cetsim.common.utils.CommonUtil;
import com.iflytek.cetsim.common.utils.logger.LogUtil;
import com.iflytek.cetsim.service.ExamTaskReportService;
import com.iflytek.cetsim.service.SndUtilDllService;
import com.sun.jna.Native;
import com.sun.jna.WString;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取考试报服务
 * Created by Administrator on 2017/3/24.
 */
@Service
@Transactional
public class ExamTaskReportServiceImpl implements ExamTaskReportService
{
    /**
     * 从考试包rpk文件中获取考试试卷的信息
     * @param examRPKPath     考试包的相对路径
     * @param serverBasePath  服务端基础路径
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getPaperRPKInfoDataService(String rpkFileName, String examRPKPath, String serverBasePath)
    {
        //考试试卷信息
        Map<String, Map<String, String>> examPaperInfoMap = null;

        //试卷解压包相对路径../cet4/sim01
        String examDirPath = examRPKPath.substring(0, examRPKPath.indexOf("."));

        //解压到试卷包文件夹全地址E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/paper/cet4/sim01/
        String examFileBaseDirPath = serverBasePath + Constants.PaperPath + examDirPath + "/";

        //多媒体文件服务器相对路径，paper/cet4/sim01/(存放考试包内文件路径item01\media\F1-T0-L1.mpg)，前台需要
        String mediaServerPath = Constants.PaperPath + examDirPath + "/";

        //rpk文件修改为sqllite文件"cet4/sim01.db"
        String sqlLiteFilePath = examFileBaseDirPath + rpkFileName + ".db";

        //如果试卷包已经存在，则直接读取试卷信息
        File simFileDir = new File(examFileBaseDirPath);

        //是否将视频资源写入文件系统
        boolean isWriteFile = false;

        if(!simFileDir.exists())
        {
            //不存在则创建新的文件夹
            createNotExitsFile(examFileBaseDirPath, true);

            String fromFilePath = serverBasePath + Constants.PaperPath + examRPKPath;

            copyFile(fromFilePath, sqlLiteFilePath);

            //第一次解压试卷包，需要写入
            isWriteFile = true;
        }

        if(new File(sqlLiteFilePath).exists())
        {
            //从rpk文件中获取试卷包数据
            examPaperInfoMap = getTotalDataFromRPK(sqlLiteFilePath, examFileBaseDirPath, mediaServerPath, isWriteFile);
        }
        else
        {
            LogUtil.info("------------------------------- 试卷包文件不存在， 文件地址为：" + sqlLiteFilePath);
            return null;
        }

        return examPaperInfoMap;
    }

    /**
     * 获取答题信息
     * @param examAnswerPath
     * @param serverBasePath
     * @return
     */
    public String getTaskAnswerInfoDataService(String examAnswerPath, String serverBasePath)
    {
        //考生答题录音文件名称（答题包文件名称格式固定，保持唯一性）
        String answerFileName = examAnswerPath.substring((examAnswerPath.lastIndexOf("/") + 1), examAnswerPath.indexOf("."));

        //考生答题录音包解压包相对路径（答题包文件名称格式固定，保持唯一性） cet4/sim02_answer
        String examAnswerDirPath = examAnswerPath.substring(0, examAnswerPath.indexOf("."));

        //解压到考生答题录音包文件夹全地址E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/answer/cet4/sim02_answer/
        String examFileBaseDirPath = serverBasePath + Constants.ANSWER_PATH + "/" + examAnswerDirPath + "/";

        //解压考生答题录音包前需要删除已经存在的文件
        File simFileDir = new File(examFileBaseDirPath);

        //多媒体文件(答题录音文件)服务器相对路径，answer/cet4/sim02_answer/，前台需要
        String mediaServerPath = Constants.ANSWER_PATH + "/" + examAnswerDirPath + "/";

        if(simFileDir != null && simFileDir.exists())
        {
            LogUtil.info("--> 答题包目录已经存在, 不需要重复解压，目录地址为:" + mediaServerPath);
            //如果文件已经存在，则不需要重新创建
            return mediaServerPath;
        }

        //删除目录下所有文件
        CommonUtil.deleteDir(simFileDir);

        //创建不存在的文件夹
        createNotExitsFile(examFileBaseDirPath, true);

        //考生答题录音包rpk原文件的路径
        String fromFilePath = serverBasePath + Constants.ANSWER_PATH + "/" + examAnswerPath;

        //rpk文件修改为zip压缩文件"cet4/sim02_answer.zip"
        String zipFilePath = examFileBaseDirPath + answerFileName + ".zip";

        LogUtil.info("--> 答题包zip文件地址:" + zipFilePath);

        //复制、重命名rpk文件为zip文件
        copyFile(fromFilePath, zipFilePath);

        LogUtil.info("--> 答题包rpk转zip文件地址成功");

        //解压zip包,携带密码
        unZipFile(zipFilePath, examFileBaseDirPath, "stip@iflytek.com");

        LogUtil.info("--> 答题包zip文件解压成功");

        return mediaServerPath;
    }

    /**
     * 2个音频文件叠加合并为一个同步音频文件，只支持wav格式
     * @param sourceFile1
     * @param sourceFile2
     * @param destFileDicPath 文件夹地址
     * @param destFileName
     * @return
     */
    public void sndChannelfile(String serverBasePath, String sourceFile1,
                                              String sourceFile2, String destFileDicPath, String destFileName) throws BusinessException
    {
        Map<String, String> resultMap = new HashMap<String, String>();

        //2个wav音频叠加合成后的wav文件地址
        String cet4LastItemTogetherWavPath = (destFileDicPath + destFileName + ".wav").replace("/", "\\");

        //2个wav音频叠加合成后的wav文件转换成mp3格式的文件地址
        String cet4LastItemTogetherMP3Path = (destFileDicPath + destFileName + ".mp3").replace("/", "\\");

        //mp3文件转wav格式，提取文件路径和名称
        String sourceFilePath1 = sourceFile1.substring(0, sourceFile1.lastIndexOf(".mp3"));
        String sourceFilePath2 = sourceFile2.substring(0, sourceFile2.lastIndexOf(".mp3"));

        String mp3SourceFile1 = sourceFile1;
        String mp3SourceFile2 = serverBasePath + sourceFile2;

        //wav文件地址
        String wavSourceFile1 = (sourceFilePath1 + ".wav").replace("/", "\\");
        String wavSourceFile2 = (serverBasePath + sourceFilePath2 + ".wav").replace("/", "\\");
        try
        {
            LogUtil.info("***************************************1******************************");
            System.setProperty("jna.protected","true");
            Native.setProtected(true);

            SndUtilDllService instance = SndUtilDllService.instance;
            LogUtil.info("**************************************22222222******************************");

            //第一个mp3文件格式转换
            if(new File(mp3SourceFile1).exists())
            {
                int result_1 = 0;

                WString src1 = new WString(mp3SourceFile1.replace("/", "\\").replace("\\\\","\\"));
                WString src2 = new WString(wavSourceFile1.replace("\\\\","\\"));

                LogUtil.info("获取音频路径:" + src1.toString()+ "," + src2.toString());
                result_1 = instance.sndNormalizefile(src1, src2);

                LogUtil.info("Normalize Finished...");

                if(result_1 == 0)
                {
                    LogUtil.info("--> --------------第一个mp3转wav成功[result:" + result_1 + "]," + wavSourceFile1);
                }
                else {
                    LogUtil.info("--> --------------第一个mp3转wav失败[result:" + result_1 + "], " + wavSourceFile1);
                    throw new BusinessException("mp3转wav格式失败");
                }
            }
            else
            {
                LogUtil.info("-->文件：" + mp3SourceFile1 + "; 不存在");
                throw new BusinessException("录音文件：" + mp3SourceFile1 + "; 不存在");
            }

            if(new File(mp3SourceFile2).exists())
            {
                //第二个mp3文件格式转换
                int result_2 = instance.sndNormalizefile(new WString(mp3SourceFile2.replace("/", "\\")), new WString(wavSourceFile2));

                if(result_2 == 0)
                {
                    LogUtil.info("--> --------------第二个mp3转wav成功 [result:" + result_2 + "], " + wavSourceFile2 + "");
                }
                else
                {
                    LogUtil.info("--> --------------第二个mp3转wav失败 [result:" + result_2 + "], " + wavSourceFile2 + "");
                    throw new BusinessException("mp3转wav格式失败");
                }
            }
            else
            {
                LogUtil.info("-->文件：" + mp3SourceFile2 + "; 不存在");
                throw new BusinessException("录音文件：" + mp3SourceFile2 + "; 不存在");
            }

            if(new File(wavSourceFile1).exists() && new File(wavSourceFile2).exists())
            {
                //叠加2个wav格式的音频文件
                int result_3 = instance.sndChannelfile(new WString(wavSourceFile1), new WString(wavSourceFile2), new WString(cet4LastItemTogetherWavPath));

                if(result_3 == 0)
                {
                    LogUtil.info("--> --------------叠加2个wav格式的音频文件成功 [result:" + result_3 + "], " + cet4LastItemTogetherWavPath + "");
                }
                else
                {
                    LogUtil.info("--> --------------叠加2个wav格式的音频文件失败 [result:" + result_3 + "], " + cet4LastItemTogetherWavPath + "");
                    throw new BusinessException("叠加2个wav格式的音频文件失败");
                }
            }
            else
            {
                LogUtil.info("处理2个音频文件叠加合成操作失败");
                throw new BusinessException("处理2个音频文件叠加合成操作失败");
            }

            if(new File(cet4LastItemTogetherWavPath).exists())
            {
                //将合成后的wav格式音频文件再转换为mp3格式文件
                int result_4 = instance.sndNormalizefile(new WString(cet4LastItemTogetherWavPath), new WString(cet4LastItemTogetherMP3Path));

                if(result_4 == 0)
                {
                    LogUtil.info("--> --------------叠加2个wav格式的音频文件转mp3文件格式成功 [result:" + result_4 + "], " + cet4LastItemTogetherMP3Path + "");
                }
                else
                {
                    LogUtil.info("--> --------------叠加2个wav格式的音频文件转mp3文件格式失败 [result:" + result_4 + "], " + cet4LastItemTogetherMP3Path + "");
                    throw new BusinessException("叠加2个wav格式的音频文件转mp3文件格式失败");
                }
            }
            else
            {
                LogUtil.info("处理2个音频文件成功后, wav文件不存在:" + cet4LastItemTogetherWavPath);
                throw new BusinessException("处理2个音频文件成功后, wav文件不存在:" + cet4LastItemTogetherWavPath);
            }

            //所有操作完成后，删除中间转换文件，只保留合成后的mp3文件
            deleteFile(wavSourceFile1);
            deleteFile(wavSourceFile2);

            deleteFile(cet4LastItemTogetherWavPath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            LogUtil.error("DLL 调用出现错误!");
            LogUtil.error(ex.getMessage());
        }
    }


    private Map<String, Map<String, String>> getTotalDataFromRPK(String sqlLiteFilePath, String examFileBaseDirPath, String mediaServerPath, boolean isWriteFile)
    {
        //考试试卷信息
        Map<String, Map<String, String>> examPaperInfoMap = new HashMap<String, Map<String, String>>();

        //SQLLite数据库处理
        try {
            Class.forName("org.sqlite.JDBC");

            //sqllite数据文件地址:E://10 workspace_svn/Manager/Service/cetsim-webservice/target/cetsim/paper/cet4/sim01.db
            Connection conn = DriverManager.getConnection("jdbc:sqlite://" + sqlLiteFilePath);

            Statement stat = conn.createStatement();

            // 查询考试包中的数据, 只读取相干信息
            ResultSet rs = stat.executeQuery("SELECT * FROM Archive where Path like 'item%';");

            while (rs.next())
            {
                //路径分隔符统一
                String path = rs.getString("Path") == null ? "" : rs.getString("Path").replace('\\','/');

                //拼装多媒体服务端相对路径，返回给客户端, 只存储视频格式文件.mpg, .jpg视频封面
                if(path.lastIndexOf(".mpg") > -1 || path.lastIndexOf(".jpg") > -1 || path.lastIndexOf(".txt") > -1)
                {
                    if(path.lastIndexOf(".mpg") > -1)
                    {
                        //视频文件将mpg格式修改为mp4格式
                        path = path.substring(0, path.lastIndexOf(".mpg")) + ".mp4";
                    }

                    //只提取第一层文件夹名称item01作为关键key值
                    String videoFileDirName = path.substring(0, path.indexOf("/"));

                    //保存所有题目的多媒体信息，截取题目名称F1-T0-L2 （题目名称唯一）
                    String mediaFileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

                    //视频文件相对地址:paper/cet4/sim01/item02/media/F1-T1-L2.mpg
                    String mediaFilePath = mediaServerPath + path;

                    Map<String, String> mediaFileMap = null;

                    //判断是否已经存在相同的题目Item
                    if(examPaperInfoMap.containsKey(videoFileDirName))
                    {
                        mediaFileMap = examPaperInfoMap.get(videoFileDirName);
                    }
                    else
                    {
                        mediaFileMap = new HashMap<String, String>();

                        examPaperInfoMap.put(videoFileDirName, mediaFileMap);
                    }

                    //试题描述信息
                    if(path.lastIndexOf(".txt") > -1)
                    {
                        String itemTitleInfo = rs.getString("Content");

                        //由于文本名称和题干视频的名称一致，需要增加表示度_txt
                        mediaFileMap.put(mediaFileName + "_txt", itemTitleInfo);
                    }
                    else
                    {
                        //以题目的名称作为key，保存视频的相对路径
                        mediaFileMap.put(mediaFileName, mediaFilePath);
                    }
                }

                //视频和图片写入文件系统
                if(isWriteFile)
                {
                    if(path.lastIndexOf(".mp4") > -1 || path.lastIndexOf(".mp3") > -1 || path.lastIndexOf(".jpg") > -1)
                    {
                        byte[] byteList = rs.getBytes("Content");

                        //将数据写入文件系统
                        writeBytesToFile(examFileBaseDirPath, path, byteList);
                    }
                }
            }

            rs.close();

            // 结束数据库的连接
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return examPaperInfoMap;
    }

    /**
     * 将考试包中的内容写入文件系统
     * @param basePath
     * @param examPath
     * @param content
     * @throws IOException
     */
    private void writeBytesToFile(String basePath, String examPath, byte[] content) throws IOException
    {
        String newPath = examPath.replace('\\','/');

        int lastFileSeparatorIndex = newPath.lastIndexOf("/");
        if(lastFileSeparatorIndex > 0)
        {
            String dirFileNameStr = newPath.substring(0, lastFileSeparatorIndex);

            String[] dirFileList = dirFileNameStr.split("/");

            String globDirFilePath = basePath;
            for(String dirFileName : dirFileList){
                globDirFilePath += dirFileName + "/";

                //循环创建每一层文件夹
                createNotExitsFile(globDirFilePath, true);
            }
        }

        String globFilePath = basePath + newPath;

        //判断文件如果已经存在(视频分段)，则打开文件继续写入二进制数据
        File file = new File(globFilePath);

        if(file.exists())
        {
            // 打开一个随机访问文件流，按读写方式续写二进制数据流
            RandomAccessFile randomFile = new RandomAccessFile(globFilePath, "rw");

            // 文件长度，字节数
            long fileLength = randomFile.length();

            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.write(content);
            randomFile.close();
        }
        else
        {
            File newFile = createNotExitsFile(globFilePath, false);

            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(content);
            fos.close();
        }
    }


    private void deleteFile(String filePath)
    {
        File file = new File(filePath);

        if (file.isFile() && file.exists())
        {
            file.delete();
        }
    }

    /**
     * 创建不存在的文件对象
     * @param filePath
     * @param isFileDir 是否是文件夹 true， false为创建普通文件
     */
    private File createNotExitsFile(String filePath, boolean isFileDir)
    {
        File file = new File(filePath);

        try
        {
            if(!file.exists())
            {
                if(isFileDir)
                {
                    file.mkdir();
                }
                else
                {
                    file.createNewFile();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return file;
    }

    private void copyFile(String fromFilePath, String toFilePath)
    {
        try
        {
            File srcFile = new File(fromFilePath);
            File destFile = new File(toFilePath);

            FileUtils.copyFile(srcFile, destFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 解压带有密码的zip文件
     * @param zipFilePath
     * @param unZipDirPath
     * @param passwd
     */
    private void unZipFile(String zipFilePath, String unZipDirPath, String passwd)
    {
        try
        {
            // 首先创建ZipFile指向磁盘上的.zip文件
            ZipFile zFile = new ZipFile(zipFilePath);

            // 设置文件名编码，在GBK系统中需要设置
            zFile.setFileNameCharset("GBK");

            // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
            if (!zFile.isValidZipFile())
            {
                System.out.println("压缩文件不合法,可能被损坏.");
            }

            // 解压目录
            File destDir = new File(unZipDirPath);
            if (destDir.isDirectory() && !destDir.exists())
            {
                destDir.mkdir();
            }

            if (zFile.isEncrypted())
            {
                // 设置密码
                zFile.setPassword(passwd.toCharArray());
            }

            // 将文件抽出到解压目录(解压)
            zFile.extractAll(unZipDirPath);

            //文件解压完成，删除压缩文件
            File zipFile = new File(zipFilePath);

            CommonUtil.deleteDir(zipFile);
        }
        catch (ZipException e)
        {
            e.printStackTrace();;
        }

    }

}
