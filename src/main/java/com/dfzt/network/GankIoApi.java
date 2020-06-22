package com.dfzt.network;

import com.dfzt.network.base.NetWorkApi;
import com.dfzt.network.interceptor.CommonRequestInterceptor;
import com.dfzt.network.listener.INetworkInfo;

import io.reactivex.functions.Function;
import okhttp3.Interceptor;

public class GankIoApi extends NetWorkApi {

    private static volatile GankIoApi mWanAndroidApi;

    private GankIoApi() {
        super("http://gank.io/api/");//这里放BaseUrl
    }

    public static GankIoApi getInstance(){
        if (mWanAndroidApi == null){
            synchronized (GankIoApi.class){
                if (mWanAndroidApi == null){
                    mWanAndroidApi  = new GankIoApi();
                    mWanAndroidApi.init(new INetworkInfo() {
                        @Override
                        public boolean isDeBug() {
                            return true;
                        }
                    });
                }
            }
        }
        return mWanAndroidApi;
    }


    public <T> T getServiceApi(Class<T> t){
        return getInstance().getRetrofit(t).create(t);
    }

    @Override
    protected Interceptor getInterceptor() {
        return new CommonRequestInterceptor();
    }

    @Override
    protected <T> Function<T, T> getAppErrorHandler() {
        return null;
    }
}
