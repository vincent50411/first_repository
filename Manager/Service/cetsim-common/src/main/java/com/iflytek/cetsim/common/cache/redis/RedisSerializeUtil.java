package com.iflytek.cetsim.common.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 */
public class RedisSerializeUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisDataSourceImpl.class);

    //序列化
    public static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            log.error("serialize error",e);
        }
        return null;
    }

    // 反序列化
    public static Object deSeialize(byte[] bytes) {
        ByteArrayInputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayOutputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            log.error("deSeialize error",e);
        }
        return null;
    }
}
