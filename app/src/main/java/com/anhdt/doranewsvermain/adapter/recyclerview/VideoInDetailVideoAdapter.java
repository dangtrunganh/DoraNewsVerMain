package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.videoresult.VideoItem;
import com.anhdt.doranewsvermain.util.VideoTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Random;

public class VideoInDetailVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<VideoItem> arrayVideos;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;
//    private UpdateListNotification updateListNotification;
//    private RecyclerView recyclerView;

    private final int VIEW_TYPE_VIDEO = 1, VIEW_TYPE_FOOTER = 2;

    private ILoadDetailVideo iLoadDetailVideo;

    public void setiLoadDetailVideo(ILoadDetailVideo iLoadDetailVideo) {
        this.iLoadDetailVideo = iLoadDetailVideo;
    }

    public VideoInDetailVideoAdapter(Context mContext, AddFragmentCallback addFragmentCallback) {
        this.mContext = mContext;
        this.arrayVideos = new ArrayList<>();
        this.addFragmentCallback = addFragmentCallback;
        if (arrayVideos.size() >= 1) {
            arrayVideos.remove(arrayVideos.size() - 1);
        }
        if (this.arrayVideos != null) {
            this.arrayVideos.add(null);
        }
//        this.arrayVideosTotal = arrayVideosTotal;
//        this.updateListNotification = updateListNotification;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayVideos.get(position) == null) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_VIDEO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        switch (viewType) {
            case VIEW_TYPE_VIDEO:
                View viewArticle = mLayoutInflater.inflate(R.layout.item_child_video_in_detail_video, viewGroup, false);
                return new VideoInDetailVideoAdapter.VideoViewHolder(viewArticle);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed_2, viewGroup, false);
                return new VideoInDetailVideoAdapter.FooterViewHolder(viewFooter);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VideoItem video = arrayVideos.get(position);
        if (viewHolder instanceof VideoInDetailVideoAdapter.VideoViewHolder) {
            if (video == null) {
                return;
            }
            VideoInDetailVideoAdapter.VideoViewHolder videoViewHolder = (VideoViewHolder) viewHolder;
            videoViewHolder.bindData(video);

        } else if (viewHolder instanceof VideoInDetailVideoAdapter.FooterViewHolder) {
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

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageCoverVideo;
        private TextView textTimeDuration;
        private TextView textNumberOfViews;
        private TextView textTitle;
        private TextView textChanelName;
        private View viewClickVideoBelow;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoverVideo = itemView.findViewById(R.id.image_cover_item_child_video_detail_video);
            textTimeDuration = itemView.findViewById(R.id.text_time_duration_item_child_video_detail_video);
            textNumberOfViews = itemView.findViewById(R.id.text_number_views_item_child_video_detail_video);
            textTitle = itemView.findViewById(R.id.text_title_item_child_video_detail_video);
            textChanelName = itemView.findViewById(R.id.text_chanel_name_item_child_video_detail_video);
            viewClickVideoBelow = itemView.findViewById(R.id.view_click_item_video_below);
            viewClickVideoBelow.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(VideoItem video) {
            String urlCoverImage = "https://i.ytimg.com/vi/" + video.getVideoId() + "/hqdefault.jpg";
            if (!urlCoverImage.equals("")) {
                Glide.with(mContext).load(urlCoverImage).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(imageCoverVideo);
            }
            textTimeDuration.setText(VideoTool.convertTimeDuration(video.getVideoDuration()));
            textTitle.setText(video.getVideoTitle());
            textChanelName.setText(video.getChannelName());
            Random rand = new Random();
            int value = rand.nextInt(50);
            int views = 200 + value;
            textNumberOfViews.setText(views + " lượt xem");
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            VideoItem video = arrayVideos.get(position);
            if (iLoadDetailVideo != null) {
                iLoadDetailVideo.loadDetailVideo(video);
            }
//            Gson gson = new Gson();
//            String jsonVideo = gson.toJson(video);
//            String jsonListVideo = gson.toJson(arrayVideos);
//            DetailVideoFragment detailVideoFragment = DetailVideoFragment.newInstance(jsonVideo, jsonListVideo);
//            detailVideoFragment.setAddFragmentCallback(addFragmentCallback);
//            addFragmentCallback.addFrgCallback(detailVideoFragment);
            //Click tổng thể, bật chi tiết video lên


//            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
//                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(idEvent, idStory, DetailEventFragment.DEFAULT_LIST_OF_STORY);
//                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
//                addFragmentCallback.addFrgCallback(detailEventFragment);
//            } else {
//                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                alertDialog.setTitle("Thông báo");
//                alertDialog.setMessage("Không có kết nối mạng");
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        (dialog, which) -> dialog.dismiss());
//                alertDialog.show();
//            }

//            //Hiện chỉ bắt sự kiện click vào cả item to, mở ra màn chi tiết event tương ứng?
//            if (idStory == null) {
//                return;
//            }
//            DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(idStory);
//            detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
//            addFragmentCallback.addFrgCallback(detailStoryFragment);
        }
    }

    public void updateListVideos(ArrayList<VideoItem> listVideos) {
        if (listVideos == null) {
            return;
        }
//        if (GeneralTool.checkIfParentHasChild(arrayVideos, (ArrayList<NotificationResult>) listNotifications)) {
//            //List cha chứa cả list con sau khi load more
//            return;
//        }
        arrayVideos.clear();
        arrayVideos.add(null);
        arrayVideos.addAll(0, listVideos);
        notifyDataSetChanged();
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

    public ArrayList<VideoItem> getArrayVideos() {
        return arrayVideos;
    }
}
