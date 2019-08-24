package com.anhdt.doranewsvermain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class OSGroup extends RealmObject {
    @SerializedName("os_code")
    @Expose
    private int osCode;
    @SerializedName("os_ver")
    @Expose
    private String osVer;
    @SerializedName("user_agent")
    @Expose
    private String userAgent;

    public OSGroup(int osCode, String osVer, String userAgent) {
        this.osCode = osCode;
        this.osVer = osVer;
        this.userAgent = userAgent;
    }

    public OSGroup() {
    }

    public int getOsCode() {
        return osCode;
    }

    public void setOsCode(int osCode) {
        this.osCode = osCode;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "OSGroup{" +
                "osCode=" + osCode +
                ", osVer=" + osVer +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
