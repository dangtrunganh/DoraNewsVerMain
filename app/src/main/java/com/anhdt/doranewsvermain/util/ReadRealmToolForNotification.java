package com.anhdt.doranewsvermain.util;

import android.content.Context;
import android.widget.Toast;

import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReadRealmToolForNotification {
    public static boolean addNotificationToRealm(Context mContext, NotificationResult notificationResult) {
        Realm.init(mContext);
        Realm articleRealm = Realm.getDefaultInstance();

        articleRealm.beginTransaction();
        try {
            NotificationResult notificationResultRealm = articleRealm.copyToRealm(notificationResult);
            articleRealm.commitTransaction();
            return true;
        } catch (Exception e) {
//            Toast.makeText(mContext, "Error, this event is already in db!", Toast.LENGTH_SHORT).show();
            articleRealm.commitTransaction();
            return false;
        }
    }

    public static ArrayList<NotificationResult> getListNotificationInLocal(Context mContext) {
        //Trả về size 0 hoặc kết quả - cả list đầu ra
        // Initialize Realm (just once per application)
        Realm.init(mContext);
        Realm articleRealm = Realm.getDefaultInstance();
        RealmResults<NotificationResult> listNotifications =
                articleRealm.where(NotificationResult.class).findAll();

        ArrayList<NotificationResult> arrayListResult = new ArrayList<>(listNotifications.size());
        for (NotificationResult notificationResult: listNotifications) {
            arrayListResult.add(articleRealm.copyFromRealm(notificationResult));
        }
        return arrayListResult;
    }

    public static void deleteNotification(Context mContext, NotificationResult notificationResult) {
        Realm.init(mContext);
        Realm articleRealm = Realm.getDefaultInstance();

        articleRealm.executeTransaction(realm -> {
            RealmResults<NotificationResult> rows = realm.where(NotificationResult.class).equalTo("idEvent", notificationResult.getIdEvent()).findAll();
            rows.deleteAllFromRealm();
        });
    }
}
