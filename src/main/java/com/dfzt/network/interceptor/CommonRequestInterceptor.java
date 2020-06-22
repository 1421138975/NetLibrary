package com.dfzt.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//自定义请求拦截器
public class CommonRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        //这里可以设置请求头
        //builder.addHeader("","");
        return chain.proceed(builder.build());
    }
}
