package com.iflytek.cetsim.common.constants;

import com.iflytek.cetsim.common.utils.PropertiesLoader;
import com.iflytek.cetsim.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>类   名：</b>Constants<br/>
 * <b>类描述：</b>全局静态常量<br/>
 * <b>创建人：</b>longzhao<br/>
 * <b>创建时间：</b>2016-1-26 下午5:12:51<br/>
 * <b>修改人：</b>longzhao<br/>
 * <b>修改时间：</b>2016-1-26 下午5:12:51<br/>
 * <b>修改备注：</b><br/>
 */
public class Constants {

    //session中缓存登录用户信息的key
    public static final String LOGIN_USER_INFO = "loginUser";

    //session过期后，重定向到地址
    public static final String REDIRECT_HOME = "/webapp/index.html";

    /**
     * 当前对象实例
     */
    private static Constants constants = new Constants();

    /**
     * 保存全局属性值
     */
    private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    /**
     *  分组锁
     * */
    public static ConcurrentHashMap<String, Long> groupLock = new ConcurrentHashMap<>();

    /**
     *  分组调用次数计数
     * */
    public static int groupCheckCount = 0;

    /**
     *  清理分组锁标记
     * */
    public static int cleanFlag = 20;

    public static ConcurrentHashMap<String, byte[]> paperMap = new ConcurrentHashMap<>();

    /**
     * 保存全局分组主服务信息
     * */
    public static List<String> startedEcpList = new ArrayList<String>();

    /**
     * 属性文件加载对象
     */
    private static PropertiesLoader loader = new PropertiesLoader("cetsim.properties");

    /**
     * 显示/隐藏
     */
    public static final String SHOW = "1";
    public static final String HIDE = "0";

    /**
     * 考试角色
     */
    public static final String ROLE_A = "A";
    public static final String ROLE_B = "B";

    /**
     * 是/否
     */
    public static final String YES = "1";
    public static final String NO = "0";

    /**
     * 对/错
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String RemainTimeToBeginExam = "remainTimeToBeginExam";
    public static final String AnwerTime = "answerTime";

    /**
     * 成功/失败/session失效
     */
    public static final int SUCCEED = 1;
    //成功状态子码，存在错误信息（部分成功部分失败，批量导入信息时）
    public static final int SUCCEED_FAIL_MESSAGE = 104;

    public static final int FAIL = 0;
    public static final int SESSION_INVALID = 2;

    /**
     * 可用/不可用
     * */
    public static final int AVAILABLE = 1;
    public static final int NOT_AVAILABLE = 0;


    /**
     * 默认翻页记录数
     */
    public static final int FIRST_PAGE = 1;
    public static final int PAGE_SIZE = 20;

    /**
     * 存入session中的当前用户对象
     */
    public static final String SESSION_YONGHU = "sessionUser";
    /**
     * 存入session中的当前用户权限列表
     */
    public static final String SESSION_PERMISSION_LIST = "sessionPermissionList";

    /**
     * 没有权限返回的URL
     */
    public static final String NO_AUTHORIZED_URL = "/sys/noAuthorized";//没有权限返回的URL
    /**
     * 没有权限返回中文说明
     */
    public static final String NO_AUTHORIZED_MSG = "您的权限不足，请联系管理员!";//
    /**
     * 返回值 没有权限 403
     */
    public static final int NO_AUTHORIZED = 403;

    /*
     *默认用户密码
     */
    public static final String DEFAULT_PASSWORD="cetset";

    /**
     * 系统文件路径
     * */
    public static final String PhotoPath = "/photo/";

    public static final String PaperPath = "/paper/";

    //试卷包中的标准音频目录, item02是朗读题，约定的目录结构
    public static final String Standard_Answer_ITEM_02 = "/item02/standard_answer/";

    public static final String PprPath = "/ppr/";

    public static final String StudentTemplatePath="/template/学生模板.xlsx";

    public static final String TeacherTemplatePath="/template/教师模板.xlsx";

    public static final String ANSWER_PATH = "/answer";

    //导出文件地址
    public static final String REPORT_PATH = "/report";

    public static final String DATFILE_PATH = "/datFile";

    public static final String DATFILE = "datFile";

    public static final String WAVFILE = "wavFile";

    public static final String PPRFILE = "pprFile";

    public static final String XMLFILE = "xmlFile";

    public static final String EVAL_SERVER_PATH = "http://127.0.0.1:3030/eval/server";

    public static final String PAPER_MODEL="papers.xml";

    public static final String PPR_MODEL="ppr.xml";

    public static final String SIM_EXAM = "sim";

    public static final String TRAIN_EXAM = "train";

    public static final String SPECIAL_EXAM = "special";



    public static final String TRAIN_TASK_ROOM_GROUPED_EVENT = "TRAIN_TASK_ROOM_GROUPED_EVENT";

    //队友A创建一个新房间请求
    public static final String CANDIDATE_A_CREATE_TASK_ROOM_REQUEST_EVENT = "CANDIDATE_A_CREATE_TASK_ROOM_REQUEST_EVENT";

    //队友B加入房间请求
    public static final String CANDIDATE_B_JION_TASK_ROOM_REQUEST_EVENT = "CANDIDATE_B_JION_TASK_ROOM_REQUEST_EVENT";

    //队友B加入房间事件
    public static final String CANDIDATE_B_JION_TASK_ROOM_EVENT = "CANDIDATE_B_JION_TASK_ROOM_EVENT";

    //A退出房间
    public static final String CANDIDATE_A_QUIT_TASK_ROOM_EVENT = "CANDIDATE_A_QUIT_TASK_ROOM_EVENT";

    //B退出房间
    public static final String CANDIDATE_B_QUIT_TASK_ROOM_EVENT = "CANDIDATE_B_QUIT_TASK_ROOM_EVENT";

    //开始自主考试
    public static final String START_TASK_EXAM_EVENT = "START_TASK_EXAM_EVENT";

    //已经存在的房间或者加入到房间
    public static ConcurrentHashMap<String, Object> trainTaskRoomMap = new ConcurrentHashMap<String, Object>();


    public static final String ASC = "ASC";

    public static final String DESC = "DESC";

    //最高分字段名称
    public static final String MAX_SCORE_COLUMN_NAME = "MAX_SCORE";

    public static final String AVG_SCORE_COLUMN_NAME = "AVG_SCORE";

    public static final String SCORE_COLUMN_NAME = "SCORE";

    public static final String SIM_EXAM_COUNT = "SIM_COUNT";

    public static final String TRAIM_EXAM_COUNT = "TRAIN_COUNT";

    public static final String SPECIAL_EXAM_COUNT = "SPECIAL_COUNT";


    //是否启用邮箱验证1：启用 0：不启用
    public static final String IS_EMAIL_VALIDATE = "IS_EMAIL_VALIDATE";

    public static final String EMAIL_HOST = "EMAIL_HOST";
    public static final String EMAIL_PORT = "EMAIL_PORT";

    public static final String EMAIL_USER_NAME = "EMAIL_USER_NAME";
    public static final String EMAIL_PASSWORD = "EMAIL_PASSWORD";


    /**
     * 获取当前对象实例
     */
    public static Constants getInstance() {
        return constants;
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            value = loader.getProperty(key);
            map.put(key, value != null ? value : StringUtils.EMPTY);
        }
        return value;
    }

    /**
     * 获取URL后缀
     */
    public static String getUrlSuffix() {
        return getConfig("urlSuffix");
    }

}
