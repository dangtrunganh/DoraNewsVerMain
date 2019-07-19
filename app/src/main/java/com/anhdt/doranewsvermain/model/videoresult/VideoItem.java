package com.anhdt.doranewsvermain.model.videoresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoItem {
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("video_description")
    @Expose
    private String videoDescription;
    @SerializedName("video_duration")
    @Expose
    private Integer videoDuration;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("video_link")
    @Expose
    private String videoLink;
    @SerializedName("video_title")
    @Expose
    private String videoTitle;
    @SerializedName("video_type")
    @Expose
    private Integer videoType;
    @SerializedName("created_at")
    @Expose
    private Integer createdAt;
    @SerializedName("time_zone")
    @Expose
    private String timeZone;
    @SerializedName("channel_image")
    @Expose
    private String channelImage;

    private boolean isFooter;

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public VideoItem() {
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public Integer getVideoType() {
        return videoType;
    }

    public void setVideoType(Integer videoType) {
        this.videoType = videoType;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }
}
