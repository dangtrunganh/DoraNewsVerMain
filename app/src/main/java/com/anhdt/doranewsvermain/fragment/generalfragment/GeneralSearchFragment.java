package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.NotificationFragment;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.SearchFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

import java.util.ArrayList;

public class GeneralSearchFragment extends BaseFragment implements AddFragmentCallback {
    //List observers call update UI
    private ArrayList<UpdateUIFollowBookmarkChild> observers = new ArrayList<>();

    private UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain; //Interface truyền Main vào, để gọi hàm của Main

    private ArrayList<Article> articlesPlayedOnTopEachFragment = new ArrayList<>();

    public void setUpdateUIFollowBookmarkChildFromMain(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain) {
        this.updateUIFollowBookmarkChildFromMain = updateUIFollowBookmarkChildFromMain;
    }

    public GeneralSearchFragment() {

    }

    public static GeneralSearchFragment newInstance() {
        Bundle args = new Bundle();
        GeneralSearchFragment fragment = new GeneralSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        addFrg();
    }

    private void addFrg() {
        FragmentManager fragmentManagerNotification = getChildFragmentManager();

        SearchFragment searchFragment = SearchFragment.newInstance();
        //Nếu chẳng may thằng Fragment này bị detach() thì MainAct gọi tới méo có gì, lỗi sure
        searchFragment.setAddFragmentCallback(this);

        FragmentTransaction ft = fragmentManagerNotification.beginTransaction();
        ft.replace(R.id.main_container_general_frg_search, searchFragment);
        ft.addToBackStack(null);

        attach(searchFragment);
        ft.commit();
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
        ft.add(R.id.main_container_general_frg_search, fragment);
        ft.addToBackStack(null);

        attach(fragment);
        ft.commit();
    }

    @Override
    public void popBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        detach();
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
        //Gọi về Main
        updateUIFollowBookmarkChildFromMain.updateUIFollow(isFollowed, idStory, stories);
    }

    @Override
    public void updateListArticleBookmarkInAddFrag(boolean isBookmarked, int idArticle, Article article) {
        updateUIFollowBookmarkChildFromMain.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            observer.updateUIFollow(isFollowed, idStory, stories);
        }
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            observer.updateUIBookmark(isBookmarked, idArticle, article);
        }
    }

    @Override
    protected int getFragmentLayout() {

        return R.layout.fragment_general_search;
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

    @Override
    public void addNotificationFragment() {
        //do nothing
        updateUIFollowBookmarkChildFromMain.addNotificationFragment();
    }

    @Override
    public void scrollToTop() {
        //do nothing
    }

    @Override
    public int getSizeOfObservers() {
        if (observers == null) {
            return 0;
        } else {
            return observers.size();
        }
    }
}
