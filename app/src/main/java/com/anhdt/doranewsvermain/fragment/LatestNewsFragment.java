package com.anhdt.doranewsvermain.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.viewpagercategory.CategoryViewPagerAdapter;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LatestNewsFragment extends BaseFragment {
    public static final String PARAM_LIST_CATEGORY_LATEST_NEWS_FRG = "PARAM_LIST_CATEGORY_LATEST_NEWS_FRG";
    private static final String ARGS_U_ID_LATEST_NEWS_FRG = "ARGS_U_ID_LATEST_NEWS_FRG";

    private ArrayList<Category> arrayCategories;
    private ViewPager viewPagerCategories;
    private TabLayout tabLayoutCategories;
    private Toolbar toolbar;

    private CategoryViewPagerAdapter categoryViewPagerAdapter;

    private AddFragmentCallback addFragmentCallback;

//    private ArrayList<Datum> arrayDatum;


    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public LatestNewsFragment() {
    }

    public static LatestNewsFragment newInstance(String uId) {
        LatestNewsFragment latestNewsFragment = new LatestNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_U_ID_LATEST_NEWS_FRG, uId);
        latestNewsFragment.setArguments(args);
        return latestNewsFragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        viewPagerCategories = view.findViewById(R.id.view_pager_category);
        viewPagerCategories.setOffscreenPageLimit(1);
        tabLayoutCategories = view.findViewById(R.id.tab_layout_category);
        toolbar = view.findViewById(R.id.actionbar_latest_news);

        setUpViewPager(viewPagerCategories);
        tabLayoutCategories.setupWithViewPager(viewPagerCategories);
    }

    private void setUpViewPager(ViewPager viewPager) {
        //Lấy list categories từ param của fragment
        Bundle bundle = getArguments();
        String uId = bundle.getString(ARGS_U_ID_LATEST_NEWS_FRG);

        arrayCategories = getListCategoriesChosen();
        if (arrayCategories.size() == 0) {
            return;
        }
        ArrayList<NewsInCategoryFragment> fragments = new ArrayList<>();
        Gson gson = new Gson();

        //add list category bình thường
        for (int i = 0; i < arrayCategories.size(); i++) {
            String jsonCategory = gson.toJson(arrayCategories.get(i));
            NewsInCategoryFragment newsInCategoryFrament = NewsInCategoryFragment.newInstance(jsonCategory, uId);
            if(addFragmentCallback == null) {
//                Log.e("bn-", "what the hell");
                Toast.makeText(getContext(), "what the hell", Toast.LENGTH_SHORT).show();
                return;
            }
            newsInCategoryFrament.setAddFragmentCallback(addFragmentCallback);
            fragments.add(newsInCategoryFrament);
        }

        //ok?
        categoryViewPagerAdapter = new CategoryViewPagerAdapter(getActivity().getSupportFragmentManager());
        categoryViewPagerAdapter.setArrayFragments(fragments);
        categoryViewPagerAdapter.setArrayCategories(arrayCategories);
        viewPager.setAdapter(categoryViewPagerAdapter);
    }

    private ArrayList<Category> getListCategoriesChosen() {
        ArrayList<Category> categories = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle == null) {
            return categories;
        }
        String jsonListCategories = bundle.getString(PARAM_LIST_CATEGORY_LATEST_NEWS_FRG);
        if (jsonListCategories == null) {
            return categories;
        }
        Gson gson = new Gson();
        categories = gson.fromJson(jsonListCategories, new TypeToken<List<Category>>() {
        }.getType());

        return categories;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_latest_news_2;
    }

    @Override
    protected void initProgressbar() {

    }
}
