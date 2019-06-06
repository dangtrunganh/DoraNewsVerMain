package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.fragment.DetailStoryFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoryFollowedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Datum> arrayStories;
    private AddFragmentCallback addFragmentCallback;
    private LayoutInflater mLayoutInflater;
//    private RecyclerView recyclerView;

    private final int VIEW_TYPE_STORIES = 1, VIEW_TYPE_FOOTER = 2;

    public StoryFollowedAdapter(Context mContext, ArrayList<Datum> arrayStories, AddFragmentCallback addFragmentCallback) {
        this.mContext = mContext;
        this.arrayStories = arrayStories;
        this.addFragmentCallback = addFragmentCallback;
        if (this.arrayStories != null) {
            this.arrayStories.add(null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayStories.get(position) == null) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_STORIES;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        switch (viewType) {
            case VIEW_TYPE_STORIES:
                View viewArticle = mLayoutInflater.inflate(R.layout.item_stories_followed, viewGroup, false);
                return new StoryFollowedAdapter.StoryViewHolder(viewArticle);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new StoryFollowedAdapter.FooterViewHolder(viewFooter);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Datum datum = arrayStories.get(position);
        if (viewHolder instanceof StoryFollowedAdapter.StoryViewHolder) {
            if (datum.getType() == TypeNewsConst.STORY) {
                Stories stories = datum.getStories();
                if (stories == null) {
                    return;
                }
                String idStory = stories.getStoryId();
                StoryViewHolder storyViewHolder = (StoryViewHolder) viewHolder;
                storyViewHolder.bindData(stories, idStory);
            }
        } else if (viewHolder instanceof StoryFollowedAdapter.FooterViewHolder) {
            //Footer, do nothing
        }
    }

    @Override
    public int getItemCount() {
        return this.arrayStories.size();
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textCategory;
        private TextView textTitleStory;
        private TextView textNumberEvent;
        private ImageView imageCoverStory;
        private ImageView imageMore;
        private String idStory;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_category_item_stories_followed);
            textTitleStory = itemView.findViewById(R.id.text_title_item_stories_followed);
            textNumberEvent = itemView.findViewById(R.id.text_number_events_item_stories_followed);
            imageCoverStory = itemView.findViewById(R.id.image_cover_item_stories_followed);
            imageMore = itemView.findViewById(R.id.image_more_item_stories_followed);
            itemView.setOnClickListener(this);
//            imageMore.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Stories stories, String idStory) {
            this.idStory = idStory;
            String category = stories.getCategory().getName();
            if (category != null) {
                textCategory.setText(category);
            }
            String titleStory = stories.getTitle();
            if (titleStory != null) {
                textTitleStory.setText(titleStory);
            }
            String numberOfEvents = String.valueOf(stories.getNumberOfEvents());
            textNumberEvent.setText(numberOfEvents + " sự kiện");

            String urlImage = stories.getImage();
            if (urlImage != null) {
                if (!urlImage.equals("")) {
                    Glide.with(mContext).load(urlImage).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(imageCoverStory);
                }
            }

        }

        @Override
        public void onClick(View v) {
            //Hiện chỉ bắt sự kiện click vào cả item to, mở ra màn chi tiết event tương ứng?
            if (idStory == null) {
                return;
            }
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(idStory);
                detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailStoryFragment);
            } else {
                //Mất mạng
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Không có kết nối mạng");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }

        }
    }

    public void updateListNews(List<Datum> listDatums) {
        if (listDatums == null) {
            return;
        }
        if (GeneralTool.checkIfParentHasChild(arrayStories, (ArrayList<Datum>) listDatums)) {
            //List cha chứa cả list con sau khi load more
            return;
        }
        arrayStories.clear();
        arrayStories.add(null);
        arrayStories.addAll(0, listDatums);
        notifyDataSetChanged();
    }

    public void addNewStoryFollowed(Stories stories) {
        Datum newDatum = new Datum();
        newDatum.setStories(stories);
        newDatum.setId(stories.getStoryId());
        newDatum.setType(TypeNewsConst.STORY);
        arrayStories.add(0, newDatum);
        notifyDataSetChanged();
    }

    public void removeStoryFollowed(String idStory) {
        for (int i = 0; i < arrayStories.size(); i++) {
            Stories currentStory = arrayStories.get(i).getStories();
            if (currentStory.getStoryId().equals(idStory)) {
                arrayStories.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public ArrayList<Datum> getArrayStories() {
        return arrayStories;
    }
}
