
package com.anhdt.doranewsvermain.model.newsresult;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stories {

    @SerializedName("story_id")
    @Expose
    private String storyId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("num_events")
    @Expose
    private Integer numberOfEvents;


    @SerializedName("events")
    @Expose
    private List<Event> events = null; //Event nằm trong stories

    @SerializedName("follow")
    @Expose
    private Integer follow;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("category")
    @Expose
    private Category category;

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Integer getFollow() {
        return follow;
    }

    public void setFollow(Integer follow) {
        this.follow = follow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(Integer numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Stories{" +
                "storyId='" + storyId + '\'' +
                ", title='" + title + '\'' +
                ", numberOfEvents=" + numberOfEvents +
                ", events=" + events +
                ", follow=" + follow +
                ", image='" + image + '\'' +
                ", category=" + category +
                '}';
    }
}
