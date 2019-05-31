package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SettingsActivity;
import com.anhdt.doranewsvermain.adapter.viewpagerfavorite.FavoriteViewPagerAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.ArticleBookmarkInTypeFavoriteFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.StoryFollowedInTypeFavoriteFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

import java.util.Objects;

public class FavoriteFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener {
    private ViewPager viewPagerTwoTypeTab;
    private TabLayout tabLayoutTwoTypeTab;
    private Toolbar toolbar;
    private ImageView imageSettings;

    private FavoriteViewPagerAdapter favoriteViewPagerAdapter;

    //Gọi về General để call Main gọi hàm addFrg()
    private AddFragmentCallback addFragmentCallback;

    //Gọi đến thằng con số 1, yêu cầu nó theo dõi/bỏ theo dõi
    private UpdateUIFollowBookmarkChild updateUIFollowFirstChildTab;

    //Gọi đến thằng con số 2, yêu cầu nó bookmark/ bỏ bookmark
    private UpdateUIFollowBookmarkChild updateUIBookmarkSecondChildTab;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        imageSettings = view.findViewById(R.id.circle_button_person_favorite_frg);
        viewPagerTwoTypeTab = view.findViewById(R.id.view_pager_type_favorite);
        viewPagerTwoTypeTab.setOffscreenPageLimit(1);
        tabLayoutTwoTypeTab = view.findViewById(R.id.tab_layout_type_favorite);
        toolbar = view.findViewById(R.id.actionbar_favorite);
        imageSettings.setOnClickListener(this);

        setUpViewPager(viewPagerTwoTypeTab);
        tabLayoutTwoTypeTab.setupWithViewPager(viewPagerTwoTypeTab);
    }

    private void setUpViewPager(ViewPager viewPager) {
        //set up adapter
        StoryFollowedInTypeFavoriteFragment storyFollowedInTypeFavoriteFragment =
                StoryFollowedInTypeFavoriteFragment.newInstance();
        storyFollowedInTypeFavoriteFragment.setAddFragmentCallback(addFragmentCallback);
        updateUIFollowFirstChildTab = storyFollowedInTypeFavoriteFragment;

        ArticleBookmarkInTypeFavoriteFragment articleBookmarkInTypeFavoriteFragment =
                ArticleBookmarkInTypeFavoriteFragment.newInstance();
        articleBookmarkInTypeFavoriteFragment.setAddFragmentCallback(addFragmentCallback);
        updateUIBookmarkSecondChildTab = articleBookmarkInTypeFavoriteFragment;

        favoriteViewPagerAdapter = new FavoriteViewPagerAdapter(getChildFragmentManager(),
                storyFollowedInTypeFavoriteFragment,
                articleBookmarkInTypeFavoriteFragment);
        viewPager.setAdapter(favoriteViewPagerAdapter);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_favorite;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Chọc vào thằng con số 1 - tab đầu của nó, bắt nó update
        updateUIFollowFirstChildTab.updateUIFollow(isFollowed, idStory, stories);
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        updateUIBookmarkSecondChildTab.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_button_person_favorite_frg:
                //Mở màn settings
                startActivity(new Intent(getContext(), SettingsActivity.class));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            default:
                break;
        }
    }
}
