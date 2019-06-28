package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SettingsActivity;
import com.anhdt.doranewsvermain.adapter.recyclerview.NotificationAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.VideoAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.model.videotest.CreateVideoData;
import com.anhdt.doranewsvermain.model.videotest.Video;
import com.anhdt.doranewsvermain.util.ReadRealmToolForNotification;

import java.util.ArrayList;
import java.util.Objects;

public class VideoFragment extends BaseFragmentNeedUpdateUI implements UpdateListNotification, View.OnClickListener {
    private RecyclerView recyclerViewVideos;
    private SwipeRefreshLayout swipeContainer;
    private ImageView imageSettings;
    private ImageView imageNotifications;
    private ConstraintLayout constraintLayoutNoNetwork;
    private ArrayList<Video> arrayListVideos;

    private VideoAdapter videoAdapter;

    private AddFragmentCallback addFragmentCallback;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public VideoFragment() {
    }

    public static VideoFragment newInstance() {
        Bundle args = new Bundle();

        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        constraintLayoutNoNetwork = view.findViewById(R.id.constraint_state_wifi_off_frg_video);
//        constraintLayoutNoNetwork.setVisibility(View.GONE);
        imageSettings = view.findViewById(R.id.circle_button_person_frg_video);
        imageSettings.setOnClickListener(this);
        imageNotifications = view.findViewById(R.id.iv_search_frg_video);
        imageNotifications.setOnClickListener(this);
        swipeContainer = view.findViewById(R.id.swipe_container_frg_video);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(() -> swipeContainer.setRefreshing(false));

        recyclerViewVideos = view.findViewById(R.id.recycler_frg_video);
        recyclerViewVideos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewVideos.setLayoutManager(linearLayoutManager);

        //===Adapter====LoadData======

        arrayListVideos = CreateVideoData.createVideos();
        if (arrayListVideos.size() == 0) {
            //Hiển thị màn hình chưa có thông báo nào
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
            recyclerViewVideos.setVisibility(View.GONE);
        } else {
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            recyclerViewVideos.setVisibility(View.VISIBLE);
        }
        videoAdapter = new VideoAdapter(getContext(), arrayListVideos, addFragmentCallback, this);
        recyclerViewVideos.setAdapter(videoAdapter);
    }

    @Override
    protected void initProgressbar() {
        //do nothing
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Không có gì để update
        //do nothing
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //Không có gì để update
        //do nothing
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_video;
    }

    @Override
    public void addNotification(NotificationResult notificationResult) {
        //do nothing
    }

    @Override
    public void removeNotification(String idNotification) {
        //do nothing
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_button_person_frg_video:
                //Mở màn settings
                startActivity(new Intent(getContext(), SettingsActivity.class));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.iv_search_frg_video:
                addFragmentCallback.addNotificationFragment();
                break;
            default:
                break;
        }
    }
}
