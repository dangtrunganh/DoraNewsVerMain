package com.anhdt.doranewsvermain.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import com.anhdt.doranewsvermain.model.DatumStory;
import com.anhdt.doranewsvermain.model.ItemDetailStory;
import com.anhdt.doranewsvermain.model.newsresult.Event;

import java.util.ArrayList;

public class GeneralTool {
    public static boolean checkIfChildOutIParent(float yChildTop, float yChildBottom, float yParentTop, float yParentBottom) {
        if (yChildTop < yParentTop) {
            return true;
        } else if (yChildBottom > yParentBottom) {
            return true;
        }
        return false;
    }

    public static int convertDpsToPixels(Context mContext, float valueDps) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (valueDps * scale + 0.5f);
    }

    public static int convertPixelsToDps(Context mContext, float valuePixels) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (int) ((valuePixels - 0.5f) / scale);
    }

    public static int getWidthScreen(Context mContext) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getHeightScreen(Context mContext) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static String getDeviceId(Context mContext) {
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("k-android_id", android_id);
        return android_id;
    }

    public static ArrayList<ItemDetailStory> convertToListDatumStory(ArrayList<Event> listEventToConvert) {
        ArrayList<ItemDetailStory> itemDetailStories = new ArrayList<>();
        ArrayList<Event> array;

        for (int i = 0; i < listEventToConvert.size(); i++) {
            //Kiểm tra với mỗi Event, phân vào 1 trong 4 type, tạo phần tử với type đó
            Event event = listEventToConvert.get(i);

        }

        return itemDetailStories;
    }
}
