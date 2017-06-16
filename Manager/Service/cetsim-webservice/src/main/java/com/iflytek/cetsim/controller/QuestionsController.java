package com.iflytek.cetsim.controller;

import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.base.model.PageModel;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dao.PaperItemMapper;
import com.iflytek.cetsim.dto.PaperItemQueryDTO;
import com.iflytek.cetsim.model.PaperItem;
import com.iflytek.cetsim.model.PaperItemExample;
import com.iflytek.cetsim.service.QuestionsService;
import org.nutz.json.Json;
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
 * 试题管理
 * Created by Administrator on 2017/5/10.
 */
@Controller
@RequestMapping("/api/question")
public class QuestionsController extends BaseController
{
    @Autowired
    private QuestionsService questionsService;
    @Autowired
    private PaperItemMapper paperItemMapper;


    /**
     * 导入专项训练试题试卷包
     *
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/importQuestion")
    @ResponseBody
    public JsonResult importPaper(HttpServletRequest request, @RequestParam("file") CommonsMultipartFile file) {
        JsonResult result = new JsonResult();

        String baseDir = request.getServletContext().getRealPath("");

        try
        {
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
            result = questionsService.importPaper(savedPath, baseDir);

        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 查询试题列表，带排序规则
     * @param paperItemQueryDTO
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/selectQuestionList")
    @ResponseBody
    public JsonResult selectQuestionList(PaperItemQueryDTO paperItemQueryDTO)
    {
        JsonResult result = new JsonResult();
        try
        {
            List<PaperItemQueryDTO> paperItemList = questionsService.selectQuestionList(paperItemQueryDTO);

            PageModel<PaperItemQueryDTO> pageModel = new PageModel<PaperItemQueryDTO>(paperItemQueryDTO.getPageIndex(), paperItemQueryDTO.getPageSize());
            pageModel.setData(paperItemList);

            pageModel.setTotal(questionsService.countQuestions(paperItemQueryDTO));

            result.setSucceed(pageModel);
        }
        catch (Exception ex)
        {
            result.setFailMsg("查询试题列表异常");
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 设置试题的状态
     * @param paperItemCode
     * @param status
     * @return
     */
    //@IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/setQuestionStatus")
    @ResponseBody
    public JsonResult setQuestionStatus(String paperItemCode, int status)
    {
        JsonResult result = new JsonResult();
        try
        {
            int succ = questionsService.setQuestionStatus(paperItemCode, status);

            if(succ > 0)
            {
                result.setSucceed("状态修改成功");
            }
            else
            {
                result.setSucceed("状态修改失败");
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }


    /**
     *
     * @param paperItemCode
     * @param allowFreeUsage
     * @return
     */
    @IflytekInterceptor(name="WebInterceptor")
    @RequestMapping("/setQuestionAllowFreeUsage")
    @ResponseBody
    public JsonResult setQuestionAllowFreeUsage(String paperItemCode, boolean allowFreeUsage)
    {
        JsonResult result = new JsonResult();
        try
        {
            PaperItemExample paperItemExample = new PaperItemExample();
            paperItemExample.or().andCodeEqualTo(paperItemCode);

            PaperItem paperItem = new PaperItem();
            paperItem.setAllowFreeUseage(allowFreeUsage);
            paperItemMapper.updateByExampleSelective(paperItem, paperItemExample);
            result.setSucceed(null);
        }
        catch (Exception ex)
        {
            result.setFailMsg(ex.getMessage());
        }

        return result;
    }





}
