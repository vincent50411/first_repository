package com.iflytek.cetsim.controller;

import com.alibaba.dubbo.common.json.JSONObject;
import com.iflytek.cetsim.base.controller.BaseController;
import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.common.constants.Constants;
import com.iflytek.cetsim.common.json.ExamJsonResult;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.ExamLoginDTO;
import com.iflytek.cetsim.service.ExamService;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.List;

/**
 * 考试机相关web接口
 *
 * Created by wbyang3 on 2017/5/8.
 */

@Controller
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private ExamService examService;

    /**
     * 考生登录
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param ip ip地址
     * @param mac mac地址
     * */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult login(String recordId,String recordType,String ip, String mac, HttpServletRequest req) {
        ExamJsonResult json = this.getJson4Exam();
        try {
            ExamLoginDTO dto = examService.login(recordId, recordType, ip, mac);
            if(dto == null) {
                logger.error("考生登录完成：未找到待考考生!");
                json.setFailMsg("未找到待考考生!");
            } else {
                //将图片用base64返回
                if(dto.getPhoto() != null)
                {
                    String basePath = req.getServletContext().getRealPath("");
                    File photoFile = Paths.get(basePath,"photo",dto.getPhoto()).toFile();
                    String mimeType = URLConnection.guessContentTypeFromName(photoFile.getName());
                    if(photoFile.exists() && photoFile.isFile())
                    {
                        dto.setPhoto("data:"+mimeType+";base64,"+ Base64.encodeBase64String(FileUtils.readFileToByteArray(photoFile)));
                    }
                }
                logger.info("考生登录完成，登录成功");
                json.setSucceed(dto, "登录成功");
            }
        } catch (Exception ex) {
            logger.error("登录接口出错!", ex);
            json.setFailMsg(ex.getMessage());
        }
        return json;
    }

    /**
     * 更新考生考试状态
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param state 要更新的状态
     * */
    @RequestMapping(value = "/updateFlowState", method = RequestMethod.POST)
    @ResponseBody
    @IflytekInterceptor(name="ClientExamInterceptor")
    public ExamJsonResult updateFlowState(String recordId,String recordType, Integer state) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.updateFlowState(recordId, recordType, state);
            logger.info(String.format("考生状态更新成功[recordId]:%s [recordType]:%s [state]:%d", recordId, recordType, state ));
            result.setSucceedMsg("更新状态成功");
        } catch (Exception ex) {
            logger.error("接口出错， 更新状态失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 更新考生分组状态
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param state 要更新的状态
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/updateGroupState", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult updateGroupState(String recordId,String recordType, Integer state) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.updateGroupState(recordId, recordType, state);
            logger.info(String.format("分组状态更新成功[recordId]:%s [recordType]:%s [state]:%d", recordId, recordType, state ));
            result.setSucceedMsg("更新分组状态成功");
        } catch (Exception ex) {
            logger.error("接口出错， 更新分组状态失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 更新考生试卷下载状态
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param state 要更新的状态
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/updatePaperState", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult updatePaperState(String recordId,String recordType, Integer state) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.updatePaperState(recordId, recordType, state);
            logger.info(String.format("试卷下载状态更新成功[recordId]:%s [recordType]:%s [state]:%d", recordId, recordType, state ));
            result.setSucceedMsg("更新试卷下载状态成功");
        } catch (Exception ex) {
            logger.error("接口出错， 更新试卷下载状态失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 更新考生答卷上传状态
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param state 要更新的状态
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/updateDataState", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult updateDataState(String recordId,String recordType, Integer state) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.updateDataState(recordId, recordType, state);
            logger.info(String.format("答卷上传状态更新成功[recordId]:%s [recordType]:%s [state]:%d", recordId, recordType, state ));
            result.setSucceedMsg("更新答卷上传状态成功");
        } catch (Exception ex) {
            logger.error("接口出错， 更新答卷上传状态失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 更新考生错误码
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param errorCode 错误码
     * */
    @RequestMapping(value = "/updateErrorCode", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult updateErrorCode(String recordId,String recordType, Integer errorCode) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.updateErrorCode(recordId, recordType, errorCode);
            logger.info(String.format("错误码更新成功[recordId]:%s [recordType]:%s [errorCode]:%d", recordId, recordType, errorCode ));
            result.setSucceedMsg("更新错误码成功");
        } catch (Exception ex) {
            logger.error("接口出错， 更新错误码失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 请求预分组
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/checkGroup", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult checkGroup(String recordId,String recordType, HttpServletRequest req) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            List<ExamLoginDTO> group = examService.checkGroup(recordId, recordType);
            //base64化考生照片
            String basePath = req.getServletContext().getRealPath("");
            for(ExamLoginDTO examinee : group) {
                if(examinee.getPhoto() != null) {
                    File photoFile = Paths.get(basePath,"photo",examinee.getPhoto()).toFile();
                    String mimeType = URLConnection.guessContentTypeFromName(photoFile.getName());
                    if(photoFile.exists() && photoFile.isFile())
                    {
                        examinee.setPhoto("data:"+mimeType+";base64,"+ Base64.encodeBase64String(FileUtils.readFileToByteArray(photoFile)));
                    }
                }
            }
            result.setSucceed(group);
            result.setSucceedMsg("请求预分组成功");
            logger.info(String.format("请求预分组完成:[recordId]%s [recordType]%s [result]%d", recordId, recordType, group.size()));
        } catch (Exception ex) {
            logger.error("接口出错， 请求预分组失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 分组异常接口, 将自己的同组ID置空,同组记录置为无效数据
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param partnerRecordId 同组考生记录ID
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/groupException", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult groupException(String recordId, String recordType, String partnerRecordId) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.groupException(recordId, recordType, partnerRecordId);
            result.setSucceedMsg("报告分组异常成功");
            logger.info(String.format("报告分组异常完成:[recordId]%s [recordType]%s [partnerId]%s", recordId, recordType, partnerRecordId));
        } catch (Exception ex) {
            logger.error("接口出错， 报告分组异常失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 退出分组接口
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/exitGroup", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult exitGroup(String recordId, String recordType) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.exitGroup(recordId, recordType);
            result.setSucceedMsg("退出分组成功");
            logger.info(String.format("退出分组成功:[recordId]%s [recordType]%s", recordId, recordType));
        } catch (Exception ex) {
            logger.error("接口出错， 退出分组成功失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取同组分组状态
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param partnerRecordId 同组考生RecordCode
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/getPartnerGroupState", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult getPartnerGroupState(String recordId,String recordType, String partnerRecordId) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            Integer state = examService.getPartnerGroupState(recordId, recordType, partnerRecordId);
            result.setSucceed(state, "获取同组分组状态成功");
            logger.info(String.format("获取同组分组状态成功[partnerRecordId]:%s [recordType]:%s [state]:%d", partnerRecordId, recordType, state ));
        } catch (Exception ex) {
            logger.error("接口出错， 获取同组分组状态失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取同组考试状态
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param partnerRecordId 同组考生RecordCode
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/getPartnerFlowState", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult getPartnerFlowState(String recordId,String recordType, String partnerRecordId) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            Integer state = examService.getPartnerFlowState(recordId, recordType, partnerRecordId);
            result.setSucceed(state, "获取同组考试状态成功");
            logger.info(String.format("获取同组考试状态成功[partnerRecordId]:%s [recordType]:%s [state]:%d", partnerRecordId, recordType, state ));
        } catch (Exception ex) {
            logger.error("接口出错， 获取同组考试状态失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }


    /**
     * 下载试卷
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param paperCode 试卷号
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/downloadPaper", method = {RequestMethod.POST,RequestMethod.GET}, produces = "application/octet-stream;charset=UTF-8")
    public ResponseEntity<byte[]> downloadPaper(String recordId, String recordType, String paperCode) {
        logger.info(String.format("开始下载试卷[paperCode]:%s",paperCode));
        byte[] response = new byte[0];
        String destFileName = paperCode + ".rpk";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", destFileName);
        try {
            //试卷优先从内存里读取
            if(Constants.paperMap.containsKey(paperCode) && Constants.paperMap.get(paperCode).length != 0)
            {
                response = Constants.paperMap.get(paperCode);
                logger.info(String.format("获取试卷成功[缓存获取]false [paperCode]%s [recordType]%s", paperCode, recordType));
            }
            else {
                response = examService.downloadPaper(recordType, recordId);
                Constants.paperMap.put(paperCode,response);
                logger.info(String.format("获取试卷成功[缓存获取]true [paperCode]%s [recordType]%s", paperCode, recordType));
            }
        } catch (Exception ex) {
            logger.error("下载试卷接口出错!", ex);
        }
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    /**
     * 上传答卷包
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param file 答卷包rpk
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/uploadAnswer", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult uploadAnswer(@RequestParam("file") CommonsMultipartFile file, String recordId, String recordType, HttpServletRequest request) {
        ExamJsonResult json = getJson4Exam();
        logger.info(String.format("开始上传答案[recordId]%s [recordType]%s", recordId, recordType));
        try {
            File path = Paths.get(request.getServletContext().getRealPath(Constants.ANSWER_PATH)).toFile();
            if(!path.exists()) {
                boolean dir = path.mkdir();
                if(!dir) throw new Exception("无法创建答卷包文件夹");
            }
            File destFile = Paths.get(request.getServletContext().getRealPath(Constants.ANSWER_PATH), recordId + ".rpk").toFile();
            file.transferTo(destFile);
            examService.uploadAnswer(recordId, recordType, recordId + ".rpk");
            json.setSucceed(true);
            logger.info("上传答卷包完成! [recordId]" + recordId);
        } catch (Exception ex) {
            logger.error("上传答卷包接口出错!", ex);
            json.setFailMsg("上传答卷包接口调用失败!" + ex.getMessage());
        }
        return json;
    }

    @RequestMapping(value = "/setEcpStart",method= RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult setEcpStart(String recordId)
    {
        ExamJsonResult json = getJson4Exam();
        if(!Constants.startedEcpList.contains(recordId)) {
            Constants.startedEcpList.add(recordId);
        }
        json.setSucceed(true);
        logger.info("考试机启动成功! [recordId]" + recordId);
        return json;
    }

    @RequestMapping(value = "/isEcpStart",method= RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult isEcpStart(String recordId)
    {
        ExamJsonResult json = getJson4Exam();
        if(Constants.startedEcpList.contains(recordId)) {
            json.setSucceed(true);
            Constants.startedEcpList.remove(recordId);
            logger.info("获取到考试机启动成功状态! [recordId]" + recordId);
        }
        else
        {
            json.setSucceed(false);
        }
        return json;
    }

    /**
     * 小题评测
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param itemCode 小题号
     * @param paperCode 试卷号
     * @param file dat中间结果文件
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/evaluateSectionScore", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult evaluateSectionScore(String recordId,String recordType, String itemCode, String paperCode,
                                               @RequestParam(Constants.DATFILE) CommonsMultipartFile file, HttpServletRequest request) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            logger.info(String.format("开始小题评测[paperCode]%s [itemCode]%s", paperCode, itemCode));
            File path = Paths.get(request.getServletContext().getRealPath(Constants.DATFILE_PATH)).toFile();
            if(!path.exists()) {
                boolean dir = path.mkdir();
            }
            File destFile = Paths.get(request.getServletContext().getRealPath(Constants.DATFILE_PATH),
                    recordId +"_"+ itemCode +".dat").toFile();
            if(file != null) {
                file.transferTo(destFile);
            }
            String datPath = destFile.getAbsolutePath();
            logger.info("[step 1]完成文件转储: [datPath]" + datPath);
            boolean evalRes = examService.evaluateItem(recordId, recordType, datPath, itemCode, paperCode);
            if(evalRes)
                result.setSucceed("[step 2]完成服务端评测");
            else
                result.setFailMsg("服务端评测过程出现异常!");
        } catch (Exception ex) {
            logger.error("接口出错， 小题评测失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 小题评测失败记录
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param itemCode 小题号
     * @param paperCode 试卷号
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/evaluateSectionFail", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult evaluateSectionFail(String recordId,String recordType, String itemCode, String paperCode, Integer engineCode) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.evaluateFail(recordId, recordType, engineCode, itemCode, paperCode);
            result.setSucceed("小题评测失败结果写入成功");
            logger.info(String.format("完成客户端评测失败结果记录[paperCode]%s [itemCode]%s [engineCode]%x", paperCode, itemCode, engineCode));
        } catch (Exception ex) {
            logger.error("接口出错， 小题评测失败结果写入错误!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 整卷评测结果
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param success 是否评测成功
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/evaluateFinish", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult evaluateFinish(String recordId,String recordType, Boolean success) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            examService.evaluateFinish(recordId, recordType, success);
            result.setSucceed("整卷评测结果写入成功");
            logger.info(String.format("完成整卷评测结果写入[recordId]%s [recordType]%s [success]%s", recordId, recordType, success));
        } catch (Exception ex) {
            logger.error("接口出错， 整卷评测结果写入错误!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 专项朗读题评测
     *
     * @param recordId 考试记录号
     * @param recordType 考试记录类型
     * @param paperCode 试卷号
     * @param file wav文件,朗读题的语音文件
     * */
    @IflytekInterceptor(name="ClientExamInterceptor")
    @RequestMapping(value = "/evaluateReadChapter", method = RequestMethod.POST)
    @ResponseBody
    public ExamJsonResult evaluateReadChapter(String recordId,String recordType, String paperCode,
                      @RequestParam("file") CommonsMultipartFile file,HttpServletRequest request) {
        ExamJsonResult result = this.getJson4Exam();
        try {
            logger.info(String.format("开始学习引擎评测朗读题[recordId]%s[paperCode]%s", recordId, paperCode));
            File path = Paths.get(request.getServletContext().getRealPath(Constants.WAVFILE)).toFile();
            File xmlDir = Paths.get(request.getServletContext().getRealPath(Constants.XMLFILE)).toFile();
            if(!path.exists()) {
                boolean dir = path.mkdir();
            }
            if(!xmlDir.exists()) {
                boolean dir = xmlDir.mkdir();
            }
            File destFile = Paths.get(request.getServletContext().getRealPath(Constants.WAVFILE),
                    recordId + ".wav").toFile();
            if(file != null) {
                file.transferTo(destFile);
            }
            String wavPath = destFile.getAbsolutePath();

            logger.info("[step 1]完成朗读题语音转储: [wavPath]" + wavPath);
            boolean readResult = examService.evaluateReadChapter(recordId, recordType, path.getParent(), wavPath, paperCode);
            if (readResult)
                result.setSucceed("[step 2]完成学习引擎评测");
            else
                result.setFlagMsg("[step 2]学习引擎评测失败");
        } catch (Exception ex) {
            logger.error("接口出错， 小题评测失败!", ex);
            result.setFailMsg(ex.getMessage());
        }
        return result;
    }
}
