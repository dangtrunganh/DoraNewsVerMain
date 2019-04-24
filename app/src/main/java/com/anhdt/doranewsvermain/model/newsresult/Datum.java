
package com.anhdt.doranewsvermain.model.newsresult;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("stories")
    @Expose
    private Stories stories;
    @SerializedName("event")
    @Expose
    private Event event;
    @SerializedName("articles")
    @Expose
    private List<Article> articles = null;

    private boolean isFooter = false;

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Stories getStories() {
        return stories;
    }

    public void setStories(Stories stories) {
        this.stories = stories;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "Datum{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", stories=" + stories +
                ", event=" + event +
                ", articles=" + articles +
                '}';
    }
}
