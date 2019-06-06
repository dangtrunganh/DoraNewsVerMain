package com.anhdt.doranewsvermain.util;

import android.annotation.SuppressLint;

import com.anhdt.doranewsvermain.BuildConfig;
import com.anhdt.doranewsvermain.model.newsresult.Article;

import java.util.ArrayList;

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

    public static String getTextTimeString(int time) {
        return String.format("%s",
                parseMilliSecondsToTimer(time));
    }
    public static String getTextForControl(String artist, String title) {
        return String.format("%s - %s", artist, title);
    }

    public static boolean checkIfTwoListEqual(ArrayList<Article> articles1, ArrayList<Article> articles2) {
        String idListArticle1 = "";
        String idListArticle2 = "";
        for (Article articleTemp1 : articles1) {
            if (articleTemp1 == null) {
                continue;
            }
            idListArticle1 += articleTemp1.getId();
        }

        for (Article articleTemp2 : articles2) {
            if (articleTemp2 == null) {
                continue;
            }
            idListArticle2 += articleTemp2.getId();
        }
        return idListArticle1.equals(idListArticle2);
    }
}
