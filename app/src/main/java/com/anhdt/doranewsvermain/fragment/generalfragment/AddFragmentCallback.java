package com.anhdt.doranewsvermain.fragment.generalfragment;

import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

import java.util.ArrayList;

public interface AddFragmentCallback {
    void addFrgCallback(BaseFragmentNeedUpdateUI fragment);

    void popBackStack();

    void popAllBackStack();

    void updateListEventFollowInAddFrag(boolean isFollowed, String idStory, Stories stories);

    void updateListArticleBookmarkInAddFrag(boolean isBookmarked, int idArticle, Article article);

    ArrayList<Article> getListArticlesPlayedOnTopEachFragment();

    void setListArticlesPlayedOnTopEachFragment(ArrayList<Article> articles);

    void clearListArticlesPlayedOnTopEachFragment();

    void addNotificationFragment();

    int getSizeOfObservers();

    void scrollToTop();
}
