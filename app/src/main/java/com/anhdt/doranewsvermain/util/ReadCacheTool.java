package com.anhdt.doranewsvermain.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstLocalCaching;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ReadCacheTool {

    public static String getListCategoryInCache(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_LIST_CATEGORY, MODE_PRIVATE);
        return pre.getString(ConstLocalCaching.KEY_PREF_LIST_CATEGORY,
                ConstLocalCaching.DEFAULT_VALUE_PREF_LIST_CATEGORY_DEFAULT);
    }

    public static void storeCategory(Context mContext, List<Category> mArrayCategoriesChosen) {
        SharedPreferences pre = mContext.getSharedPreferences(ConstLocalCaching.FILE_NAME_PREF_LIST_CATEGORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.clear();
        editor.apply();

        //Chuyển list về dạng json để lưu xuống
        Gson gson = new Gson();
        String json = gson.toJson(mArrayCategoriesChosen);
        editor.putString(ConstLocalCaching.KEY_PREF_LIST_CATEGORY, json);

        editor.commit();
        editor.apply();
    }


    //Bất cứ khi nào cần uID, chỉ cần vào trong cache là lấy được luôn, gọi hàm này là xong
    //Chắc chỉ cần sử dụng nó trong Detail Event để follow 1 Long Event

    /**
     * Hàm này thực hiện lấy UId từ SharedPreference ra
     *
     * @param mContext Ngữ cảnh hiện tại
     * @return UId tương ứng get được, chú ý có thể "" - xâu rỗng (default)
     */
    public static String getUId(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_UID_M_TOKEN_DEVICE_ID, MODE_PRIVATE);
        return pre.getString(ConstLocalCaching.KEY_PREF_UID,
                ConstLocalCaching.DEFAULT_VALUE_PREF_UID_DEFAULT);
    }

    /**
     * Hàm này thực hiện lưu UId xuống SharedPreference
     *
     * @param mContext Ngữ cảnh hiện tại
     * @param uId      tương ứng là uId cần lưu xuống local
     */
    public static void storeUIdMTokenDeviceId(Context mContext, String uId, String mToken, String deviceId) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_UID_M_TOKEN_DEVICE_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.clear();
        editor.apply();

        //===Storage-UId-MToken-DeviceId====
        editor.putString(ConstLocalCaching.KEY_PREF_UID, uId);
        editor.putString(ConstLocalCaching.KEY_PREF_M_TOKEN, mToken);
        editor.putString(ConstLocalCaching.KEY_PREF_DEVICE_ID, deviceId);
        //Lưu xuống file
        editor.apply();

    }

    /**
     * Hàm này thực hiện lấy mToken từ SharedPreference ra
     *
     * @param mContext Ngữ cảnh hiện tại
     * @return mToken tương ứng get được, chú ý có thể "" - xâu rỗng (default)
     */
    public static String getMToken(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_UID_M_TOKEN_DEVICE_ID, MODE_PRIVATE);
        return pre.getString(ConstLocalCaching.KEY_PREF_M_TOKEN,
                ConstLocalCaching.DEFAULT_VALUE_PREF_M_TOKEN_DEFAULT);
    }

    /**
     * Hàm này thực hiện lấy DeviceId từ SharedPreference ra
     *
     * @param mContext Ngữ cảnh hiện tại
     * @return deviceId tương ứng get được, chú ý có thể "" - xâu rỗng (default)
     */
    public static String getDeviceId(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_UID_M_TOKEN_DEVICE_ID, MODE_PRIVATE);
        return pre.getString(ConstLocalCaching.KEY_PREF_DEVICE_ID,
                ConstLocalCaching.DEFAULT_VALUE_PREF_DEVICE_ID_DEFAULT);
    }


    //===========================================//
    public static void clearCacheCategory(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_LIST_CATEGORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearCacheUId(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_UID_M_TOKEN_DEVICE_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.clear();
        editor.apply();
    }

    //===General====
    public static void clearCacheGeneral(Context mContext, String fileName) {
        SharedPreferences pre = mContext.getSharedPreferences
                (fileName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.clear();
        editor.apply();
    }


    /*cache=================================cache*/
    //=====Cache Datum-News in Hot News Tab ======
    //1 ===========STORE==============
    //Mỗi lần store xuống, là xóa cmn hết cái cũ đi :v
    public static void storeHotNews(Context mContext, List<Datum> arrayListDatumHotNews) {
        //Bước 1: Chuyển list sang json String
        Gson gson = new Gson();
        String json = gson.toJson(arrayListDatumHotNews);

        //Bước 2: Lưu vào trong share preference
        SharedPreferences pre = mContext.getSharedPreferences(
                ConstLocalCaching.FILE_NAME_PREF_CACHE_GENERAL_NEWS,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();

        //Xóa list cũ đi rồi mới add vào
        editor.remove(ConstLocalCaching.KEY_PREF_CACHE_HOT_NEWS);
        editor.apply();

        editor.putString(ConstLocalCaching.KEY_PREF_CACHE_HOT_NEWS, json);
        //chấp nhận lưu xuống file
        editor.commit();
        editor.apply();
    }


    public static void storeNewsByCategory(Context mContext, String idCategory, ArrayList<Datum> arrayListNewsByCategory) {
        //Bước 1: Chuyển list sang json String
        Gson gson = new Gson();
        String json = gson.toJson(arrayListNewsByCategory);

        //Bước 2: Lưu vào trong share preference
        SharedPreferences pre = mContext.getSharedPreferences(
                ConstLocalCaching.FILE_NAME_PREF_CACHE_GENERAL_NEWS,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();

        //Xóa list cũ đi rồi mới add vào
        editor.remove(idCategory);
        editor.apply();

        editor.putString(idCategory, json);
        //chấp nhận lưu xuống file
        editor.commit();
        editor.apply();
    }


    //2 ===========GET==============
    //Trả về list get được hoặc list rỗng (nếu lỗi)
    //Get List HotNews
    public static ArrayList<Datum> getListHotNews(Context mContext) {
        //Ko có gì thì trả về list size = 0, có thì trả về list đó
        //1. get jsonString listNews
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_CACHE_GENERAL_NEWS, MODE_PRIVATE);
        String jsonHotNews = pre.getString(ConstLocalCaching.KEY_PREF_CACHE_HOT_NEWS,
                ConstLocalCaching.DEFAULT_VALUE_PREF_CACHE_HOT_NEWS);
        if (jsonHotNews == null) {
            return new ArrayList<>();
        }
        if (jsonHotNews.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_CACHE_HOT_NEWS)) {
//            Toast.makeText(mContext, "LE-Nothing in pref-hot-news-cache", Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(jsonHotNews, new TypeToken<List<Datum>>() {
            }.getType());
        }
    }

    public static ArrayList<Datum> getListNewsByCategory(Context mContext, String idCategory) {
        //Ko có gì thì trả về list size = 0, có thì trả về list đó
        //1. get jsonString listNews
        SharedPreferences pre = mContext.getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_CACHE_GENERAL_NEWS, MODE_PRIVATE);

        String jsonNewsByCategory = pre.getString(idCategory,
                ConstLocalCaching.DEFAULT_VALUE_PREF_CACHE_NEWS_BY_CATEGORY);
        if (jsonNewsByCategory == null) {
            return new ArrayList<>();
        }
        if (jsonNewsByCategory.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_CACHE_NEWS_BY_CATEGORY)) {
//            Toast.makeText(mContext, "LA-Nothing in pref-news-by-category-cache", Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(jsonNewsByCategory, new TypeToken<List<Datum>>() {
            }.getType());
        }
    }
    /*cache=================================cache*/


    //=============Convert==============
    //===Convert jsonString to List Category======


    //3 ============= WELCOME ACTIVITY ==========
    public static void storeWelcomeScreen(Context mContext) {
        //Bước 2: Lưu vào trong share preference
        SharedPreferences pre = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_welcome_activity),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(mContext.getString(R.string.key_welcome_activity), "INIT_OK");
        //chấp nhận lưu xuống file
        editor.apply();
    }

    public static boolean checkPreferenceWelcomeActivity(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences(mContext.getString(R.string.preference_welcome_activity), MODE_PRIVATE);
        String pref = pre.getString(mContext.getString(R.string.key_welcome_activity), "");
        if (pref == null) {
            return false;
        } else {
            return pref.equals("INIT_OK");
        }
    }

    public static void clearPreference(Context mContext) {
        SharedPreferences pre = mContext.
                getSharedPreferences(mContext.getString(R.string.preference_welcome_activity), MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.clear();
        editor.apply();
    }
}
