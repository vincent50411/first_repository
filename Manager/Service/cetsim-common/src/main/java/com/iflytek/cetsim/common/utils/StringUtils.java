package com.iflytek.cetsim.common.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <b>类 名：</b>StringUtils<br/>
 * <b>类描述：</b>字符串工具类<br/>
 * <b>创建人：</b>longzhao<br/>
 * <b>创建时间：</b>2015-7-17 上午9:53:03<br/>
 * <b>修改人：</b>mhli2<br/>
 * <b>修改时间：</b>2015-8-10 下午5:11:36<br/>
 * <b>修改备注：</b>增加部分方法，增加注释<br/>
 * 
 * @version 1.0<br/>
 * 
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils
{
    /**
     * key前缀
     */
    public static final String PRIFIX_STR = "[";

    /**
     * key后缀
     */
    public static final String SUFFIX_STR = "]";

    /**
     * 去除字符串中的html标签.
     * 
     * <pre>
     * StringUtils.replaceHtml(null)  = ""
     * StringUtils.replaceHtml("")    = ""
     * StringUtils.replaceHtml("<td>content</td>") = "content"
     * StringUtils.replaceHtml("<>content</td>") = ""
     * </pre>
     * 
     * @param html 待处理的字符串，可以为 null
     * @return String 处理过的字符串，若输入为null则返回 null
     * @since 1.0
     */
    public static String replaceHtml(String html)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(html))
        {
            return StringUtils.EMPTY;
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll(StringUtils.EMPTY);
        return s;
    }

    /**
     * 缩进字符串（不区分中英文字符），在给定的 length 内取得字符串的缩进,当给定字符串的长度小于length则返回原字符串.
     * 
     * <pre>
     * StringUtils.abbr(null,1)  = ""
     * StringUtils.abbr("",1)    = ""
     * StringUtils.abbr("   ",1) = ""
     * StringUtils.abbr("abc",5) = "ab..."
     * StringUtils.abbr("abc",4) = "a..."
     * StringUtils.abbr("ab",5)  = "ab"
     * </pre>
     * 
     * @param str 目标字符串
     * @param length 截取长度，至少为4（包含...的长度）
     * @return 缩进以后的字符串
     */
    public static String abbr(String str, int length)
    {
        if (isBlank(str))
        {
            return StringUtils.EMPTY;
        }
        try
        {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : str.toCharArray())
            {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3)
                {
                    sb.append(c);
                }
                else
                {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 字符串转换为 Integer 数组.
     * 
     * <pre>
     * StringUtils.toIntegerArray(null, ",")     == []
     * StringUtils.toIntegerArray("", ",")       == []
     * StringUtils.toIntegerArray("  ", ",")     == []
     * StringUtils.toIntegerArray("2,3,4", ",")  == [2,3,4]
     * </pre>
     * 
     * @param val 字符串
     * @param regex 分隔符
     * @return Integer[] Integer数组
     * @exception NumberFormatException
     * @since 1.0
     */
    public static Integer[] toIntegerArray(String val, String regex) throws NumberFormatException
    {
        if (isBlank(val))
        {
            return new Integer[0];
        }
        else
        {
            String[] valArr = val.split(regex);
            int arrLen = valArr.length;
            Integer[] it = new Integer[arrLen];
            for (int i = 0; i < arrLen; i++)
            {
                it[i] = Integer.parseInt(valArr[i].trim());
            }
            return it;
        }
    }

    /**
     * 数组转成字符串，可在打印日志的时候用.
     * 
     * @param args 内容数组
     * @return 转换结果（不同的数据项用半角逗号隔开）
     */
    public static String argsToString(Object[] args)
    {
        StringBuilder s = new StringBuilder("args:[");
        for (Object o : args)
        {
            s.append(o).append(",");
        }
        s.append("]");

        return s.toString();
    }

    /**
     * 
     * 将每一个元素都为字符串的 list 转换为以逗号分割的字符串.
     * 
     * @param list 待处理的 list
     * @return String 处理过的以逗号分隔的字符串
     * @since 1.0
     */
    public static String argsToString(List<String> list)
    {
        StringBuilder s = new StringBuilder();
        if (list == null || list.size() == 0)
        {
            return StringUtils.EMPTY;
        }
        else
        {
            s.append(list.get(0));
            for (int i = 1; i < list.size(); i++)
            {
                s.append(",").append(list.get(i));
            }
            return s.toString();
        }
    }

    /**
     * 判断字符串数组是否有字符串为空或者空字符串.
     * 
     * <pre>
     * StringUtils.isNullOrEmpty(null)             = true
     * StringUtils.isNullOrEmpty(null, "foo")      = true
     * StringUtils.isNullOrEmpty("", "bar")        = true
     * StringUtils.isNullOrEmpty("bob", "")        = true
     * StringUtils.isNullOrEmpty("  bob  ", null)  = true
     * StringUtils.isNullOrEmpty(" ", "bar")       = false
     * StringUtils.isNullOrEmpty("foo", "bar")     = false
     * </pre>
     * 
     * @param str 需要验证的字符串
     * @return Boolean 若字符串数组中包含空或者空字符串，则返回true
     * @since 1.0
     */
    public static Boolean isNullOrEmpty(String... str)
    {
        if (ArrayUtils.isEmpty(str))
        {
            return true;
        }
        else
        {
            for (String s : str)
            {
                if (s == null || s.isEmpty())
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 判断字符串是否非空、非空字符串或者不仅仅由空白字符组成.
     * 
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     * 
     * @param str 需要验证的字符串，可以为null
     * @return boolean 若非空、非空字符串或者不仅仅由空白字符组成，则返回true
     * @since 1.0
     */
    public static boolean isNotBlank(String str)
    {
        return org.apache.commons.lang3.StringUtils.isNotBlank(str);
    }

    /**
     * 判断字符串是否非空或者非空字符串.
     * 
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     * 
     * @param str 需要验证的字符串，可以为null
     * @return boolean 若非空或者非空字符串，则返回true
     * @since 1.0
     */
    public static boolean isNotEmpty(String str)
    {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(str);
    }

    /**
     * 
     * 判断所有的字符串是否都不为空或者空字符串.
     * 
     * <pre>
     * StringUtils.isNoneEmpty(null)             = false
     * StringUtils.isNoneEmpty(null, "foo")      = false
     * StringUtils.isNoneEmpty("", "bar")        = false
     * StringUtils.isNoneEmpty("bob", "")        = false
     * StringUtils.isNoneEmpty("  bob  ", null)  = false
     * StringUtils.isNoneEmpty(" ", "bar")       = true
     * StringUtils.isNoneEmpty("foo", "bar")     = true
     * </pre>
     * 
     * @param str 需要验证的字符串，可以为多个，可以为空或者空字符串
     * @return boolean 若所有的字符串都不为空和空字符串，返回true
     * @since 1.0
     */
    public static boolean isNoneEmpty(String... str)
    {
        return org.apache.commons.lang3.StringUtils.isNoneEmpty(str);
    }

    /**
     * 判断字符串是否为空或者空字符串.
     * 
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * 
     * @param str 需要验证的字符串
     * @return boolean 若字符串为空或者空字符串，返回true
     * @since 1.0
     */
    public static boolean isEmpty(String str)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(str);
    }

    /**
     * 
     * 判断字符串是否为空、空字符串或者仅由空白字符组成.
     * 
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str 需要验证的字符串，可以为null
     * @return boolean 若为空、空字符串或者仅由空白字符组成，则返回true
     * @since 1.0
     */
    public static boolean isBlank(String str)
    {
        return org.apache.commons.lang3.StringUtils.isBlank(str);
    }

    /**
     * 移除字符串开头和结尾的空白字符包括空格、制表符等.
     * 
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     * 
     * @param str 需要处理的字符串，可以为空
     * @return String 处理过的字符串，若输入的字符串为空，则返回null
     * @since 1.0
     */
    public static String trim(String str)
    {
        return org.apache.commons.lang3.StringUtils.trim(str);
    }

    /**
     * 比较两个字符串是否相等.
     * 
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     * 
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return boolean 若两个字符串相等则返回true
     * @since 1.0
     */
    public static boolean equals(String str1, String str2)
    {
        return org.apache.commons.lang3.StringUtils.equals(str1, str2);
    }

    /**
     * 忽略大小写,比较两个字符串是否相等,若相等则返回true,反之则返回false.
     * 
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     * 
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return boolean 忽略大小写，若两个字符串相等，则返回ture
     * @since 1.0
     */
    public static boolean equalsIgnoreCase(String str1, String str2)
    {
        return org.apache.commons.lang3.StringUtils.equalsIgnoreCase(str1, str2);
    }

    /**
     * 将字符串转化为大写.
     * 
     * <pre>
     * StringUtils.upperCase(null)  = null
     * StringUtils.upperCase("")    = ""
     * StringUtils.upperCase("aBc") = "ABC"
     * </pre>
     * 
     * @param str 需要转化的字符串
     * @return String 转化后的字符串，若输入null则返回null
     * @since 1.0
     */
    public static String upperCase(String str)
    {
        return org.apache.commons.lang3.StringUtils.upperCase(str);
    }

    /**
     * 将字符串转化为小写.
     * 
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("aBc") = "abc"
     * </pre>
     * 
     * @param str 需要转化的字符串
     * @return String 转化后的字符串，若输入null则返回null
     * @since 1.0
     */
    public static String lowerCase(String str)
    {
        return org.apache.commons.lang3.StringUtils.lowerCase(str);
    }

    /**
     * htmlEscape(html转义)
     * 
     * @param content 待转义字符串
     * @return String 转义后字符串
     * @since 1.0
     */
    public static String htmlEscape(String content)
    {
        if (isNullOrEmpty(content))
        {
            return EMPTY;
        }
        String html = content;
        html = html.replaceAll("&amp;", "&");
        // "
        html = html.replace("&quot;", "\"");
        // 替换跳格
        html = html.replace("&nbsp;&nbsp;", "\t");
        // 替换空格
        html = html.replace("&nbsp;", " ");
        html = html.replace("&lt;", "<");
        html = html.replaceAll("&gt;", ">");
        return html;
    }

    /**
     * 
     * setAttrValue(对扩展属性操作)
     * 
     * @param attrStr 需要操作所有属性串 {[key1]value1[key1]}{[key2]value2[key2]}
     * @param key 需操作的属性名
     * @param value 需操作的属性值
     * @return  成功返回处理后的属性串
     * @exception @since 1.0
     * @author WANGCAN
     */
    public static String setAttrValue(String attrStr, String key, String value)
    {

        if(StringUtils.isNullOrEmpty(attrStr)){
            attrStr = "";
        }
        String temStr = org.apache.commons.lang3.StringUtils.substringBetween(attrStr, PRIFIX_STR + key + SUFFIX_STR);
        if (null == temStr)
        {
            attrStr += String.format("%s%s%s%s", "{", PRIFIX_STR + key + SUFFIX_STR, value.trim(), PRIFIX_STR + key + SUFFIX_STR + "}");
        }
        else
        {
            String newAttr = String.format("%s%s%s%s", "{", PRIFIX_STR + key + SUFFIX_STR, value.trim(), PRIFIX_STR + key + SUFFIX_STR + "}");
            temStr = String.format("%s%s%s%s", "{", PRIFIX_STR + key + SUFFIX_STR, temStr.trim(), PRIFIX_STR + key + SUFFIX_STR + "}");
            attrStr = attrStr.replace(temStr, newAttr);
        }
        return attrStr;
    }

    /**
     * ,
     * 
     * getAttrValue(根据属性串获取某一属性)
     * 
     * @param attrStr 需要操作所有属性串  格式： {[key1]value1[key1]}{[key2]value2[key2]}
     * @param key
     * @return value
     * @exception @since 1.0
     * @author WANGCAN
     */
    public static String getAttrValue(String attrStr, String key)
    {
        return org.apache.commons.lang3.StringUtils.substringBetween(attrStr, PRIFIX_STR + key.trim() + SUFFIX_STR);
    }
    /**
     * getHashTableAttr(属性切分为键值对)
     * @param attrString 需要操作所有属性串  格式： {[key1]value1[key1]}{[key2]value2[key2]}
     * @exception @since 1.0
     * @author tangjian
     * @return Hashtable<k,v>
     */
    public static Hashtable<String ,String> getHashTableAttr(List<String> keys,String attrString)
    {
        if(keys == null || keys.size() == 0) 
        {
         return new Hashtable<String,String>();
        }
        Hashtable<String ,String> table = new Hashtable<String ,String>();
        for(String Key :  keys)
        {
        	table.put(Key, getAttrValue(attrString,Key));
        }
        return table;
    }
    /**
     * 
     * getBianMaKeyList(解析编码规则中编码表达式)
     * 
     * @param bmExpression
     * @return 
     * @exception 
     * @since 1.0
     * @author tangjian
     */
    public static String[] getBianMaKeyList(String bmExpression){
        bmExpression = bmExpression.replaceAll("\\%\\%", "%");
        if(bmExpression.startsWith("%"))
        {
            bmExpression = bmExpression.substring(1);    
        }
        if(bmExpression.endsWith("%")){
            bmExpression = bmExpression.substring(0,bmExpression.length()-1);
        }
        bmExpression=bmExpression.replaceAll("\\%", ",");
        return bmExpression.split(",");
    }
    /**
     * 获取属性值K-V
     * @param attrStr
     * @param attrStr 只限定为单选和复选
     * @return  Hashtable<String ,String>
     * @author tangjian
     */
    public static Hashtable<String ,String> getAttrAllKeyValue(String attrStr){
    	String tempStr="" , keyString = "";
    	String pattern = "\\{\\[";
    	Pattern r = Pattern.compile(pattern);
    	String[] ms = r.split(attrStr);
    	String keyItem = "";
    	Hashtable<String ,String> tableHashtable = new Hashtable<String ,String>();
    	
    	for(int i =0;i<ms.length;i++)
    	{
    		keyItem = ms[i];
    		if(!isEmpty(keyItem.trim())){
        		keyItem = keyItem.substring(0,keyItem.indexOf(']'));
        		tempStr = StringUtils.getAttrValue(attrStr,keyItem);
        		if(tempStr == null) tempStr = "";
        		tableHashtable.put(keyItem,tempStr);
    		}
    	}
    	return tableHashtable;
    }
    
    /**
     * 获得用户远程地址
     * 
     * @param request
     * @return String
     * @exception /throws
     */
	public static String getRemoteAddr(HttpServletRequest request){
		String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("X-Forwarded-For");
        }else if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("Proxy-Client-IP");
        }else if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}
	
    /**
     * 根据字符串生成Map  (如： "aa,bb,cc,dd","1,1,0,0","rukou" 最后生成  {aa=rukou,bb=rukou}
     * 
     * @param str1,str2,str3
     * @return Map
     */
	public static Multimap<String,String> getPermission(String str1,String str2,String str3) {
		Multimap<String,String> multiMap = HashMultimap.create(); 
		if (isBlank(str1) || isBlank(str1) || isBlank(str1)) {
			return null;
		}
		String[] xianzhi = str1.split(",");
		for(int i=0;i<xianzhi.length;i++) {
			if (i<xianzhi.length-1) {
				if("1".equals(str2.substring(0,str2.indexOf(",")))) {
					multiMap.put(str3,xianzhi[i]);
				}
				str2 = str2.substring(str2.indexOf(",")+1);
			} else if("1".equals(str2)){
				multiMap.put(str3,xianzhi[i]);
			}
		}
//		Multimap<String,String> processMap = Maps.transformEntries(multiMap, new Function() {
//			@Override
//			public Object apply(Object arg0) {
//				return str3;
//			}
//		});
		return multiMap;
	}
	
    /**
     * 将map转换为list(如： {aa=rukou,bb=rukou}可转为  {rukou:aa,rukou:bb})
     * 
     * @param map
     * @return list
     */
	public static List<String> transPermission(Multimap<String,String> map) {
		List<String> list = Lists.newArrayList();
		for(Map.Entry<String,String> entry:map.entries()){  
			String key = entry.getKey();  
            String value = entry.getValue();  
            list.add((new StringBuffer(key).append(":").append(value)).toString());  
		}
		return list;
	}
	
	/**
	 * 生成验证码字符串
	 * 
	 * @param g
	 * @return 验证码字符串
	 */
	public static String createCharacter(Graphics g) {
		char[] codeSeq = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
				'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };
		String[] fontTypes = {"Arial","Arial Black","AvantGarde Bk BT","Calibri"}; 
		Random random = new Random();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);
			g.setColor(new Color(50 + random.nextInt(100), 50 + random.nextInt(100), 50 + random.nextInt(100)));
			g.setFont(new Font(fontTypes[random.nextInt(fontTypes.length)],Font.BOLD,26)); 
			g.drawString(r, 15 * i + 5, 19 + random.nextInt(8));
			s.append(r);
		}
		return s.toString();
	}
    
    public static void main(String[] args)
    {
//    	Map<String,String> map = getPermission("aa,bb,cc,dd","1,1,0,0","rukou");
//    	String str =  Joiner.on(",").withKeyValueSeparator(":").join(map);
//    	System.out.println(str);
//    	Map<Integer,String> map1 = Maps.newHashMap();
//    	map1.put(1, "aa");
//    	map1.put(2, "bb");
//    	Map<Integer,String> map2 = Maps.newHashMap();
//    	map2.put(1, "cc");
//    	map2.put(3, "dd");
//    	MapDifference differenceMap = Maps.difference(map1, map2);  
//    	differenceMap.areEqual();  
//    	Map entriesDiffering = differenceMap.entriesDiffering();  
//    	System.out.println(entriesDiffering.get(1));
//    	Map entriesOnlyOnLeft = differenceMap.entriesOnlyOnLeft();  
//    	Map entriesOnlyOnRight = differenceMap.entriesOnlyOnRight(); 
//    	Map entriesInCommon = differenceMap.entriesInCommon(); 
    	long time=new Date().getTime();
    	 System.out.println(""+time);
    }
}
