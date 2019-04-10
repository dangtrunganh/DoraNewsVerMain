package com.anhdt.doranewsvermain.api;

import com.anhdt.doranewsvermain.model.newsresult.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerAPI {

    //hot news
    @GET("hot")
    Call<News> getHotNews(@Query("reload") String reload,
                          @Query("deviceid") String deviceid);

}
