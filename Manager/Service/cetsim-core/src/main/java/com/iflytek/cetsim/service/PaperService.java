package com.iflytek.cetsim.service;


import com.iflytek.cetsim.base.interceptor.IflytekInterceptor;
import com.iflytek.cetsim.common.json.JsonResult;
import com.iflytek.cetsim.dto.PaperInfoDTO;
import com.iflytek.cetsim.dto.PaperQueryDTO;
import com.iflytek.cetsim.model.Paper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 试卷信息接口
 *
 * Created by code2life on 2017/3/14.
 */
public interface PaperService
{

    /**
     * 导入试卷包
     *
     * @return
     */
    JsonResult importPaper(String srcFile, String desDir)throws SQLException,IOException;

    List<PaperInfoDTO> selectPaper(PaperQueryDTO paperQueryDTO);

    int countPaper(PaperQueryDTO paperQueryDTO);

    int setPaperStatus(String paperCode, int status);

    int setAllowPlanUsage(String paperCode, boolean allowPlanUsage);

    int setAllowFreeUsage(String paperCode, boolean allowFreeUsage);


    Paper selectPaperByCode(String paperCode);


}
