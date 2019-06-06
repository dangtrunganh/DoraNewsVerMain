package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SettingsActivity;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.NotificationAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.util.ReadRealmToolForNotification;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationFragment extends BaseFragmentNeedUpdateUI implements UpdateListNotification, View.OnClickListener {
    private RecyclerView recyclerViewNotification;
    private SwipeRefreshLayout swipeContainer;
    private ImageView imageSettings;
    private ConstraintLayout constraintLayoutNoNetwork;
    private ArrayList<NotificationResult> arrayListNotifications;

    private NotificationAdapter notificationAdapter;

    private AddFragmentCallback addFragmentCallback;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance() {
        Bundle args = new Bundle();

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        constraintLayoutNoNetwork = view.findViewById(R.id.constraint_state_wifi_off_frg_notification);
//        constraintLayoutNoNetwork.setVisibility(View.GONE);
        imageSettings = view.findViewById(R.id.circle_button_person_frg_notification);
        imageSettings.setOnClickListener(this);
        swipeContainer = view.findViewById(R.id.swipe_container_frg_notification);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(() -> swipeContainer.setRefreshing(false));

        recyclerViewNotification = view.findViewById(R.id.recycler_frg_notification);
        recyclerViewNotification.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewNotification.setLayoutManager(linearLayoutManager);

        //===Adapter====LoadData======
        arrayListNotifications = ReadRealmToolForNotification.getListNotificationInLocal(getContext());
        if (arrayListNotifications.size() == 0) {
            //Hiển thị màn hình chưa có thông báo nào
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
        } else {
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);
        }
        notificationAdapter = new NotificationAdapter(getContext(), arrayListNotifications, addFragmentCallback, this);
        recyclerViewNotification.setAdapter(notificationAdapter);
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Không có gì để update
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //Không có gì để update
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_notification;
    }

    @Override
    public void addNotification(NotificationResult notificationResult) {
//        Log.e("y9y-", notificationResult.toString());
        notificationAdapter.addNewNotifications(notificationResult);
        if (notificationAdapter.getArrayNotifications().size() == 1) {
            //Vì luôn có ít nhất một phần tử null làm footer
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
        } else {
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void removeNotification(String idNotification) {
        notificationAdapter.removeNotification(idNotification);
        if (notificationAdapter.getArrayNotifications().size() == 1) {
            //Vì luôn có ít nhất một phần tử null làm footer
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
        } else {
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_button_person_frg_notification:
                //Mở màn settings
                startActivity(new Intent(getContext(), SettingsActivity.class));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            default:
                break;
        }
    }
}
