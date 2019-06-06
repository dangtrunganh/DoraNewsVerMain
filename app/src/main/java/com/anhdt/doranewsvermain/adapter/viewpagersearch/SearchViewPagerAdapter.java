package com.anhdt.doranewsvermain.adapter.viewpagersearch;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.ArticleInTypeSearchFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.EventInTypeSearchFragment;

public class SearchViewPagerAdapter extends FragmentStatePagerAdapter {
    private EventInTypeSearchFragment eventInTypeSearchFragment;
    private ArticleInTypeSearchFragment articleInTypeSearchFragment;
    public SearchViewPagerAdapter(FragmentManager fm,
                                  EventInTypeSearchFragment eventInTypeSearchFragment,
                                  ArticleInTypeSearchFragment articleInTypeSearchFragment) {
        super(fm);
        this.eventInTypeSearchFragment = eventInTypeSearchFragment;
        this.articleInTypeSearchFragment = articleInTypeSearchFragment;
    }

    @Override
    public BaseFragmentNeedUpdateUI getItem(int position) {
        switch (position) {
            case 0:
                return eventInTypeSearchFragment;
            case 1:
                return articleInTypeSearchFragment;
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
                return "Sự kiện";
            case 1:
                return "Bài báo";
            default:
                return "Lỗi tên";
        }
    }
}
