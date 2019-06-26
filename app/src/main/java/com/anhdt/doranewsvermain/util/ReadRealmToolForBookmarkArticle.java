package com.anhdt.doranewsvermain.util;

import android.content.Context;

import com.anhdt.doranewsvermain.model.newsresult.Article;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReadRealmToolForBookmarkArticle {
    private static final String FILE_NAME_DB_LIST_ARTICLES = "ListArticles.realm";

    public static void addArticleToRealm(Context mContext, Article article) {
        Realm.init(mContext);
        Realm articleRealm = Realm.getDefaultInstance();
//        Realm articleRealm =
//                Realm.getInstance(
//                        new RealmConfiguration.Builder(mContext)
//                                .name(FILE_NAME_DB_LIST_ARTICLES)
//                                .build()
//                );

        articleRealm.beginTransaction();
        Article copyArticle = articleRealm.copyToRealm(article);
        articleRealm.commitTransaction();
    }

    public static ArrayList<Article> getListArticleInLocal(Context mContext) {
        //Trả về size 0 hoặc kết quả - cả list đầu ra
        // Initialize Realm (just once per application)
        Realm.init(mContext);
        Realm articleRealm = Realm.getDefaultInstance();
        RealmResults<Article> listArticles =
                articleRealm.where(Article.class).findAll();

        ArrayList<Article> arrayListResult = new ArrayList<>(listArticles.size());
        for (Article article : listArticles) {
            arrayListResult.add(articleRealm.copyFromRealm(article));
        }
        return arrayListResult;
    }

    public static void setListBookmark(Context mContext, ArrayList<Article> articles) {
        ArrayList<Article> listBookmarkArticles = ReadRealmToolForBookmarkArticle.getListArticleInLocal(mContext);
        if (listBookmarkArticles.size() == 0) {
            return;
        }

        //Cách 1 - bubble toàn tập
        for (int i = 0; i < articles.size(); i++) {
            for (int j = 0; j < listBookmarkArticles.size(); j++) {
                if (articles.get(i).getId().equals(listBookmarkArticles.get(j).getId())) {
                    articles.get(i).setBookmarked(true);
                }
            }
        }

        //Cách 2 - query
    }

    public static void setArticleBookmarked(Context mContext, Article article) {
        ArrayList<Article> listBookmarkArticles = ReadRealmToolForBookmarkArticle.getListArticleInLocal(mContext);
        if (listBookmarkArticles.size() == 0) {
            return;
        }

        //Cách 1 - bubble toàn tập=
        for (int j = 0; j < listBookmarkArticles.size(); j++) {
            if (article.getId().equals(listBookmarkArticles.get(j).getId())) {
                article.setBookmarked(true);
            }
        }
    }

    public static void deleteArticleBookmark(Context mContext, Article article) {
        Realm.init(mContext);
        Realm articleRealm = Realm.getDefaultInstance();

        articleRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Article> rows = realm.where(Article.class).equalTo("id", article.getId()).findAll();
                rows.deleteAllFromRealm();
            }
        });
    }

//    public void updateArticleBookmark
}
