package com.moviewall.util;


import com.moviewall.bean.Msg;
import com.moviewall.exception.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpUtil {

    /**
     * 发送get请求，回应使用utf-8编码进行转换
     * @param url
     * @param strings 请求的键和值，必须为0或2的倍数
     * @return
     * @throws Exception
     */
    public static String sentGet(String url, String[] strings) throws HttpException {
        return HttpUtil.sentGet(url, strings, "utf-8");
    }

    /**
     * 发送get请求
     * @param url
     * @param strings        请求的键和值，必须为0或2的倍数
     * @param defaultCharset 转换回应时使用的编码
     * @return
     * @throws Exception
     */
    public static String sentGet(String url, String[] strings, String defaultCharset) throws HttpException {


        if (strings != null) {

            //长度应该为0或2的倍数
            Assert.isTrue(strings.length % 2 == 0, "strings长度错误！");
            StringBuffer stringBuffer = new StringBuffer(url);
            stringBuffer.append("?");
            for (int i = 0; i < strings.length; i++, i++) {
                stringBuffer.append(strings[i]).append("=").append(strings[i + 1]).append("&");
            }

            //删除最后一个"&"
            url = stringBuffer.substring(0, stringBuffer.length() - 1);
        }

        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = null;

        HttpClient httpClient = new DefaultHttpClient();

        try {
            //发送请求
            httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw new HttpException("发送失败！");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        try {
            //获取回应
            if (httpResponse != null) {
                return EntityUtils.toString(httpResponse.getEntity(), defaultCharset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw new HttpException("格式转换失败！");
            } catch (HttpException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发送post请求
     * @param url
     * @param strings 请求的键和值，必须为0或2的倍数
     * @return
     * @throws HttpException
     */
    public static String sentPost(String url, String[] strings) throws HttpException {
        return sentPost(url, strings, "utf-8");
    }

    /**
     * 发送post请求，回应使用utf-8编码进行转换
     * @param url
     * @param strings 请求的键和值，必须为0或2的倍数
     * @param defaultCharset 转换回应时使用的编码
     * @return
     * @throws HttpException
     */
    public static String sentPost(String url, String[] strings, String defaultCharset) throws HttpException {

        //长度应该为0或2的倍数
        Assert.notNull(strings, "strings不能为空！");
        Assert.isTrue(strings.length % 2 == 0, "strings长度错误！");

        List<NameValuePair> params = new ArrayList<>(strings.length / 2);
        for (int i = 0; i < strings.length; i++, i++) {
            params.add(new BasicNameValuePair(strings[i], strings[i + 1]));
        }

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpPost httpPost = new HttpPost();
        HttpClient httpClient = new DefaultHttpClient();

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw new HttpException("发送失败!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        try {
            //获取回应
            if (httpResponse != null) {
                return EntityUtils.toString(httpResponse.getEntity(), defaultCharset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw new HttpException("格式转换失败！");
            } catch (HttpException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

}
