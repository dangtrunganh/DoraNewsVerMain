
package com.anhdt.doranewsvermain.model.newsresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {

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
    private Category category;

    @SerializedName("long_event_id")
    @Expose
    private String longEventId;

    @SerializedName("articles_list")
    @Expose
    private List<Article> listArticles;

    @SerializedName("follow")
    @Expose
    private Integer follow; //1 or 0

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Article> getListArticles() {
        return listArticles;
    }

    public void setListArticles(List<Article> listArticles) {
        this.listArticles = listArticles;
    }

    public String getLongEventId() {
        return longEventId;
    }

    public void setLongEventId(String longEventId) {
        this.longEventId = longEventId;
    }

    public Integer getFollow() {
        return follow;
    }

    public void setFollow(Integer follow) {
        this.follow = follow;
    }

    @Override
    public String toString() {
        return "Event{" +
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
                ", listArticles=" + listArticles +
                ", follow=" + follow +
                '}';
    }
}
