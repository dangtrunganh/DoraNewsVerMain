package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> mListCategories;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private TextView textQuantityChosen;
    private List<Category> mListCategoryChosen;
    private Button btnChoose;

    @SuppressLint("SetTextI18n")
    public CategoryAdapter(List<Category> mListCategories, Context mContext, TextView textQuantityChosen, Button btnChose) {
        this.mListCategories = mListCategories;
        this.mContext = mContext;
        this.textQuantityChosen = textQuantityChosen;
        this.mListCategoryChosen = new ArrayList<>();
        this.btnChoose = btnChose;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = mLayoutInflater.inflate(R.layout.item_category_2, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mListCategories.get(position));
    }

    @Override
    public int getItemCount() {
        if (mListCategories == null) {
            return 0;
        }
        return mListCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private RelativeLayout mImageInvisible;
        private ImageView mImageInvisible;
        private CircleImageView mImageCoverCategory;
        private TextView mTextTitleCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageInvisible = itemView.findViewById(R.id.relative_invisible_item_category);
            mImageCoverCategory = itemView.findViewById(R.id.image_cover_item_category);
            mTextTitleCategory = itemView.findViewById(R.id.text_title_item_category);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Category category) {
            if (category == null) {
                return;
            }
            if (category.getUrlImage() != null) {
                Glide.with(itemView.getContext()).load(category.getUrlImage()).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(mImageCoverCategory);
            }
            mTextTitleCategory.setText(category.getName());
            if (category.isSelected()) {
                mImageInvisible.setVisibility(View.VISIBLE);
            } else {
                mImageInvisible.setVisibility(View.INVISIBLE);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
//            mContext.startActivity(new Intent(mContext, DetailEventActivity.class));
            Category category = mListCategories.get(position);
            if (!category.isSelected()) {
                mImageInvisible.setVisibility(View.VISIBLE);
                category.setSelected(true);
                mListCategoryChosen.add(category);
                textQuantityChosen.setText("(Đã chọn " + mListCategoryChosen.size() + "/" + mListCategories.size() + ")");
                if (mListCategoryChosen.size() >= 3) {
                    btnChoose.setVisibility(View.VISIBLE);
//                    btnChoose.setEnabled(true);
//                    btnChoose.setText("OK");
//                    btnChoose.setBackgroundColor(0xff64DD17);
                } else {
                    btnChoose.setVisibility(View.GONE);
//                    btnChoose.setEnabled(false);
//                    btnChoose.setText("CHỌN CHỦ ĐỀ BẠN QUAN TÂM");
//                    btnChoose.setBackgroundColor(0x66282525);
                }
            } else {
                mImageInvisible.setVisibility(View.INVISIBLE);
                category.setSelected(false);
                Log.e("BEFORE", mListCategoryChosen.size() + " " + mListCategoryChosen.toString());
//                for (Category category1 : mListCategoryChosen) {
//                    if (category.getId().equals(category1.getId())) {
//                        mListCategoryChosen.remove(category1);
//                    }
//                }
                for (Iterator<Category> iterator = mListCategoryChosen.iterator(); iterator.hasNext(); ) {
                    Category value = iterator.next();
                    if (value.getId().equals(category.getId())) {
                        iterator.remove();
                    }
                }
//                mListCategoryChosen.remove(category);
                Log.e("AFTER", mListCategoryChosen.size() + " " + mListCategoryChosen.toString());
                textQuantityChosen.setText("(Đã chọn " + mListCategoryChosen.size() + "/" + mListCategories.size() + ")");
                if (mListCategoryChosen.size() >= 3) {
                    btnChoose.setVisibility(View.VISIBLE);
//                    btnChoose.setEnabled(true);
//                    btnChoose.setText("OK");
//                    btnChoose.setBackgroundColor(0xff64DD17);
                } else {
                    btnChoose.setVisibility(View.GONE);
//                    btnChoose.setEnabled(false);
//                    btnChoose.setText("CHỌN CHỦ ĐỀ BẠN QUAN TÂM");
//                    btnChoose.setBackgroundColor(0x66282525);
                }
            }
        }
    }

    /**
     * @return List of songs
     */
    public List<Category> getListCategories() {
        return mListCategories;
    }

    /**
     * Update adapter when list events is changed
     *
     * @param listCategories List songs after being changed
     */
    public void updateListCategories(List<Category> listCategories) {
        mListCategories = listCategories;
        notifyDataSetChanged();
    }

    public List<Category> getListCategoryChosen() {
        return mListCategoryChosen;
    }

    public void setListCategoryChosen(List<Category> listCategoryChosen) {
        mListCategoryChosen = listCategoryChosen;
    }
}
