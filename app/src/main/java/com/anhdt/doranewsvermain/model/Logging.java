package com.anhdt.doranewsvermain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Logging extends RealmObject {
    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("article_id")
    @Expose
    private int articleId;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("os_group")
    @Expose
    private OSGroup osGroup;
    //    private int osCode;
//    private String osVer;
//    private String userAgent;
    @SerializedName("event_app")
    @Expose
    private int eventApp;
    @SerializedName("event_id")
    @Expose
    private String eventId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @PrimaryKey
    @SerializedName("time_create")
    @Expose
    private long timeCreate;

    public Logging() {
    }

    public Logging(String sessionId, int articleId, String ip, OSGroup osGroup, int eventApp, String eventId, String categoryId, long timeCreate) {
        this.sessionId = sessionId;
        this.articleId = articleId;
        this.ip = ip;
        this.osGroup = osGroup;
        this.eventApp = eventApp;
        this.eventId = eventId;
        this.categoryId = categoryId;
        this.timeCreate = timeCreate;
    }

    //    public Logging(String sessionId, int articleId, String ip, int osCode, String osVer, String userAgent, int eventApp, String eventId) {
//        this.sessionId = sessionId;
//        this.articleId = articleId;
//        this.ip = ip;
//        this.osCode = osCode;
//        this.osVer = osVer;
//        this.userAgent = userAgent;
//        this.eventApp = eventApp;
//        this.eventId = eventId;
//    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

//    public int getOsCode() {
//        return osCode;
//    }
//
//    public void setOsCode(int osCode) {
//        this.osCode = osCode;
//    }
//
//    public String getOsVer() {
//        return osVer;
//    }
//
//    public void setOsVer(String osVer) {
//        this.osVer = osVer;
//    }
//
//    public String getUserAgent() {
//        return userAgent;
//    }
//
//    public void setUserAgent(String userAgent) {
//        this.userAgent = userAgent;
//    }

    public int getEventApp() {
        return eventApp;
    }

    public void setEventApp(int eventApp) {
        this.eventApp = eventApp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public OSGroup getOsGroup() {
        return osGroup;
    }

    public void setOsGroup(OSGroup osGroup) {
        this.osGroup = osGroup;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Override
    public String toString() {
        return "Logging{" +
                "sessionId='" + sessionId + '\'' +
                ", articleId=" + articleId +
                ", ip='" + ip + '\'' +
                ", osGroup=" + osGroup +
                ", eventApp=" + eventApp +
                ", eventId='" + eventId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", timeCreate=" + timeCreate +
                '}';
    }
}
