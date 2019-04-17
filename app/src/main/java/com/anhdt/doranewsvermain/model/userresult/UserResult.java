package com.anhdt.doranewsvermain.model.userresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResult {
    @SerializedName("uuid")
    @Expose
    private String uId;

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }
}
