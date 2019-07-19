package com.anhdt.doranewsvermain.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoTool {
    public static String convertTimeDuration(int timeInteger) {
        if (timeInteger >= 3600) {
            //Quy có cả giờ
            int timeHours = timeInteger/3600;
            int timeMinutes = (timeInteger - timeHours * 3600) / 60;
            int timeSeconds = (timeInteger - timeHours * 3600) - timeMinutes * 60;
            String timeStringHours = String.valueOf(timeHours);
            String timeStringMinutes = String.valueOf(timeMinutes);
            String timeStringSeconds = String.valueOf(timeSeconds);
            if (timeHours <= 9) {
                timeStringHours = "0" + timeStringHours;
            }
            if (timeMinutes <= 9) {
                timeStringMinutes = "0" + timeStringMinutes;
            }
            if (timeSeconds <= 9) {
                timeStringSeconds = "0" + timeStringSeconds;
            }
            return timeStringHours + ":" + timeStringMinutes + ":" + timeStringSeconds;
        } else {
            //Chỉ có phút
            int timeMinutes = timeInteger/60;
            int timeSeconds = timeInteger - timeMinutes * 60;
            String timeStringMinutes = String.valueOf(timeMinutes);
            String timeStringSeconds = String.valueOf(timeSeconds);
            if (timeMinutes <= 9) {
                timeStringMinutes = "0" + timeStringMinutes;
            }
            if (timeSeconds <= 9) {
                timeStringSeconds = "0" + timeStringSeconds;
            }
            return timeStringMinutes + ":" + timeStringSeconds;
        }
    }

    public static String convertTimeLongToDateVietnamese(long timeLongSeconds) {
        @SuppressLint("SimpleDateFormat") DateFormat simple = new SimpleDateFormat("dd/MM, yyyy");
        Date result = new Date(timeLongSeconds * 1000);
        String x = simple.format(result);
        String[] y = x.split("/");
        return y[0] + " thg " + y[1];
    }

    public static String convertTimeLongToDateUS(long timeLongSeconds) {
        @SuppressLint("SimpleDateFormat") DateFormat simple = new SimpleDateFormat("dd/MM/yyyy");
        Date result = new Date(timeLongSeconds * 1000);
        return simple.format(result);
    }
}
