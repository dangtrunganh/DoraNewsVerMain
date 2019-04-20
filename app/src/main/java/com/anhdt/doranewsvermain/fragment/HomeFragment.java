package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.ActionNetworkStateChange;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.broadcastreceiver.NetworkChangeReceiver;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollow;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends BaseFragmentNeedUpdateUI implements ActionNetworkStateChange, UpdateUIFollow {
    private static final String ARGS_U_ID_HOME_FRG = "ARGS_U_ID_HOME_FRG";

    //Các biến lưu trữ trạng thái mạng trước đó để bật tắt animation skeleton
    private static final String CONNECTED = "CONNECTED";
    private static final String DISCONNECTED = "DISCONNECTED";

    private RecyclerView recyclerViewHotNews;
    private Toolbar toolbar;
    private TextView textNoNetwork;
    private ShimmerFrameLayout mShimmerViewContainer;
    private SwipeRefreshLayout swipeContainer;

    private AddFragmentCallback addFragmentCallback;
    private String uId;
    private HotNewsAdapter hotNewsAdapter;


    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String uId) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_U_ID_HOME_FRG, uId);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        textNoNetwork = view.findViewById(R.id.text_no_network);
        textNoNetwork.setVisibility(View.GONE);
        swipeContainer = view.findViewById(R.id.swipe_container_frg_home);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        toolbar = view.findViewById(R.id.actionBar);

        recyclerViewHotNews = view.findViewById(R.id.recycler_hot_news);
        recyclerViewHotNews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewHotNews.setLayoutManager(linearLayoutManager);

        //====Adapter====
        hotNewsAdapter = new HotNewsAdapter(new ArrayList<>(), getContext(), recyclerViewHotNews, getChildFragmentManager(),
                ConstGeneralTypeTab.TYPE_TAB_HOME, addFragmentCallback);
        recyclerViewHotNews.setAdapter(hotNewsAdapter);

        //====get uId from args====
        Bundle bundle = new Bundle();
        uId = bundle.getString(ARGS_U_ID_HOME_FRG);


        //==============LOAD DATA================
        //load data đồng thời đổ dữ liệu lên RecyclerView
        //Có mạng
        loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, uId);
        //setUpLoadMore()
        setUpLoadMore();
        swipeContainer.setOnRefreshListener(() -> {
            loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, uId);
        });
    }

    private void setUpLoadMore() {
        if (hotNewsAdapter.isFlagFinishLoadData()) {
            return;
        }
        //Sự kiện loadMore()
        hotNewsAdapter.setLoadMore(() -> {
            hotNewsAdapter.addItemLoading();
            new Handler().postDelayed(() -> {
                hotNewsAdapter.removeItemLoading();
                loadData(LoadPageConst.LOAD_MORE_PAGE, uId);
            }, 2000); // Time to load
        });
    }

    private void loadData(int typeLoadData, String deviceId) {
        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
            if (oldStateNetWork.equals(DISCONNECTED)) {
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                mShimmerViewContainer.startShimmerAnimation();
                oldStateNetWork = CONNECTED;
            }
            actionDisableLoadBaseOnNetworkState(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final ServerAPI apiService = retrofit.create(ServerAPI.class);

            Call<News> call = apiService.getHotNews(String.valueOf(typeLoadData), deviceId);

            call.enqueue(new Callback<News>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                    News news = response.body();
                    if (news == null) {
                        Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        swipeContainer.setRefreshing(false);
                        return;
                    }
                    if (news.getData().size() == 0) {
                        //Khi data nhận về == 0 thì chuyển flag = true, tức là sẽ không load tiếp nữa
                        hotNewsAdapter.setFlagFinishLoadData(true);
                    }
                    hotNewsAdapter.updateListNews(news.getData());
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                }
            });
        } else {
            //Mất mạng
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmerAnimation();
            oldStateNetWork = DISCONNECTED;
            actionDisableLoadBaseOnNetworkState(false);
            swipeContainer.setRefreshing(false);
        }

    }

    private void actionDisableLoadBaseOnNetworkState(boolean state) {
        if (state) {
            //Có mạng
            //Bật hết các View lên
            recyclerViewHotNews.setVisibility(View.VISIBLE);
            textNoNetwork.setVisibility(View.GONE);
        } else {
            //Mất mạng
            //Bật hết các View lên
            recyclerViewHotNews.setVisibility(View.GONE);
            textNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initProgressbar() {
        //hiển thị progressBar ở đây
    }

    @Override
    public void actionWhenNetworkChange(String newState) {
        if (newState.equals(NetworkChangeReceiver.CONNECTED)) {
            //Cái này chỉ gọi khi nào quay lại trực tuyến
        } else if (newState.equals(NetworkChangeReceiver.DISCONNECTED)) {
            //Cái này gọi khi mất mạng
        }
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory) {
        //Không làm gì
    }
}
