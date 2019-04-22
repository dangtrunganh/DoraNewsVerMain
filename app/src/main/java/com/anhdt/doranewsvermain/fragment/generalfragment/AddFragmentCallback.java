package com.anhdt.doranewsvermain.fragment.generalfragment;

import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;

public interface AddFragmentCallback {
    void addFrgCallback(BaseFragmentNeedUpdateUI fragment);

    void popBackStack();

    void popAllBackStack();

    void updateListEventFollow(boolean isFollowed, String idStory);
}
