package com.anhdt.doranewsvermain.fragment.secondchildfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.ArticleItemAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseNormalFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;

import java.util.ArrayList;

public class ArticleBookmarkInTypeFavoriteFragment extends BaseFragmentNeedUpdateUI {
//    private static final String CONNECTED = "CONNECTED";
//    private static final String DISCONNECTED = "DISCONNECTED";

    private AddFragmentCallback addFragmentCallback;

    private Context mContext;
    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayoutNoNetwork;
    private ArrayList<Article> articles;
    private ArticleItemAdapter articleItemAdapter;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public ArticleBookmarkInTypeFavoriteFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    public static ArticleBookmarkInTypeFavoriteFragment newInstance() {
        Bundle args = new Bundle();

        ArticleBookmarkInTypeFavoriteFragment fragment = new ArticleBookmarkInTypeFavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        constraintLayoutNoNetwork = view.findViewById(R.id.constraint_state_wifi_off_frg_article_bookmark);
        constraintLayoutNoNetwork.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recycler_frg_article_bookmark);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //===Load data====
        articles = new ArrayList<>();
        articles = ReadRealmToolForBookmarkArticle.getListArticleInLocal(mContext);

        if (articles.size() == 0) {
            //Hiển thị màn hình chưa có thông báo nào
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        articleItemAdapter = new ArticleItemAdapter(mContext,
                articles, addFragmentCallback,
                null, ArticleItemAdapter.IN_HOME);
        recyclerView.setAdapter(articleItemAdapter);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_article_bookmark;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //do nothing
    }


    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //gọi hàm update
        //true - theo dõi
        if (articleItemAdapter != null) {
            if (isBookmarked) {
                //Theo dõi - thêm một story mới vào list theo dõi
                articleItemAdapter.addNewArticleBookmarked(article);
            } else {
                //Hủy theo dõi, bỏ ra khỏi list
                articleItemAdapter.removeArticleBookmarked(idArticle);
            }
            if (articleItemAdapter.getmCurrentArrayArticles().size() == 0) {
                //Hiển thị màn hình chưa có thông báo nào
                constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                constraintLayoutNoNetwork.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void addNotificationFragment() {

    }
}
