package com.example.myapplication.Utils;

import android.util.Log;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

/***
 * @author 燕富成
 * @version 0.0.1
 */
public class HttpUtils {

    private static OkHttpClient okHttpClient;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            ResourceBundle rb = ResourceBundle.getBundle("HttpInterceptor");
            for (String interceptor : rb.keySet()) {
                //遍历key中包含interceptor的数据
                if(interceptor.toLowerCase().contains("interceptor")) {
                    @SuppressWarnings("rawtypes")
                    Class clazz;
                    try {
                        //通过反射动态设置到拦截器
                        clazz = Class.forName(rb.getString(interceptor));
                        builder.addInterceptor((Interceptor) clazz.newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e) {
            Log.d("HttpUtil", "未添加拦截器");
        }
        okHttpClient = builder.build();
    }

    /***
     * <p>获取OkHttpClient</p>
     * @return okHttpClient
     */
    public static OkHttpClient okHttpClient() {
        return okHttpClient;
    }
    /***
     * <p>执行一个同步Get请求</p>
     * @param url
     * @param headers
     * @return Response
     * @throws IOException
     */
    public static Response doblockGetRequest(String url, Map<String, String> headers) throws IOException {
        //封装Request
        Request request = packageRequest(url,headers,RequestType.GET,null);
        //执行异步请求
        return doExecute(request);
    }

    /***
     * <p>执行一个同步Post请求</p>
     * @param url
     * @param headers
     * @param rbt
     * @param responseBody
     * @return Response
     * @throws IOException
     */
    public static Response doblockPostRequest(String url, Map<String ,String> headers, RequestBodyType rbt, Object responseBody) throws IOException {
        //封装请求体
        RequestBody requestBody = packageRequestBody(rbt, responseBody);
        //封装request对象
        Request request = packageRequest(url,headers,RequestType.POST,requestBody);
        //执行同步请求
        Response response = doExecute(request);
        return response;
    }

    /***
     * <p>执行一个异步Get请求</p>
     * @param url
     * @param headers
     * @param callback
     */
    public static void doNonblockGetRequest(String url, Map<String ,String> headers, Callback callback) {
        //封装Request
        Request request = packageRequest(url,headers,RequestType.GET,null);
        //执行异步请求
        doEnqueue(request,callback);
    }

    /***
     * <p>执行一个异步Post请求</p>
     * @param url
     * @param headers
     * @param rbt
     * @param responseBody
     * @param callback
     */
    public static void doNonblockPostRequest(String url, Map<String ,String> headers, RequestBodyType rbt, Object responseBody, Callback callback) {
        //封装请求体
        RequestBody requestBody = packageRequestBody(rbt, responseBody);
        //封装request对象
        Request request = packageRequest(url,headers,RequestType.POST,requestBody);
        //执行异步请求
        doEnqueue(request,callback);
    }

    /***
     * <p>封装Request对象</p>
     * @param url
     * @param headers
     * @param responseBody
     * @return request
     */
    public static Request packageRequest(String url, Map<String ,String> headers, RequestType rt, RequestBody responseBody) {
        if(url == null) {
            throw new RuntimeException("url is null");
        }
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        //添加请求头
        if(headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.header(header.getKey(),header.getValue());
            }
        }
        //添加请求体
        if(rt.equals(RequestType.POST)) {
            builder.post(responseBody);
        }
        return builder.build();
    }

    /***
     * <p>封装请求体</p>
     * @param type
     * @param responseBody
     * @return responseBody
     */
    public static RequestBody packageRequestBody(RequestBodyType type, Object responseBody) {
        switch(type) {
            case FORM : {
                @SuppressWarnings("unchecked")
                Map<String, String> reBody = (Map<String, String>)responseBody;
                FormBody.Builder fb = new FormBody.Builder();
                for (Map.Entry<String, String> body : reBody.entrySet()) {
                    fb.add(body.getKey(),body.getValue());
                }
                return fb.build();
            }
            case JSON : {
                MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
                return RequestBody.create(mediaType, (String) responseBody);
            }
            case STREAM : {
                return (RequestBody) responseBody;
            }
            case FILE : {
                MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
                return RequestBody.create(mediaType, (File)responseBody);
            }
            default : {
                return null;
            }
        }
    }

    /***
     * <p>执行同步请求</p>
     * @param request
     * @return response
     * @throws IOException
     */
    public static Response doExecute(Request request) throws IOException {
        Call call = okHttpClient.newCall(request);
        return call.execute();
    }

    /***
     * <p>执行异步请求</p>
     * @param request
     * @param callback
     */
    public static void doEnqueue(Request request, Callback callback) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public static enum RequestBodyType {
        FORM,//传入Map<String, String>
        STREAM,//传入封装好的RequestBody
        JSON,//传入String字符串,请求格式是JSON
        FILE//传入File文件
    }

    private static enum RequestType {
        POST,GET
    }

}
