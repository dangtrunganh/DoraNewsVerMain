package com.anhdt.doranewsvermain.fragment;

import android.os.Bundle;

import com.anhdt.doranewsvermain.R;

public class MoreFragment extends BaseFragment {

    public static MoreFragment newInstance() {
        MoreFragment moreFragment = new MoreFragment();
        Bundle args = new Bundle();
//        args.putString(PARAM_CATEGORY_SLUG, categoryJsonString);
//        args.putString(PARAM_CATEGORY_NAME, categoryName);
//        args.putBoolean(PARAM_HOT_EVENT, isHot);
//        genreFragment.setArguments(args);
        return moreFragment;
    }

    @Override
    protected void initializeComponents() {

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initProgressbar() {

    }
}
