package com.anhdt.doranewsvermain.fragment.generalfragment;

import android.os.Bundle;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

public class GeneralNotificationFragment extends BaseFragment implements AddFragmentCallback {

    public static GeneralNotificationFragment newInstance() {
        GeneralNotificationFragment generalNotificationFragment = new GeneralNotificationFragment();
        Bundle args = new Bundle();
//        args.putString(PARAM_CATEGORY_SLUG, categoryJsonString);
//        args.putString(PARAM_CATEGORY_NAME, categoryName);
//        args.putBoolean(PARAM_HOT_EVENT, isHot);
//        genreFragment.setArguments(args);
        return generalNotificationFragment;
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
    public void updateListEventFollowInAddFrag(boolean isFollowed, String idStory, Stories stories) {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {

    }

    @Override
    public void updateListArticleBookmarkInAddFrag(boolean isBookmarked, int idArticle, Article article) {
//        updateUIFollowBookmarkChildFromMain.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
//        for (UpdateUIFollowBookmarkChild observer : observers) {
//            observer.updateUIBookmark(isBookmarked, idArticle, article);
//        }
    }
}
