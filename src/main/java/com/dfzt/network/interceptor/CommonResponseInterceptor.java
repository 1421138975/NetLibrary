package com.dfzt.network.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//自定义返回拦截器
public class CommonResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        long requestTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        Log.e("PPS",response.code() + "  RequestTime " + (System.currentTimeMillis() - requestTime));
        return response;
    }
}
