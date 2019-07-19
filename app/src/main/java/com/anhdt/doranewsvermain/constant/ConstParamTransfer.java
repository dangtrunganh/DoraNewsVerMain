package com.anhdt.doranewsvermain.constant;

public class ConstParamTransfer {

    //====Các key bắn từ PendingIntent trên Notification khi app đang bị kill, trả về cho SplashActivity
    public static final String KEY_EVENT_ID_NOTIFICATION = "event_id";
    public static final String KEY_LONG_EVENT_ID_NOTIFICATION = "long_event_id";



    //====Từ SplashActivity sang MainActivity====
    //Truyền từ SplashActivity, trong trường hợp đã có data category ở local, chỉ có trường hợp này thì mới có sự chuyển dịch này
    //Còn nếu không thì chỉ
    //Truyền sang màn MainActivity, với 2 param id_event và id_long_event
    public static final String TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN = "TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN";
    public static final String TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN = "TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN";
    public static final String TRANSFER_LIST_CATEGORY_FR_SPLASH_TO_MAIN = "TRANSFER_LIST_CATEGORY_FR_SPLASH_TO_MAIN";
    public static final String TRANSFER_U_ID_FR_SPLASH_TO_MAIN = "TRANSFER_U_ID_FR_SPLASH_TO_MAIN";
    public static final String TRANSFER_LIST_NEWS_BY_CATEGORY_FR_SPLASH_TO_MAIN = "TRANSFER_LIST_NEWS_BY_CATEGORY_FR_SPLASH_TO_MAIN";
    public static final String DEFAULT_TRANSFER_LIST_NEWS_BY_CATEGORY_FR_SPLASH_TO_MAIN = "null";


    //====Từ DetaillArticle sang ReadOriginalArticleActivity====
    public static final String TRANSFER_URL_FR_DETAIL_ARTICLE_TO_READ_ORIGINAL_ACT = "TRANSFER_URL_FR_DETAIL_ARTICLE_TO_READ_ORIGINAL_ACT";


    //====Từ SplashActivity sang PickCategoryActivity====
    public static final String TRANSFER_U_ID_FR_SPLASH_TO_PICK_CATEGORY = "TRANSFER_U_ID_FR_SPLASH_TO_PICK_CATEGORY";
}
