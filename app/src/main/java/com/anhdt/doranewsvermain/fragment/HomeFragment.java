package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends BaseFragment {
    private RecyclerView recyclerViewHotNews;

    private ArrayList<Datum> arrayDatum;

    private HotNewsAdapter hotNewsAdapter;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(PARAM_CATEGORY_SLUG, categoryJsonString);
//        args.putString(PARAM_CATEGORY_NAME, categoryName);
//        args.putBoolean(PARAM_HOT_EVENT, isHot);
//        genreFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        recyclerViewHotNews = view.findViewById(R.id.recycler_hot_news);
        recyclerViewHotNews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewHotNews.setLayoutManager(linearLayoutManager);

        arrayDatum = new ArrayList<>();


        //load data đồng thời đổ dữ liệu lên RecyclerView
        loadData();
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServerAPI apiService = retrofit.create(ServerAPI.class);

        Call<News> call = apiService.getHotNews(String.valueOf(LoadPageConst.RELOAD_INIT_CURRENT_PAGE), "");

        call.enqueue(new Callback<News>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                News news = response.body();
                if (news == null) {
                    Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //main data trong news, key "data:"
                arrayDatum = (ArrayList<Datum>) news.getData();
//                ArrayList<Datum> newArray = new ArrayList<>();
//                newArray.addAll(arrayDatum);
//                newArray.addAll(arrayDatum);
                hotNewsAdapter = new HotNewsAdapter(arrayDatum, getContext());
                recyclerViewHotNews.setAdapter(hotNewsAdapter);
                hotNewsAdapter.notifyDataSetChanged();
                //load data to recyclerView
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
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
