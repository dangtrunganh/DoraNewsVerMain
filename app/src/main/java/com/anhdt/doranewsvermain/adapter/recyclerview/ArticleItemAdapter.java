package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralHomeFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralLatestNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ArticleItemAdapter extends RecyclerView.Adapter<ArticleItemAdapter.ArticleItemViewHolder> {
    public static final int LOAD_MORE_DETAIL_EVENT = 0;
    public static final int IN_HOME = 1;

    //Số bài viết tối đa mỗi lần load thêm là bao nhiêu
    private static final int NUMBER_VISIBLE_ARTICLES = 3;

    private Context mContext;
    private ArrayList<Article> mArrayArticles;
    private LayoutInflater mLayoutInflater;
    private FragmentManager fragmentManager;
    private int typeTabContent;
    private AddFragmentCallback addFragmentCallback;

    private ArrayList<Article> mCurrentArrayArticles;
    private TextView textLoadMore;
    private int currentItemCount;

    public ArticleItemAdapter(Context mContext, ArrayList<Article> mArrayArticles/*, FragmentManager fragmentManager*/,
                              int typeTabContent, AddFragmentCallback addFragmentCallback,
                              TextView textLoadMore, int typeLoadMore) {
        this.mContext = mContext;
        this.mArrayArticles = mArrayArticles;
        this.typeTabContent = typeTabContent;
        this.addFragmentCallback = addFragmentCallback;

        //Nếu ngoài home thì trường này là null, ví ko có load more
        this.textLoadMore = textLoadMore;
        if (typeLoadMore == IN_HOME) {
            //Trong màn home thì chính là nó luôn
            this.mCurrentArrayArticles = this.mArrayArticles;
        } else {
            this.mCurrentArrayArticles = new ArrayList<>();
            initUpdateThresHoldArticles();
        }
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
        Article article = mCurrentArrayArticles.get(position);
        articleItemViewHolder.bindData(article);
    }

    @Override
    public int getItemCount() {
        if (mCurrentArrayArticles == null) {
            return 0;
        }
        return mCurrentArrayArticles.size();
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
            Glide.with(itemView.getContext()).load(article.getImage()).
                    apply(new RequestOptions().override(400, 0).
                            placeholder(R.drawable.image_default).error(R.drawable.image_default))
                    .into(mImageCoverArticle);

            mTextSource.setText(article.getSource().getName());
            mTextTitle.setText(article.getTitle());
            mTextTimeReadable.setText(article.getReadableTime());
            mTextSummary.setText(GeneralTool.getSummaryOfArticle(article, ConstParam.MEDIUM));
        }

        @Override
        public void onClick(View v) {
            //Sự kiện khi kích vào một bài báo thường
            int position = getAdapterPosition();
            //Chuyển sang màn hình Chi tiết các bài báo
            Gson gson = new Gson();
            String jsonListArticles = gson.toJson(mCurrentArrayArticles);
            DetailNewsFragment detailNewsFragment = DetailNewsFragment.newInstance(jsonListArticles, position);
            detailNewsFragment.setAddFragmentCallback(addFragmentCallback);
            if (typeTabContent == ConstGeneralTypeTab.TYPE_TAB_HOME) {
                detailNewsFragment.setFragmentManager(GeneralHomeFragment.fragmentManagerHome);
                addFragmentCallback.addFrgCallback(detailNewsFragment);
            } else if (typeTabContent == ConstGeneralTypeTab.TYPE_TAB_LATEST_HOME) {
                detailNewsFragment.setFragmentManager(GeneralLatestNewsFragment.fragmentManagerLatest);
                addFragmentCallback.addFrgCallback(detailNewsFragment);
            }
        }
    }

//    public void updateListArticles(ArrayList<Article> articles) {
//        mCurrentArrayArticles.addAll(articles);
//        notifyDataSetChanged();
//    }


    //======Chỉ sử dụng các hàm này trong việc LoadMore ở DetailEventFragment======
    //Thực hiện việc nhét 3 thằng đầu vào trong RecyclerView
    public void initUpdateThresHoldArticles() {
        //Thực tế hàm này chỉ được gọi đúng 1 lần

        //Trong hàm khởi tạo, mArrayArticles là cả list full rồi
        if (mArrayArticles == null) {
            return;
        }
        if (mArrayArticles.size() <= NUMBER_VISIBLE_ARTICLES) {
            mCurrentArrayArticles.addAll(mArrayArticles);
            currentItemCount = mCurrentArrayArticles.size();
            textLoadMore.setVisibility(View.INVISIBLE);
            textLoadMore.setText("");
        } else {
            //List có nhiều hơn NUMBER_VISIBLE_ARTICLES
            for (int i = 0; i < NUMBER_VISIBLE_ARTICLES; i++) {
                mCurrentArrayArticles.add(mArrayArticles.get(i));
                currentItemCount = NUMBER_VISIBLE_ARTICLES;
            }
            textLoadMore.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    public void loadMoreArticles() {
        if (mArrayArticles.size() <= NUMBER_VISIBLE_ARTICLES) {
            return;
        }
        if (mArrayArticles.size() <= currentItemCount + NUMBER_VISIBLE_ARTICLES) {
            //Ví dụ: mListNews.size() = 5, currentItemCount = 3, NUMBER_VISIBLE_ARTICLES = 3
            for (int i = currentItemCount; i < mArrayArticles.size(); i++) {
                mCurrentArrayArticles.add(mArrayArticles.get(i));
            }
            currentItemCount = mCurrentArrayArticles.size();
            textLoadMore.setVisibility(View.INVISIBLE);
            textLoadMore.setText("");
        } else {
            for (int i = currentItemCount; i < currentItemCount + NUMBER_VISIBLE_ARTICLES; i++) {
                mCurrentArrayArticles.add(mArrayArticles.get(i));
            }
            currentItemCount = currentItemCount + NUMBER_VISIBLE_ARTICLES;
        }
        notifyDataSetChanged();
    }
}
