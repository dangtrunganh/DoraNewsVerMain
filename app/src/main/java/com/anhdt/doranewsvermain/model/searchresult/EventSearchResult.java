package com.anhdt.doranewsvermain.model.searchresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventSearchResult {
    @SerializedName("score")
    @Expose
    private Double score;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "EventSearchResult{" +
                "score=" + score +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
