package com.anhdt.doranewsvermain.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.ArticleItemAdapter2;
import com.anhdt.doranewsvermain.adapter.viewpagerarticle.ArticleInViewPagerAdapter;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class DetailNewsFragment extends BaseFragmentNeedUpdateUI implements UpdateUIFollowBookmarkChild, View.OnClickListener {
    private static final String ARGS_LIST_ARTICLE = "ARGS_LIST_ARTICLE";
    private static final String ARGS_POSITION_CLICKED = "ARGS_POSITION_CLICKED";

    private static final String ARGS_IS_FROM_DETAIL_EVENT = "ARGS_IS_FROM_DETAIL_EVENT";

    private static final int BOOKMARK = 1;
    private static final int UNBOOKMARK = 0;
    private ArticleInViewPagerAdapter articleInViewPagerAdapter;
    private android.support.v4.view.ViewPager vpgNews;
    private TextView textViewArticle;
    private ImageView imageBookmark;
    private View viewClickBookmark;
    private int stateBookmarked = -1;
    private Context mContext;
    private ArrayList<Article> articlesBookmarked;
    private Article currentArticle;
    private int currentPage = 0;
//    private ScrollingPagerIndicator indicator;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    //    public void setFragmentManager(FragmentManager fragmentManager) {
////        this.fragmentManager = fragmentManager;
//        //Chỉ khi fragment Manager được set thì mới set up cho view pager
//    }

    public static DetailNewsFragment newInstance(String jsonListArticles, int positionClicked, boolean isFromDetailEvent) {
        DetailNewsFragment fragment = new DetailNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_ARTICLE, jsonListArticles);
        args.putInt(ARGS_POSITION_CLICKED, positionClicked);
        args.putBoolean(ARGS_IS_FROM_DETAIL_EVENT, isFromDetailEvent);
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
        textViewArticle = view.findViewById(R.id.text_view_article_detail_news);
        imageBookmark = view.findViewById(R.id.image_bookmark_detail_news);
        viewClickBookmark = view.findViewById(R.id.view_click_bookmark_detail_news);
//        imageBookmark.setOnClickListener(this);
        viewClickBookmark.setOnClickListener(this);
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
        articlesBookmarked = new ArrayList<>();
        articlesBookmarked = ReadRealmToolForBookmarkArticle.getListArticleInLocal(mContext);
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
        if (mArrayNews.size() == 0) {
            return;
        }
//        if (bundle.getBoolean(ARGS_IS_FROM_DETAIL_EVENT)) {
//            mArrayNews.remove(mArrayNews.size() - 1);
//        }
        if (mArrayNews.get(mArrayNews.size() - 1) == null) {
            mArrayNews.remove(mArrayNews.size() - 1);
        }
        if (mArrayNews.get(mArrayNews.size() - 1).getId().equals(ArticleItemAdapter2.DEFAULT_ID_ARTICLE_FOOTER)) {
            mArrayNews.remove(mArrayNews.size() - 1);
        }
        this.getControlVoice().setCurrentListVoiceOnTopStack(mArrayNews);
        vpgNews = view.findViewById(R.id.vpg_news_detail_news);
        vpgNews.requestDisallowInterceptTouchEvent(true);
//        indicator = view.findViewById(R.id.indicator_detail_news);

        //=======
        setUpViewPager();
    }

    private void setUpViewPager() {
//        if (fragmentManager == null) {
//            return;
//        }
        articleInViewPagerAdapter = new ArticleInViewPagerAdapter(getChildFragmentManager(), mArrayNews, this.getControlVoice());
        vpgNews.setAdapter(articleInViewPagerAdapter);
//        vpgNews.setOffscreenPageLimit(mArrayNews.size());
        vpgNews.setOffscreenPageLimit(5);

//        indicator.attachToPager(vpgNews);
        vpgNews.setCurrentItem(position);

        int size = mArrayNews.size();
//        indicator.setVisibility(View.GONE);
        textViewArticle.setVisibility(View.VISIBLE);
        if (size == 0) {
            return;
        }
        //====
        //setup cho bài báo đầu tiên xem đã bookmark chưa
        currentArticle = mArrayNews.get(0); //bài báo đầu tiên
        if (articlesBookmarked.size() > 0) {
            //Cách 1 - bubble toàn tập
            for (int i = 0; i < mArrayNews.size(); i++) {
                for (int j = 0; j < articlesBookmarked.size(); j++) {
                    if (mArrayNews.get(i).getId().equals(articlesBookmarked.get(j).getId())) {
                        mArrayNews.get(i).setBookmarked(true);
                    }
                }
            }
        }
        updateUIWhenBookmark(currentArticle.isBookmarked());
        //====
        if (size == 1) {
            textViewArticle.setText("Tin tóm tắt");
            return;
        }
        textViewArticle.setText("Tin tóm tắt (" + (position + 1) + "/" + size + ")");
        vpgNews.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
                currentArticle = mArrayNews.get(i);
                updateUIWhenBookmark(currentArticle.isBookmarked());

                int page = i + 1;
                textViewArticle.setText("Tin tóm tắt (" + page + "/" + size + ")");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

//        if (mArrayNews.size() == 1) {
//            indicator.setVisibility(View.GONE);
//            textViewArticle.setVisibility(View.VISIBLE);
//        } else {
//            indicator.setVisibility(View.VISIBLE);
//            textViewArticle.setVisibility(View.GONE);
//        }
    }

    public void updateUIWhenBookmark(boolean isBookmarked) {
        //true - đã bookmark
        if (isBookmarked) {
            imageBookmark.setImageLevel(BOOKMARK);
        } else {
            //Không theo dõi
            imageBookmark.setImageLevel(UNBOOKMARK);
        }
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
        for (int i = 0; i < mArrayNews.size(); i++) {
            if (mArrayNews.get(i).getId().equals(article.getId())) {
                mArrayNews.get(i).setBookmarked(isBookmarked);
            }
        }
        if (currentArticle.getId().equals(article.getId())) {
            updateUIWhenBookmark(isBookmarked);
        }
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_click_bookmark_detail_news:
                //bookmark news
//                Toast.makeText(mContext, "Click!", Toast.LENGTH_SHORT).show();
                if (currentArticle.isBookmarked()) {
                    //Bỏ lưu
                    currentArticle.setBookmarked(false);
                    ReadRealmToolForBookmarkArticle.deleteArticleBookmark(mContext, currentArticle);
                    addFragmentCallback.updateListArticleBookmarkInAddFrag(false, currentArticle.getId(), currentArticle);
//                            Toast.makeText(mContext, "Bỏ lưu!", Toast.LENGTH_SHORT).show();
                } else {
                    //Lưu lại
                    currentArticle.setBookmarked(true);
                    ReadRealmToolForBookmarkArticle.addArticleToRealm(mContext, currentArticle);
                    addFragmentCallback.updateListArticleBookmarkInAddFrag(true, currentArticle.getId(), currentArticle);
                    Toast.makeText(mContext, "Đã lưu!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
