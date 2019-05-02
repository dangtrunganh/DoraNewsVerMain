package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.FavoriteFragment;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.LatestNewsFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.ArticleBookmarkInTypeFavoriteFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;

import java.util.ArrayList;

public class GeneralFavoriteFragment extends BaseFragment implements AddFragmentCallback, ControlVoice {
    public static FragmentManager fragmentManagerFavorite;

    //List observers call update UI
    private ArrayList<UpdateUIFollowBookmarkChild> observers = new ArrayList<>();

    private UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain; //Interface truyền Main vào, để gọi hàm của Main

    public void setUpdateUIFollowBookmarkChildFromMain(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain) {
        this.updateUIFollowBookmarkChildFromMain = updateUIFollowBookmarkChildFromMain;
    }

    public static GeneralFavoriteFragment newInstance() {
        GeneralFavoriteFragment generalFavoriteFragment = new GeneralFavoriteFragment();
//        Bundle args = new Bundle();
//        args.putString(PARAM_CATEGORY_SLUG, categoryJsonString);
//        args.putString(PARAM_CATEGORY_NAME, categoryName);
//        args.putBoolean(PARAM_HOT_EVENT, isHot);
//        genreFragment.setArguments(args);
        return generalFavoriteFragment;
    }

    @Override
    protected void initializeComponents() {
        addFrg();
    }

    private void addFrg() {
        //Lấy list categories từ GeneralLatestNewsFragment tổng ra
        fragmentManagerFavorite = getChildFragmentManager();
//        Bundle bundle = getArguments();
        FavoriteFragment favoriteFragment = FavoriteFragment.newInstance();
//        Bundle args = new Bundle();
//        args.putString(LatestNewsFragment.PARAM_LIST_CATEGORY_LATEST_NEWS_FRG, jsonCategories);
//        articleBookmarkInTypeFavoriteFragment.setArguments(args);
        favoriteFragment.setAddFragmentCallback(this);


//        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManagerFavorite.beginTransaction();
        ft.replace(R.id.main_container_general_frg_favorite, favoriteFragment);
        ft.addToBackStack(null);

        attach(favoriteFragment);
        ft.commit();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_general_favorite;
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
        ft.add(R.id.main_container_general_frg_favorite, fragment);
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
        Log.e("0099-", "follow");
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
}
