package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;

public class MoreFragment extends BaseFragment implements AddFragmentCallback {

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
}
