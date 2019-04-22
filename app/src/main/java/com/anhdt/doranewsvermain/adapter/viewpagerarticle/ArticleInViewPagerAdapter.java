package com.anhdt.doranewsvermain.adapter.viewpagerarticle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anhdt.doranewsvermain.fragment.ArticleFramentInDetailNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ArticleInViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Article> mArrayNews;
//    private FragmentManager fragmentManager;

    public ArticleInViewPagerAdapter(FragmentManager fm, ArrayList<Article> mArrayNews) {
        super(fm);
//        super(fm);
//        this.fragmentManager = fm;
        this.mArrayNews = mArrayNews;
    }

    @Override
    public ArticleFramentInDetailNewsFragment getItem(int position) {
        Gson gson = new Gson();
        Article article = mArrayNews.get(position);
        String jsonArticle = gson.toJson(article);

        String jsonListTotal = gson.toJson(mArrayNews);
        return ArticleFramentInDetailNewsFragment.newInstance(jsonArticle, jsonListTotal, position);
    }

    @Override
    public int getCount() {
        if (mArrayNews == null) {
            return 0;
        }
        return mArrayNews.size();
    }
}
