package com.anhdt.doranewsvermain.fragment.secondchildfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.StoryFollowedAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseNormalFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
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

public class StoryFollowedInTypeFavoriteFragment extends BaseFragmentNeedUpdateUI {
    private static final String CONNECTED = "CONNECTED";
    private static final String DISCONNECTED = "DISCONNECTED";

    private AddFragmentCallback addFragmentCallback;

    private Context mContext;

    private RecyclerView recyclerViewStoriesFollowed;
    private StoryFollowedAdapter storyFollowedAdapter;

    private String uId;

    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng
    private ShimmerFrameLayout mShimmerViewContainer;
    private TextView textNoNetwork;
    private SwipeRefreshLayout swipeContainer;

    //============
    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public StoryFollowedInTypeFavoriteFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    public static StoryFollowedInTypeFavoriteFragment newInstance() {

        Bundle args = new Bundle();

        StoryFollowedInTypeFavoriteFragment fragment = new StoryFollowedInTypeFavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container_frg_story_followed);
        textNoNetwork = view.findViewById(R.id.text_no_network_frg_story_followed);
        textNoNetwork.setVisibility(View.GONE);
        swipeContainer = view.findViewById(R.id.swipe_container_frg_story_followed);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerViewStoriesFollowed = view.findViewById(R.id.recycler_frg_story_followed);
        recyclerViewStoriesFollowed.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewStoriesFollowed.setLayoutManager(linearLayoutManager);

        storyFollowedAdapter = new StoryFollowedAdapter(mContext, new ArrayList<>(), addFragmentCallback);
        recyclerViewStoriesFollowed.setAdapter(storyFollowedAdapter);

        this.uId = ReadCacheTool.getUId(mContext);
        if (uId == null) {
            return;
        }
        loadData(uId);
        swipeContainer.setOnRefreshListener(() -> {
            loadData(uId);
        });
    }

    private void loadData(String uId) {
        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
            if (oldStateNetWork.equals(DISCONNECTED)) {
                //Nếu trạng thái trước đó là Disconnected thì bật lại shimmer
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                mShimmerViewContainer.startShimmerAnimation();
                oldStateNetWork = CONNECTED;
            }
            actionDisableLoadBaseOnNetworkState(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final ServerAPI apiService = retrofit.create(ServerAPI.class);
            Call<News> call = apiService.getListStoriesFollowed(uId);
            call.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    News news = response.body();
                    if (news == null) {
//                        Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        swipeContainer.setRefreshing(false);
                        return;
                    }
                    storyFollowedAdapter.updateListNews(news.getData());

                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    Toast.makeText(mContext, "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                }
            });
        } else {
            //Mất mạng
            actionDisableLoadBaseOnNetworkState(false);
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmerAnimation();
            oldStateNetWork = DISCONNECTED;
            swipeContainer.setRefreshing(false);
        }
    }

    private void actionDisableLoadBaseOnNetworkState(boolean state) {
        if (state) {
            //Có mạng
            //Bật hết các View lên
            recyclerViewStoriesFollowed.setVisibility(View.VISIBLE);
            textNoNetwork.setVisibility(View.GONE);
        } else {
            //Mất mạng
            //Bật hết các View lên
            recyclerViewStoriesFollowed.setVisibility(View.GONE);
            textNoNetwork.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_story_followed;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //true - theo dõi
        if (isFollowed) {
            //Theo dõi - thêm một story mới vào list theo dõi
            storyFollowedAdapter.addNewStoryFollowed(stories);
        } else {
            //Hủy theo dõi, bỏ ra khỏi list
            storyFollowedAdapter.removeStoryFollowed(idStory);
        }
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //do nothing
    }
}
