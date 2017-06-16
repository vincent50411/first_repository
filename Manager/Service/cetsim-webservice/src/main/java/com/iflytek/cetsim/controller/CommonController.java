package com.iflytek.cetsim.controller;

import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.common.enums.PaperTypeEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.common.utils.VersionUtil;
import com.iflytek.cetsim.dao.ConfigurationMapper;
import com.iflytek.cetsim.dao.PaperItemTypeMapper;
import com.iflytek.cetsim.dao.PaperTypeMapper;
import com.iflytek.cetsim.dto.ScoreLevelUtil;
import com.iflytek.cetsim.dto.UpgradeDTO;
import com.iflytek.cetsim.model.Configuration;
import com.iflytek.cetsim.model.PaperItemType;
import com.iflytek.cetsim.model.PaperType;
import com.iflytek.cetsim.model.PaperTypeExample;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/api/common")
public class CommonController extends BaseController
{
    @Autowired
    private PaperItemTypeMapper paperItemTypeMapper;

    @Autowired
    private PaperTypeMapper paperTypeMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    /**
     * 全局的分数等级配置
     * @return
     */
    @RequestMapping(value = "/scorelevel")
    @ResponseBody
    public JsonResult getScoreLevel() {
        JsonResult result = this.getJson();
        try {

            HashMap<String,Object> map = new HashMap<>();
            map.put(PaperTypeEnum.CET4.getTypeCode(), ScoreLevelUtil.getLevels(PaperTypeEnum.CET4.getTypeCode()));
            map.put(PaperTypeEnum.CET6.getTypeCode(), ScoreLevelUtil.getLevels(PaperTypeEnum.CET6.getTypeCode()));
            result.setSucceed(map);
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取教师端报告界面的评语信息
     * @return
     */
    @RequestMapping(value = "/testPlanScoreDesc")
    @ResponseBody
    public JsonResult getTestPlanScoreDesc() {
        JsonResult result = this.getJson();
        try {
            HashMap<String,Object> map = new HashMap<>();
            map.put("SCORE_DESC", ScoreLevelUtil.getLevelsForTeacher());
            result.setSucceed(map);
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/version")
    @ResponseBody
    public List<UpgradeDTO> getVersion(String ver, String pro, boolean multi, HttpServletRequest req){
        List<UpgradeDTO> result = new ArrayList<>();
        try {
            String path = req.getServletContext().getRealPath("upgrade");
            File file = Paths.get(path, "upgrade.json").toFile();
            List<UpgradeDTO> upgradeDTOs = com.alibaba.fastjson.JSON.parseArray(FileUtils.readFileToString(file), UpgradeDTO.class);
            for (UpgradeDTO dto : upgradeDTOs) {
                if (!dto.getProduct().equals(pro)) {
                    continue;
                }
                if (VersionUtil.CompareVersion(ver, dto.getMiniVer(), dto.getVer())) {
                    result.add(dto);
                }
            }
            Collections.sort(result,new Comparator<UpgradeDTO>(){
                public int compare(UpgradeDTO arg0, UpgradeDTO arg1) {
                    if(arg0.getVer().equals(arg1.getVer())) return 0;
                    String[] ver_arg0 = arg0.getVer().split("\\.");
                    String[] ver_arg1 = arg1.getVer().split("\\.");
                    int flag = 1;
                    flag *= multi ? -1:1;
                    boolean match = true;
                    for (int v = 0;v < ver_arg0.length;v++)
                    {
                        if(ver_arg1.length >= ver_arg0.length) {
                            match = match && (Integer.parseInt(ver_arg0[v]) <= Integer.parseInt(ver_arg1[v]));
                        }
                    }
                    return  match ? flag : -flag;
                }
            });
            for (int i = 0; i < result.size(); i++) {
                String pkg = result.get(i).getPkg();
                if(pkg.indexOf("http") >= 0) { continue; }
                File pkgFile = FileUtils.getFile(pkg);
                if(FileUtils.getFile(pkg).exists())
                {
                    File target = Paths.get(path,pkgFile.getName()).toFile();
                    if(pkg.indexOf(path) == -1){
                        FileUtils.copyFile(pkgFile,target,true);
                    }
                    result.get(i).setPkg(req.getRequestURL().substring(0,req.getRequestURL().indexOf("/api")) + "/upgrade/"+pkgFile.getName());
                }
                else
                {
                    return new ArrayList<UpgradeDTO>();
                }
            }
            if(!multi)
            {
                List<UpgradeDTO> dtos = new ArrayList<>();
                dtos.add(result.get(0));
                return dtos;
            }
            return result;
        }catch (Exception ex){

        }
        return result;
    }

    @RequestMapping(value = "/upgrade",method = {RequestMethod.GET,RequestMethod.POST})
    public void getUpgrade(String path, HttpServletRequest req, HttpServletResponse res){
        try {
            String basePath = req.getServletContext().getRealPath("upgrade");
            File file = Paths.get(basePath,path).toFile();
            res.setContentType("application/octet-stream");
            //文件名编码，解决乱码问题
            String fileName = file.getName();
            String encodedFileName = null;
            encodedFileName = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
            //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
            res.setHeader("Content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");
            //文件下载
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(in, res.getOutputStream());
        }catch (Exception ex){}
    }

    @RequestMapping(value = "/servertime")
    @ResponseBody
    public JsonResult getDate() {
        JsonResult result = this.getJson();
        try {
            result.setSucceed(new Date());
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 试题类型列表
     * @return
     */
    @RequestMapping(value = "/PaperItemType")
    @ResponseBody
    public JsonResult getPaperItemType()
    {
        JsonResult result = this.getJson();
        try
        {
            List<PaperItemType> paperItemTypes = paperItemTypeMapper.getAllInfo();

            PaperItemType newObj = new PaperItemType();
            newObj.setName("全部题型");

            paperItemTypes.add(0, newObj);

            result.setSucceed(paperItemTypes);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取试题类型异常");
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 试卷类型列表
     * @return
     */
    @RequestMapping(value = "/PaperType")
    @ResponseBody
    public JsonResult getPaperType()
    {
        JsonResult result = this.getJson();
        try
        {
            PaperTypeExample example = new PaperTypeExample();

            List<PaperType> paperTypes = paperTypeMapper.selectByExample(example);

            PaperType newObj = new PaperType();
            newObj.setName("全部试卷");
            paperTypes.add(0, newObj);

            result.setSucceed(paperTypes);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取试卷类型异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 查询所有系统配置信息
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/queryAllSysConfigInfoList",method =  RequestMethod.GET)
    @ResponseBody
    public JsonResult queryAllSysConfigInfoList()
    {
        JsonResult result = new JsonResult();

        try
        {
            List<Configuration> configurations = configurationMapper.selectAllConfig();

            result.setSucceed(configurations);
        }
        catch (Exception ex)
        {
            result.setFailMsg("获取系统配置信息异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 根据配置名称修改值
     * @param configKey
     * @param configValue
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping(value="/updateSysConfigByKey",method =  RequestMethod.POST)
    @ResponseBody
    public JsonResult updateSysConfigByKey(String configKey, String configValue)
    {
        JsonResult result = new JsonResult();

        try
        {
            Configuration configuration = new Configuration();

            configuration.setConfigKey(configKey);
            configuration.setConfigValue(configValue);

            int succ = configurationMapper.updateByPrimaryKey(configuration);

            if(succ > 0)
            {
                result.setSucceed("修改系统配置信息成功~");
            }
            else
            {
                result.setFailMsg("修改系统配置信息失败~");
            }
        }
        catch (Exception ex)
        {
            result.setFailMsg("修改系统配置信息异常~");
            ex.printStackTrace();
        }

        return result;
    }


}
