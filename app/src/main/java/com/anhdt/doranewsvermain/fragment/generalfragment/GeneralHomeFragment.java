package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.HomeFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;

import java.util.ArrayList;

public class GeneralHomeFragment extends BaseFragment implements AddFragmentCallback {
    //    private FragmentManager fragmentManager = getChildFragmentManager();
    public static final String PARAM_U_ID_GENERAL_HOME_FRG = "PARAM_U_ID_GENERAL_HOME_FRG";
    public static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    public static final String ARG_STORY_ID = "ARG_STORY_ID";
    public static final String DEFAULT_ID_ARG = "";

    public static FragmentManager fragmentManagerHome;
    private ArrayList<UpdateUIFollow> observers = new java.util.ArrayList<>();
//    private A
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
        HomeFragment fragmentHome = HomeFragment.newInstance(uId);

        fragmentHome.setAddFragmentCallback(this);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_container_frg_home, fragmentHome);
        ft.addToBackStack(null);

        attach(fragmentHome);
        ft.commit();

        //======
        //Thêm DetailEventFragment nếu idEvent và idStory != ""
        if (idEvent != null && idStory != null) {
            if (!idEvent.equals(DEFAULT_ID_ARG) && !idStory.equals(DEFAULT_ID_ARG)) {
                FragmentTransaction ftDetailEvent = fragmentManager.beginTransaction();
                ftDetailEvent.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(ConstGeneralTypeTab.TYPE_TAB_HOME, idEvent, idStory, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                detailEventFragment.setAddFragmentCallback(this);
                ftDetailEvent.add(R.id.main_container_frg_home, detailEventFragment);
                ftDetailEvent.addToBackStack(null);
                attach(detailEventFragment);
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

        //========
        if (fragment instanceof DetailNewsFragment) {
            //Đầu tiên kiểm tra xem Fragment này có đang trùng với đỉnh stack ko?
//            ArrayList<Article> newArticles = ((DetailNewsFragment) fragment).getmArrayNews(); //List mới
//            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
//            DetailNewsFragment fragmentNeedUI = (DetailNewsFragment) fragmentManager.findFragmentByTag(fragmentTag);
            if (this.getControlVoice() != null) {
                fragment.setControlVoice(this.getControlVoice());
            }
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.add(R.id.main_container_frg_home, fragment);
        String tag = fragment.getClass().getName();
        transaction.addToBackStack(tag);

        attach(fragment);
        transaction.commit();
    }

    @Override
    public void popBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        detach();
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
        for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
            fragmentManager.popBackStack();
            detach();
        }
    }

    @Override
    public void updateListEventFollow(boolean isFollowed, String idStory) {
        for (UpdateUIFollow observer : observers) {
            observer.updateUIFollow(isFollowed, idStory);
        }
    }
}
