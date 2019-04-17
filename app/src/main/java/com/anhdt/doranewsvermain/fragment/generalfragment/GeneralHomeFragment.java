package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.HomeFragment;
import com.google.gson.Gson;

public class GeneralHomeFragment extends BaseFragment implements AddFragmentCallback {
    //    private FragmentManager fragmentManager = getChildFragmentManager();
    public static final String PARAM_U_ID_GENERAL_HOME_FRG = "PARAM_U_ID_GENERAL_HOME_FRG";

    public static FragmentManager fragmentManagerHome;

    public static GeneralHomeFragment newInstance() {
        Bundle args = new Bundle();

        GeneralHomeFragment fragment = new GeneralHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
//        View view = getView();
//        if (view == null) {
//            return;
//        }
        //Transition sang HomeFragment
        addFrg();
    }

    private void addFrg() {
        Bundle bundle = getArguments();
        String uId = bundle.getString(PARAM_U_ID_GENERAL_HOME_FRG);
        fragmentManagerHome = getChildFragmentManager();
        FragmentManager fragmentManager = getChildFragmentManager();
        HomeFragment fragment = HomeFragment.newInstance(uId);

        fragment.setAddFragmentCallback(this);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_container_frg_home, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_general_home;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void addFrgCallback(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_container_frg_home, fragment);
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
        for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
            fragmentManager.popBackStack();
        }
    }
}
