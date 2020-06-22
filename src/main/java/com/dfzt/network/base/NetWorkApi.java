package com.dfzt.network.base;

import com.dfzt.network.interceptor.CommonResponseInterceptor;
import com.dfzt.network.listener.INetworkInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetWorkApi {

    //定义一个map 用来换成Retrofit
    private Map<String, Retrofit> mCacheRetrofit = new HashMap<>();
    //定义一个BaseUrl
    private String mBaseUrl;
    //定义一个OkhttpClient
    private OkHttpClient mOkhttpClient;
    private INetworkInfo mNetworkInfo;
    protected NetWorkApi(String mBaseUrl){
        this.mBaseUrl = mBaseUrl;
    }

    public void init(INetworkInfo networkInfo){
        this.mNetworkInfo = networkInfo;
    }

    protected Retrofit getRetrofit(Class cls){
        //判断这个网络请求是否保存下来了
        if (mCacheRetrofit.get(mBaseUrl + cls.getName()) != null){
            return mCacheRetrofit.get(mBaseUrl + cls.getName());
        }
        Retrofit retrofit = new Retrofit.Builder()
                .client(getOkhttpClient())//设置OKhttpclient
                .baseUrl(mBaseUrl)//设置baseUrl
                //设置GSON解析
                .addConverterFactory(GsonConverterFactory.create())
                //设置返回的适配器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mCacheRetrofit.put(mBaseUrl + cls.getName(),retrofit);
        return retrofit;
    }


    protected OkHttpClient getOkhttpClient(){
        if (mOkhttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .writeTimeout(55, TimeUnit.SECONDS);
            if (mNetworkInfo != null && mNetworkInfo.isDeBug()){
                //设置拦截器
                builder.addInterceptor(getInterceptor());
                builder.addInterceptor(new CommonResponseInterceptor());
            }
            mOkhttpClient = builder.build();
        }
        return mOkhttpClient;
    }

    //线程切换
    public static <T> ObservableTransformer<T,T> applySchedulers(final Observer<T> observer){
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable observable = upstream.subscribeOn(Schedulers.io())//给上面的代码分配线程
                        .observeOn(AndroidSchedulers.mainThread());//给下面的代码分配线程
                observable.subscribe(observer);
                return observable;
            }
        };
    }

    //设置域名的拦截器
    protected abstract Interceptor getInterceptor();

    //设置错误的方式
    protected abstract <T> Function<T,T> getAppErrorHandler();
}
