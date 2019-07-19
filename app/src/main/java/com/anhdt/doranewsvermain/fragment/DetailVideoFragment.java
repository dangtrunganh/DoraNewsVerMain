package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.ILoadDetailVideo;
import com.anhdt.doranewsvermain.adapter.recyclerview.VideoInDetailVideoAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.videoresult.VideoItem;
import com.anhdt.doranewsvermain.util.VideoTool;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DetailVideoFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener, ILoadDetailVideo {
    private static String ARG_VIDEO = "ARG_VIDEO";
    private static String ARG_LIST_VIDEO = "ARG_LIST_VIDEO";
    private Toolbar mToolbar;
    private RecyclerView recyclerViewVideos;
    private ImageView imageShowDescription;
    private ConstraintLayout constraintLayoutDescription;
    private AddFragmentCallback addFragmentCallback;

    private TextView textTitle, textDescription;
    private TextView textNumberOfViews;
    private TextView textNumberOfLikes;
    private TextView textNumberOfDislikes;
    private TextView textTimePublishVideo;
    private View viewShowDescription;
    private VideoItem video;
    private ProgressDialog dialog;

    private View viewLike, viewDisLike, viewCopy, viewShare;

    //webview youtube
//    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;
    private ArrayList<VideoItem> mArrayVideos, mArrayVideosToListBelow;

    private VideoInDetailVideoAdapter videoInDetailVideoAdapter;
    //    private ArrayList<Video> arrayVideos;
    private String API_KEY = "AIzaSyDX9yNqS0tib0vjKEEyqN88eEMlbimaoCs";
    private String videoId = "ZV6SR3azDGE";
    private boolean isShowed = false;
    private Context mContext;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    public static DetailVideoFragment newInstance(String jsonVideo, String jsonListVideo) {
        Bundle args = new Bundle();
        DetailVideoFragment fragment = new DetailVideoFragment();
        args.putString(ARG_VIDEO, jsonVideo);
        args.putString(ARG_LIST_VIDEO, jsonListVideo);
        fragment.setArguments(args);
        return fragment;
    }

    private void copyToClipboard(String urlVideo) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Url video", urlVideo);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.image_up_down_description_detail_video:
            case R.id.view_show_description_detail_video:
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
            case R.id.view_click_share_detail_video:
                if (video != null) {
                    shareLink(video.getVideoLink(), video.getVideoTitle());
                }
                break;
            case R.id.view_click_like_detail_video:
//                Toast.makeText(mContext, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_click_dislike_detail_video:
//                Toast.makeText(mContext, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_click_copy_detail_video:
                if (video != null) {
                    copyToClipboard(video.getVideoLink());
                    Toast.makeText(mContext, "Đã sao chép url video vào bộ nhớ tạm", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void shareLink(String urlVideo, String videoTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, videoTitle);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlVideo);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Chọn ứng dụng để chia sẻ"));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        viewLike = view.findViewById(R.id.view_click_like_detail_video);
        viewDisLike = view.findViewById(R.id.view_click_dislike_detail_video);
        viewCopy = view.findViewById(R.id.view_click_copy_detail_video);
        viewShare = view.findViewById(R.id.view_click_share_detail_video);

        viewLike.setOnClickListener(this);
        viewDisLike.setOnClickListener(this);
        viewCopy.setOnClickListener(this);
        viewShare.setOnClickListener(this);
        youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager()
                .findFragmentById(R.id.image_cover_detail_video);
//        youTubePlayerView = view.findViewById(R.id.image_cover_detail_video);
//        youTubePlayerView.initialize(API_KEY, this);
        textTimePublishVideo = view.findViewById(R.id.text_time_created_detail_video);
        viewShowDescription = view.findViewById(R.id.view_show_description_detail_video);
        viewShowDescription.setOnClickListener(this);
        recyclerViewVideos = view.findViewById(R.id.recycler_list_recommend_videos_in_stories_detail_video);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewVideos.setLayoutManager(linearLayoutManager);
        recyclerViewVideos.setHasFixedSize(true);
//        recyclerViewVideos.setNestedScrollingEnabled(false);
        imageShowDescription = view.findViewById(R.id.image_up_down_description_detail_video);
//        imageShowDescription.setOnClickListener(this);
        textTitle = view.findViewById(R.id.text_title_video_detail_video);
//        textTitle.setOnClickListener(this);
        textDescription = view.findViewById(R.id.text_description_detail_video);
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
        video = gson.fromJson(jsonVideo, VideoItem.class);
        mArrayVideos = gson.fromJson(jsonListVideo, new TypeToken<List<VideoItem>>() {
        }.getType());


        if (video != null && mArrayVideos != null) {
            if (mArrayVideos.get(mArrayVideos.size() - 1).isFooter()) {
                mArrayVideos.remove(mArrayVideos.size() - 1);
            }
            if (mArrayVideos.get(mArrayVideos.size() - 1) == null) {
                mArrayVideos.remove(mArrayVideos.size() - 1);
            }
            mArrayVideosToListBelow = new ArrayList<>();
            videoId = video.getVideoId();
            //create array for list video below
            createArrayForBelowList(video);
            videoInDetailVideoAdapter = new VideoInDetailVideoAdapter(mContext, addFragmentCallback);
            videoInDetailVideoAdapter.updateListVideos(mArrayVideosToListBelow);

            videoInDetailVideoAdapter.setiLoadDetailVideo(this);
            recyclerViewVideos.setAdapter(videoInDetailVideoAdapter);
//            String title = video.getSnippet().getTitle().replaceAll("&quot; ", "\"");
            textTitle.setText(video.getVideoTitle());
            textDescription.setText(video.getVideoDescription());
            textTimePublishVideo.setText("Xuất bản " + VideoTool.convertTimeLongToDateVietnamese(video.getCreatedAt()));
            Random rand = new Random();
            int value = rand.nextInt(50);
            int views = 200 + value;
            textNumberOfViews.setText(views + " lượt xem");
//            textNumberOfLikes.setText(video.getNumberOfLikes() + "");
//            textNumberOfDislikes.setText(video.getNumberOfDislikes() + "");
        }

        if (youTubePlayerFragment == null) {
            return;
        }
        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;
                    youTubePlayer.setFullscreen(false);
                    youTubePlayer.setShowFullscreenButton(false);
                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //cue the 1st video by default
//                    youTubePlayer.cueVideo(videoId);
                    youTubePlayer.loadVideo(videoId);
//                    youTubePlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("youtube-error", "Youtube Player View initialization failed");
            }
        });
    }

    private void createArrayForBelowList(VideoItem video) {
        String id = video.getVideoId();
        if (mArrayVideos == null) {
            return;
        }
        if (mArrayVideosToListBelow == null) {
            return;
        }
        mArrayVideosToListBelow.clear();
        mArrayVideosToListBelow.addAll(mArrayVideos);
        for (int i = 0; i < mArrayVideosToListBelow.size(); i++) {
            if (id.equals(mArrayVideosToListBelow.get(i).getVideoId())) {
                mArrayVideosToListBelow.remove(i);
                return;
            }
        }
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

    @SuppressLint("SetTextI18n")
    @Override
    public void loadDetailVideo(VideoItem video) {
        dialog.show();
        if (video != null && mArrayVideos != null && youTubePlayerFragment != null && youTubePlayer != null) {
            //load selected video
            createArrayForBelowList(video);
            videoInDetailVideoAdapter.updateListVideos(mArrayVideosToListBelow);
            textTitle.setText(video.getVideoTitle());
            textDescription.setText(video.getVideoDescription());
            textTimePublishVideo.setText("Xuất bản " + VideoTool.convertTimeLongToDateVietnamese(video.getCreatedAt()));
            Random rand = new Random();
            int value = rand.nextInt(50);
            int views = 200 + value;
            textNumberOfViews.setText(views + " lượt xem");
//            youTubePlayer.cueVideo(video.getId().getVideoId());
//            youTubePlayer.play();
            this.video = video;
            this.videoId = video.getVideoId();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        youTubePlayer.loadVideo(video.getVideoId());
                        dialog.dismiss();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    super.run();
                }
            };
            thread.start();
        } else {
            dialog.dismiss();
        }


    }
}
