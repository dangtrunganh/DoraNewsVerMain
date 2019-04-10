package com.anhdt.doranewsvermain.adapter.autoviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.customview.NewsBackgroundLayout;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoryAdapter extends PagerAdapter {
    private ArrayList<Event> arrayEvents;
    private LayoutInflater inflater;
    private Context mContext;


    public StoryAdapter(ArrayList<Event> arrayEvents, Context context) {
        this.arrayEvents = arrayEvents;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayEvents.size();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = inflater.inflate(
                R.layout.item_event_in_vpg_stories, container, false);

        ImageView newsBackgroundLayout = itemView.findViewById(R.id.lnl_background_text_event_item_event_in_vpg);
        TextView tvSum = itemView.findViewById(R.id.text_sum_content_item_event_in_vpg);
        TextView tvCategory = itemView.findViewById(R.id.text_category_item_event_in_vpg);
        TextView tvNumberArticles = itemView.findViewById(R.id.text_number_articles_item_event_in_vpg);
        TextView tvTimeReadable = itemView.findViewById(R.id.text_time_item_event_in_vpg);
        TextView tvTitleEvent = itemView.findViewById(R.id.text_title_event_item_event_in_vpg);

        Event event = arrayEvents.get(position);
        Picasso.get().load(event.getImage()).into(newsBackgroundLayout);
        tvCategory.setText(event.getCategory().getName());
        tvNumberArticles.setText(String.valueOf(event.getNumArticles()) + " bài báo");
        tvTimeReadable.setText(event.getReadableTime());
        tvTitleEvent.setText(event.getTitle());

        //summary
        String summaryContent = event.getContent();
        SpannableString str = new SpannableString(summaryContent);
        if (!summaryContent.equals("")) {
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#B3000000"));
            str.setSpan(backgroundColorSpan, 0, summaryContent.length(), 0);
        }

        Animation mAnimation;
        tvSum.setText(str);
        mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.move_summary);
        mAnimation.setFillAfter(true);
        tvSum.setAnimation(mAnimation);


        container.addView(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Có position rồi này
            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof View) {
            return;
        }
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return o.equals(view);
    }


}
