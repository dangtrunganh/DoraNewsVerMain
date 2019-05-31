package com.anhdt.doranewsvermain.model.searchresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatumSearchResult {
    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("data")
    @Expose
    private DataSearchResult dataSearchResult;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private Integer status;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataSearchResult getDataSearchResult() {
        return dataSearchResult;
    }

    public void setDataSearchResult(DataSearchResult dataSearchResult) {
        this.dataSearchResult = dataSearchResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DatumSearchResult{" +
                "code=" + code +
                ", dataSearchResult=" + dataSearchResult +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
