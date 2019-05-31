package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.SplashActivity;
import com.anhdt.doranewsvermain.adapter.recyclerview.SearchResultAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.StoryFollowedAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.searchresult.DataSearchResult;
import com.anhdt.doranewsvermain.model.searchresult.DatumSearchResult;
import com.anhdt.doranewsvermain.model.searchresult.EventSearchResult;
import com.anhdt.doranewsvermain.model.userresult.UserResult;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.facebook.shimmer.ShimmerFrameLayout;

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

public class SearchFragment extends BaseFragmentNeedUpdateUI implements SearchView.OnQueryTextListener {
    private static final String CONNECTED = "CONNECTED";
    private static final String DISCONNECTED = "DISCONNECTED";

    private AddFragmentCallback addFragmentCallback;

    private Context mContext;
    private FragmentActivity fragmentActivity;

    private RecyclerView recyclerViewSearchResult;
    private SearchView searchView;


    private SearchResultAdapter searchResultAdapter;
    private ProgressDialog dialog;

    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng

    private ShimmerFrameLayout mShimmerViewContainer;
    private TextView textNoNetwork;
    private SwipeRefreshLayout swipeContainer;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public SearchFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
        fragmentActivity = getActivity();
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        textNoNetwork = view.findViewById(R.id.text_no_network_frg_search);
        textNoNetwork.setVisibility(View.GONE);
        swipeContainer = view.findViewById(R.id.swipe_container_frg_search);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Searching..");
        dialog.setCancelable(false);

        recyclerViewSearchResult = view.findViewById(R.id.recycler_frg_search);
        recyclerViewSearchResult.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchResult.setLayoutManager(linearLayoutManager);

        searchResultAdapter = new SearchResultAdapter(mContext, new ArrayList<>(), addFragmentCallback);
        recyclerViewSearchResult.setAdapter(searchResultAdapter);

        searchView = view.findViewById(R.id.sv_search_frg_search);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.clearFocus();
        searchView.setOnQueryTextListener(this);

        swipeContainer.setOnRefreshListener(() -> {
            String query = searchView.getQuery().toString();
            if (!query.equals("")) {
                search(query);
            } else {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void actionDisableLoadBaseOnNetworkState(boolean state) {
        if (state) {
            //Có mạng
            //Bật hết các View lên
            recyclerViewSearchResult.setVisibility(View.VISIBLE);
            textNoNetwork.setVisibility(View.GONE);
        } else {
            //Mất mạng
            //Bật hết các View lên
            recyclerViewSearchResult.setVisibility(View.GONE);
            textNoNetwork.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //do nothing
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //do nothing
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_search;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Query search
        if (fragmentActivity == null) {
            searchView.clearFocus();
            return false;
        }
        View view = fragmentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (query != null) {
            if (!query.equals("")) {
                search(query);
                searchView.clearFocus();
                return false;
            } else {
                searchView.clearFocus();
                return false;
            }
        } else {
            searchView.clearFocus();
            return false;
        }
    }

    private void search(String query) {
        //Gửi thông tin lên server
        dialog.show();
        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
            if (oldStateNetWork.equals(DISCONNECTED)) {
                //Nếu trạng thái trước đó là Disconnected thì bật lại shimmer
//                mShimmerViewContainer.setVisibility(View.VISIBLE);
//                mShimmerViewContainer.startShimmerAnimation();
                oldStateNetWork = CONNECTED;
            }
            actionDisableLoadBaseOnNetworkState(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.URL_SEARCH_POST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            ServerAPI apiService = retrofit.create(ServerAPI.class);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("keywords", query)
                    .build();

            Call<DatumSearchResult> call = apiService.searchEvent(requestBody);
            call.enqueue(new Callback<DatumSearchResult>() {
                @Override
                public void onResponse(Call<DatumSearchResult> call, Response<DatumSearchResult> response) {
                    DatumSearchResult datumSearchResult = response.body();

                    if (datumSearchResult == null) {
                        Toast.makeText(mContext, "Error: Call API successfully, but datumSearchResult is null!", Toast.LENGTH_SHORT).show();
                        swipeContainer.setRefreshing(false);
                        dialog.dismiss();
                        return;
                    }
                    DataSearchResult dataSearchResult = datumSearchResult.getDataSearchResult();
                    if (dataSearchResult == null) {
                        Toast.makeText(mContext, "Error: Call API successfully, but dataSearchResult is null!", Toast.LENGTH_SHORT).show();
                        swipeContainer.setRefreshing(false);
                        dialog.dismiss();
                        return;
                    }
                    List<EventSearchResult> eventSearchResults = dataSearchResult.getEventSearchResults();
                    if (eventSearchResults == null) {
                        Toast.makeText(mContext, "Error: Call API successfully, but list eventSearchResults is null!", Toast.LENGTH_SHORT).show();
                        swipeContainer.setRefreshing(false);
                        dialog.dismiss();
                        return;
                    }

                    if (eventSearchResults.size() == 0) {
                        Toast.makeText(mContext, "Không tìm được sự kiện, bài viết nào!", Toast.LENGTH_SHORT).show();
                        swipeContainer.setRefreshing(false);
                        dialog.dismiss();
                        return;
                    }
                    searchResultAdapter.updateListResults(eventSearchResults);
                    swipeContainer.setRefreshing(false);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<DatumSearchResult> call, Throwable t) {
                    Toast.makeText(mContext, "Fail to search data!", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                    dialog.dismiss();
                }
            });
        } else {
            //Mất mạng
            actionDisableLoadBaseOnNetworkState(false);
//            mShimmerViewContainer.setVisibility(View.GONE);
//            mShimmerViewContainer.stopShimmerAnimation();
            oldStateNetWork = DISCONNECTED;
            swipeContainer.setRefreshing(false);
            dialog.dismiss();
        }
    }

    public void showOrHideListResult(boolean isShowed) {
        //true --> show, false --> hide
        if (isShowed) {

        } else {

        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
