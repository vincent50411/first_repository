package com.iflytek.cetsim.common.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Created by pengwang on 2017/3/23.
 */
public class Zip4jUtils {
    //解压压缩文件
    public static void extractAllFiles(String srcFile,String desDir) {
        try {
            ZipFile zipFile = new ZipFile(srcFile);
            zipFile.extractAll(desDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

}
