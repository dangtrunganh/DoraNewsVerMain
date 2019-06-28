package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.VideoInDetailVideoAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.videotest.Video;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailVideoFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener {
    private static String ARG_VIDEO = "ARG_VIDEO";
    private static String ARG_LIST_VIDEO = "ARG_LIST_VIDEO";
    private Toolbar mToolbar;
    private RecyclerView recyclerViewVideos;
    private ImageView imageShowDescription;
    private ConstraintLayout constraintLayoutDescription;
    private AddFragmentCallback addFragmentCallback;

    private TextView textTitle;
    private TextView textNumberOfViews;
    private TextView textNumberOfLikes;
    private TextView textNumberOfDislikes;

    //webview youtube
//    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;

    private VideoInDetailVideoAdapter videoInDetailVideoAdapter;
    //    private ArrayList<Video> arrayVideos;
    private String API_KEY = "AIzaSyDX9yNqS0tib0vjKEEyqN88eEMlbimaoCs";
    private String videoId = "ZV6SR3azDGE";
    private boolean isShowed = false;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static DetailVideoFragment newInstance(String jsonVideo, String jsonListVideo) {
        Bundle args = new Bundle();
        DetailVideoFragment fragment = new DetailVideoFragment();
        args.putString(ARG_VIDEO, jsonVideo);
        args.putString(ARG_LIST_VIDEO, jsonListVideo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_up_down_description_detail_video:
            case R.id.text_title_video_detail_video:
                if (!isShowed) {
                    //Chưa show gì
                    isShowed = true;
                    imageShowDescription.setImageResource(R.drawable.ic_up);
                    constraintLayoutDescription.setVisibility(View.VISIBLE);
                } else {
                    isShowed = false;
                    imageShowDescription.setImageResource(R.drawable.ic_down);
                    constraintLayoutDescription.setVisibility(View.GONE);
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager()
                .findFragmentById(R.id.image_cover_detail_video);
//        youTubePlayerView = view.findViewById(R.id.image_cover_detail_video);
//        youTubePlayerView.initialize(API_KEY, this);
        recyclerViewVideos = view.findViewById(R.id.recycler_list_recommend_videos_in_stories_detail_video);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewVideos.setLayoutManager(linearLayoutManager);
        recyclerViewVideos.setHasFixedSize(true);
//        recyclerViewVideos.setNestedScrollingEnabled(false);
        imageShowDescription = view.findViewById(R.id.image_up_down_description_detail_video);
        imageShowDescription.setOnClickListener(this);
        textTitle = view.findViewById(R.id.text_title_video_detail_video);
        textTitle.setOnClickListener(this);
        textNumberOfViews = view.findViewById(R.id.text_number_views_detail_video);
        constraintLayoutDescription = view.findViewById(R.id.constraint_layout_description_detail_video);
        constraintLayoutDescription.setVisibility(View.GONE);
        textNumberOfLikes = view.findViewById(R.id.text_number_likes_detail_video);
        textNumberOfDislikes = view.findViewById(R.id.text_number_dislikes_detail_video);

        //Set back icon to toolbar
        mToolbar = view.findViewById(R.id.toolbar_detail_video);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> addFragmentCallback.popBackStack());

        //===GetData====
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        String jsonVideo = bundle.getString(ARG_VIDEO);
        String jsonListVideo = bundle.getString(ARG_LIST_VIDEO);

        Gson gson = new Gson();
        Video video = gson.fromJson(jsonVideo, Video.class);
        ArrayList<Video> mArrayVideos = gson.fromJson(jsonListVideo, new TypeToken<List<Video>>() {
        }.getType());


        if (video != null && mArrayVideos != null) {
//            Log.e("xoi-video", video.toString());
//            Log.e("xoi-video-list", mArrayVideos.toString());
            videoId = video.getIdVideo();
            videoInDetailVideoAdapter = new VideoInDetailVideoAdapter(getContext(),
                    mArrayVideos, addFragmentCallback);
            recyclerViewVideos.setAdapter(videoInDetailVideoAdapter);

            textTitle.setText(video.getTitleVideo());
            textNumberOfViews.setText(video.getNumberOfViews() + " lượt xem");
            textNumberOfLikes.setText(video.getNumberOfLikes() + "");
            textNumberOfDislikes.setText(video.getNumberOfDislikes() + "");
        }

        if (youTubePlayerFragment == null) {
            return;
        }
        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                player.setFullscreen(false);
                if (!wasRestored) {
                    youTubePlayer = player;
                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //cue the 1st video by default
                    youTubePlayer.cueVideo(videoId);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("youtube-error", "Youtube Player View initialization failed");
            }
        });
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_detail_video;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {

    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {

    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }
}
