package com.anhdt.doranewsvermain.util;

import android.content.Context;
import android.util.Log;

import com.anhdt.doranewsvermain.config.ConfigSettings;
import com.anhdt.doranewsvermain.model.Logging;
import com.anhdt.doranewsvermain.model.newsresult.Article;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ReadRealmToolForLogging {
    private static final String FILE_NAME_DB_LIST_LOGGING = "logging.realm";

    public static void addLoggingToRealm(Context mContext, Logging logging) {
        Log.e("pil-number", ConfigSettings.numberOfLogging + "");
        Log.e("pil-detail", logging.toString());
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(FILE_NAME_DB_LIST_LOGGING)
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);
//        Realm articleRealm =
//                Realm.getInstance(
//                        new RealmConfiguration.Builder(mContext)
//                                .name(FILE_NAME_DB_LIST_ARTICLES)
//                                .build()
//                );

        articleRealm.beginTransaction();
        Logging copyArticle = articleRealm.copyToRealm(logging);
        articleRealm.commitTransaction();
        articleRealm.close();
    }

    public static ArrayList<Logging> getListLoggingInLocal(Context mContext) {
        //Trả về size 0 hoặc kết quả - cả list đầu ra
        // Initialize Realm (just once per application)
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(FILE_NAME_DB_LIST_LOGGING)
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);
        RealmResults<Logging> listLogging =
                articleRealm.where(Logging.class).findAll();

        ArrayList<Logging> arrayListResult = new ArrayList<>(listLogging.size());
        for (Logging article : listLogging) {
            arrayListResult.add(articleRealm.copyFromRealm(article));
        }
        articleRealm.close();
        return arrayListResult;
    }

//    public static void setListBookmark(Context mContext, ArrayList<Article> articles) {
//        ArrayList<Article> listBookmarkArticles = ReadRealmToolForBookmarkArticle.getListArticleInLocal(mContext);
//        if (listBookmarkArticles.size() == 0) {
//            return;
//        }
//
//        //Cách 1 - bubble toàn tập
//        for (int i = 0; i < articles.size(); i++) {
//            for (int j = 0; j < listBookmarkArticles.size(); j++) {
//                if (articles.get(i).getId().equals(listBookmarkArticles.get(j).getId())) {
//                    articles.get(i).setBookmarked(true);
//                }
//            }
//        }
//        //Cách 2 - query
//    }

//    public static void setArticleBookmarked(Context mContext, Article article) {
//        ArrayList<Article> listBookmarkArticles = ReadRealmToolForBookmarkArticle.getListArticleInLocal(mContext);
//        if (listBookmarkArticles.size() == 0) {
//            return;
//        }
//
//        //Cách 1 - bubble toàn tập=
//        for (int j = 0; j < listBookmarkArticles.size(); j++) {
//            if (article.getId().equals(listBookmarkArticles.get(j).getId())) {
//                article.setBookmarked(true);
//            }
//        }
//    }

    public static void deleteArticleBookmark(Context mContext, Article article) {
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(FILE_NAME_DB_LIST_LOGGING)
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
//        Realm articleRealm = Realm.getDefaultInstance();
        Realm articleRealm = Realm.getInstance(configuration);

        articleRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Article> rows = realm.where(Article.class).equalTo("sessionId", article.getId()).findAll();
                rows.deleteAllFromRealm();
            }
        });
        articleRealm.close();
    }

    public static void deleteAllLogging(Context mContext) {
        Realm.init(mContext);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(FILE_NAME_DB_LIST_LOGGING)
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getInstance(configuration);
        // obtain the results of a query
        RealmResults<Logging> results = realm.where(Logging.class).findAll();
        // All changes to data must happen in a transaction
        realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
        ConfigSettings.numberOfLogging = 0;
    }
//    public void updateArticleBookmark
}
