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
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.ItemDetailStory;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class StoryItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Click ở bất kì vị trí nào cũng đều có event, get event ID ra mà sổ ra màn Detail nhé
    ArrayList<ItemDetailStory> arrayItemDetailStories;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String idStory;
    private int typeTabContent; // 1 or 2 - tab home hay tab latest

    private AddFragmentCallback addFragmentCallback;
    private final int TYPE_EVENT_TOPEST_SINGLE = 8, TYPE_NORMAL = 9, TYPE_ERROR = 10;
//            TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL = 1,
//            TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL = 2,
//            TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL = 3,
//            TYPE_ERROR = 4;

    public StoryItemAdapter(ArrayList<ItemDetailStory> arrayItemDetailStories, Context mContext,
                            int typeTabContent, String idStory, AddFragmentCallback addFragmentCallback) {
        this.arrayItemDetailStories = arrayItemDetailStories;
        this.mContext = mContext;
        this.typeTabContent = typeTabContent;
        this.idStory = idStory;
        this.addFragmentCallback = addFragmentCallback;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayItemDetailStories.get(position) == null) {
            return TYPE_ERROR;
        }
        int typeItem = arrayItemDetailStories.get(position).getType();
        switch (typeItem) {
            case ItemDetailStory.TYPE_EVENT_TOPEST_SINGLE:
                return TYPE_EVENT_TOPEST_SINGLE;
            case ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL:
            case ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL:
            case ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL:
                return TYPE_NORMAL;
            default:
                return TYPE_ERROR;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        switch (viewType) {
            case TYPE_EVENT_TOPEST_SINGLE:
                View viewTopest = mLayoutInflater.inflate(R.layout.item_latest_event_in_detail_story, viewGroup, false);
                return new StoryItemAdapter.TopEventViewHolder(viewTopest);
            case TYPE_NORMAL:
                View viewNormalStory = mLayoutInflater.inflate(R.layout.item_other_event_in_detail_story, viewGroup, false);
                return new StoryItemAdapter.NormalEventViewHolder(viewNormalStory);
        }
        return null; //Với ERROR_VIEW
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ItemDetailStory itemDetailStory = arrayItemDetailStories.get(position);
        if (viewHolder instanceof StoryItemAdapter.TopEventViewHolder &&
                itemDetailStory.getType() == ItemDetailStory.TYPE_EVENT_TOPEST_SINGLE) {
            StoryItemAdapter.TopEventViewHolder topEventViewHolder = (TopEventViewHolder) viewHolder;
            topEventViewHolder.binData(itemDetailStory);
        } else if (viewHolder instanceof StoryItemAdapter.NormalEventViewHolder) {
            StoryItemAdapter.NormalEventViewHolder normalEventViewHolder = (NormalEventViewHolder) viewHolder;
            normalEventViewHolder.bindData(itemDetailStory);
        }
    }

    public class NormalEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textLabelTimeLine, textLabelToday, textCategory, textTitleEvent, textTimeEvent;
        private ImageView imageCoverEvent, imageMore, imageCircle;

        public NormalEventViewHolder(@NonNull View itemView) {
            super(itemView);
            textLabelTimeLine = itemView.findViewById(R.id.text_time_line_item_other);
            textLabelToday = itemView.findViewById(R.id.text_label_time_item_other);
            imageCircle = itemView.findViewById(R.id.image_circle_item_other);

            textCategory = itemView.findViewById(R.id.text_category_item_other);
            textTitleEvent = itemView.findViewById(R.id.text_title_item_other);
            textTimeEvent = itemView.findViewById(R.id.text_time_item_other);
            imageCoverEvent = itemView.findViewById(R.id.image_cover_item_other);

            imageMore = itemView.findViewById(R.id.image_more_item_other);
            imageMore.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void disableView(int typeObject) {
            if (typeObject == ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL) {
                textLabelTimeLine.setVisibility(View.VISIBLE);
                textLabelToday.setVisibility(View.VISIBLE);
                imageCircle.setVisibility(View.VISIBLE);
            } else if (typeObject == ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL) {
                textLabelTimeLine.setVisibility(View.GONE);
                textLabelToday.setVisibility(View.VISIBLE);
                imageCircle.setVisibility(View.VISIBLE);
            } else if (typeObject == ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL) {
                textLabelTimeLine.setVisibility(View.GONE);
                textLabelToday.setVisibility(View.GONE);
                imageCircle.setVisibility(View.GONE);
            }
        }

        public void bindData(ItemDetailStory itemDetailStory) {
            //Title top là trên cùng - time line
            Event event = itemDetailStory.getEvent();
            disableView(itemDetailStory.getType());
            int type = itemDetailStory.getType();
            if (type == ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL) {
                textLabelTimeLine.setText(itemDetailStory.getTitleTop());
                textLabelToday.setText(itemDetailStory.getTitleTime());
            } else if (type == ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL) {
                textLabelToday.setText(itemDetailStory.getTitleTime());
            } /*else if (type == ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL) {
                //Không làm gì cả hihi, vì bị GONE hết bên trên rồi
            }*/
            //Chung
            textCategory.setText(event.getCategory().getName());
            textTitleEvent.setText(event.getTitle());
            textTimeEvent.setText(event.getReadableTime());
            if (event.getImage() != null) {
                Glide.with(mContext).load(event.getImage()).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(imageCoverEvent);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == R.id.image_more_item_other) {
                //Thực hiện hành động khi click vào button more
                Toast.makeText(mContext, "Clicked more option!", Toast.LENGTH_SHORT).show();
            } else {
                //Thực hiện Hành động khi click vào cả item, -> Mở màn Detail Event Fragment
                String idEvent = arrayItemDetailStories.get(position).getEvent().getId();
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(typeTabContent, idEvent/*, titleEvent*/, DetailEventFragment.DEFAULT_ID_STORY);
                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailEventFragment);
            }
        }
    }

    public class TopEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textLatestLabel, textCategory, textTitleEvent, textTime, textNumArticles;
        private ImageView imageCoverEvent, imageMore;

        public TopEventViewHolder(@NonNull View itemView) {
            super(itemView);
            textLatestLabel = itemView.findViewById(R.id.text_title_label_latest_event_item_story);
            textCategory = itemView.findViewById(R.id.text_category_latest_event_item_story);
            textTitleEvent = itemView.findViewById(R.id.text_title_name_event_latest_event_item_story);
            textTime = itemView.findViewById(R.id.text_time_name_event_latest_event_item_story);
            textNumArticles = itemView.findViewById(R.id.text_number_articles_latest_event_item_story);
            imageCoverEvent = itemView.findViewById(R.id.image_cover_latest_event_item_story);
            imageMore = itemView.findViewById(R.id.image_more_latest_event_item_story);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void binData(ItemDetailStory itemDetailStory) {
            Event event = itemDetailStory.getEvent();
            textLatestLabel.setText(itemDetailStory.getTitleTop());
            textCategory.setText(event.getCategory().getName());

            textTitleEvent.setText(event.getTitle());

            textTime.setText(event.getReadableTime());

            textNumArticles.setText(event.getNumArticles() + " bài báo");

            if (event.getImage() != null) {
                Glide.with(mContext).load(event.getImage()).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(imageCoverEvent);
            }

            imageMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == R.id.image_more_latest_event_item_story) {
                //Thực hiện hành động khi click vào button more
                Toast.makeText(mContext, "Clicked more option!", Toast.LENGTH_SHORT).show();
            } else {
                //Thực hiện Hành động khi click vào cả item --> Mở DetailEventFragment tương ứng
                String idEvent = arrayItemDetailStories.get(position).getEvent().getId();
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(typeTabContent, idEvent/*, titleEvent*/, DetailEventFragment.DEFAULT_ID_STORY);
                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailEventFragment);
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayItemDetailStories.size();
    }
}
