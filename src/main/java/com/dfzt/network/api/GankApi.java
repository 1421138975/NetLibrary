package com.dfzt.network.api;

import com.dfzt.network.javabean.PhotoBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GankApi {
    @GET("data/%E7%A6%8F%E5%88%A9/{pageSize}/{page}")
    Observable<PhotoBean> getPhoto(@Path("pageSize") int pageSize,@Path("page") int page);
}
