package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.HomeFragment;
import com.anhdt.doranewsvermain.fragment.LatestNewsFragment;

public class GeneralLatestNewsFragment extends BaseFragment implements AddFragmentCallback {
    public static final String PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG = "PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG";
    public static final String PARAM_U_ID_GENERAL_LATEST_NEWS_FRG = "PARAM_U_ID_GENERAL_LATEST_NEWS_FRG";
    public static FragmentManager fragmentManagerLatest;
    public static GeneralLatestNewsFragment newInstance() {

        Bundle args = new Bundle();

        GeneralLatestNewsFragment fragment = new GeneralLatestNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void addFrg() {
        //Lấy list categories từ GeneralLatestNewsFragment tổng ra
        fragmentManagerLatest = getChildFragmentManager();
        Bundle bundle = getArguments();
        String jsonCategories = bundle.getString(GeneralLatestNewsFragment.PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG);
        String uId = bundle.getString(PARAM_U_ID_GENERAL_LATEST_NEWS_FRG);

        //Sau đó lại put vào cho LatestNewsFragment con
        LatestNewsFragment latestNewsFragment = LatestNewsFragment.newInstance(uId);
        Bundle args = new Bundle();
        args.putString(LatestNewsFragment.PARAM_LIST_CATEGORY_LATEST_NEWS_FRG, jsonCategories);
        latestNewsFragment.setArguments(args);


        latestNewsFragment.setAddFragmentCallback(this);

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_container_frg_latest_newss, latestNewsFragment);
        ft.addToBackStack(latestNewsFragment.getClass().getName());
        ft.commit();
    }

    @Override
    protected void initializeComponents() {
        addFrg();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_general_latest_news;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void addFrgCallback(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_container_frg_latest_newss, fragment);
        ft.addToBackStack(null);
        ft.commit();
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
        for(int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
            fragmentManager.popBackStack();
        }
    }
}
