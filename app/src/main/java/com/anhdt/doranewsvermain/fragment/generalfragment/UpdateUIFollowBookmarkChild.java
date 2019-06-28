package com.anhdt.doranewsvermain.fragment.generalfragment;

import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

public interface UpdateUIFollowBookmarkChild {
    //Observer
    void updateUIFollow(boolean isFollowed, String idStory, Stories stories);

    void updateUIBookmark(boolean isBookmarked, int idArticle, Article article);

    void addNotificationFragment();

    void scrollToTop();
}
