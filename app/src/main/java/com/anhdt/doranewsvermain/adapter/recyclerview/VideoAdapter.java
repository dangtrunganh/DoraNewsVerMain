package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailVideoFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.videoresult.VideoItem;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.VideoTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<VideoItem> arrayVideos;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;
    private boolean isLoading;
    private ILoadMore loadMore;
    //    private UpdateListNotification updateListNotification;
    private RecyclerView recyclerView;

    private final int VIEW_TYPE_NOTIFICATION = 1, VIEW_TYPE_FOOTER = 2, VIEW_TYPE_LOADING = 3;

    public VideoAdapter(Context mContext, ArrayList<VideoItem> arrayVideos,
                        AddFragmentCallback addFragmentCallback, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.arrayVideos = arrayVideos;
        this.addFragmentCallback = addFragmentCallback;
        if (this.arrayVideos != null) {
            VideoItem itemFooter = new VideoItem();
            itemFooter.setFooter(true);
        }
        this.recyclerView = recyclerView;

        //load more
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //====
                int lastVisibleItem = 0;
                if (linearLayoutManager != null) {
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }
                if (!isLoading && lastVisibleItem >= arrayVideos.size() - 2) {
                    if (loadMore != null) {
                        loadMore.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayVideos.get(position) == null) {
            return VIEW_TYPE_LOADING;
        }
        if (arrayVideos.get(position).isFooter()) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_NOTIFICATION;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        switch (viewType) {
            case VIEW_TYPE_NOTIFICATION:
                View viewVideo = mLayoutInflater.inflate(R.layout.item_video_2, viewGroup, false);
                return new VideoAdapter.VideoViewHolder(viewVideo);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new VideoAdapter.FooterViewHolder(viewFooter);
            case VIEW_TYPE_LOADING:
                View viewLoading = mLayoutInflater.inflate(R.layout.item_loading, viewGroup, false);
                return new VideoAdapter.LoadingViewHolder(viewLoading);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VideoItem video = arrayVideos.get(position);
        if (viewHolder instanceof VideoAdapter.VideoViewHolder) {
            if (video == null) {
                return;
            }
            VideoAdapter.VideoViewHolder videoViewHolder = (VideoAdapter.VideoViewHolder) viewHolder;
            videoViewHolder.bindData(video);

        } else if (viewHolder instanceof VideoAdapter.LoadingViewHolder) {
            VideoAdapter.LoadingViewHolder loadingViewHolder = (VideoAdapter.LoadingViewHolder) viewHolder;
            loadingViewHolder.setIndeterminate();
        } else if (viewHolder instanceof VideoAdapter.FooterViewHolder) {
            //Footer, do nothing
        }
    }

    @Override
    public int getItemCount() {
        return this.arrayVideos.size();
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar_news);
        }

        public void setIndeterminate() {
            progressBar.setIndeterminate(true);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageCoverVideo;
        private TextView textTimeDuration;
        private CircleImageView circleImageLogo;
        private TextView textTitle;
        private TextView textChanelName;
        private TextView textTimePublish;
        private TableRow tableRowShare;
        private View viewClick;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoverVideo = itemView.findViewById(R.id.image_cover_item_video);
            viewClick = itemView.findViewById(R.id.view_click_item_video);
            circleImageLogo = itemView.findViewById(R.id.image_circle_logo_item_video);
            textTimeDuration = itemView.findViewById(R.id.text_time_item_video);
            textTitle = itemView.findViewById(R.id.text_title_item_video);

            textChanelName = itemView.findViewById(R.id.text_chanel_name_item_video);
            textTimePublish = itemView.findViewById(R.id.text_time_publish_item_video);
            tableRowShare = itemView.findViewById(R.id.table_row_share_item_video);
//            imageCoverVideo.setOnClickListener(this);
            viewClick.setOnClickListener(this);
            tableRowShare.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(VideoItem video) {
            String urlCoverImage = "https://i.ytimg.com/vi/" + video.getVideoId() + "/hqdefault.jpg";
            Log.e("image-", urlCoverImage);
            if (!urlCoverImage.equals("")) {
                Glide.with(mContext).load(urlCoverImage).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(imageCoverVideo);
            }

            String urlLogo = video.getChannelImage();
            if (urlLogo != null) {
                if (!urlLogo.equals("")) {
                    Glide.with(mContext).load(urlLogo).
                            apply(new RequestOptions().override(200, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(circleImageLogo);
                }
            }
            textTimeDuration.setText(VideoTool.convertTimeDuration(video.getVideoDuration()));
//            String title = video.getSnippet().getTitle().replaceAll("&quot; ", "\"");
            textTitle.setText(video.getVideoTitle());
            textChanelName.setText(video.getChannelName());
            textTimePublish.setText(VideoTool.convertTimeLongToDateUS(video.getCreatedAt()));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            VideoItem video = arrayVideos.get(position);
            switch (v.getId()) {
                case R.id.view_click_item_video:
                    Gson gson = new Gson();
                    String jsonVideo = gson.toJson(video);
                    String jsonListVideo = gson.toJson(arrayVideos);
                    DetailVideoFragment detailVideoFragment = DetailVideoFragment.newInstance(jsonVideo, jsonListVideo);
                    detailVideoFragment.setAddFragmentCallback(addFragmentCallback);
                    addFragmentCallback.addFrgCallback(detailVideoFragment);
                    break;
                case R.id.table_row_share_item_video:
                    //Share video cho bạn bè//
                    shareLink(video.getVideoLink(), video.getVideoTitle());
                    break;
                default:
                    break;
            }
        }
    }

    private void shareLink(String urlVideo, String videoTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, videoTitle);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlVideo);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Chọn ứng dụng để chia sẻ"));
    }

    public void updateListVideos(ArrayList<VideoItem> listVideos) {
        //Dùng cho load lần đầu và refresh
        if (listVideos == null) {
            return;
        }
        if (GeneralTool.checkIfVideoParentHasChild(arrayVideos, listVideos)) {
            //List cha chứa cả list con sau khi load more
            return;
        }
        arrayVideos.clear();
        VideoItem itemFooter = new VideoItem();
        itemFooter.setFooter(true);
        arrayVideos.add(itemFooter);
        arrayVideos.addAll(0, listVideos);
        notifyDataSetChanged();
    }

    public void addItemLoading() {
        //Thêm vào trước Footer
//        arrayDatums.add(null);
//        notifyItemInserted(arrayDatums.size() - 1);
        arrayVideos.add(arrayVideos.size() - 1, null);
        notifyItemInserted(arrayVideos.size() - 2);
    }

    public void removeItemLoading() {
        //Thêm vào trước Footer
        if (arrayVideos.size() < 2) {
            return;
        }
        arrayVideos.remove(arrayVideos.size() - 2);
        notifyItemRemoved(arrayVideos.size() - 1);
    }

    public void addLoadMoreListVideos(ArrayList<VideoItem> listVideos) {
        if (listVideos == null) {
            this.setLoaded();
            return;
        }
        if (GeneralTool.checkIfVideoParentHasChild(arrayVideos, listVideos)) {
            //List cha chứa cả list con sau khi load more
            this.setLoaded();
            return;
        }
        arrayVideos.addAll(arrayVideos.size() - 1, listVideos);
        notifyDataSetChanged();
        this.setLoaded();
    }

    //    public void addNewNotifications(NotificationResult notificationResult) {
//        arrayVideos.add(0, notificationResult);
//        notifyDataSetChanged();
//    }
//
//    public void removeNotification(String idEvent) {
//        for (int i = 0; i < arrayVideos.size(); i++) {
//            NotificationResult currentNotification = arrayVideos.get(i);
//            if (currentNotification.getIdEvent().equals(idEvent)) {
//                arrayVideos.remove(i);
//                notifyItemRemoved(i);
//                break;
//            }
//        }
//    }
    public void setLoaded() {
        isLoading = false;
    }

    public ArrayList<VideoItem> getArrayVideos() {
        return arrayVideos;
    }
}
