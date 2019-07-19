package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newssourceresult.NewsSource;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewsSourceAdapter extends RecyclerView.Adapter<NewsSourceAdapter.ViewHolder> implements View.OnClickListener, SectionIndexer {
    private List<NewsSource> mListSource;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private TextView textPickDone, textPickAll;
    private List<NewsSource> mListSourceChosen;
    private boolean isPickAllMode;
    private ArrayList<Integer> mSectionPositions;

    public NewsSourceAdapter(List<NewsSource> mListSource, Context mContext,
                             TextView textPickDone, TextView textPickAll) {
        this.mListSource = mListSource;
        this.mContext = mContext;
        this.textPickDone = textPickDone;
        this.textPickAll = textPickAll;
        this.mListSourceChosen = new ArrayList<>();
        this.textPickAll.setOnClickListener(this);
    }

    @NonNull
    @Override
    public NewsSourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = mLayoutInflater.inflate(R.layout.item_news_source, parent, false);
        return new NewsSourceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindData(mListSource.get(position));
    }

    @Override
    public int getItemCount() {
        if (mListSource == null) {
            return 0;
        }
        return mListSource.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_pick_all_source_act:
                if (!isPickAllMode) {
                    this.textPickAll.setText("Bỏ chọn tất cả");
                    isPickAllMode = true;
                    for (NewsSource newsSourceAll : mListSource) {
                        newsSourceAll.setSelected(true);
                    }
                    mListSourceChosen.clear();
                    mListSourceChosen.addAll(mListSource);
                    notifyDataSetChanged();
                } else {
                    this.textPickAll.setText("Chọn tất cả");
                    isPickAllMode = false;
                    for (NewsSource newsSourceAll : mListSource) {
                        newsSourceAll.setSelected(false);
                    }
                    mListSourceChosen.clear();
                    notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = mListSource.size(); i < size; i++) {
            String section = String.valueOf(mListSource.get(i).getId().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageCoverSource, mImagePickedSource;
        private TextView mTextNameSource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageCoverSource = itemView.findViewById(R.id.image_cover_item_source);
            mImagePickedSource = itemView.findViewById(R.id.image_pick_item_source);
            mTextNameSource = itemView.findViewById(R.id.text_name_item_source);
            itemView.setOnClickListener(this);
        }

        public void bindData(NewsSource newsSource) {
            if (newsSource == null) {
                return;
            }
            String iconUrl = newsSource.getIcon();
            if (iconUrl != null) {
                if (!iconUrl.equals("")) {
                    Glide.with(itemView.getContext()).load(/*"https://" + */iconUrl).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(mImageCoverSource);
                }
            }
            mTextNameSource.setText(newsSource.getName());
            if (newsSource.isSelected()) {
                mImagePickedSource.setVisibility(View.VISIBLE);
            } else {
                mImagePickedSource.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            NewsSource currentNewsSource = mListSource.get(position);
            if (currentNewsSource.isSelected()) { //Bỏ chọn
                currentNewsSource.setSelected(false);
                mImagePickedSource.setVisibility(View.GONE);

                for (Iterator<NewsSource> iterator = mListSourceChosen.iterator(); iterator.hasNext(); ) {
                    NewsSource value = iterator.next();
                    if (value.getId().equals(currentNewsSource.getId())) {
                        iterator.remove();
                    }
                }
            } else {
                currentNewsSource.setSelected(true);
                mImagePickedSource.setVisibility(View.VISIBLE);
                mListSourceChosen.add(currentNewsSource);
            }
        }
    }

    public void updateListSources(List<NewsSource> listSources) {
        mListSource = listSources;
        //update những source đã pick rồi trong danh sách
        if (mListSourceChosen != null) {
            if (mListSourceChosen.size() != 0) {
                for (NewsSource newsSourceAll : mListSource) {
                    for (NewsSource newsSourceChosen : mListSourceChosen) {
                        if (newsSourceAll.getId().equals(newsSourceChosen.getId())) {
                            newsSourceAll.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setmListSourceChosen(List<NewsSource> mListSourceChosen) {
        this.mListSourceChosen = mListSourceChosen;

        if (mListSourceChosen != null) {
            if (mListSourceChosen.size() != 0) {
                for (NewsSource newsSourceAll : mListSource) {
                    for (NewsSource newsSourceChosen : mListSourceChosen) {
                        if (newsSourceAll.getId().equals(newsSourceChosen.getId())) {
                            newsSourceAll.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<NewsSource> getmListSourceChosen() {
        return mListSourceChosen;
    }
}
