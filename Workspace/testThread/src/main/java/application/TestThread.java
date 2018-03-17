package application;

import threads.MyThread;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuwens on 2017/12/18.
 */
public class TestThread
{

    //local branch dev 001

    // test on dev

    //22222  --> bug  update to 101


    public static void main(String[] args)
    {

        test4();

        //testPatter();

        //test0();

        //test1();

        //test3();

        //String result = doSomethinForFilterTitle("2 .糖尿病的防治");

        //System.out.println(result);


        TestThread testThread = new TestThread();

        MyThread mt = new MyThread();

        Thread th1=new Thread(mt,"窗口1");
        Thread th2=new Thread(mt,"窗口2");
        Thread th3=new Thread(mt,"窗口3");


        //启动三个线程，也即是三个窗口，开始卖票
        //th1.start();
        //th2.start();
        //th3.start();

    }

    private static void test0()
    {
        String v1 = "2017.05.24";

        String v2 = "2014.07.20";

        int result = v1.compareTo(v2);

        System.out.println(result);


    }

    private static void test4()
    {
        //方法一：中文操作系统中打印GBK
        System.out.println(System.getProperty("file.encoding"));

        //方法二：中文操作系统中打印GBK
        System.out.println(Charset.defaultCharset());

        //String s = "values ('459862361', 'null', '030200,1593', '头孢氨苄(沪现代制药-0.25gx12粒)', 'TOUBAOANBIANHUXIANDAIZHIYAOLI', ";

        //String s = "D:\\workspace\\java-workspace\\fzzl\\assist_diagnose_inquiry\\src\\main\\resources\\inq\\data\\normal\\num_not_change_list";

        String s =
                "<div id='2' type='span'>" +
                "<div id='32'>111[专家共识]烟雾病和烟雾综合征诊断与治疗中国专家共识（2017）</div>" +
                "<div id='42'>111[专家共识]烟雾病和烟雾综合征诊断与治疗中国专家共识（2017）</div>" +
                "</div>" +
                "<div id='2' type='span'>" +
                "<div id='52'>111[专家共识]烟雾病和烟雾综合征诊断与治疗中国专家共识（2017）</div>" +
                "<div id='62'>111[专家共识]烟雾病和烟雾综合征诊断与治疗中国专家共识（2017）</div>" +
                "</div>" +
                "<div id='2' type='span'>" +
                "<div id='72'>111[专家共识]烟雾病和烟雾综合征诊断与治疗中国专家共识（2017）</div>" +
                "<div id='82'>111[专家共识]烟雾病和烟雾综合征诊断与治疗中国专家共识（2017）</div>" +
                "</div>";
        //s = s.replaceAll("\'.\'", "");

        //String regEx = "\'(.*?)\'";

        String s2 = "<div id='52' type='span'>111" +
                "是是是11</div>";

        String s3 = "<div id='52' type='span'>111是是是11span'</div>";

        String mm = "<div id='52' type='span'><imgclass=\"\"data-ratio=\"0.8201058201058201\"data-s=\"300,640\"\n" +
                "src=\'http://mmbiz.qpic.cn/mmbiz_png/dibuibpvv6zwiabbvT2kHiacTZgj7MuuI574Pcldd7atcIhYrNzVteE9WI6oPYIa2j8O5A7ibNAOTMBQQfMA4WVA8Tg/640?wx_fmt=png\'\n" +
                "data-type=\"png\"data-w=\"567\" />111是是是11span'</div>";


        String img = "<meta><  img  sss><div><xmg>";

        //String regEx = "inq[\\\\/].*";
        String regEx = "(<div[type='span']*[^>]+>)(.+?)(</div></div>)";

        String regEx3 = "<div[\\S\\s]*?>([\\S\\s]*?)<\\/div>";

        String regEx2 = "[\\d]+";

        String regEx4 = "type='(.*?[^<>])'";

        String regEx_html = "<\\s*img\\s*([^>]*)\\s*>";

        String imgEx = "src[\\s]{0,}=[\\s\"\']{0,}([^\"]+)[\\s\"\']";

        Pattern pattern = Pattern.compile(regEx_html);

        Matcher matcher = pattern.matcher(img);

        int index = 0;

        while (matcher.find())
        {
            String findValue = matcher.group(0);

            //String startDiv = matcher.group(1);

            System.out.println("index:" + index + "; " + findValue);

            index ++;
        }


    }

