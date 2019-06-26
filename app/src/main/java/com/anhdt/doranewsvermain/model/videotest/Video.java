package com.anhdt.doranewsvermain.model.videotest;

public class Video {
    private String idVideo;
    private String urlVideo;
    private String titleVideo;
    private String numberOfViews;
    private String timeCreated;
    private String imageThumbnail;
    private String chanelName;
    private String imageLogoChanel;
    private String timeDuration;
    private int numberOfLikes;
    private int numberOfDislikes;

    public Video(String idVideo, String urlVideo, String titleVideo, String numberOfViews,
                 String timeCreated, String imageThumbnail, String chanelName,
                 String imageLogoChanel, String timeDuration, int numberOfLikes,
                 int numberOfDislikes) {
        this.idVideo = idVideo;
        this.urlVideo = urlVideo;
        this.titleVideo = titleVideo;
        this.numberOfViews = numberOfViews;
        this.timeCreated = timeCreated;
        this.imageThumbnail = imageThumbnail;
        this.chanelName = chanelName;
        this.imageLogoChanel = imageLogoChanel;
        this.timeDuration = timeDuration;
        this.numberOfLikes = numberOfLikes;
        this.numberOfDislikes = numberOfDislikes;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(String idVideo) {
        this.idVideo = idVideo;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getTitleVideo() {
        return titleVideo;
    }

    public void setTitleVideo(String titleVideo) {
        this.titleVideo = titleVideo;
    }

    public String getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(String numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public String getImageLogoChanel() {
        return imageLogoChanel;
    }

    public void setImageLogoChanel(String imageLogoChanel) {
        this.imageLogoChanel = imageLogoChanel;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfDislikes() {
        return numberOfDislikes;
    }

    public void setNumberOfDislikes(int numberOfDislikes) {
        this.numberOfDislikes = numberOfDislikes;
    }

    public String getChanelName() {
        return chanelName;
    }

    public void setChanelName(String chanelName) {
        this.chanelName = chanelName;
    }

    @Override
    public String toString() {
        return "Video{" +
                "idVideo='" + idVideo + '\'' +
                ", urlVideo='" + urlVideo + '\'' +
                ", titleVideo='" + titleVideo + '\'' +
                ", numberOfViews='" + numberOfViews + '\'' +
                ", timeCreated='" + timeCreated + '\'' +
                ", imageThumbnail='" + imageThumbnail + '\'' +
                ", chanelName='" + chanelName + '\'' +
                ", imageLogoChanel='" + imageLogoChanel + '\'' +
                ", timeDuration='" + timeDuration + '\'' +
                ", numberOfLikes=" + numberOfLikes +
                ", numberOfDislikes=" + numberOfDislikes +
                '}';
    }
}
