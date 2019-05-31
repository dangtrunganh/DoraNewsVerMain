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
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
import com.anhdt.doranewsvermain.model.searchresult.EventSearchResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private ArrayList<EventSearchResult> arrayEventSearchResult;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;

    private final int VIEW_TYPE_EVENT_RESULT = 1, VIEW_TYPE_FOOTER = 2;

    public SearchResultAdapter(Context mContext, ArrayList<EventSearchResult> arrayEventSearchResult, AddFragmentCallback addFragmentCallback) {
        this.mContext = mContext;
        this.arrayEventSearchResult = arrayEventSearchResult;
        this.addFragmentCallback = addFragmentCallback;
        if (this.arrayEventSearchResult != null) {
            this.arrayEventSearchResult.add(null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayEventSearchResult.get(position) == null) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_EVENT_RESULT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        switch (viewType) {
            case VIEW_TYPE_EVENT_RESULT:
                View viewEvent = mLayoutInflater.inflate(R.layout.item_stories_followed, viewGroup, false);
                return new SearchResultAdapter.EventViewHolder(viewEvent);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new SearchResultAdapter.FooterViewHolder(viewFooter);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        EventSearchResult eventSearchResult = arrayEventSearchResult.get(position);
        if (viewHolder instanceof SearchResultAdapter.EventViewHolder) {
            if (eventSearchResult == null) {
                return;
            }
            SearchResultAdapter.EventViewHolder eventViewHolder = (EventViewHolder) viewHolder;
            eventViewHolder.bindData(eventSearchResult);

        } else if (viewHolder instanceof SearchResultAdapter.FooterViewHolder) {
            //Footer, do nothing
        }
    }

    @Override
    public int getItemCount() {
        return this.arrayEventSearchResult.size();
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textCategory;
        private TextView textTitleEvent;
        private TextView textNumberEvent;
        private ImageView imageCoverStory;
        private ImageView imageMore;
        private String idStory;
        private String idEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_category_item_stories_followed);
            textTitleEvent = itemView.findViewById(R.id.text_title_item_stories_followed);
            textNumberEvent = itemView.findViewById(R.id.text_number_events_item_stories_followed);
            imageCoverStory = itemView.findViewById(R.id.image_cover_item_stories_followed);
            imageMore = itemView.findViewById(R.id.image_more_item_stories_followed);
            itemView.setOnClickListener(this);
//            imageMore.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(EventSearchResult eventSearchResult) {
//            this.idStory = eventSearchResult.getIdStory();
            this.idEvent = eventSearchResult.getId();
            if (idEvent != null) {
                String category = "ID: " + idEvent;
                textCategory.setText(category);
            }

            String titleContent = eventSearchResult.getTitle();
            if (titleContent != null) {
                textTitleEvent.setText(titleContent);
            }

            double score = eventSearchResult.getScore();
            textNumberEvent.setText(score + " sự kiện");

//            String urlImage = eventSearchResult.getUrlImage();
//            if (urlImage != null) {
//                if (!urlImage.equals("")) {
//                    Glide.with(mContext).load(urlImage).
//                            apply(new RequestOptions().override(400, 0).
//                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
//                            .into(imageCoverNotice);
//                }
//            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image_more_item_notification) {
                //More? Xóa notice?
            } else {
                //Click tổng thể, bật chi tiết event lên
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(idEvent, DetailEventFragment.DEFAULT_ID_STORY, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailEventFragment);
            }
        }
    }

    public void updateListResults(List<EventSearchResult> listEventResults) {
        if (listEventResults == null) {
            return;
        }
        arrayEventSearchResult.clear();
        arrayEventSearchResult.add(null);
        arrayEventSearchResult.addAll(0, listEventResults);
        notifyDataSetChanged();
    }

    public void addNewEventResult(EventSearchResult eventSearchResult) {
        arrayEventSearchResult.add(0, eventSearchResult);
        notifyDataSetChanged();
    }

    public void removeEventResult(String idEvent) {
        for (int i = 0; i < arrayEventSearchResult.size(); i++) {
            EventSearchResult eventSearchResult = arrayEventSearchResult.get(i);
            if (eventSearchResult.getId().equals(idEvent)) {
                arrayEventSearchResult.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
