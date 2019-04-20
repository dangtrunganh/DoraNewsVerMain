package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.fragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.HomeFragment;
import com.google.gson.Gson;

public class GeneralHomeFragment extends BaseFragment implements AddFragmentCallback {
    //    private FragmentManager fragmentManager = getChildFragmentManager();
    public static final String PARAM_U_ID_GENERAL_HOME_FRG = "PARAM_U_ID_GENERAL_HOME_FRG";

    public static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    public static final String ARG_STORY_ID = "ARG_STORY_ID";
    public static final String DEFAULT_ID_ARG = "";


    public static FragmentManager fragmentManagerHome;

    public static GeneralHomeFragment newInstance() {
        Bundle args = new Bundle();

        GeneralHomeFragment fragment = new GeneralHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {

        //First addFrg() - run only one time
        addFrg();
    }

    private void addFrg() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        String uId = bundle.getString(PARAM_U_ID_GENERAL_HOME_FRG);
        String idEvent = bundle.getString(ARG_EVENT_ID);
        String idStory = bundle.getString(ARG_STORY_ID);

        fragmentManagerHome = getChildFragmentManager();
        FragmentManager fragmentManager = getChildFragmentManager();
        HomeFragment fragment = HomeFragment.newInstance(uId);

        fragment.setAddFragmentCallback(this);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_container_frg_home, fragment);
        ft.addToBackStack(null);
        ft.commit();

        //======
        //Thêm DetailEventFragment nếu idEvent và idStory != ""
        if (idEvent != null && idStory != null) {
            if (!idEvent.equals(DEFAULT_ID_ARG) && !idStory.equals(DEFAULT_ID_ARG)) {
                FragmentTransaction ftDetailEvent = fragmentManager.beginTransaction();
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(ConstGeneralTypeTab.TYPE_TAB_HOME, idEvent, idStory, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                detailEventFragment.setAddFragmentCallback(this);
                ftDetailEvent.add(R.id.main_container_frg_home, detailEventFragment);
                ftDetailEvent.addToBackStack(null);
                ftDetailEvent.commit();
            }
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_general_home;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void addFrgCallback(BaseFragmentNeedUpdateUI fragment) {
//        if (!isAdded()) return;
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.add(R.id.main_container_frg_home, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void popBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void popAllBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
//        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void updateListEventFollow(boolean isFollowed, String idStory) {

    }
}
