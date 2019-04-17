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


    //====uId=====
    //Tên file
    public static final String FILE_NAME_PREF_UID = "data_uid";
    //key lưu uId
    public static final String KEY_PREF_UID = "uid";
    //default nếu uId rỗng
    public static final String DEFAULT_VALUE_PREF_KEY_UID_DEFAULT = "";

    //====Cache-event-theo-category + articles, lưu vào chung 1 file, cho 2 key riêng====
    //Tên file
    public static final String FILE_NAME_PREF_CACHE_HOT = "cache_hot";
    //key lưu list event
    public static final String KEY_PREF_CACHE_HOT_EVENT = "key_event";
    //key lưu list articles
    public static final String DEFAULT_VALUE_PREF_CACHE_HOT_EVENT = "";
    //key lưu list article
    public static final String KEY_PREF_CACHE_HOT_ARTICLE = "key_article";
    //key lưu list articles
    public static final String DEFAULT_VALUE_PREF_CACHE_HOT_ARTICLE = "";

    //Từ lần sau, mỗi category đã load, lưu vào file theo tên slug của category đó
}