    private static void test3()
    {
        //<img rel="http://drugs.dxy.cn/image/original/2016/10/28/1477633326041.png"  src = "http://drugs.dxy.cn/image/2016/10/28/1477633326041.png" alt = "图片描述" />
        //<img rel="http://drugs.dxy.cn/image/original/2012/03/06/1331029130.jpg"  src = http://drugs.dxy.cn/image/original/2012/03/06/1331029130.jpg/>
        //String s = "sdfsdf<<img rel  = \"http://drugs.dxy.cn/image/original/2013/08/01/1375339229.png\"  src = \"http://drugs.dxy.cn/image/2013/08/01/1375339229.png\" alt = \"图片描述\" />dfsdfsdfsdfsdf";

        //String s = "rel  = \"http://drugs.dxy.cn/image/original/2013/08/01/1375339229.png\"";

        //String s = "src = \"http://drugs.dxy.cn/image/original/2012/05/07/1336360843026.jpg";

        String s = "2　 .高血压病的防治";

        //String regEx = "\"(.+)\"";

        //String s = "缬沙坦是一种口服有效的特异性的血管紧张素Ⅱ（AT<sub>1</sub>）受体拮抗剂，它选择性地作用于";

        //String regEx = "(一种口服有效)|(血管)|(选择性)";

        //String regEx = "=[\\s]*[\"]*(.+)[\"]*";

        String regEx = "^[．0-9\\.]+[　 \\.]{0,}|^[．]+|[：]{1,}$";

        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(s);

        while (matcher.find())
        {
            String findValue = matcher.group();

            System.out.println(findValue);
        }


    }

    public static String doSomethinForFilterTitle(String titleContent)
    {
        String oldTitleContent = titleContent;


        String repxValue = "^[．0-9\\.]+[　 \\.]{0,}|^[．]+|[：]{1,}$";

        //替换标题开头和结尾的空格，中文：也替换掉
        titleContent = titleContent.trim().replaceAll(repxValue, "");

        //有些场景下，存在一些特殊的等级或者疾病需要程度形容，如：1级高血压，2型糖尿病等,不能替换前面的数字
        List<String> specialWordList = Arrays.asList(new String[]{"级", "型"});

        for (String word : specialWordList) {
            if(titleContent.startsWith(word))
            {
                //如果替换以后的内容是以特殊单词开头的，则不作过滤处理，返回原内容
                return oldTitleContent;
            }
        }

        return titleContent;
    }


    private static void test1()
    {
        String str = "2型糖\\尿`~!@#$%^&*()+=|{}':;',[].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？病";

        String result = replaceSpecialCharacterToNone(str, "\\\\");

        System.out.println(result.trim());

    }

    public static String replaceSpecialCharacterToNone(String originalValue, String replaceToValue)
    {
        //特殊字符的正则
        String specialCharacterRegEx = "[\\\\`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

        if(originalValue == null)
        {
            return originalValue;
        }

        Pattern p = Pattern.compile(specialCharacterRegEx);
        Matcher matcher = p.matcher(originalValue);

        while(matcher.find())
        {
            String matchWord = matcher.group();

            originalValue = originalValue.replace(matchWord, replaceToValue);
        }

        //返回被替换后的字符串
        return originalValue.trim();

    }


    private static void testPatter()
    {
        String inputStr = "．急性丙型肝炎患者的治疗和管理";

        String rexp = "^\\d+(\\.){1,}";

        //匹配以数字和.包含全角2个连续空格，一个英文空格开头的 或者 以：结束的
        /*
         * 数字开头
         * 代小数点 8.2.3
         * 存在中文 \u4E00-\u9FA5, \uFE30-\uFFA0全角字符， 特殊符号不在此范围
         * 去除．
         * 以空格为分割点
         * ：结束
         */
        String rexp2 = "^[．0-9\\.]+[　 ]|^[．]+|[：]{1,}$";

        String rexp3 = "^[．]+";

        //编译正则
        Pattern p = Pattern.compile(rexp);

        Matcher matcher = p.matcher(inputStr);

        boolean result = matcher.find();

        if(result)
        {
            System.out.println("true");
        }
        else
        {
            System.out.println("false");
        }

        String newStr = inputStr.replaceAll(rexp2, "-");

        System.out.println("newStr:" + newStr);

    }



    private void test()
    {
        /*Runnable runnable = () -> {

            int totalTickNum = 100;

            synchronized (Integer.valueOf(totalTickNum)){

                while(true)
                {
                    if(totalTickNum < 0)
                    {
                        break;
                    }
                    totalTickNum --;

                    System.out.printf("当前是第[%d]张票\n", totalTickNum);
                }
            }

        };*/

        MyThread mt = new MyThread();

        Thread th1=new Thread(mt,"窗口1");
        Thread th2=new Thread(mt,"窗口2");
        Thread th3=new Thread(mt,"窗口3");


        //启动三个线程，也即是三个窗口，开始卖票
        th1.start();
        th2.start();
        th3.start();

    }

//sdfsdfsdfsdfsdf
    //-------------------------------------001

    //dev pp111









}
