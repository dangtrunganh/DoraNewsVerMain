package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SettingsActivity;
import com.anhdt.doranewsvermain.adapter.viewpagercategory.CategoryViewPagerAdapter;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.NewsInCategoryFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LatestNewsFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener {
    public static final String PARAM_LIST_CATEGORY_LATEST_NEWS_FRG = "PARAM_LIST_CATEGORY_LATEST_NEWS_FRG";
    private static final String ARGS_U_ID_LATEST_NEWS_FRG = "ARGS_U_ID_LATEST_NEWS_FRG";

    private ArrayList<Category> arrayCategories;
    private ViewPager viewPagerCategories;
    private TabLayout tabLayoutCategories;
    private ImageView imageSettings;
    private ImageView imageNotifications;
    private Toolbar toolbar;

    private CategoryViewPagerAdapter categoryViewPagerAdapter;

    private AddFragmentCallback addFragmentCallback;
    private Context mContext;

    private ArrayList<UpdateUIFollowBookmarkChild> observers = new ArrayList<>();

//    private ArrayList<Datum> arrayDatum;


    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public LatestNewsFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    public void attach(UpdateUIFollowBookmarkChild updateUIFollowBookmarkChild) {
        observers.add(updateUIFollowBookmarkChild);
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

        imageSettings = view.findViewById(R.id.circle_button_person_latest_frg);
        imageSettings.setOnClickListener(this);
        imageNotifications = view.findViewById(R.id.iv_search_latest_frg);
        imageNotifications.setOnClickListener(this);
        viewPagerCategories = view.findViewById(R.id.view_pager_category);
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
            if (addFragmentCallback == null) {
//                Log.e("bn-", "what the hell");
                Toast.makeText(getContext(), "what the hell", Toast.LENGTH_SHORT).show();
                return;
            }
            newsInCategoryFrament.setAddFragmentCallback(addFragmentCallback);
            fragments.add(newsInCategoryFrament);
            attach(newsInCategoryFrament);
        }

        //ok?
        categoryViewPagerAdapter = new CategoryViewPagerAdapter(getActivity().getSupportFragmentManager());
        categoryViewPagerAdapter.setArrayFragments(fragments);
        categoryViewPagerAdapter.setArrayCategories(arrayCategories);
        viewPager.setAdapter(categoryViewPagerAdapter);
//        viewPagerCategories.setOffscreenPageLimit(arrayCategories.size());
        viewPagerCategories.setOffscreenPageLimit(1);
//        viewPager.setPageTransformer(true, new RotateUpTransformer());
//        viewPager.setPageTransformer(true, new DefaultTransformer());
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

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Không làm gì
        //Nếu sau này tab này có thêm event hay story thì làm tương tự updateUIBookmark bên dưới
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        for (UpdateUIFollowBookmarkChild observer : observers) {
            if (observer != null) {
                observer.updateUIBookmark(isBookmarked, idArticle, article);
            }
        }
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
        int position = viewPagerCategories.getCurrentItem();
        observers.get(position).scrollToTop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_button_person_latest_frg:
                //Mở màn settings
                startActivity(new Intent(mContext, SettingsActivity.class));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.iv_search_latest_frg:
                addFragmentCallback.addNotificationFragment();
                break;
            default:
                break;
        }
    }
}
