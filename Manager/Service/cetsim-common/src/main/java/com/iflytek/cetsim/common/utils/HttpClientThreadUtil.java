package com.iflytek.cetsim.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Joiner;

/**
 * 
 * <b>类 名：</b>HttpClientThreadUtil<br/>
 * <b>类描述：</b>多线程处理HttpClient请求<br/>
 * <b>创建人：</b>canwang2<br/>
 * <b>创建时间：</b>2016年7月18日 下午1:59:36<br/>
 * <b>修改人：</b>haoshen3<br/>
 * <b>修改时间：</b>2016年10月16日 下午2:46:22<br/>
 * <b>修改备注：</b><br/>
 *
 * @version 4.0<br/>
 *
 */
public class HttpClientThreadUtil
{
	private static Logger logger = LoggerFactory.getLogger(HttpClientThreadUtil.class);
    /**
     * 最大请求数
     */
    private final static Integer REQUEST_MAX = 100;
    private final static Integer requestTimeOut = 60000;
    private final static Integer connectTimeOut = 60000;
    private final static Integer socketTimeOut = 60000;
    private final static String ENCODE = "UTF-8";

    /**
     * reqList中Map的格式{url:'请求地址',params:{请求参数}}
     * 
     * @param reqList
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String[] threadPost(List<Map<String, Object>> reqList)
    {
        String[] results = new String[reqList.size()];
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置线程数最大100,如果超过100为请求个数
        cm.setMaxTotal(reqList.size() > REQUEST_MAX ? reqList.size() : REQUEST_MAX);
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        try
        {
            PostThread[] postThreads = new PostThread[reqList.size()];
            for (int i = 0; i < reqList.size(); i++)
            {
                Map<String, Object> req = reqList.get(i);
                HttpPost post = new HttpPost((String) req.get("url"));
                postThreads[i] = new PostThread(httpclient, post, (Map<String, Object>) req.get("params"), ENCODE, i + 1);
            }
            // 执行线程
            for (PostThread pt : postThreads)
            {
                pt.start();
            }
            // 设置所有线程执行完毕之后再执行后续代码
            for (PostThread pt : postThreads)
            {
                pt.join();
            }
            for (int i = 0; i < reqList.size(); i++)
            {
                results[i] = postThreads[i].call();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.debug("多线程post请求参数：" + reqList.toString());
            logger.debug("多线程post方法异常：" + e.getMessage());
        }
        finally
        {
            try
            {
                httpclient.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * 
     * singlePost(单个post请求)
     * 
     * @param url 请求URL
     * @param params 参数
     * @return
     * @exception @since 4.0
     * @author canwang2
     */
    public static String singlePost(String url, Map<String, String> params)
    {
    	
    	// 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // 创建httppost    
        HttpPost httppost = new HttpPost(url); 
        
        String result = "";
        // 创建参数队列    
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Iterator<Entry<String, String>> it = params.entrySet().iterator();  
        while(it.hasNext())  
        {  
            Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next(); 
            logger.info("key=" + entry.getKey() + ";value=" + entry.getValue());
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
        } 
        
        UrlEncodedFormEntity uefEntity;  
        try {  
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                	result = EntityUtils.toString(entity);
                } 
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return result;
    }

    /**
     *
     * singlePost(单个post请求)
     *
     * @param url 请求URL
     * @param params 参数
     * @return
     * @exception @since 4.0
     * @author canwang2
     */
    public static String singlePostJSON(String url, Map<String, String> params)
    {

        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);

        String result = "";

        UrlEncodedFormEntity uefEntity;
        try {
            httppost.setHeader("Content-Type","application/json");
            httppost.setEntity(new StringEntity(JSON.toJSONString(params)));
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static String singleGet(String url,Map<String, String> params) {
    	CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
        	String result = Joiner.on("&").withKeyValueSeparator("=")  .join(params); 
        	
            // 创建httpget    
            HttpGet httpget = new HttpGet(url + "?" + result);  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();
                if (entity != null)
                {
                    result = EntityUtils.toString(entity);
                }
            } finally {  
                response.close();  
            } 
            return result;
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
		return null;
    }

