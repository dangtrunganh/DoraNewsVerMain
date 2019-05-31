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
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.DetailStoryFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<NotificationResult> arrayNotifications;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;
//    private RecyclerView recyclerView;

    private final int VIEW_TYPE_NOTIFICATION = 1, VIEW_TYPE_FOOTER = 2;

    public NotificationAdapter(Context mContext, ArrayList<NotificationResult> arrayNotifications, AddFragmentCallback addFragmentCallback) {
        this.mContext = mContext;
        this.arrayNotifications = arrayNotifications;
        this.addFragmentCallback = addFragmentCallback;
        if (this.arrayNotifications != null) {
            this.arrayNotifications.add(null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayNotifications.get(position) == null) {
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
                View viewArticle = mLayoutInflater.inflate(R.layout.item_notification_in_recycler_view, viewGroup, false);
                return new NotificationViewHolder(viewArticle);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new NotificationAdapter.FooterViewHolder(viewFooter);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        NotificationResult notificationResult = arrayNotifications.get(position);
        if (viewHolder instanceof NotificationAdapter.NotificationViewHolder) {
            if (notificationResult == null) {
                return;
            }
            NotificationAdapter.NotificationViewHolder notificationViewHolder = (NotificationViewHolder) viewHolder;
            notificationViewHolder.bindData(notificationResult);

        } else if (viewHolder instanceof NotificationAdapter.FooterViewHolder) {
            //Footer, do nothing
        }
    }

    @Override
    public int getItemCount() {
        return this.arrayNotifications.size();
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textHot;
        private TextView textTitleContent;
        private ImageView imageCoverNotice;
        private ImageView imageMore;

        private String idEvent;
        private String idStory;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textHot = itemView.findViewById(R.id.text_title_hot_item_notification);
            textTitleContent = itemView.findViewById(R.id.text_title_item_notification);
            imageCoverNotice = itemView.findViewById(R.id.image_cover_item_notification);
            imageMore = itemView.findViewById(R.id.image_more_item_notification);
            itemView.setOnClickListener(this);
            imageMore.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(NotificationResult notificationResult) {
            this.idStory = notificationResult.getIdStory();
            this.idEvent = notificationResult.getIdEvent();
            String titleHot = notificationResult.getTitleHot();
            if (titleHot != null) {
                textHot.setText(titleHot);
            }
            String titleContent = notificationResult.getTitleContent();
            if (titleContent != null) {
                textTitleContent.setText(titleContent);
            }

            String urlImage = notificationResult.getUrlImage();
            if (urlImage != null) {
                if (!urlImage.equals("")) {
                    Glide.with(mContext).load(urlImage).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(imageCoverNotice);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image_more_item_notification) {
                //More? Xóa notice?
            } else {
                //Click tổng thể, bật chi tiết event lên
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(idEvent, idStory, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailEventFragment);
            }
//            //Hiện chỉ bắt sự kiện click vào cả item to, mở ra màn chi tiết event tương ứng?
//            if (idStory == null) {
//                return;
//            }
//            DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(idStory);
//            detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
//            addFragmentCallback.addFrgCallback(detailStoryFragment);
        }
    }

    public void updateListNotifications(List<NotificationResult> listNotifications) {
        if (listNotifications == null) {
            return;
        }
//        if (GeneralTool.checkIfParentHasChild(arrayNotifications, (ArrayList<NotificationResult>) listNotifications)) {
//            //List cha chứa cả list con sau khi load more
//            return;
//        }
        arrayNotifications.clear();
        arrayNotifications.add(null);
        arrayNotifications.addAll(0, listNotifications);
        notifyDataSetChanged();
    }

    public void addNewNotifications(NotificationResult notificationResult) {
        arrayNotifications.add(0, notificationResult);
        notifyDataSetChanged();
    }

    public void removeStoryFollowed(String idEvent) {
        for (int i = 0; i < arrayNotifications.size(); i++) {
            NotificationResult currentNotification = arrayNotifications.get(i);
            if (currentNotification.getIdEvent().equals(idEvent)) {
                arrayNotifications.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
