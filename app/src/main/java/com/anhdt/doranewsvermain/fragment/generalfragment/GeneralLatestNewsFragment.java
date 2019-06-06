package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.LatestNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;

import java.util.ArrayList;

public class GeneralLatestNewsFragment extends BaseFragment implements AddFragmentCallback, ControlVoice {
    public static final String PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG = "PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG";
    public static final String PARAM_U_ID_GENERAL_LATEST_NEWS_FRG = "PARAM_U_ID_GENERAL_LATEST_NEWS_FRG";
    public static FragmentManager fragmentManagerLatest;
    private ArrayList<UpdateUIFollowBookmarkChild> observers = new ArrayList<>();

    private UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain;

    private ArrayList<Article> articlesPlayedOnTopEachFragment = new ArrayList<>();

    public UpdateUIFollowBookmarkChild getUpdateUIFollowBookmarkChildFromMain() {
        return updateUIFollowBookmarkChildFromMain;
    }

    public void setUpdateUIFollowBookmarkChildFromMain(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain) {
        this.updateUIFollowBookmarkChildFromMain = updateUIFollowBookmarkChildFromMain;
    }

    public static GeneralLatestNewsFragment newInstance() {
        Bundle args = new Bundle();
        GeneralLatestNewsFragment fragment = new GeneralLatestNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        //First addFrg() - run only one time
        addFrg();
    }

    private void addFrg() {
        //Lấy list categories từ GeneralLatestNewsFragment tổng ra
        fragmentManagerLatest = getChildFragmentManager();
        Bundle bundle = getArguments();

        //List jsonCategories
        String jsonCategories = bundle.getString(GeneralLatestNewsFragment.PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG);

        //uId
        String uId = bundle.getString(PARAM_U_ID_GENERAL_LATEST_NEWS_FRG);

        //Sau đó lại put vào cho LatestNewsFragment con
        LatestNewsFragment latestNewsFragment = LatestNewsFragment.newInstance(uId);
        Bundle args = new Bundle();
        args.putString(LatestNewsFragment.PARAM_LIST_CATEGORY_LATEST_NEWS_FRG, jsonCategories);
        latestNewsFragment.setArguments(args);
        latestNewsFragment.setAddFragmentCallback(this);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_container_frg_latest_newss, latestNewsFragment);
        ft.addToBackStack(null);

        attach(latestNewsFragment);
        ft.commit();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_general_latest_news;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void addFrgCallback(BaseFragmentNeedUpdateUI fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();

        //Kiểm tra để addFragmentDetailNews vào khi click vào thanh control voice
        if (fragment instanceof DetailNewsFragment) {
             if (this.getControlVoice() != null) {
                 fragment.setControlVoice(this.getControlVoice());
             }
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        ft.add(R.id.main_container_frg_latest_newss, fragment);
        ft.addToBackStack(null);

        attach(fragment);
        ft.commit();
    }

    @Override
    public void popBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        detach();
        //detach Observer

    }

    public void attach(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChild) {
        observers.add(updateUIFollowBookmarkChild);
    }

    public void detach() {
        //Detach thằng trên cùng
        observers.remove(observers.size() - 1);
    }

    @Override
    public void popAllBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
            fragmentManager.popBackStack();
            detach();
        }
    }

    @Override
    public void updateListEventFollowInAddFrag(boolean isFollowed, String idStory, Stories stories) {
        updateUIFollowBookmarkChildFromMain.updateUIFollow(isFollowed, idStory, stories);
    }

    @Override
    public void playVoiceAtPosition(ArrayList<Article> articles, int position) {
        if (this.getControlVoice() != null) {
            this.getControlVoice().playVoiceAtPosition(articles, position);
        }
    }

    @Override
    public void setCurrentListVoiceOnTopStack(ArrayList<Article> articles) {
        this.getControlVoice().setCurrentListVoiceOnTopStack(articles);
    }

    @Override
    public void deleteCurrentListVoiceOnTopStack() {
        this.getControlVoice().deleteCurrentListVoiceOnTopStack();
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            observer.updateUIFollow(isFollowed, idStory, stories);
        }
    }

    @Override
    public void updateListArticleBookmarkInAddFrag(boolean isBookmarked, int idArticle, Article article) {
        updateUIFollowBookmarkChildFromMain.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            observer.updateUIBookmark(isBookmarked, idArticle, article);
        }
    }

    @Override
    public ArrayList<Article> getListArticlesPlayedOnTopEachFragment() {
        return articlesPlayedOnTopEachFragment;
    }

    @Override
    public void setListArticlesPlayedOnTopEachFragment(ArrayList<Article> articles) {
        articlesPlayedOnTopEachFragment = articles;
    }

    @Override
    public void clearListArticlesPlayedOnTopEachFragment() {
        articlesPlayedOnTopEachFragment = new ArrayList<>();
    }
}
