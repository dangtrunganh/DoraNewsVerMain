package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class EventAdapterHorizontal extends RecyclerView.Adapter<EventAdapterHorizontal.EventHorizontalViewHolder> {
    private ArrayList<Event> arrayListEvents;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int typeTabContent; //Cần biến này để biết được Fragment General nào sẽ xử lý chính
    private String idStory;
    private AddFragmentCallback addFragmentCallback;

    private String idCurrentEvent;

    public EventAdapterHorizontal(ArrayList<Event> arrayListEvents, Context mContext, int typeTabContent,
                                  String idStory, AddFragmentCallback addFragmentCallback, String idCurrentEvent) {
        this.arrayListEvents = arrayListEvents;
        this.mContext = mContext;
        this.typeTabContent = typeTabContent;
        this.idStory = idStory;
        this.addFragmentCallback = addFragmentCallback;
        this.idCurrentEvent = idCurrentEvent;
    }

    @NonNull
    @Override
    public EventHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = mLayoutInflater.inflate(R.layout.item_child_event_in_detail_event_horizontal, viewGroup, false);
        return new EventAdapterHorizontal.EventHorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHorizontalViewHolder eventHorizontalViewHolder, int position) {
        eventHorizontalViewHolder.bindData(arrayListEvents.get(position));
    }

    @Override
    public int getItemCount() {
        if (arrayListEvents == null) {
            return 0;
        }
        return arrayListEvents.size();
    }

    public class EventHorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textCategory, textTitleEvent, textTime;
        private ImageView imageCoverEvent, btnMoreFunction;
        private CardView cardViewBound;
        private View viewBoundCurrentEvent, viewBoundImage;

        public EventHorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_category_item_child_event_deh);
            textTitleEvent = itemView.findViewById(R.id.text_title_item_child_event_deh);
            textTime = itemView.findViewById(R.id.text_time_item_child_event_deh);
            imageCoverEvent = itemView.findViewById(R.id.image_cover_item_child_event_deh);
            btnMoreFunction = itemView.findViewById(R.id.image_more_item_child_event_deh);
            cardViewBound = itemView.findViewById(R.id.card_view_bound_deh);
            viewBoundCurrentEvent = itemView.findViewById(R.id.view_story_bound_deh);
            viewBoundImage = itemView.findViewById(R.id.view_story_bound_image_deh);

            btnMoreFunction.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindData(Event event) {
            if (event == null) {
                return;
            }
            if (event.getImage() != null) {
                Glide.with(itemView.getContext()).load(event.getImage()).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(imageCoverEvent);
            }
            textCategory.setText(event.getCategory().getName());
            textTitleEvent.setText(event.getTitle());
            textTime.setText(event.getReadableTime());

            if (event.getId().equals(idCurrentEvent)) {
                cardViewBound.setEnabled(false);
                viewBoundCurrentEvent.setVisibility(View.VISIBLE);
                viewBoundImage.setVisibility(View.VISIBLE);
            } else {
                cardViewBound.setEnabled(true);
                viewBoundCurrentEvent.setVisibility(View.GONE);
                viewBoundImage.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //Sự kiện khi click vào nút more nhỏ
            if (v.getId() == R.id.image_more_item_child_event_deh) {
                Toast.makeText(mContext, "Clicked button more for each event!", Toast.LENGTH_SHORT).show();
            } else {
                //Click vào tổng thế view
                //Bật lên màn chi tiết của event tiếp theo
                Event event = arrayListEvents.get(position);
                String idEvent = event.getId();
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(typeTabContent, idEvent, idStory);

                //Cần set lại addFragmentCallback, vì từ DetailEventFragment truyền sang cho EventAdapterHorizontal
                //Nhưng detailEventFragment là cái hoàn toàn mới, cần callBack để back lại
                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailEventFragment);
            }

        }
    }

    public void updateListEvents(ArrayList<Event> events) {
        arrayListEvents.clear();
        arrayListEvents.addAll(events);
        notifyDataSetChanged();
    }
}
