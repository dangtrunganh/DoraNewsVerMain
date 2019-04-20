package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsInCategoryFragment extends BaseFragment {
    public static final String PARAM_CATEGORY_NEWS_IN_CATEGORY_FRG = "PARAM_CATEGORY_NEWS_IN_CATEGORY_FRG";
    public static final String PARAM_U_ID_NEWS_IN_CATEGORY_FRG = "PARAM_U_ID_NEWS_IN_CATEGORY_FRG";

    private static final String CONNECTED = "CONNECTED";
    private static final String DISCONNECTED = "DISCONNECTED";

    private RecyclerView recyclerViewListNewsInCategory;
    private SwipeRefreshLayout swipeContainer;
    private TextView textNoNetwork;
    private Category currentCategory;
    private HotNewsAdapter hotNewsAdapter;
    private FragmentManager fragmentManager;
    private String uId;
    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng
    private ShimmerFrameLayout mShimmerViewContainer;

    private AddFragmentCallback addFragmentCallback;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public NewsInCategoryFragment() {

    }

    public static NewsInCategoryFragment newInstance(String jsonCategory, String uId) {
        NewsInCategoryFragment newsInCategoryFrament = new NewsInCategoryFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_CATEGORY_NEWS_IN_CATEGORY_FRG, jsonCategory);
        args.putString(PARAM_U_ID_NEWS_IN_CATEGORY_FRG, uId);
        newsInCategoryFrament.setArguments(args);
        return newsInCategoryFrament;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
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
        textNoNetwork = view.findViewById(R.id.text_no_network_news_in_category);
        textNoNetwork.setVisibility(View.GONE);
        swipeContainer = view.findViewById(R.id.swipe_container_news_in_category);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container_news_in_category);

        currentCategory = getCurrentCategoryFromBundle();
        if (currentCategory == null) {
            return;
        }

        Bundle bundle = getArguments();
        uId = bundle.getString(PARAM_U_ID_NEWS_IN_CATEGORY_FRG);

        //=====Setup RecycleView - List news in each category=====
        recyclerViewListNewsInCategory = view.findViewById(R.id.recycler_news_in_category);
        recyclerViewListNewsInCategory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewListNewsInCategory.setLayoutManager(linearLayoutManager);

        hotNewsAdapter = new HotNewsAdapter(new ArrayList<>(), getContext(), recyclerViewListNewsInCategory, getActivity().getSupportFragmentManager(),
                ConstGeneralTypeTab.TYPE_TAB_LATEST_HOME, addFragmentCallback);
        recyclerViewListNewsInCategory.setAdapter(hotNewsAdapter);

        //load data đồng thời đổ dữ liệu lên RecyclerView
        loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, currentCategory.getId(), uId);

        //setUpLoadMore()
        setUpLoadMore(currentCategory.getId());
        swipeContainer.setOnRefreshListener(() -> {
            loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, currentCategory.getId(), uId);
        });
    }

    private void setUpLoadMore(String idCategory) {
        if (hotNewsAdapter.isFlagFinishLoadData()) {
            return;
        }
        //Sự kiện loadMore()
        hotNewsAdapter.setLoadMore(() -> {
            hotNewsAdapter.addItemLoading();
            new Handler().postDelayed(() -> {
                hotNewsAdapter.removeItemLoading();
                loadData(LoadPageConst.LOAD_MORE_PAGE, idCategory, uId);
            }, 2000); // Time out to load
        });
    }

    private void loadData(int typeLoadData, String idCategory, String deviceId) {
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

            Call<News> call = apiService.getNewsInEachCategory(String.valueOf(typeLoadData),
                    deviceId,
                    idCategory
            );

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
            recyclerViewListNewsInCategory.setVisibility(View.VISIBLE);
            textNoNetwork.setVisibility(View.GONE);
        } else {
            //Mất mạng
            //Bật hết các View lên
            recyclerViewListNewsInCategory.setVisibility(View.GONE);
            textNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    private Category getCurrentCategoryFromBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return null;
        }
        String jsonCurrentCategory = bundle.getString(PARAM_CATEGORY_NEWS_IN_CATEGORY_FRG);
        if (jsonCurrentCategory == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonCurrentCategory, new TypeToken<Category>() {
        }.getType());
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_news_in_category;
    }

    @Override
    protected void initProgressbar() {

    }
}
