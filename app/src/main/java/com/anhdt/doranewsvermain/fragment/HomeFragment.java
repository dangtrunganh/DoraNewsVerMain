package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends BaseFragment {
    private static final String ARGS_U_ID_HOME_FRG = "ARGS_U_ID_HOME_FRG";
    private RecyclerView recyclerViewHotNews;

    private Toolbar toolbar;
    private AddFragmentCallback addFragmentCallback;

    private String uId;
//    private FragmentManager fragmentManager;

//    private ArrayList<Datum> arrayDatum;

    //    public void setFragmentManager(FragmentManager fragmentManager) {
//        this.fragmentManager = fragmentManager;
//    }
    private ShimmerFrameLayout mShimmerViewContainer;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    private HotNewsAdapter hotNewsAdapter;

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
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        toolbar = view.findViewById(R.id.actionBar);

        recyclerViewHotNews = view.findViewById(R.id.recycler_hot_news);
        recyclerViewHotNews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewHotNews.setLayoutManager(linearLayoutManager);

        //====Adapter====
        hotNewsAdapter = new HotNewsAdapter(new ArrayList<>(), getContext(), recyclerViewHotNews, getChildFragmentManager(),
                ConstGeneralTypeTab.TYPE_TAB_HOME, addFragmentCallback);
        recyclerViewHotNews.setAdapter(hotNewsAdapter);

        //====get uId from args====
        Bundle bundle = new Bundle();
        uId = bundle.getString(ARGS_U_ID_HOME_FRG);

        //load data đồng thời đổ dữ liệu lên RecyclerView
        loadData(LoadPageConst.RELOAD_INIT_CURRENT_PAGE, uId);

        //setUpLoadMore()
        setUpLoadMore();
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
                    return;
                }

                if (news.getData().size() < HotNewsAdapter.VISIBLE_THRESHOLD) {
                    //Khi data nhận về nhỏ có size nhỏ hơn Threshold thì chuyển flag = true, tức là sẽ không load tiếp nữa
                    //Nếu bằng thì tức là load đúng size buffer = 3, tức là vẫn còn mà
                    hotNewsAdapter.setFlagFinishLoadData(true);
                }

//                Log.e("pp-", String.valueOf(news.getData().size()));
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

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initProgressbar() {
        //hiển thị progressBar ở đây
    }
}
