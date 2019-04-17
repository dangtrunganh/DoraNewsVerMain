package com.anhdt.doranewsvermain.fragment;

import android.os.Bundle;

import com.anhdt.doranewsvermain.R;

public class FavoriteFragment extends BaseFragment {

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
}
