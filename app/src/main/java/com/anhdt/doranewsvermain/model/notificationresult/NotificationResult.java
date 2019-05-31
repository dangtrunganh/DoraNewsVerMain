package com.anhdt.doranewsvermain.model.notificationresult;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationResult extends RealmObject {
    private String titleHot;
    private String titleContent;
    private String urlImage;

    @PrimaryKey
    private String idEvent;

    private String idStory;

    public NotificationResult(String titleHot, String titleContent, String urlImage, String idEvent, String idStory) {
        this.titleHot = titleHot;
        this.titleContent = titleContent;
        this.urlImage = urlImage;
        this.idEvent = idEvent;
        this.idStory = idStory;
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

    @Override
    public String toString() {
        return "NotificationResult{" +
                "titleHot='" + titleHot + '\'' +
                ", titleContent='" + titleContent + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", idEvent='" + idEvent + '\'' +
                ", idStory='" + idStory + '\'' +
                '}';
    }
}
