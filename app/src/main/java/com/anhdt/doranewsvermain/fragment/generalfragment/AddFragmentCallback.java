package com.anhdt.doranewsvermain.fragment.generalfragment;

import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

public interface AddFragmentCallback {
    void addFrgCallback(BaseFragmentNeedUpdateUI fragment);

    void popBackStack();

    void popAllBackStack();

    void updateListEventFollowInAddFrag(boolean isFollowed, String idStory, Stories stories);

    void updateListArticleBookmarkInAddFrag(boolean isBookmarked, int idArticle, Article article);
}
