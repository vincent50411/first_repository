package com.iflytek.cetsim.common.utils;

/**
 * Created by qxb-810 on 2017/4/10.
 */
public class VersionUtil {

    public static boolean CompareVersion(String compareVer,String minVer, String maxVer){
        String[] compare = compareVer.split("\\.");
        String[] min = minVer.split("\\.");
        String[] max = maxVer.split("\\.");

        int len=Math.min(compare.length, Math.min(max.length,min.length));
        boolean match = true;
        for(int v = 0; v < len;v++){
            if(v == len - 1) {
                match = match && (Integer.parseInt(min[v]) <= Integer.parseInt(compare[v]) && Integer.parseInt(compare[v]) < Integer.parseInt(max[v]));
            } else {
                match = match && (Integer.parseInt(min[v]) <= Integer.parseInt(compare[v]) && Integer.parseInt(compare[v]) <= Integer.parseInt(max[v]));
            }
        }
        return match;
    }
}
