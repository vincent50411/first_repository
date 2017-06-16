package com.iflytek.cetsim.service.impl;

import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.enums.PaperAllowFreeUsageEnum;
import com.iflytek.cetsim.common.enums.PaperAllowPlanUsageEnum;
import com.iflytek.cetsim.common.enums.PaperStatusEnum;
import com.iflytek.cetsim.common.enums.PaperTypeEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.CommonUtil;
import com.iflytek.cetsim.common.utils.DateUtils;
import com.iflytek.cetsim.common.utils.Dom4jUtils;
import com.iflytek.cetsim.common.utils.Zip4jUtils;
import com.iflytek.cetsim.dao.PaperBufferMapper;
import com.iflytek.cetsim.dao.PaperMapper;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.PaperQueryDTO;
import com.iflytek.cetsim.model.Paper;
import com.iflytek.cetsim.model.PaperBuffer;
import com.iflytek.cetsim.service.PaperService;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class PaperServiceImpl implements PaperService
{
    @Autowired
    private PaperMapper paperMapper = null;

    @Autowired
    private PaperBufferMapper paperBufferMapper = null;

    /**
     * 导入试卷包
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
            Element el = paperEls.get(i);

            //获取试卷包模式 mode="special"，如果为专项试题，则不允许导入
            String modeValue = el.attribute("mode") == null ? "" : el.attribute("mode").getValue();
            if("special".equals(modeValue))
            {
                result.setFailMsg("试卷包中包含专项训练试题包，不能导入~");

                return result;
            }

            String paperRPKFilePath = el.attribute("file").getValue();

            //需要检查试卷文件是否存在，如果不存在直接返回异常, 保持事务一致性
            String srcRpkFile = Paths.get(unZipDir, paperRPKFilePath).toString();

            Paper paper = getPaper(el);

            HashMap<String, Object> paramMap = new HashMap<>();

            //code是主键唯一性
            paramMap.put("code", paper.getCode());
            //paramMap.put("paperTypeCode", paper.getPaperTypeCode());

            //判断试卷是否存在
            int paperNum = paperMapper.selectPaperByNameAndType(paramMap);

            //已经存在试卷名称相同的记录
            if (paperNum > 0) {
                //退出程序前，删除所有临时文件
                CommonUtil.deleteDir(new File(unZipDir));

                result.setFailMsg("已经存在试卷代码为:[" + paper.getCode() + "]的试卷");

                //返回试卷名称
                return result;
            }

            paperMapper.insertSelective(paper);
            rpkNameDict.put(paperRPKFilePath, paper.getCode());
            try
            {
                //向试卷数据表增加记录
                byte[] b = FileUtils.readFileToByteArray(new File(srcRpkFile));

                PaperBuffer paperBuffer = new PaperBuffer();
                paperBuffer.setCode(CommonUtil.GUID());
                paperBuffer.setBuffer(b);
                //测试用
                paperBuffer.setMd5("123456789");
                paperBuffer.setPaperCode(paper.getCode());

                //表引擎为InnoDb类型支持事务操作，但是blob字段大小受限制，只能把引擎设置为MyISAM类型
                paperBufferMapper.insertSelective(paperBuffer);

            }
            catch (IOException ex)
            {
                //如果抛出异常，则手动删除保存的记录（paper表无法应用事务，原因是表达引擎不是InnoDb类型mysql）
                paperMapper.deleteByPrimaryKey(paper.getCode());

                ex.printStackTrace();

                //退出程序前，删除所有临时文件
                CommonUtil.deleteDir(new File(unZipDir));

                result.setFailMsg("试卷包中缺失试卷文件[" + srcRpkFile + "],请检查后重新上传");

                //事务无法自定捕获，只能手动控制返回值
                return result;
            }
            catch (Exception ex)
            {
                //如果抛出异常，则手动删除保存的记录（paper表无法应用事务，原因是表达引擎不是InnoDb类型mysql）
                paperMapper.deleteByPrimaryKey(paper.getCode());

                ex.printStackTrace();

                //退出程序前，删除所有临时文件
                CommonUtil.deleteDir(new File(unZipDir));

                result.setFailMsg("保存试卷异常");

                //事务无法自定捕获，只能手动控制返回值
                return result;
            }
        }

        //将文件中的RPK文件夹下的指定ppr文件复制到指定目录下
        String pprResult = copyPPRFiles(unZipDir, baseDir, rpkNameDict);
        if (pprResult.equals("fail")){
            result.setFailMsg("PPR文件复制异常");
            return result;
        }

        //退出程序前，删除所有临时文件
        CommonUtil.deleteDir(new File(unZipDir));

        result.setSucceedMsg("导入成功");

        return result;
    }

    public static String copyPPRFiles(String unZipDir, String baseDir, HashMap<String, String> paperMap)
    {
        String result = "";
        try {
            //解压后的文件
            File unZipAllFiles = new File(unZipDir);
            File[] files = unZipAllFiles.listFiles();
            //遍历解压后的文件寻找.rpk文件
            for (File file : files) {
                String fileName = file.getName();
                //String postfix = fileName.substring(fileName.lastIndexOf(".")+1);
                if (fileName.contains(".rpk")) {
                    //获得.rpk文件名并改为.db文件查找其中的ppr文件夹，再冲文件夹下寻找指定的ppr文件
                    String prefix = fileName.substring(0, fileName.indexOf("."));
                    String sqlLiteFilePath = unZipDir + "/" + prefix + ".db";
                    String fromFilePath = unZipDir + "/" + fileName;
                    //复制rpk文件到db文件
                    copyFiles(fromFilePath, sqlLiteFilePath);
                    //再查找db文件中的ppr文件夹
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite://" + sqlLiteFilePath);
                    Statement stat = conn.createStatement();
                    // 查询考试包中的数据, 只读取相干信息
                    ResultSet rs = stat.executeQuery("SELECT * FROM Archive where Path like '%ppr%';");
                    //寻找ppr文件夹下的ppr文件
                    while (rs.next()) {
                        String path = rs.getString("Path") == null ? "" : rs.getString("Path").replace('\\', '/');

                        if (path.contains("pra")) {
                            byte[] byteList = rs.getBytes("Content");
                            //将数据写入文件系统
                            writeBytesToFile(baseDir, paperMap.get(prefix + ".rpk"), byteList);
                            break;
                        }
                    }

                }
                else
                    continue;
            }
        }catch (Exception ex){
            result = "fail";
            return result;
        }
        return result;
    }

    public static void writeBytesToFile(String baseDir, String prefix, byte[] content) throws IOException
    {
        File pprDest = new File(baseDir + "/ppr");
        //File pprDestFile = new File(baseDir + "/ppr" + "/" + prefix + "_04_02.ppr");
        String globFilePath = baseDir + Constants.PprPath + prefix + "_04_02.ppr";
        if (!pprDest.exists())
            pprDest.mkdirs();

        File pprDestFile = new File(globFilePath);

        //如果该ppr文件存在，则先删除再重新创建
        if(pprDestFile.exists())
            pprDestFile.delete();
        //创建ppr文件
        pprDestFile.createNewFile();
        //写入
        FileOutputStream fos = new FileOutputStream(pprDestFile);
        fos.write(content);
        fos.close();

    }

    public static void copyFiles(String fromFilePath, String toFilePath)
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

    private Paper getPaper(Element el) {
        Paper paper = new Paper();
        paper.setName(el.attribute("title").getValue());

        paper.setCode(el.attribute("code").getValue());


        //试卷导入时间取应用服务器当前时间
        paper.setCreateTime(DateUtils.parseDate(DateUtils.getDateTime()));

        //直接显示code
        paper.setPaperTypeCode(el.attribute("papertype").getValue().toLowerCase());

        paper.setStatus((short)PaperStatusEnum.ENABLED.getStatusCode());
        paper.setAllowFreeUseage(PaperAllowFreeUsageEnum.ENABLED.getAllowFreeUsageCode());
        paper.setAllowPlanUseage(PaperAllowPlanUsageEnum.ENABLED.getAllowPlanUsageCode());
        return paper;
    }

    public List<PaperInfoDTO> selectPaper(PaperQueryDTO paperQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("limit", paperQueryDTO.getPageSize());
        paramMap.put("offset", (paperQueryDTO.getPageIndex() - 1) * paperQueryDTO.getPageSize());

        if (paperQueryDTO.getPaperTypeCode() != null)
        {
            paramMap.put("paperTypeCode", paperQueryDTO.getPaperTypeCode());
        }

        if (paperQueryDTO.getStatus() != null)
        {
            //试卷权限：总权限开1 总权限关0 仅模拟测试开2 仅自主考试开3
            if(paperQueryDTO.getStatus() == 2)
            {
                //模拟测试启用
                paramMap.put("allow_plan_useage", 1);
            }
            else if(paperQueryDTO.getStatus() == 3)
            {
                //自主考试启用
                paramMap.put("allow_free_useage", 1);
            }
            else
            {
                //总权限启用或者禁用
                paramMap.put("status", paperQueryDTO.getStatus());
            }
        }

        if (paperQueryDTO.getName() != null && !"".equals(paperQueryDTO.getName()))
        {
            paramMap.put("name", "%" + paperQueryDTO.getName() + "%");
        }

        //增加排序规则
        if(paperQueryDTO.getOrderColumnName() != null && paperQueryDTO.getOrderCode() != null)
        {
            paramMap.put(paperQueryDTO.getOrderColumnName().toUpperCase() + "_" + paperQueryDTO.getOrderCode().toUpperCase(), "true");
        }
        else
        {
            paramMap.put("order_condition_default", "true");
        }

        //模拟测试的试卷的使用次数是以测试任务（一个测试计划可以有多个测试任务）为单位，专项训练和自主考试的使用次数是以实际考试成功的人数为单位
        List<PaperInfoDTO> paperInfoDTOList = paperMapper.selectPaperByExample(paramMap);

        return paperInfoDTOList;

    }

    public int countPaper(PaperQueryDTO paperQueryDTO)
    {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        if (paperQueryDTO.getPaperTypeCode() != null)
        {
            paramMap.put("paperTypeCode", paperQueryDTO.getPaperTypeCode());
        }

        if (paperQueryDTO.getStatus() != null)
        {
            //试卷权限：总权限开1 总权限关0 仅模拟测试开2 仅自主考试开3
            if(paperQueryDTO.getStatus() == 2)
            {
                //模拟测试启用
                paramMap.put("allow_plan_useage", 1);
            }
            else if(paperQueryDTO.getStatus() == 3)
            {
                //自主考试启用
                paramMap.put("allow_free_useage", 1);
            }
            else
            {
                //总权限启用或者禁用
                paramMap.put("status", paperQueryDTO.getStatus());
            }
        }

        if (paperQueryDTO.getName() != null && !"".equals(paperQueryDTO.getName()))
        {
            paramMap.put("name", "%" + paperQueryDTO.getName() + "%");
        }

        return paperMapper.countByExample(paramMap);
    }

    /**
     * 设置试卷总权限，如果总权限关闭，则模拟考试和自主考试都禁用，反之都启用
     * 试卷状态(0:不可用, 1:可用)
     * @param paperCode
     * @param status
     * @return
     */
    public int setPaperStatus(String paperCode, int status)
    {
        Paper paper = new Paper();
        paper.setCode(paperCode);
        paper.setStatus((short)status);

        if(status == 0)
        {
            paper.setAllowPlanUseage(false);
            paper.setAllowFreeUseage(false);
        }
        else
        {
            paper.setAllowPlanUseage(true);
            paper.setAllowFreeUseage(true);
        }

        return paperMapper.updateByPrimaryKeySelective(paper);
    }

    /**
     * 如果试卷总状态为禁用状态，不允许开放权限
     * @param paperCode
     * @param allowPlanUsage
     * @return
     */
    public int setAllowPlanUsage(String paperCode, boolean allowPlanUsage)
    {
        Paper paper = new Paper();
        paper.setCode(paperCode);
        paper.setAllowPlanUseage(allowPlanUsage);

        return paperMapper.updateAllowUseage(paper);
    }

    /**
     * 如果试卷总状态为禁用状态，不允许开放权限
     * @param paperCode
     * @param allowFreeUsage
     * @return
     */
    public int setAllowFreeUsage(String paperCode, boolean allowFreeUsage)
    {
        Paper paper = new Paper();
        paper.setCode(paperCode);
        paper.setAllowFreeUseage(allowFreeUsage);

        return paperMapper.updateAllowUseage(paper);
    }

    public Paper selectPaperByCode(String paperCode)
    {
        return paperMapper.selectByPrimaryKeyNotBlob(paperCode);
    }



}
