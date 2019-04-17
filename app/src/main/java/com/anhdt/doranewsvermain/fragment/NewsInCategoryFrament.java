package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstGeneral;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsInCategoryFrament extends BaseFragment {
    public static final String PARAM_CATEGORY_NEWS_IN_CATEGORY_FRG = "PARAM_CATEGORY_NEWS_IN_CATEGORY_FRG";
    public static final String PARAM_U_ID_NEWS_IN_CATEGORY_FRG = "PARAM_U_ID_NEWS_IN_CATEGORY_FRG";

    private RecyclerView recyclerViewListNewsInCategory;
    private Category currentCategory;
    private HotNewsAdapter hotNewsAdapter;
    private FragmentManager fragmentManager;
    private String uId;
    private ShimmerFrameLayout mShimmerViewContainer;

    private AddFragmentCallback addFragmentCallback;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public NewsInCategoryFrament() {

    }

    public static NewsInCategoryFrament newInstance(String jsonCategory, String uId) {
        NewsInCategoryFrament newsInCategoryFrament = new NewsInCategoryFrament();
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
                ConstGeneral.TYPE_TAB_LATEST_HOME, addFragmentCallback);
        recyclerViewListNewsInCategory.setAdapter(hotNewsAdapter);


        //load data đồng thời đổ dữ liệu lên RecyclerView
        loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, currentCategory.getId(), uId);

        //setUpLoadMore()
        setUpLoadMore(currentCategory.getId());
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
                    return;
                }

                if (news.getData().size() < HotNewsAdapter.VISIBLE_THRESHOLD) {
                    //Khi data nhận về nhỏ có size nhỏ hơn Threshold thì chuyển flag = true, tức là sẽ không load tiếp nữa
                    //Nếu bằng thì tức là load đúng size buffer = 3, tức là vẫn còn mà
                    hotNewsAdapter.setFlagFinishLoadData(true);
                }

                hotNewsAdapter.updateListNews(news.getData());
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
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
