package com.anhdt.doranewsvermain.model.newssourceresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeneralDataNewsSource {
    @SerializedName("data")
    @Expose
    private List<NewsSource> data = null;

    public List<NewsSource> getData() {
        return data;
    }

    public void setData(List<NewsSource> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GeneralDataNewsSource{" +
                "data=" + data +
                '}';
    }
}
