package com.anhdt.doranewsvermain.util;

import android.annotation.SuppressLint;

import com.anhdt.doranewsvermain.BuildConfig;

public class VoiceTool {
//    public static String covertUrlFetchMusicGenre(String genre, int limit, int offset) {
//        return String.format("%s%s%s&%s=%s&%s=%d&%s=%d", Constant.BASE_URL,
//                Constant.PARA_MUSIC_GENRE, genre, Constant.CLIENT_ID,
//                BuildConfig.API_KEY, Constant.LIMIT, limit, Constant.PARA_OFFSET, offset);
//    }

    @SuppressLint("DefaultLocale")
    public static String parseMilliSecondsToTimer(long milliseconds) {
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        return hours > 0 ? String.format("%02d:%02d:%02d",
                hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }
    public static String getTextTimeSeekBar(long timeCurrent, long totalTime) {
        return String.format("%s/%s",
                parseMilliSecondsToTimer(timeCurrent), parseMilliSecondsToTimer(totalTime));
    }
    public static String getTextForControl(String artist, String title) {
        return String.format("%s - %s", artist, title);
    }
}
