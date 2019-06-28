package com.anhdt.doranewsvermain.model.notificationresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationResult extends RealmObject {
    @SerializedName("title")
    @Expose
    private String titleHot;

    @SerializedName("body")
    @Expose
    private String titleContent;

    @SerializedName("url_image")
    @Expose
    private String urlImage;

    @SerializedName("event_id")
    @Expose
    private String idEvent;

    @SerializedName("long_event_id")
    @Expose
    private String idStory;

    @SerializedName("time_create")
    @Expose
    private String timeCreate;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String idNotification;

    public NotificationResult(String titleHot, String titleContent,
                              String urlImage, String idEvent,
                              String idStory, String idNotification) {
        this.titleHot = titleHot;
        this.titleContent = titleContent;
        this.urlImage = urlImage;
        this.idEvent = idEvent;
        this.idStory = idStory;
        this.idNotification = idNotification;
    }

    public NotificationResult() {

    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getIdStory() {
        return idStory;
    }

    public void setIdStory(String idStory) {
        this.idStory = idStory;
    }

    public String getTitleHot() {
        return titleHot;
    }

    public void setTitleHot(String titleHot) {
        this.titleHot = titleHot;
    }

    public String getTitleContent() {
        return titleContent;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    @Override
    public String toString() {
        return "NotificationResult{" +
                "titleHot='" + titleHot + '\'' +
                ", titleContent='" + titleContent + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", idEvent='" + idEvent + '\'' +
                ", idStory='" + idStory + '\'' +
                ", timeCreate='" + timeCreate + '\'' +
                ", idNotification='" + idNotification + '\'' +
                '}';
    }
}
