package com.anhdt.doranewsvermain.constant;

public class ConstLocalCaching {
    //====Cache-Of-App=====

    //====Category=====
    //Tên file
    public static final String FILE_NAME_PREF_LIST_CATEGORY = "data_categories";
    //key list category
    public static final String KEY_PREF_LIST_CATEGORY = "list_categories";
    //default nếu list rỗng
    public static final String DEFAULT_VALUE_PREF_LIST_CATEGORY_DEFAULT = "";


    //====uId=====mToken=======deviceId======
    //Tên FILE
    public static final String FILE_NAME_PREF_UID_M_TOKEN_DEVICE_ID = "data_uid_m_token_device_id";

    //key lưu uId
    public static final String KEY_PREF_UID = "key_uid";
    //default nếu uId rỗng
    public static final String DEFAULT_VALUE_PREF_UID_DEFAULT = "";

    //key lưu deviceId
    public static final String KEY_PREF_DEVICE_ID = "key_device_id";
    //default nếu deviceId rỗng
    public static final String DEFAULT_VALUE_PREF_DEVICE_ID_DEFAULT = "";

    //key lưu mTokenFirebase
    public static final String KEY_PREF_M_TOKEN = "key_m_token";
    //default nếu mTokenFirebase rỗng
    public static final String DEFAULT_VALUE_PREF_M_TOKEN_DEFAULT = "";




    //====Cache-news-datum-theo-category + tab hot, lưu vào chung 1 file, cho nhiều key, maybe?====
    //Tên file
    public static final String FILE_NAME_PREF_CACHE_GENERAL_NEWS = "cache_general_news";
    //key lưu list event
    public static final String KEY_PREF_CACHE_HOT_NEWS = "key_hot_news";
    //Giá trị mặc định trả về khi get theo KEY_PREF_CACHE_HOT_NEWS
    public static final String DEFAULT_VALUE_PREF_CACHE_HOT_NEWS = "";

    //Key lưu News theo category sẽ là id của Category đó

    //Giá trị mặc định trả về khi get theo DEFAULT_VALUE_PREF_CACHE_HOT_ARTICLE
    public static final String DEFAULT_VALUE_PREF_CACHE_NEWS_BY_CATEGORY= "";
}
