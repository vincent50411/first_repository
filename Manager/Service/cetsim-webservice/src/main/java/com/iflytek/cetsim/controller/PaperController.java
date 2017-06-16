package com.iflytek.cetsim.controller;

import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.common.enums.PaperStatusEnum;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.PaperQueryDTO;
import com.iflytek.cetsim.model.Paper;
import com.iflytek.cetsim.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Created by pengwang on 2017/3/22.
 */
@Controller
@RequestMapping("/api/paper")
public class PaperController extends BaseController {
    @Autowired
    private PaperService paperService;

    /**
     * 导入试卷包
     *
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/importPaper")
    @ResponseBody
    public JsonResult importPaper(HttpServletRequest request, @RequestParam("file") CommonsMultipartFile file) {
        JsonResult result = new JsonResult();
        try {
            String baseDir = request.getServletContext().getRealPath("");
            String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            String savedPath = baseDir + fileName;
            File savedFile = new File(savedPath);
            file.transferTo(savedFile);

            //增加对上次文件类型检查,试卷包类型之运行为zip格式压缩文件(文件名称统一转成小写,避免大小写)
            if(!fileName.toLowerCase().endsWith(".zip"))
            {
                //如果上次的文件格式不符合，则直接返回错误信息
                result.setFailMsg("上传的文件[" + fileName + "]类型非法，试卷包格式必须为zip压缩文件");

                return result;
            }

            //如果返回试卷名称，则表示已经存在相同名称的试卷
            result = paperService.importPaper(savedPath, baseDir);

        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 查询试卷列表
     *
     * @param paperQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/selectPaper")
    @ResponseBody
    public JsonResult selectPaper(PaperQueryDTO paperQueryDTO) {
        JsonResult result = new JsonResult();
        try {
            List<PaperInfoDTO> papers = paperService.selectPaper(paperQueryDTO);

            int count = paperService.countPaper(paperQueryDTO);
            PageModel<PaperInfoDTO> model = new PageModel<PaperInfoDTO>(paperQueryDTO.getPageIndex(), paperQueryDTO.getPageSize());
            model.setData(papers);
            model.setTotal(count);
            result.setSucceed(model);
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 设置试卷总权限，如果总权限关闭，则模拟考试和自主考试都禁用，反之都启用
     * 试卷状态(0:不可用, 1:可用)
     * @param paperCode
     * @param status
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/setPaperStatus")
    @ResponseBody
    public JsonResult setPaperStatus(String paperCode, Integer status)
    {
        JsonResult result = new JsonResult();
        try
        {
            //设置试卷总权限，如果总权限关闭，则模拟考试和自主考试都禁用，反之都启用,试卷状态(0:不可用, 1:可用)
            int succNum = paperService.setPaperStatus(paperCode, status);

            if(succNum > 0)
            {
                result.setSucceedMsg("设置成功");
            }
            else {
                result.setSucceedMsg("设置失败");
            }
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 设置模拟考试权限
     * 如果试卷总状态为禁用状态，不允许开放模拟考试权限
     *
     * @param paperCode 试卷唯一代码
     * @param allowPlanUsage
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/setAllowPlanUsage")
    @ResponseBody
    public JsonResult setAllowPlanUseage(String paperCode, boolean allowPlanUsage) {
        JsonResult result = new JsonResult();
        try
        {
            Paper paperInfo = paperService.selectPaperByCode(paperCode);

            if(paperInfo == null)
            {
                result.setFailMsg("试卷不存在");
                return result;
            }

            if(paperInfo != null && paperInfo.getStatus() == PaperStatusEnum.DISABLED.getStatusCode())
            {
                result.setSucceedMsg("总权限已被禁用，请先开启总权限");
                return result;
            }

            int succNum = paperService.setAllowPlanUsage(paperCode, allowPlanUsage);
            if(succNum > 0)
            {
                if(allowPlanUsage)
                {
                    result.setSucceedMsg("启用 " + paperInfo.getName() +" 模拟测试权限成功");
                }
                else
                {
                    result.setSucceedMsg("禁用 " + paperInfo.getName() +" 模拟测试权限成功");
                }
            }
            else
            {
                result.setSucceedMsg("启用失败或者试卷总权限被禁用");
            }
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 设置自主考试权限
     * 如果试卷总状态为禁用状态，不允许开放主考试权限
     * @param paperCode
     * @param allowFreeUsage
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/setAllowFreeUsage")
    @ResponseBody
    public JsonResult setAllowFreeUsage(String paperCode, boolean allowFreeUsage) {
        JsonResult result = new JsonResult();
        try {
            Paper paperInfo = paperService.selectPaperByCode(paperCode);

            if(paperInfo == null)
            {
                result.setFailMsg("试卷不存在");
                return result;
            }

            if(paperInfo != null && paperInfo.getStatus() == PaperStatusEnum.DISABLED.getStatusCode())
            {
                result.setSucceedMsg("总权限已被禁用，请先开启总权限");
                return result;
            }


            int succNum = paperService.setAllowFreeUsage(paperCode, allowFreeUsage);

            if(succNum > 0)
            {
                if(allowFreeUsage)
                {
                    result.setSucceedMsg("启用 " + paperInfo.getName() +" 自主考试权限成功");
                }
                else
                {
                    result.setSucceedMsg("禁用 " + paperInfo.getName() +" 自主考试权限成功");
                }
            }
            else
            {
                result.setSucceedMsg("启用失败或者试卷总权限被禁用");
            }
        } catch (Exception ex) {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }




}
