
package com.anhdt.doranewsvermain.model.newsresult;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Media {

    @SerializedName("body")
    @Expose
    private List<Body> body = null;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("voice")
    @Expose
    private String voice;

    public List<Body> getBody() {
        return body;
    }

    public void setBody(List<Body> body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    @Override
    public String toString() {
        return "Media{" +
                "body=" + body +
                ", type='" + type + '\'' +
                ", voice='" + voice + '\'' +
                '}';
    }
}
