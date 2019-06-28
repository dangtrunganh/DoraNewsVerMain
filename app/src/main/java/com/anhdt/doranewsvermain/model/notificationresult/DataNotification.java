package com.anhdt.doranewsvermain.model.notificationresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataNotification {
    @SerializedName("data")
    @Expose
    private ArrayList<NotificationResult> arrayNotifications;

    public ArrayList<NotificationResult> getArrayNotifications() {
        return arrayNotifications;
    }

    public void setArrayNotifications(ArrayList<NotificationResult> arrayNotifications) {
        this.arrayNotifications = arrayNotifications;
    }

    @Override
    public String toString() {
        return "DataNotification{" +
                "arrayNotifications=" + arrayNotifications +
                '}';
    }
}
