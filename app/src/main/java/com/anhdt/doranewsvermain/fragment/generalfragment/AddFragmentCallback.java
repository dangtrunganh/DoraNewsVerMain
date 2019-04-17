package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public interface AddFragmentCallback {
    void addFrgCallback(Fragment fragment);

    void popBackStack();

    void popAllBackStack();
}
