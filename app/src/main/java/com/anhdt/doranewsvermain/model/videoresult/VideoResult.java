package com.anhdt.doranewsvermain.model.videoresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VideoResult {
    @SerializedName("data")
    @Expose
    private ArrayList<VideoItem> data = null;

    public ArrayList<VideoItem> getData() {
        return data;
    }

    public void setData(ArrayList<VideoItem> data) {
        this.data = data;
    }
}
