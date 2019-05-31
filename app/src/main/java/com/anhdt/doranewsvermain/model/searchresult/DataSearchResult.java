package com.anhdt.doranewsvermain.model.searchresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataSearchResult {
    //Trong Data chứa list event
    @SerializedName("events")
    @Expose
    private List<EventSearchResult> eventSearchResults = null;

    public List<EventSearchResult> getEventSearchResults() {
        return eventSearchResults;
    }

    public void setEventSearchResults(List<EventSearchResult> eventSearchResults) {
        this.eventSearchResults = eventSearchResults;
    }

    @Override
    public String toString() {
        return "DataSearchResult{" +
                "eventSearchResults=" + eventSearchResults +
                '}';
    }
}
