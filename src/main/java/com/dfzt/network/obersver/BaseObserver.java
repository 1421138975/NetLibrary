package com.dfzt.network.obersver;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver implements Observer {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Object o) {
        onSuccess(o);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {

    }

    //成功的回调
    public abstract void onSuccess(Object o);
    //失败的回调
    public abstract void onFailure(Throwable e);
}
