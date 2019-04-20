package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.HomeFragment;
import com.anhdt.doranewsvermain.fragment.LatestNewsFragment;

import java.util.ArrayList;
import java.util.List;

public class GeneralLatestNewsFragment extends BaseFragment implements AddFragmentCallback {
    public static final String PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG = "PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG";
    public static final String PARAM_U_ID_GENERAL_LATEST_NEWS_FRG = "PARAM_U_ID_GENERAL_LATEST_NEWS_FRG";
    public static FragmentManager fragmentManagerLatest;
    private ArrayList<UpdateUIFollow> observers = new ArrayList<>();
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

        attach(latestNewsFragment);
        ft.commit();
    }

    @Override
    protected void initializeComponents() {

        //First addFrg() - run only one time
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
    public void addFrgCallback(BaseFragmentNeedUpdateUI fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        ft.add(R.id.main_container_frg_latest_newss, fragment);
        ft.addToBackStack(null);

        attach(fragment);
        ft.commit();
    }

    @Override
    public void popBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        detach();
        //detach Observer

    }

    public void attach(UpdateUIFollow updateUIFollow) {
        observers.add(updateUIFollow);
    }

    public void detach() {
        //Detach thằng trên cùng
        observers.remove(observers.size() - 1);
    }

    @Override
    public void popAllBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
//        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        for(int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
            fragmentManager.popBackStack();
            detach();
        }
    }

    @Override
    public void updateListEventFollow(boolean isFollowed, String idStory) {
        for(UpdateUIFollow observer : observers) {
            observer.updateUIFollow(isFollowed, idStory);
        }
    }
}
