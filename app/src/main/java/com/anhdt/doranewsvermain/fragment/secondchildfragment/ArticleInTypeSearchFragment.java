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
import com.anhdt.doranewsvermain.model.searchresult.DataSearchResult;
import com.anhdt.doranewsvermain.model.searchresult.DatumSearchResult;
import com.anhdt.doranewsvermain.model.searchresult.EventSearchResult;
import com.anhdt.doranewsvermain.util.GeneralTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ArticleInTypeSearchFragment extends BaseFragmentNeedUpdateUI implements DisplaySearchResults {
    private static final int INIT_PAGE_SEARCH = 0;
    private AddFragmentCallback addFragmentCallback;

    private Context mContext;
    private RecyclerView recyclerView;
    private ArrayList<Article> articles;
    private ArticleItemAdapter articleItemAdapter;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static ArticleInTypeSearchFragment newInstance() {
        Bundle args = new Bundle();

        ArticleInTypeSearchFragment fragment = new ArticleInTypeSearchFragment();
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
        recyclerView = view.findViewById(R.id.recycler_frg_article_search);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        articles = new ArrayList<>();
        articleItemAdapter = new ArticleItemAdapter(mContext,
                articles, addFragmentCallback,
                null, ArticleItemAdapter.IN_HOME);
        recyclerView.setAdapter(articleItemAdapter);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_article_search;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {

    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        if (articleItemAdapter != null) {
            articleItemAdapter.updateUIBookmark(isBookmarked, idArticle, article);
        }
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }

    @Override
    public void displayArticleResults(String keywords, ProgressDialog dialog) {
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

        Call<News> call = apiService.searchArticles(requestBody);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                articleItemAdapter.clearList();
                News news = response.body();
                if (news == null) {
                    Toast.makeText(mContext, "Error: Call API successfully, but datumSearchResult is null!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                List<Datum> results = news.getData();
                if (results == null) {
                    Toast.makeText(mContext, "Không tìm được bài viết nào!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (results.size() == 0) {
                    Toast.makeText(mContext, "Không tìm được bài viết nào!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                Log.e("hhi-", results.toString());
                //XXX-Tạm thời chỗ này nhé
                List<Article> articles = new ArrayList<>();
                for (Datum datum : results) {
                    if (datum.getType() == TypeNewsConst.ARTICLE) {
                        articles.add(datum.getArticle());
                    }
                }
                articleItemAdapter.clearAndReloadInSearching(articles);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                articleItemAdapter.clearList();
                Toast.makeText(mContext, "Fail to search data!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void displayEventResults(String keywords, ProgressDialog dialog) {
        //do nothing
    }
}
