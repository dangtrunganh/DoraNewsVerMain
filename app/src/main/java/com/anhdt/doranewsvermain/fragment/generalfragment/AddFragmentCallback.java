package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.anhdt.doranewsvermain.fragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.BaseFragmentNeedUpdateUI;

public interface AddFragmentCallback {
    void addFrgCallback(BaseFragmentNeedUpdateUI fragment);

    void popBackStack();

    void popAllBackStack();

    void updateListEventFollow(boolean isFollowed, String idStory);
}
