package com.iflytek.cetsim.common.cache.client;


import java.util.List;

/**
 * <b>类   名：</b>CacheClient<br/>
 * <b>类描述：</b>缓存访问客户端接口（定义缓存工具类方法）<br/>
 * <b>创建人：</b>longzhao<br/>
 * <b>创建时间：</b>2015-9-7 下午1:35:14<br/>
 * <b>修改人：</b>longzhao<br/>
 * <b>修改时间：</b>2015-9-7 下午1:35:14<br/>
 * <b>修改备注：</b><br/>
 *
 * @version 1.0<br/>
 * 
 */
public interface CacheClient
{
    /**
     * 
     * getCache(根据键值取得缓存对应数据)
     * 
     * @param key 键值
     * @return 缓存对应的数据
     * @since 1.0
     * @author longzhao
     */
    public String getCache(String key);
    
    /**
     * 
     * multiGetCaches(从缓存中取得多个数据)
     * 
     * @param keys 键值列表
     * @return 缓存中对应的多个值列表
     * @since 1.0
     * @author longzhao
     */
    public List<String> multiGetCaches(String... keys);
    
    /**
     * 
     * setCache(设置缓存)
     * 
     * @param key 键值
     * @param value 缓存内容
     * @since 1.0
     * @author longzhao
     */
    public void setCache(String key, String value);
    
    /**
     * 
     * setCacheWithExpire(设置固定时间自动过期的缓存)
     * 
     * @param key 键值
     * @param value 缓存内容
     * @param timeout 过期时间（单位：秒）
     * @since 1.0
     * @author longzhao
     */
    public void setCacheWithExpire(String key, String value, long timeout);
}
