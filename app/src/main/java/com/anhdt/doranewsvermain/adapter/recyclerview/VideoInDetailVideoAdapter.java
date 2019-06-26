package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailVideoFragment;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.UpdateListNotification;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.videotest.Video;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class VideoInDetailVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Video> arrayVideos;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;
//    private UpdateListNotification updateListNotification;
//    private RecyclerView recyclerView;

    private final int VIEW_TYPE_VIDEO = 1, VIEW_TYPE_FOOTER = 2;

    public VideoInDetailVideoAdapter(Context mContext, ArrayList<Video> arrayVideos, AddFragmentCallback addFragmentCallback) {
        this.mContext = mContext;
        this.arrayVideos = arrayVideos;
        this.addFragmentCallback = addFragmentCallback;
        if (this.arrayVideos != null) {
            this.arrayVideos.add(null);
        }
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
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new VideoInDetailVideoAdapter.FooterViewHolder(viewFooter);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Video video = arrayVideos.get(position);
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

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoverVideo = itemView.findViewById(R.id.image_cover_item_child_video_detail_video);
            textTimeDuration = itemView.findViewById(R.id.text_time_duration_item_child_video_detail_video);
            textNumberOfViews = itemView.findViewById(R.id.text_number_views_item_child_video_detail_video);
            textTitle = itemView.findViewById(R.id.text_title_item_child_video_detail_video);
            textChanelName = itemView.findViewById(R.id.text_chanel_name_item_child_video_detail_video);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Video video) {
            String urlCoverImage = video.getImageThumbnail();
            if (urlCoverImage != null) {
                if (!urlCoverImage.equals("")) {
                    Glide.with(mContext).load(urlCoverImage).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(imageCoverVideo);
                }
            }
            textTimeDuration.setText(video.getTimeDuration());
            textTitle.setText(video.getTitleVideo());
            textChanelName.setText(video.getChanelName());
            textNumberOfViews.setText(video.getNumberOfViews() + " lượt xem");
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Video video = arrayVideos.get(position);
            Gson gson = new Gson();
            String jsonVideo = gson.toJson(video);
            String jsonListVideo = gson.toJson(arrayVideos);
            DetailVideoFragment detailVideoFragment = DetailVideoFragment.newInstance(jsonVideo, jsonListVideo);
            detailVideoFragment.setAddFragmentCallback(addFragmentCallback);
            addFragmentCallback.addFrgCallback(detailVideoFragment);
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

    public void updateListVideos(ArrayList<Video> listVideos) {
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

    public ArrayList<Video> getArrayVideos() {
        return arrayVideos;
    }
}
