package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.ActionNetworkStateChange;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SettingsActivity;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.broadcastreceiver.NetworkChangeReceiver;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener {
    private static final String ARGS_U_ID_HOME_FRG = "ARGS_U_ID_HOME_FRG";

    //Các biến lưu trữ trạng thái mạng trước đó để bật tắt animation skeleton
    private static final String CONNECTED = "CONNECTED";
    private static final String DISCONNECTED = "DISCONNECTED";

    private RecyclerView recyclerViewHotNews;
    private ImageView imageNotifications;
    private Toolbar toolbar;
//    private TextView textNoNetwork;
    private ConstraintLayout constraintLayoutNoNetwork;
    private ImageView imageSettings;
    private ShimmerFrameLayout mShimmerViewContainer;
    private SwipeRefreshLayout swipeContainer;

    private AddFragmentCallback addFragmentCallback;
    private String uId;
    private HotNewsAdapter hotNewsAdapter;

    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng

    private ArrayList<Datum> arrayListDatum = new ArrayList<>(); //Phải có list tổng này để lưu cache xuống local
    private Context mContext;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
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
        imageSettings = view.findViewById(R.id.circle_button_person);
        imageNotifications = view.findViewById(R.id.iv_search);
        imageSettings.setOnClickListener(this);
        imageNotifications.setOnClickListener(this);

        constraintLayoutNoNetwork = view.findViewById(R.id.constraint_state_wifi_off_frg_home);
        constraintLayoutNoNetwork.setVisibility(View.GONE);
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
        hotNewsAdapter = new HotNewsAdapter(new ArrayList<>(), mContext, recyclerViewHotNews/*, getChildFragmentManager()*/,
                /*ConstGeneralTypeTab.TYPE_TAB_HOME, */addFragmentCallback);
        recyclerViewHotNews.setAdapter(hotNewsAdapter);

        //====get uId from args====
        Bundle bundle = new Bundle();
        uId = bundle.getString(ARGS_U_ID_HOME_FRG);


        //==============LOAD DATA================
        //load data đồng thời đổ dữ liệu lên RecyclerView
        //Có mạng
        String deviceId = ReadCacheTool.getDeviceId(mContext);
        loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, deviceId);
        //setUpLoadMore()
        setUpLoadMore(deviceId);
        swipeContainer.setOnRefreshListener(() -> loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, deviceId));
    }

    private void setUpLoadMore(String deviceID) {
//        if (hotNewsAdapter.isFlagFinishLoadData()) {
//            return;
//        }
        //Sự kiện loadMore()
        hotNewsAdapter.setLoadMore(() -> {
            hotNewsAdapter.addItemLoading();
            new Handler().postDelayed(() -> {
                hotNewsAdapter.removeItemLoading();
                loadData(LoadPageConst.LOAD_MORE_PAGE, deviceID);
            }, 2000); // Time to load
        });
    }

    private void loadData(int typeLoadData, String deviceId) {
        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
            if (oldStateNetWork.equals(DISCONNECTED)) {
                //Quay lại trực tuyến, cho mShimmerViewContainer.setVisibility(View.VISIBLE);
                //                mShimmerViewContainer.startShimmerAnimation(); chạy, hehe

                //Đây là nguyên nhân khi từ offline sang online thì có nháy skeleton
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

            String uId = ReadCacheTool.getUId(getContext());
            Call<News> call = apiService.getHotNews(String.valueOf(typeLoadData), deviceId, uId);

            call.enqueue(new Callback<News>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                    News news = response.body();
                    if (news == null) {
//                        Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "HomeFragment - Call API successfully, but data is null!");
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        swipeContainer.setRefreshing(false);
                        return;
                    }
//                    if (news.getDataSearchResult().size() == 0) {
//                        //Khi data nhận về == 0 thì chuyển flag = true, tức là sẽ không load tiếp nữa
//                        hotNewsAdapter.setFlagFinishLoadData(true);
//                    }
                    if (typeLoadData == LoadPageConst.LOAD_MORE_PAGE) {
                        if (/*Collections.disjoint(arrayListDatum, news.getDataSearchResult())*/!GeneralTool.checkIfParentHasChild(arrayListDatum, (ArrayList<Datum>) news.getData())) {
                            //List tổng có chứa list trả về hay ko? Nếu có thi thôi
                            //ko trùng thì vào trong này
                            arrayListDatum.addAll(news.getData());
                        }
                        hotNewsAdapter.updateListNews(news.getData()); //add all, nếu trùng cả danh sách thì thôi ko add nữa
                    } else if (typeLoadData == LoadPageConst.RELOAD_INIT_CURRENT_PAGE) {
                        if (!GeneralTool.checkIfParentHasChild(arrayListDatum, (ArrayList<Datum>) news.getData())/*arrayListDatum.equals(news.getDataSearchResult())*/) {
                            //List tổng ko chứa cả list con mới bắn về, thực hiện update tại đây
                            arrayListDatum.clear();
                            arrayListDatum.addAll(news.getData());
                        }
                        hotNewsAdapter.reloadInNormalState(news.getData()); //xóa hết đi rồi mới add
                    }
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                    ReadCacheTool.storeHotNews(getContext(), arrayListDatum);
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
//                    Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                }
            });
        } else {
            //Mất mạng
            arrayListDatum = ReadCacheTool.getListHotNews(getContext());
            if (arrayListDatum.size() != 0) {
                //Lấy list từ Local lên
                //Nếu ko trùng thì xóa sạch list đi, apply list từ offline lên
                hotNewsAdapter.reloadInNormalState(arrayListDatum);
            } else {
                actionDisableLoadBaseOnNetworkState(false);
            }
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
            oldStateNetWork = DISCONNECTED;
            swipeContainer.setRefreshing(false);
        }
    }

    private void actionDisableLoadBaseOnNetworkState(boolean state) {
        if (state) {
            //Có mạng
            //Bật hết các View lên
            recyclerViewHotNews.setVisibility(View.VISIBLE);
            constraintLayoutNoNetwork.setVisibility(View.GONE);
        } else {
            //Mất mạng
            //Bật hết các View lên
            recyclerViewHotNews.setVisibility(View.GONE);
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
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

//    @Override
//    public void actionWhenNetworkChange(String newState) {
//        if (newState.equals(NetworkChangeReceiver.CONNECTED)) {
//            //Cái này chỉ gọi khi nào quay lại trực tuyến
//        } else if (newState.equals(NetworkChangeReceiver.DISCONNECTED)) {
//            //Cái này gọi khi mất mạng
//        }
//    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Phải update lại biến isFollowed cho story bị thay đổi, cả trong LatestNews
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //Phải update lại biến bookmark cho bài báo bị thay đổi, cả trong LatestNews và DetailEvent
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        if (recyclerViewHotNews != null) {
            recyclerViewHotNews.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_button_person:
                //Mở màn settings
                startActivity(new Intent(mContext, SettingsActivity.class));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.iv_search:
                addFragmentCallback.addNotificationFragment();
                break;
            default:
                break;
        }
    }
}
