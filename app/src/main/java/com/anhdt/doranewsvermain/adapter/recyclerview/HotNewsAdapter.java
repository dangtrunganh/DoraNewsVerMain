package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.autoviewpager.StoryAdapter;
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.customview.NewsBackgroundLayout;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.Event_;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.bumptech.glide.Glide;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import me.relex.circleindicator.CircleIndicator;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HotNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Datum> arrayDatums;
    private ILoadMore loadMore;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private final int VIEW_TYPE_ARTICLE = 1, VIEW_TYPE_EVENT = 2, VIEW_TYPE_STORY = 3, ERROR_VIEW = 4;

    public HotNewsAdapter(ArrayList<Datum> arrayDatums, Context mContext) {
        this.arrayDatums = arrayDatums;
        this.mContext = mContext;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public int getItemViewType(int position) {
        int typeItem = arrayDatums.get(position).getType();
        switch (typeItem) {
            case TypeNewsConst.ARTICLE:
                return VIEW_TYPE_ARTICLE;
            case TypeNewsConst.EVENT:
                return VIEW_TYPE_EVENT;
            case TypeNewsConst.STORY:
                return VIEW_TYPE_STORY;
            default:
                return ERROR_VIEW;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        switch (viewType) {
            case VIEW_TYPE_ARTICLE:
                View viewArticle = mLayoutInflater.inflate(R.layout.item_article_hot_news, viewGroup, false);
                return new HotNewsAdapter.ArticleViewHolder(viewArticle);
            case VIEW_TYPE_EVENT:
                View viewEvent = mLayoutInflater.inflate(R.layout.item_event_hot_news, viewGroup, false);
                return new HotNewsAdapter.EventViewHolder(viewEvent);
            case VIEW_TYPE_STORY:
                View viewStory = mLayoutInflater.inflate(R.layout.item_stories_hot_news, viewGroup, false);
                return new HotNewsAdapter.StoryViewHolder(viewStory);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Datum datum = arrayDatums.get(position);
        if (viewHolder instanceof HotNewsAdapter.ArticleViewHolder && datum.getType() == TypeNewsConst.ARTICLE) {
            //Liệu type có đúng?
            //Lấy ra list các articles
            ArrayList<Article> articleArrayList = (ArrayList<Article>) datum.getArticles();
            if (articleArrayList == null) {
                return;
            }
            HotNewsAdapter.ArticleViewHolder articleViewHolder = (ArticleViewHolder) viewHolder;
            articleViewHolder.bindData(articleArrayList);
        } else if (viewHolder instanceof HotNewsAdapter.EventViewHolder && datum.getType() == TypeNewsConst.EVENT) {
            Event_ event_ = datum.getEvent();
            if (event_ == null) {
                return;
            }
            HotNewsAdapter.EventViewHolder eventViewHolder = (EventViewHolder) viewHolder;
            eventViewHolder.bindData(event_);
        } else if (viewHolder instanceof HotNewsAdapter.StoryViewHolder && datum.getType() == TypeNewsConst.STORY) {
            Stories stories = datum.getStories();
            if (stories == null) {
                return;
            }
            ArrayList<Event> arrayListEvents = (ArrayList<Event>) stories.getEvents();
            HotNewsAdapter.StoryViewHolder storyViewHolder = (StoryViewHolder) viewHolder;
            storyViewHolder.bindData(arrayListEvents);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerArticle;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerArticle = itemView.findViewById(R.id.recycler_articles_hot_news);
        }

        public void bindData(ArrayList<Article> articleArrayList) {
//            Log.e("xxx-", articleArrayList.toString());
//            Log.e("xxx-size", String.valueOf(articleArrayList.size()));
            ArticleItemAdapter articleItemAdapter = new ArticleItemAdapter(mContext, articleArrayList);
            recyclerArticle.setHasFixedSize(true);
            recyclerArticle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            recyclerArticle.setAdapter(articleItemAdapter);
            recyclerArticle.setNestedScrollingEnabled(false);
        }

//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            //Làm gì đó khi click vào article - bài báo thường, tuy nhiên cái này nằm trong adapter con
//            //...
//        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private NewsBackgroundLayout lnlCoverEvent;
        private TextView tvCategory;
        private TextView tvTitleEvent;
        private TextView tvTimeReadable;
        private TextView tvNumberArticles;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            lnlCoverEvent = itemView.findViewById(R.id.image_event_cover_item_event);
            tvCategory = itemView.findViewById(R.id.text_category_item_event);
            tvTitleEvent = itemView.findViewById(R.id.text_title_item_event);
            tvTimeReadable = itemView.findViewById(R.id.text_readable_time_item_event);
            tvNumberArticles = itemView.findViewById(R.id.text_number_articles_item_event);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Event_ event_) {
            tvCategory.setText(event_.getCategory().getName());
            tvTitleEvent.setText(event_.getTitle());
            tvTimeReadable.setText(event_.getReadableTime());
            tvNumberArticles.setText(String.valueOf(event_.getNumArticles()) + " bài báo");

            Picasso.get().load(event_.getImage()).into(lnlCoverEvent);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //Sự kiện khi click vào một event
            //...
        }
    }


    public class StoryViewHolder extends RecyclerView.ViewHolder {
        private AutoScrollViewPager autoScrollViewPagerStories;
        private CircleIndicator indicator;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            autoScrollViewPagerStories = itemView.findViewById(R.id.viewpager_item_stories);
            indicator = itemView.findViewById(R.id.indicator_vp_item_stories);
        }

        public void bindData(ArrayList<Event> arrayEvents) {
            StoryAdapter storyAdapterVP = new StoryAdapter(arrayEvents, mContext);
            autoScrollViewPagerStories.setAdapter(storyAdapterVP);
            indicator.setViewPager(autoScrollViewPagerStories);
            autoScrollViewPagerStories.startAutoScroll();
            autoScrollViewPagerStories.setInterval(5000);
        }
    }

    @Override
    public int getItemCount() {
        if (arrayDatums == null) {
            return 0;
        }
        return arrayDatums.size();
    }
}
