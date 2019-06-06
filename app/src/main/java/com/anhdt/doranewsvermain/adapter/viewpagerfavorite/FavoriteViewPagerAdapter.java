package com.anhdt.doranewsvermain.adapter.viewpagerfavorite;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseNormalFragment;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.FavoriteFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.ArticleBookmarkInTypeFavoriteFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.NewsInCategoryFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.StoryFollowedInTypeFavoriteFragment;
import com.anhdt.doranewsvermain.model.newsresult.Category;

import java.util.ArrayList;

public class FavoriteViewPagerAdapter extends FragmentStatePagerAdapter {
    private StoryFollowedInTypeFavoriteFragment storyFollowedInTypeFavoriteFragment;
    private ArticleBookmarkInTypeFavoriteFragment articleBookmarkInTypeFavoriteFragment;

    public FavoriteViewPagerAdapter(FragmentManager fm, StoryFollowedInTypeFavoriteFragment storyFollowedInTypeFavoriteFragment,
                                    ArticleBookmarkInTypeFavoriteFragment articleBookmarkInTypeFavoriteFragment) {
        super(fm);
        this.storyFollowedInTypeFavoriteFragment = storyFollowedInTypeFavoriteFragment;
        this.articleBookmarkInTypeFavoriteFragment = articleBookmarkInTypeFavoriteFragment;
    }

    @Override
    public BaseFragmentNeedUpdateUI getItem(int position) {
        switch (position) {
            case 0:
                return storyFollowedInTypeFavoriteFragment;
            case 1:
                return articleBookmarkInTypeFavoriteFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Đang theo dõi";
            case 1:
                return "Đã lưu";
            default:
                return "Lỗi tên";
        }
    }
}
