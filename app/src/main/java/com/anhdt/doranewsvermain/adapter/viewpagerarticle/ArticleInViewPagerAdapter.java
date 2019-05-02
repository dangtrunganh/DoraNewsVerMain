package com.anhdt.doranewsvermain.adapter.viewpagerarticle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anhdt.doranewsvermain.fragment.secondchildfragment.ArticleFramentInDetailNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ArticleInViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Article> mArrayNews;
    private FragmentManager fragmentManager;
    private ControlVoice controlVoice;

    public ArticleInViewPagerAdapter(FragmentManager fm, ArrayList<Article> mArrayNews, ControlVoice controlVoice) {
        super(fm);
        this.fragmentManager = fm;
        this.mArrayNews = mArrayNews;
        this.controlVoice = controlVoice;
    }

    @Override
    public ArticleFramentInDetailNewsFragment getItem(int position) {
        Gson gson = new Gson();
        Article article = mArrayNews.get(position);
        String jsonArticle = gson.toJson(article);
        String jsonListTotal = gson.toJson(mArrayNews);
        ArticleFramentInDetailNewsFragment articleFramentInDetailNewsFragment = ArticleFramentInDetailNewsFragment.newInstance(jsonArticle, jsonListTotal, position);
        articleFramentInDetailNewsFragment.setControlVoice(this.controlVoice);
        return articleFramentInDetailNewsFragment;
    }

    @Override
    public int getCount() {
        if (mArrayNews == null) {
            return 0;
        }
        return mArrayNews.size();
    }
}
