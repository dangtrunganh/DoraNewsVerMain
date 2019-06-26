package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.DetailVideoFragment;
import com.anhdt.doranewsvermain.fragment.firstchildfragment.UpdateListNotification;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.model.videotest.Video;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForNotification;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Video> arrayVideos;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;
//    private UpdateListNotification updateListNotification;
//    private RecyclerView recyclerView;

    private final int VIEW_TYPE_NOTIFICATION = 1, VIEW_TYPE_FOOTER = 2;

    public VideoAdapter(Context mContext, ArrayList<Video> arrayVideos, AddFragmentCallback addFragmentCallback, UpdateListNotification updateListNotification) {
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
                View viewArticle = mLayoutInflater.inflate(R.layout.item_video, viewGroup, false);
                return new VideoAdapter.VideoViewHolder(viewArticle);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new VideoAdapter.FooterViewHolder(viewFooter);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Video video = arrayVideos.get(position);
        if (viewHolder instanceof VideoAdapter.VideoViewHolder) {
            if (video == null) {
                return;
            }
            VideoAdapter.VideoViewHolder videoViewHolder = (VideoAdapter.VideoViewHolder) viewHolder;
            videoViewHolder.bindData(video);

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

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageCoverVideo;
        private TextView textTimeDuration;
        private CircleImageView circleImageLogo;
        private TextView textTitle;
        private TextView textChanelName;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoverVideo = itemView.findViewById(R.id.image_cover_item_video);
            textTimeDuration = itemView.findViewById(R.id.text_time_item_video);
            circleImageLogo = itemView.findViewById(R.id.image_circle_logo_item_video);
            textTitle = itemView.findViewById(R.id.text_title_item_video);
            textChanelName = itemView.findViewById(R.id.text_chanel_name_item_video);
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
            String urlLogo = video.getImageLogoChanel();
            if (urlLogo != null) {
                if (!urlLogo.equals("")) {
                    Glide.with(mContext).load(urlLogo).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(circleImageLogo);
                }
            }
            textTimeDuration.setText(video.getTimeDuration());
            textTitle.setText(video.getTitleVideo());
            textChanelName.setText(video.getChanelName() + " - " + video.getNumberOfViews() + " lượt xem" + " - " + video.getTimeCreated());
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
