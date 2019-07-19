//
//package com.anhdt.doranewsvermain.model.videotestresult;
//
//import java.util.ArrayList;
//
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
//public class VideoResult {
//    @SerializedName("kind")
//    @Expose
//    private String kind;
//
//    @SerializedName("etag")
//    @Expose
//    private String etag;
//
//    @SerializedName("nextPageToken")
//    @Expose
//    private String nextPageToken;
//
//    @SerializedName("prevPageToken")
//    @Expose
//    private String prevPageToken;
//
//    @SerializedName("regionCode")
//    @Expose
//    private String regionCode;
//
//    @SerializedName("pageInfo")
//    @Expose
//    private PageInfo pageInfo;
//
//    @SerializedName("items")
//    @Expose
//    private ArrayList<Item> items = null;
//
//    public String getKind() {
//        return kind;
//    }
//
//    public void setKind(String kind) {
//        this.kind = kind;
//    }
//
//    public String getEtag() {
//        return etag;
//    }
//
//    public void setEtag(String etag) {
//        this.etag = etag;
//    }
//
//    public String getNextPageToken() {
//        return nextPageToken;
//    }
//
//    public void setNextPageToken(String nextPageToken) {
//        this.nextPageToken = nextPageToken;
//    }
//
//    public String getRegionCode() {
//        return regionCode;
//    }
//
//    public void setRegionCode(String regionCode) {
//        this.regionCode = regionCode;
//    }
//
//    public PageInfo getPageInfo() {
//        return pageInfo;
//    }
//
//    public void setPageInfo(PageInfo pageInfo) {
//        this.pageInfo = pageInfo;
//    }
//
//    public ArrayList<Item> getItems() {
//        return items;
//    }
//
//    public void setItems(ArrayList<Item> items) {
//        this.items = items;
//    }
//
//    public String getPrevPageToken() {
//        return prevPageToken;
//    }
//
//    public void setPrevPageToken(String prevPageToken) {
//        this.prevPageToken = prevPageToken;
//    }
//
//    @Override
//    public String toString() {
//        return "VideoResult{" +
//                "kind='" + kind + '\'' +
//                ", etag='" + etag + '\'' +
//                ", nextPageToken='" + nextPageToken + '\'' +
//                ", prevPageToken='" + prevPageToken + '\'' +
//                ", regionCode='" + regionCode + '\'' +
//                ", pageInfo=" + pageInfo +
//                ", items=" + items +
//                '}';
//    }
//}
