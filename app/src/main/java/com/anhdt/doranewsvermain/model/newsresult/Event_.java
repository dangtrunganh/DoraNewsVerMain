
package com.anhdt.doranewsvermain.model.newsresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event_ {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("num_articles")
    @Expose
    private Integer numArticles;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("readableTime")
    @Expose
    private String readableTime;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("category")
    @Expose
    private Category_ category;
    @SerializedName("long_event_id")
    @Expose
    private String longEventId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumArticles() {
        return numArticles;
    }

    public void setNumArticles(Integer numArticles) {
        this.numArticles = numArticles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReadableTime() {
        return readableTime;
    }

    public void setReadableTime(String readableTime) {
        this.readableTime = readableTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Category_ getCategory() {
        return category;
    }

    public void setCategory(Category_ category) {
        this.category = category;
    }

    public String getLongEventId() {
        return longEventId;
    }

    public void setLongEventId(String longEventId) {
        this.longEventId = longEventId;
    }

    @Override
    public String toString() {
        return "Event_{" +
                "id='" + id + '\'' +
                ", numArticles=" + numArticles +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", time='" + time + '\'' +
                ", readableTime='" + readableTime + '\'' +
                ", date='" + date + '\'' +
                ", category=" + category +
                ", longEventId='" + longEventId + '\'' +
                '}';
    }
}
