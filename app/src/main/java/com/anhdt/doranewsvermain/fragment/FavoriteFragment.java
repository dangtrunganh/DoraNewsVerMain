package com.anhdt.doranewsvermain.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;

public class FavoriteFragment extends BaseFragment implements AddFragmentCallback {

    public static FavoriteFragment newInstance() {
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        Bundle args = new Bundle();
//        args.putString(PARAM_CATEGORY_SLUG, categoryJsonString);
//        args.putString(PARAM_CATEGORY_NAME, categoryName);
//        args.putBoolean(PARAM_HOT_EVENT, isHot);
//        genreFragment.setArguments(args);
        return favoriteFragment;
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
    public void addFrgCallback(BaseFragment fragment) {

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
}
