package com.anhdt.doranewsvermain.fragment.secondchildfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.ArticleItemAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.HotNewsAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.DisplaySearchResults;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.model.newsresult.Stories;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class EventInTypeSearchFragment extends BaseFragmentNeedUpdateUI implements DisplaySearchResults {
    private static final int INIT_PAGE_SEARCH = 0;
    private AddFragmentCallback addFragmentCallback;

    private Context mContext;
    private RecyclerView recyclerView;
    private HotNewsAdapter hotNewsAdapter;
    private ArrayList<Datum> listDatums;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static EventInTypeSearchFragment newInstance() {
        Bundle args = new Bundle();

        EventInTypeSearchFragment fragment = new EventInTypeSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        recyclerView = view.findViewById(R.id.recycler_frg_event_search);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        listDatums = new ArrayList<>();
        hotNewsAdapter = new HotNewsAdapter(listDatums, mContext, recyclerView, addFragmentCallback);
        recyclerView.setAdapter(hotNewsAdapter);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_event_search;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void displayArticleResults(String keywords, ProgressDialog dialog) {
        //do nothing
    }

    @Override
    public void displayEventResults(String keywords, ProgressDialog dialog) {
        //search articles by keywords
        //Gửi thông tin lên server
//        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ServerAPI apiService = retrofit.create(ServerAPI.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("keywords", keywords)
                .addFormDataPart("page", String.valueOf(INIT_PAGE_SEARCH))
                .build();

        Call<News> call = apiService.searchEvents(requestBody);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                hotNewsAdapter.clearList();
                News news = response.body();
                if (news == null) {
                    Toast.makeText(mContext, "Error: Call API successfully, but datumSearchResult is null!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                List<Datum> results = news.getData();
                if (results == null) {
                    Toast.makeText(mContext, "Không tìm được sự kiện nào!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (results.size() == 0) {
                    Toast.makeText(mContext, "Không tìm được sự kiện nào!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                hotNewsAdapter.reloadInNormalState(results);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                hotNewsAdapter.clearList();
                Toast.makeText(mContext, "Fail to search data!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {

    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {

    }
}
