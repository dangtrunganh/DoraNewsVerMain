package com.anhdt.doranewsvermain.util;

import android.content.Context;
import android.widget.Toast;

import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ReadRealmToolForNotification {
    public static boolean addNotificationToRealm(Context mContext, NotificationResult notificationResult) {
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("notification.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);

        //====begin======
        articleRealm.beginTransaction();
        try {
            NotificationResult notificationResultRealm = articleRealm.copyToRealm(notificationResult);
            articleRealm.commitTransaction();
            articleRealm.close();
            return true;
        } catch (Exception e) {
//            Toast.makeText(mContext, "Error, this event is already in db!", Toast.LENGTH_SHORT).show();
//            articleRealm.commitTransaction();
            articleRealm.close();
            return false;
        }
    }

    public static boolean addListNotificationToRealm(Context mContext, ArrayList<NotificationResult> notificationResultsAPI, ArrayList<NotificationResult> notificationResultsLocal) {
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("notification.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);
        articleRealm.beginTransaction();
        try {
            for (NotificationResult notificationResultAPI : notificationResultsAPI) {
                if (!GeneralTool.checkIfEventExistInLocal(notificationResultAPI.getIdNotification(), notificationResultsLocal)) {
                    //Nếu không tồn tại trong Local thì thực hiện update vào db
                    NotificationResult notificationResultRealm = articleRealm.copyToRealm(notificationResultAPI);
                    articleRealm.commitTransaction();
                }
            }
            articleRealm.close();
            return true;
        } catch (Exception e) {
//            Toast.makeText(mContext, "Error, this event is already in db!", Toast.LENGTH_SHORT).show();
//            articleRealm.commitTransaction();
            articleRealm.close();
            return false;
        }
    }

    public static ArrayList<NotificationResult> getListNotificationInLocal(Context mContext) {
        //Trả về size 0 hoặc kết quả - cả list đầu ra
        // Initialize Realm (just once per application)
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("notification.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);

        RealmResults<NotificationResult> listNotifications =
                articleRealm.where(NotificationResult.class).findAll();

        ArrayList<NotificationResult> arrayListResult = new ArrayList<>(listNotifications.size());
        for (NotificationResult notificationResult: listNotifications) {
            arrayListResult.add(articleRealm.copyFromRealm(notificationResult));
        }
        articleRealm.close();
        return arrayListResult;
    }

    public static void deleteNotification(Context mContext, NotificationResult notificationResult) {
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("notification.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);

        articleRealm.executeTransaction(realm -> {
            RealmResults<NotificationResult> rows = realm.where(NotificationResult.class).equalTo("idEvent", notificationResult.getIdEvent()).findAll();
            rows.deleteAllFromRealm();
            articleRealm.close();
        });
    }
}
