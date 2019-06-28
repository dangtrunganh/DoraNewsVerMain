package com.anhdt.doranewsvermain.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.viewpagerarticle.ArticleInViewPagerAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class DetailNewsFragment extends BaseFragmentNeedUpdateUI implements UpdateUIFollowBookmarkChild {
    private static final String ARGS_LIST_ARTICLE = "ARGS_LIST_ARTICLE";
    private static final String ARGS_POSITION_CLICKED = "ARGS_POSITION_CLICKED";
    private ArticleInViewPagerAdapter articleInViewPagerAdapter;
    private android.support.v4.view.ViewPager vpgNews;
    private ScrollingPagerIndicator indicator;

    private ArrayList<Article> mArrayNews;
    int position = 0;

//    private FragmentManager fragmentManager;
    private AddFragmentCallback addFragmentCallback;

    public ArrayList<Article> getmArrayNews() {
        return mArrayNews;
    }

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

//    public void setFragmentManager(FragmentManager fragmentManager) {
////        this.fragmentManager = fragmentManager;
//        //Chỉ khi fragment Manager được set thì mới set up cho view pager
//    }

    public static DetailNewsFragment newInstance(String jsonListArticles, int positionClicked) {
        DetailNewsFragment fragment = new DetailNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_ARTICLE, jsonListArticles);
        args.putInt(ARGS_POSITION_CLICKED, positionClicked);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        //Inflate view
        Toolbar toolbar = view.findViewById(R.id.toolbar_detail_news);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> addFragmentCallback.popBackStack());
//        getContext().setSupportActionBar(toolbar);
//        try {
////            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //Lấy dữ liệu
        Bundle bundle = getArguments();
        if (bundle == null) {
            mArrayNews = new ArrayList<>();
            return;
        }
        String jsonListArticles = bundle.getString(ARGS_LIST_ARTICLE);
        if (jsonListArticles == null) {
            mArrayNews = new ArrayList<>();
            return;
        }
        position = bundle.getInt(ARGS_POSITION_CLICKED);

        Gson gson = new Gson();
        mArrayNews = gson.fromJson(jsonListArticles, new TypeToken<List<Article>>() {
        }.getType());

        //===
        if (mArrayNews == null) {
            return;
        }
        if (this.getControlVoice() == null) {
            return;
        }
        this.getControlVoice().setCurrentListVoiceOnTopStack(mArrayNews);
        vpgNews = view.findViewById(R.id.vpg_news_detail_news);
        vpgNews.setOffscreenPageLimit(1);
        vpgNews.requestDisallowInterceptTouchEvent(true);
        indicator = view.findViewById(R.id.indicator_detail_news);

        //=======
        setUpViewPager();
    }

    private void setUpViewPager() {
//        if (fragmentManager == null) {
//            return;
//        }
        articleInViewPagerAdapter = new ArticleInViewPagerAdapter(getChildFragmentManager(), mArrayNews, this.getControlVoice());
        vpgNews.setAdapter(articleInViewPagerAdapter);

        indicator.attachToPager(vpgNews);
        vpgNews.setCurrentItem(position);
    }

    @Override
    public void onDetach() {
        if (this.getControlVoice() != null) {
            this.getControlVoice().deleteCurrentListVoiceOnTopStack();
        }
        super.onDetach();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_detail_news;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Không làm gì
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //update nếu có thằng bị bookmark tại đây
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }
}
