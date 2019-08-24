package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.anhdt.doranewsvermain.MainActivity;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.HomeFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.util.ReadRealmToolForNotification;

import java.util.ArrayList;

public class GeneralHomeFragment extends BaseFragment implements AddFragmentCallback {
    //    private FragmentManager fragmentManager = getChildFragmentManager();
    public static final String PARAM_U_ID_GENERAL_HOME_FRG = "PARAM_U_ID_GENERAL_HOME_FRG";
    public static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    public static final String ARG_STORY_ID = "ARG_STORY_ID";
    public static final String DEFAULT_ID_ARG = "";

    public static FragmentManager fragmentManagerHome;
    private ArrayList<UpdateUIFollowBookmarkChild> observers = new java.util.ArrayList<>();

    private UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain;

    private ArrayList<Article> articlesPlayedOnTopEachFragment = new ArrayList<>();

    public UpdateUIFollowBookmarkChild getUpdateUIFollowBookmarkChildFromMain() {
        return updateUIFollowBookmarkChildFromMain;
    }

    public void setUpdateUIFollowBookmarkChildFromMain(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChildFromMain) {
        this.updateUIFollowBookmarkChildFromMain = updateUIFollowBookmarkChildFromMain;
    }

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
                //Co intent ban ve, mo detail event len (khi app o background)
//                NotificationResult notificationResult = new NotificationResult("Tin hot", contentNotice, urlImageFromBroadcast,
//                        idEvent, idStory);
//
//                boolean x = ReadRealmToolForNotification.addNotificationToRealm(MainActivity.this, notificationResult);
//                if (x) {
//                    //Gọi update bên Notification Fragment
//                    generalNotificationFragment.addNotification(notificationResult);
//                }
                FragmentTransaction ftDetailEvent = fragmentManager.beginTransaction();
                ftDetailEvent.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                String catId = "default_cat_id";
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(/*ConstGeneralTypeTab.TYPE_TAB_HOME,*/ idEvent, idStory, DetailEventFragment.DEFAULT_LIST_OF_STORY, catId);
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
            if (this.getControlVoice() != null) {
                fragment.setControlVoice(this.getControlVoice());
            }
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.add(R.id.main_container_frg_home, fragment);
        transaction.addToBackStack(null);
        attach(fragment);
        transaction.commit();
    }

    @Override
    public void popBackStack() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        detach();
    }

    public void attach(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChild) {
        observers.add(updateUIFollowBookmarkChild);
//        Log.e("pii-", observers.size() + "");
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
    public void updateListEventFollowInAddFrag(boolean isFollowed, String idStory, Stories stories) {
        //Gọi về Main, bảo Main update
        updateUIFollowBookmarkChildFromMain.updateUIFollow(isFollowed, idStory, stories);
        //Cái này sẽ override, gọi lại từ Home sau
//        for (UpdateUIFollowBookmarkChild observer : observers) {
//            observer.updateUIFollow(isFollowed, idStory);
//        }
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            observer.updateUIFollow(isFollowed, idStory, stories);
        }
    }

    @Override
    public void updateListArticleBookmarkInAddFrag(boolean isBookmarked, int idArticle, Article article) {
        updateUIFollowBookmarkChildFromMain.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public ArrayList<Article> getListArticlesPlayedOnTopEachFragment() {
        return articlesPlayedOnTopEachFragment;
    }

    @Override
    public void setListArticlesPlayedOnTopEachFragment(ArrayList<Article> articles) {
        articlesPlayedOnTopEachFragment = articles;
    }

    @Override
    public void clearListArticlesPlayedOnTopEachFragment() {
        articlesPlayedOnTopEachFragment = new ArrayList<>();
    }

    @Override
    public void addNotificationFragment() {
        //do nothing
        updateUIFollowBookmarkChildFromMain.addNotificationFragment();
    }

    @Override
    public int getSizeOfObservers() {
        if (observers == null) {
            return 0;
        } else {
            return observers.size();
        }
    }

    @Override
    public void scrollToTop() {
        if (observers.size() == 1) {
            observers.get(0).scrollToTop();
        }
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            observer.updateUIBookmark(isBookmarked, idArticle, article);
        }
    }
}
