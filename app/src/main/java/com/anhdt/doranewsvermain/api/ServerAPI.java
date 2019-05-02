package com.anhdt.doranewsvermain.api;

import com.anhdt.doranewsvermain.model.categoryresult.CategoryResult;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.userresult.UserResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerAPI {

    //hot news
    @GET("hot")
    Call<News> getHotNews(@Query("reload") String reload,
                          @Query("deviceid") String deviceId,
                          @Query("uid") String uId);

    //list news in each category
    @GET("categories/newsfeed")
    Call<News> getNewsInEachCategory(@Query("reload") String reload,
                                     @Query("deviceid") String deviceId,
                                     @Query("catid") String catId,
                                     @Query("uid") String uId);

    @GET("categories")
    Call<CategoryResult> getResultCategory();

    @POST("active")
    Call<UserResult> login(@Body RequestBody body);

    @GET("event/detail")
    Call<Event> getDetailEvent(@Query("eventid") String eventId,
                               @Query("uid") String uId);

    @GET("story/detail")
    Call<Stories> getDetailStory(@Query("storyid") String storyId,
                                 @Query("uid") String uId);

    @GET("follow")
    Call<Stories> followEvent(@Query("uid") String uId,
                              @Query("eid") String eventId,
                              @Query("type") String typeFollow); //1 or 0

    @GET("follow")
    Call<Stories> followStory(@Query("uid") String uId,
                              @Query("storyid") String storyId,
                              @Query("type") String typeFollow); //1 or 0

    @GET("follow/stories")
    Call<News> getListStoriesFollowed(@Query("uid") String uId); //Return list stories followed
}
