
package com.anhdt.doranewsvermain.model.newsresult;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stories {

    @SerializedName("story_id")
    @Expose
    private String storyId;
    @SerializedName("events")
    @Expose
    private List<Event> events = null; //Event nằm trong stories

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

    @Override
    public String toString() {
        return "Stories{" +
                "storyId='" + storyId + '\'' +
                ", events=" + events +
                '}';
    }
}