    /**
     * reqList中Map的格式{url:'请求地址',params:{请求参数},encode:'编码'}
     * 
     * @param reqList
     * @return
     */
    public static String[] threadGet(List<Map<String, Object>> reqList)
    {
        System.out.println("get共执行" + reqList.size() + "个请求");
        String[] results = new String[reqList.size()];
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置线程数最大100,如果超过100为请求个数
        cm.setMaxTotal(reqList.size() > REQUEST_MAX ? reqList.size() : REQUEST_MAX);
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        try
        {
            GetThread[] getThreads = new GetThread[reqList.size()];
            for (int i = 0; i < reqList.size(); i++)
            {
                Map<String, Object> req = reqList.get(i);
                HttpGet get = new HttpGet((String) req.get("url"));
                getThreads[i] = new GetThread(httpclient, get, i + 1);
            }
            // 执行线程
            for (GetThread gt : getThreads)
            {
                gt.start();
            }
            // 设置所有线程执行完毕之后再执行后续代码
            for (GetThread gt : getThreads)
            {
                gt.join();
            }
            for (int i = 0; i < reqList.size(); i++)
            {
                results[i] = getThreads[i].call();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.debug("多线程get方法异常：" + e.getMessage());
        }
        finally
        {
            try
            {
                httpclient.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * 实现Callable回调接口
     */
    static class PostThread extends Thread implements Callable<String>
    {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpPost httppost;
        private final int id;
        private String result = null;

        public PostThread(CloseableHttpClient httpClient, HttpPost httppost, Map<String, Object> params, String encode, int id)
                throws UnsupportedEncodingException
        {
            // 设置超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(requestTimeOut).setConnectTimeout(connectTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httppost.setConfig(requestConfig);
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty())
            {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, Object> entry : params.entrySet())
                {
                    Object value = entry.getValue();
                    if (value != null)
                    {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value.toString()));
                    }
                }
            }
            if (pairs != null && pairs.size() > 0)
            {
                encode = encode == null ? "UTF-8" : encode;
                httppost.setEntity(new UrlEncodedFormEntity(pairs, encode));
            }
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httppost = httppost;
            this.id = id;
        }

        @Override
        public void run()
        {
            try
            {
                CloseableHttpResponse response = httpClient.execute(httppost, context);
                try
                {
                    // get the response body as an array of bytes
                    HttpEntity entity = response.getEntity();
                    if (entity != null)
                    {
                        result = EntityUtils.toString(entity);
                    }
                }
                finally
                {
                    response.close();
                }
            }
            catch (Exception e)
            {
                logger.error(id + " - error: " + e);
            }
        }

        @Override
        public String call() throws Exception
        {
            return result;
        }
    }

    /**
     * A thread that performs a GET.
     */
    static class GetThread extends Thread implements Callable<String>
    {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private final int id;
        private String result = null;

        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, int id)
        {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
            this.id = id;
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run()
        {
            try
            {
                System.out.println(id + " - about to get something from " + httpget.getURI());
                CloseableHttpResponse response = httpClient.execute(httpget, context);
                try
                {
                    System.out.println(id + " - get executed");
                    // get the response body as an array of bytes
                    HttpEntity entity = response.getEntity();
                    if (entity != null)
                    {
                        result = EntityUtils.toString(entity);
                    }
                }
                finally
                {
                    response.close();
                }
            }
            catch (Exception e)
            {
                System.out.println(id + " - error: " + e);
            }
        }

        @Override
        public String call() throws Exception
        {
            return result;
        }
    }

    public static void main(String[] args)
    {
        String method = "res.book.list";
        String accessToke = "ab1qqqqc";
        String appKey = "KtSNKxk3";
        String format = "json";
        String version = "1.0";
        String url = "http://test.open.changyan.com/api";
        List<Map<String, Object>> reqList = new ArrayList<Map<String, Object>>();
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("access_token", accessToke);
        params.put("format", format);
        params.put("version", version);
        params.put("appkey", appKey);
        params.put("bookcode", "01010101-001");
        req.put("url", url);
        req.put("params", params);
        reqList.add(req);
        long time1 = System.currentTimeMillis();
        String[] result = HttpClientThreadUtil.threadPost(reqList);
        System.out.println(Arrays.toString(result));
        System.out.println(HttpClientThreadUtil.singlePost(url, params));
        System.out.println(System.currentTimeMillis() - time1);
    }

}
