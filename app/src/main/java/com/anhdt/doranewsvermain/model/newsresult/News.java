
package com.anhdt.doranewsvermain.model.newsresult;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {
    //root key
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    @SerializedName("pages_size")
    @Expose
    private Integer pagesSize;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Integer getPagesSize() {
        return pagesSize;
    }

    public void setPagesSize(Integer pagesSize) {
        this.pagesSize = pagesSize;
    }

    @Override
    public String toString() {
        return "News{" +
                "data=" + data +
                ", pagesSize=" + pagesSize +
                '}';
    }
}
