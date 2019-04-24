package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;

import java.util.ArrayList;

public class GeneralFavoriteFragment extends BaseFragment implements AddFragmentCallback, ControlVoice {

    public static GeneralFavoriteFragment newInstance() {
        GeneralFavoriteFragment generalFavoriteFragment = new GeneralFavoriteFragment();
        Bundle args = new Bundle();
//        args.putString(PARAM_CATEGORY_SLUG, categoryJsonString);
//        args.putString(PARAM_CATEGORY_NAME, categoryName);
//        args.putBoolean(PARAM_HOT_EVENT, isHot);
//        genreFragment.setArguments(args);
        return generalFavoriteFragment;
    }

    @Override
    protected void initializeComponents() {

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_favorite;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void addFrgCallback(BaseFragmentNeedUpdateUI fragment) {

    }

    @Override
    public void popBackStack() {

    }

    @Override
    public void popAllBackStack() {

    }

    @Override
    public void updateListEventFollow(boolean isFollowed, String idStory) {

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
}
