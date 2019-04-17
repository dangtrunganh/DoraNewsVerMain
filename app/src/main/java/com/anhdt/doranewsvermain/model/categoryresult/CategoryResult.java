package com.anhdt.doranewsvermain.model.categoryresult;

import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryResult {
    @SerializedName("data")
    @Expose
    private List<Category> data = null;

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CategoryResult{" +
                "data=" + data +
                '}';
    }
}
