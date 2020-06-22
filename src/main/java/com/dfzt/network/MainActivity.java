package com.dfzt.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.dfzt.network.api.GankApi;
import com.dfzt.network.base.NetWorkApi;
import com.dfzt.network.javabean.PhotoBean;

import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private GankApi mGankApi;
    private Disposable mDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(":").build();
        Call call = client.newCall(request);
        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });*/
        //这里就是动态代理这个接口 获取这个接口的实例对象
        mGankApi = GankIoApi.getInstance().getServiceApi(GankApi.class);
        //这个时候通过接口的这个实例对象 调用里面的方法 返回的是一个被观察者对象
        mGankApi.getPhoto(20,1)
                //这里面是进行线程切换,上面网络请求为IO线程,下面的观察者对象为主线程
                .compose(NetWorkApi.applySchedulers(new Observer<PhotoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(PhotoBean photoBean) {
                        Log.e("PPS"," 获取数据成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("PPS"," onError()  " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()){
                //这就是终止回调 防止内存泄漏
                mDisposable.dispose();
            }
        }
    }
}
