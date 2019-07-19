package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SettingsActivity;
import com.anhdt.doranewsvermain.adapter.recyclerview.NotificationAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.notificationresult.DataNotification;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForNotification;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotificationFragment extends BaseFragmentNeedUpdateUI implements UpdateListNotification, View.OnClickListener {
    private RecyclerView recyclerViewNotification;
    private SwipeRefreshLayout swipeContainer;
    private ImageView imageSettings;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ConstraintLayout constraintLayoutNoNetwork;
    private ArrayList<NotificationResult> arrayListNotifications;
    private ArrayList<NotificationResult> arrayListNotificationsFromAPI;

    private Context mContext;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        Log.d("main-ff", "onPause()");
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container_frg_notification);
        constraintLayoutNoNetwork = view.findViewById(R.id.constraint_state_wifi_off_frg_notification);
//        constraintLayoutNoNetwork.setVisibility(View.GONE);
        imageSettings = view.findViewById(R.id.circle_button_person_frg_notification);
        imageSettings.setOnClickListener(this);
        swipeContainer = view.findViewById(R.id.swipe_container_frg_notification);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        recyclerViewNotification = view.findViewById(R.id.recycler_frg_notification);
        recyclerViewNotification.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewNotification.setLayoutManager(linearLayoutManager);

        //===Adapter====LoadData======
//        mContext = getContext();

//        arrayListNotifications = ReadRealmToolForNotification.getListNotificationInLocal(getContext());
        arrayListNotifications = ReadCacheTool.getRealListNotificationInLocal(mContext);

        String uId = ReadCacheTool.getUId(Objects.requireNonNull(getContext()));
        swipeContainer.setOnRefreshListener(() -> loadDataFromAPI(uId));
        loadDataFromAPI(uId);
//        notificationAdapter = new NotificationAdapter(getContext(), arrayListNotifications, addFragmentCallback, this);
//        recyclerViewNotification.setAdapter(notificationAdapter);
    }

    private void showView(boolean isShowed) {
        if (isShowed) {
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);
            Log.e("kpl-", "isShowed");
        } else {
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
            Log.e("kpl-", "is not showed");
        }
    }

    private void loadDataFromAPI(String uId) {
        //Đọc list notification từ server
        arrayListNotificationsFromAPI = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ServerAPI apiService = retrofit.create(ServerAPI.class);
        Call<DataNotification> call = apiService.getListNotifications(uId);
        call.enqueue(new Callback<DataNotification>() {
            @Override
            public void onResponse(Call<DataNotification> call, Response<DataNotification> response) {
                DataNotification dataNotification = response.body();
                if (dataNotification == null) {
                    swipeContainer.setRefreshing(false);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (arrayListNotifications.size() == 0) {
                        showView(false);
                    } else {
                        showView(true);
                    }
                    return;
                }
                arrayListNotificationsFromAPI = dataNotification.getArrayNotifications();
                if (arrayListNotificationsFromAPI == null) {
                    if (arrayListNotifications.size() > 0) {
                        showView(true);
                        notificationAdapter = new NotificationAdapter(getContext(), arrayListNotifications, addFragmentCallback, NotificationFragment.this);
                        recyclerViewNotification.setAdapter(notificationAdapter);
                    } else {
                        showView(false);
                    }
                    swipeContainer.setRefreshing(false);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    return;
                }
                if (arrayListNotificationsFromAPI.size() == 0) {
                    if (arrayListNotifications.size() > 0) {
                        showView(true);
                        notificationAdapter = new NotificationAdapter(getContext(), arrayListNotifications, addFragmentCallback, NotificationFragment.this);
                        recyclerViewNotification.setAdapter(notificationAdapter);
                    } else {
                        showView(false);
                    }
                    swipeContainer.setRefreshing(false);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    return;
                }
                //Check trùng???
                //List này đã khác rỗng, thực hiện lưu xuống database
                showView(true);
//                ReadRealmToolForNotification.addListNotificationToRealm(getContext(), arrayListNotificationsFromAPI, arrayListNotifications);
                Log.e("kpl-", arrayListNotificationsFromAPI.toString());
                ReadCacheTool.storeNotification(mContext, arrayListNotificationsFromAPI);
                notificationAdapter = new NotificationAdapter(getContext(), arrayListNotificationsFromAPI, addFragmentCallback, NotificationFragment.this);
                recyclerViewNotification.setAdapter(notificationAdapter);
                swipeContainer.setRefreshing(false);
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DataNotification> call, Throwable t) {
                Log.e("Error", "When get data");
                swipeContainer.setRefreshing(false);
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                if (arrayListNotifications.size() == 0) {
                    showView(false);
                } else {
                    showView(true);
                    notificationAdapter = new NotificationAdapter(getContext(), arrayListNotifications, addFragmentCallback, NotificationFragment.this);
                    recyclerViewNotification.setAdapter(notificationAdapter);
                }
                return;
            }
        });
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
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //nothing
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
