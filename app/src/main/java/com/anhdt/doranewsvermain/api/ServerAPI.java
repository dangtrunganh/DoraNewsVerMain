package com.anhdt.doranewsvermain.api;

import com.anhdt.doranewsvermain.model.Logging;
import com.anhdt.doranewsvermain.model.categoryresult.CategoryResult;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.newssourceresult.GeneralDataNewsSource;
import com.anhdt.doranewsvermain.model.notificationresult.DataNotification;
import com.anhdt.doranewsvermain.model.userresult.UserResult;
import com.anhdt.doranewsvermain.model.videoresult.VideoResult;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    @GET("categories/pick")
    Call<CategoryResult> sendCategoriesPicked(@Query("uid") String uid,
                                              @Query("picklist") String pickList);

    @GET("my-notify")
    Call<DataNotification> getListNotifications(@Query("uid") String uid);

    //=====POST=====
    @POST("active")
    Call<UserResult> login(@Body RequestBody body);

//    @POST("search")
//    Call<DatumSearchResult> searchEvent(@Body RequestBody body);
    //=====POST=====

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

    @GET("video")
    Call<VideoResult> getListVideos(@Query("reload") String reload,
                                    @Query("deviceid") String deviceId,
                                    @Query("uid") String uId); //1 or 0

    @GET("follow/stories")
    Call<News> getListStoriesFollowed(@Query("uid") String uId); //Return list stories followed

    @GET("sources")
    Call<GeneralDataNewsSource> getListNewsSource(); //list news source

    //Send pick category and news source information to server
    @GET("sources/pick")
    Call<GeneralDataNewsSource> sendNewsSourcePicked(@Query("uid") String uid,
                                                     @Query("picklist") String pickList); //list news source

    @GET("sources/pick")
    Call<GeneralDataNewsSource> getNewsSourcePicked(@Query("uid") String uid); //get list news source picked


    //=====POST====Search=====
    @POST("search/events")
    Call<News> searchEvents(@Body RequestBody body);

    @POST("search/articles")
    Call<News> searchArticles(@Body RequestBody body);


    //=====GET====API======youtube=========
//    @GET("search")
//    Call<VideoResult> getListVideos(@Query("part") String part,
//                                    @Query("order") String order,
//                                    @Query("channelId") String channelId,
//                                    @Query("maxResults") String maxResults,
//                                    @Query("key") String key,
//                                    @Query("pageToken") String pageToken,
//                                    @Query("publishedAfter") String publishedAfter);

    @GET("trace")
    Call<Void> getLoggingClickEvent(@Query("session_id") String sessionId,
            /*@Query("category_id") String categoryId,*/
                                    @Query("ip") String ip,
                                    @Query("os_code") int osCode,
                                    @Query("os_ver") String osVer,
                                    @Query("user_agent") String userAgent,
                                    @Query("event_id") String eventId); //Return list stories followed

//    @GET("trace")
//    Call<Void> getLoggingClickArticle(@Query("session_id") String sessionId,
//                                      @Query("article_id") int articleId,
//                                      @Query("ip") String ip,
//                                      @Query("os_code") int osCode,
//                                      @Query("os_ver") String osVer,
//                                      @Query("user_agent") String userAgent,
//                                      @Query("event_app") int eventApp); //Return list stories followed

    //    @GET("trace")
//    Call<Void> getLoggingStartApp(@Query("session_id") String sessionId,
//                          @Query("ip") String ip,
//                          @Query("os_code") int osCode,
//                          @Query("os_ver") String osVer,
//                          @Query("event_id") String eventId); //Return list stories followed
    @GET("trace")
    Call<Void> getLoggingStartApp(@Query("session_id") String sessionId,
                                  @Query("ip") String ip,
                                  @Query("os_code") int osCode,
                                  @Query("os_ver") String osVer,
                                  @Query("user_agent") String userAgent,
                                  @Query("event_app") int eventApp); //Return list stories followed

    //=====GET===LOGGING
//    @Headers({
//            "Content-Type: application/json"
//    })
    @POST("trace")
    Call<ResponseBody> postRawJSON(@Body final List<Logging> jsonLogging);
}
