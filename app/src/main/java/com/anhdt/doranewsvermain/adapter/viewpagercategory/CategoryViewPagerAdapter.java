package com.anhdt.doranewsvermain.adapter.viewpagercategory;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anhdt.doranewsvermain.fragment.NewsInCategoryFragment;
import com.anhdt.doranewsvermain.model.newsresult.Category;

import java.util.ArrayList;

public class CategoryViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<NewsInCategoryFragment> arrayFragments;
    private ArrayList<Category> arrayCategories;

    public CategoryViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.arrayFragments = new ArrayList<>();
        this.arrayCategories = new ArrayList<>();
    }

    public void setArrayFragments(ArrayList<NewsInCategoryFragment> arrayFragments) {
        this.arrayFragments = arrayFragments;
    }

    public void setArrayCategories(ArrayList<Category> arrayCategories) {
        this.arrayCategories = arrayCategories;
    }

    @Override
    public NewsInCategoryFragment getItem(int position) {
        return this.arrayFragments.get(position);
    }

    @Override
    public int getCount() {
        return arrayFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return arrayCategories.get(position).getName();
    }
}
