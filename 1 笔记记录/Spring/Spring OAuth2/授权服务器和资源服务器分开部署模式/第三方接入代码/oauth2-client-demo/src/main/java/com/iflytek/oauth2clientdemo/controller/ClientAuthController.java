package com.iflytek.oauth2clientdemo.controller;


import com.ibm.oauth.OAuthConstants;
import com.ibm.oauth.OAuthUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模拟第三方应用，采用授权码模式，鉴权服务器和资源服务器分开部署模式(tokenstore采用JWT或者redis存储，内存存储不行)
 */
@RestController
@RequestMapping("/oauth-client")
public class ClientAuthController {

    private String token = "";

    //   http://localhost:8433/oauth/authorize?client_id=appid&response_type=code&redirect_uri=http://localhost:10003/oauth-client/callbackCode

    /**
     * 该接口模拟获取鉴权服务器返回的授权码，拿到授权码后，请求鉴权服务器获取token，拿到token后，请求资源服务器
     *
     * 接受客户端返回的code，提交申请access token的请求
     */

    @RequestMapping("/callbackCode")
    public Object toLogin(@RequestParam("code") String codeValue){
        System.out.println(codeValue);

        try {
            //请求鉴权服务器，获取token值
            token = getPostRequest("appid", "123456", codeValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = "";
        try {

            //请求资源服务器，获取资源
            OAuthUtils.httpGetRequest("http://localhost:10004/userTest/info?access_token=" + token);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 请求鉴权服务器，获取token，需要携带的参数按照OAuth2协议标准,注意请求header设置
     * @param clientId
     * @param clientSecret
     * @param codeValue
     * @return
     */
    public String getPostRequest(String clientId, String clientSecret, String codeValue)
    {
        HttpPost post = new HttpPost("http://localhost:8433/oauth/token");

        List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
        parametersBody.add(new BasicNameValuePair("grant_type", "authorization_code"));

        parametersBody.add(new BasicNameValuePair("client_id", clientId));

        parametersBody.add(new BasicNameValuePair("redirect_uri", "http://localhost:10003/oauth-client/callbackCode"));

        //redirect_uri=http://localhost:10003/oauth-client/callbackCode
        parametersBody.add(new BasicNameValuePair("client_secret", clientSecret));

        //parametersBody.add(new BasicNameValuePair("scope", scope));

        parametersBody.add(new BasicNameValuePair("code", codeValue));

        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        String accessToken = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));

            // Add Basic Authorization header clientid 和secret的base64  username + ":" + password;
            String authMessage = "Basic " + encodeCredentials(clientId, clientSecret);

            post.addHeader("Authorization", authMessage);

            //header contenty-type:application/x-www-form-urlencoded
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");

            post.releaseConnection();

            response = client.execute(post);

            int code = response.getStatusLine().getStatusCode();

            System.out.println("code:" + code);

            Map<String, String> map = OAuthUtils.handleResponse(response);
            accessToken = map.get(OAuthConstants.ACCESS_TOKEN);


            System.out.println("accessToken:" + accessToken);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return accessToken;
    }


    public static String encodeCredentials(String username, String password) {
        String cred = username + ":" + password;
        String encodedValue = null;
        byte[] encodedBytes = Base64.encodeBase64(cred.getBytes());
        encodedValue = new String(encodedBytes);
        System.out.println("encodedBytes " + new String(encodedBytes));

        byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
        System.out.println("decodedBytes " + new String(decodedBytes));

        return encodedValue;

    }



    private String httpGetRequest(String url) throws IOException {
        HttpGet httpGetRequest = new HttpGet(url);

        HttpClient httpClient = new DefaultHttpClient();

        //httpGetRequest.setHeader("Authorization", "Bearer " + token);

        HttpResponse response = httpClient.execute(httpGetRequest);

        String result = "";

        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result= EntityUtils.toString(response.getEntity(),"utf-8");

            System.out.println(result);
        }

        return result;
    }


}
