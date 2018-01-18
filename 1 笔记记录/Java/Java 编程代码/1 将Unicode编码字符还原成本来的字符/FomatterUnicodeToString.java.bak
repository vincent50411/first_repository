package com.iflytek.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将Unicode字符编码 还原成本来的字符
 */
public class FomatterUnicodeToString {

    public static void main(String[] args)
    {
        test2();
    }

    private static void test2()
    {
        String message = "$$ sdfsdfsdfsdf";

        String resultMessage = getFixStr("\\u266B");

        //替换特殊的字符需要加\  replaceAll中 \\\\标识一个真正的\
        String newM = message.replaceAll("\\$\\$", "");

        System.out.println("--> result:" + resultMessage);
    }

    /**
     * 修改字符串中的unicode码
     * @param s 源str
     * @return  修改后的str
     */
    public static String decode2( String s )
    {
        StringBuilder sb = new StringBuilder( s.length() );
        char[] chars = s.toCharArray();
        for( int i = 0; i < chars.length; i++ )
        {
            char c = chars[i];
            if( c == '\\' && chars[i + 1] == 'u')
            {
                char cc = 0;
                for( int j = 0; j < 4; j++ )
                {
                    char ch = Character.toLowerCase( chars[i + 2 + j] );
                    if( '0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f' )
                    {
                        cc |= ( Character.digit( ch, 16 ) << ( 3 - j ) * 4 );
                    }else
                    {
                        cc = 0;
                        break;
                    }
                }
                if ( cc > 0 )
                {
                    i += 5;
                    sb.append( cc );
                    continue;
                }
            }
            sb.append( c );
        }
        return sb.toString();
    }

    static Pattern p = Pattern.compile( "(\\\\u.{4})" );

    /**
     * 获取修复后的字符串
     * @param str
     * @return
     */
    public static String getFixStr( String str )
    {
        String ret = str;

        Matcher m = p.matcher( ret );
        while( m.find() )
        {
            String xxx = m.group( 0 );

            String value = decode2( xxx );

            xxx = xxx.replaceAll("\\\\", "");

            ret = str.replaceAll( "\\\\" + xxx, value);
        }

        return ret;
    }



}
