package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstGeneral;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.HomeFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralHomeFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralLatestNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ArticleItemAdapter extends RecyclerView.Adapter<ArticleItemAdapter.ArticleItemViewHolder> {

    private Context mContext;

    private ArrayList<Article> mArrayArticles;

    private LayoutInflater mLayoutInflater;
    private FragmentManager fragmentManager;

    private int typeTabContent;

    private AddFragmentCallback addFragmentCallback;

    public ArticleItemAdapter(Context mContext, ArrayList<Article> mArrayArticles/*, FragmentManager fragmentManager*/, int typeTabContent, AddFragmentCallback addFragmentCallback) {
        this.mContext = mContext;
        this.mArrayArticles = mArrayArticles;
//        this.fragmentManager = fragmentManager;
        this.typeTabContent = typeTabContent;
        this.addFragmentCallback = addFragmentCallback;
    }

    @NonNull
    @Override
    public ArticleItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View viewArticle = mLayoutInflater.inflate(R.layout.item_article_hot_news_in_recycler_view, viewGroup, false);

        return new ArticleItemAdapter.ArticleItemViewHolder(viewArticle);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleItemViewHolder articleItemViewHolder, int position) {
        Article article = mArrayArticles.get(position);
        articleItemViewHolder.bindData(article);
    }

    @Override
    public int getItemCount() {
        if (mArrayArticles == null) {
            return 0;
        }
        return mArrayArticles.size();
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageCoverArticle;
        private TextView mTextSource;
        private TextView mTextTitle;
        private TextView mTextTimeReadable;
        private TextView mTextSummary;

        public ArticleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.text_title_articles_item_article);

            mImageCoverArticle = itemView.findViewById(R.id.image_cover_item_article);

            mTextSummary = itemView.findViewById(R.id.text_summary_item_article);

            mTextSource = itemView.findViewById(R.id.text_source_item_article);

            mTextTimeReadable = itemView.findViewById(R.id.text_time_item_article);

            itemView.setOnClickListener(this);
        }

        public void bindData(Article article) {
            //bind Data với bài báo thường
            if (article == null) {
                return;
            }
//            Log.d("article: ", article.getImage());
            Glide.with(itemView.getContext()).load(article.getImage()).
                    apply(new RequestOptions().override(400, 0).
                            placeholder(R.drawable.image_default).error(R.drawable.image_default))
                    .into(mImageCoverArticle);

            mTextSource.setText(article.getSource().getName());
            mTextTitle.setText(article.getTitle());
            mTextTimeReadable.setText(article.getReadableTime());
            boolean flag = true;
            for (int i = 0; i < article.getMedias().size(); i++) {
                if (article.getMedias().get(i).getType().equals(ConstParam.MEDIUM)) {
                    String summarization = article.getMedias().get(i).getBody().get(0).getContent();
                    mTextSummary.setText(summarization);
                    flag = false;
                    break;
                }
            }

            if (flag) {
                mTextSummary.setText("\n\n\n\n");
            }
        }

        @Override
        public void onClick(View v) {
            //Sự kiện khi kích vào một bài báo thường
            int position = getAdapterPosition();

            //Chuyển sang màn hình Chi tiết các bài báo
            Gson gson = new Gson();
            String jsonListArticles = gson.toJson(mArrayArticles);
            DetailNewsFragment detailNewsFragment = DetailNewsFragment.newInstance(jsonListArticles, position);
            detailNewsFragment.setAddFragmentCallback(addFragmentCallback);
//            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (typeTabContent == ConstGeneral.TYPE_TAB_HOME) {
//                ft.replace(R.id.main_container_frg_home, detailNewsFragment);
//                ft.addToBackStack(detailNewsFragment.getClass().getName());
//                ft.commit();
                Log.e("home-", "home");
                Log.e("home-jsonList", jsonListArticles);
                Log.e("home-position", String.valueOf(position));
                detailNewsFragment.setFragmentManager(GeneralHomeFragment.fragmentManagerHome);
                addFragmentCallback.addFrgCallback(detailNewsFragment);
            } else if (typeTabContent == ConstGeneral.TYPE_TAB_LATEST_HOME) {
                Log.e("latest-", "latest");
                Log.e("latest-jsonList", jsonListArticles);
                Log.e("latest-position", String.valueOf(position));
                detailNewsFragment.setFragmentManager(GeneralLatestNewsFragment.fragmentManagerLatest);
                addFragmentCallback.addFrgCallback(detailNewsFragment);
            }
        }
    }
    public void updateListArticles(ArrayList<Article> articles) {
        mArrayArticles.addAll(articles);
        notifyDataSetChanged();
    }
}
