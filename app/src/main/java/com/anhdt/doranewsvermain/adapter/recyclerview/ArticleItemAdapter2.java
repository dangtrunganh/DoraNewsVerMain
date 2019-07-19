package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ArticleItemAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements UpdateUIFollowBookmarkChild {
    //Số bài viết tối đa mỗi lần load thêm là bao nhiêu
    public static final int DEFAULT_ID_ARTICLE_FOOTER = -101;
    private static final int NUMBER_VISIBLE_ARTICLES = 5;
    private final int VIEW_TYPE_ARTICLE = 1, VIEW_TYPE_FOOTER = 2, VIEW_TYPE_LOADING = 3;
    private Context mContext;
    private ArrayList<Article> mArrayArticles;
    private LayoutInflater mLayoutInflater;
    private AddFragmentCallback addFragmentCallback;
    private ArrayList<Article> mCurrentArrayArticles;
    private int currentItemCount;
    private RecyclerView recyclerView;

    private boolean isLoading;
    private ILoadMore loadMore;

    //Adapter này chỉ dùng cho màn DetailEvent
    public ArticleItemAdapter2(Context mContext, ArrayList<Article> mArrayArticles, AddFragmentCallback addFragmentCallback,
                               RecyclerView recyclerView) {
        this.mContext = mContext;
        this.mArrayArticles = mArrayArticles;
        this.addFragmentCallback = addFragmentCallback;

        //Nếu ngoài home thì trường này là null, ví ko có load more
        this.mCurrentArrayArticles = new ArrayList<>();
        this.recyclerView = recyclerView;
        //load more
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //====
                int lastVisibleItem = 0;
                if (linearLayoutManager != null) {
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }
                if (!isLoading && lastVisibleItem >= mCurrentArrayArticles.size() - 2 && currentItemCount < mCurrentArrayArticles.size()) {
                    if (loadMore != null) {
                        loadMore.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }


    @Override
    public int getItemViewType(int position) {
        if (mCurrentArrayArticles.get(position) == null) {
            return VIEW_TYPE_LOADING;
        }
        if (mCurrentArrayArticles.get(position).getId() == DEFAULT_ID_ARTICLE_FOOTER) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_ARTICLE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
//        View viewArticle = mLayoutInflater.inflate(R.layout.item_article_hot_news_in_recycler_view, viewGroup, false);
//
//        return new ArticleItemAdapter2.ArticleItemViewHolder(viewArticle);


        //===
        switch (viewType) {
            case VIEW_TYPE_ARTICLE:
                View viewArticle = mLayoutInflater.inflate(R.layout.item_article_hot_news_in_recycler_view, viewGroup, false);
                return new ArticleItemAdapter2.ArticleItemViewHolder(viewArticle);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view_stories_followed, viewGroup, false);
                return new ArticleItemAdapter2.FooterViewHolder(viewFooter);
            case VIEW_TYPE_LOADING:
                View viewLoading = mLayoutInflater.inflate(R.layout.item_loading, viewGroup, false);
                return new ArticleItemAdapter2.LoadingViewHolder(viewLoading);
        }
        return null;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar_news);
        }

        public void setIndeterminate() {
            progressBar.setIndeterminate(true);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
//        Article article = mCurrentArrayArticles.get(position);
//        articleItemViewHolder.bindData(article);

        //====
        Article article = mCurrentArrayArticles.get(position);
        if (viewHolder instanceof ArticleItemAdapter2.ArticleItemViewHolder) {
            if (article == null) {
                return;
            }
            ArticleItemAdapter2.ArticleItemViewHolder articleItemViewHolder = (ArticleItemViewHolder) viewHolder;
            articleItemViewHolder.bindData(article);

        } else if (viewHolder instanceof ArticleItemAdapter2.LoadingViewHolder) {
            ArticleItemAdapter2.LoadingViewHolder loadingViewHolder = (ArticleItemAdapter2.LoadingViewHolder) viewHolder;
            loadingViewHolder.setIndeterminate();
        } else if (viewHolder instanceof ArticleItemAdapter2.FooterViewHolder) {
            //Footer, do nothing
        }
    }

    @Override
    public int getItemCount() {
        if (mCurrentArrayArticles == null) {
            return 0;
        }
        return mCurrentArrayArticles.size();
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //do nothing
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //update lại list
        //true - lưu lại, false - bỏ lưu
        for (Article articleTmp : mArrayArticles) {
            if (articleTmp.getId().equals(article.getId())) {
                articleTmp.setBookmarked(isBookmarked);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageCoverArticle;
        private TextView mTextSource;
        private TextView mTextTitle;
        private TextView mTextTimeReadable;
        private TextView mTextSummary;
        private ImageView mImageCoverSource;
        private ImageView mImageMore;

        public ArticleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.text_title_articles_item_article);
            mImageCoverArticle = itemView.findViewById(R.id.image_cover_item_article);
            mTextSummary = itemView.findViewById(R.id.text_summary_item_article);
            mTextSource = itemView.findViewById(R.id.text_source_item_article);
            mTextTimeReadable = itemView.findViewById(R.id.text_time_item_article);
            mImageCoverSource = itemView.findViewById(R.id.image_cover_source_item_article);
            mImageMore = itemView.findViewById(R.id.image_more_item_article);

            itemView.setOnClickListener(this);
            mImageMore.setOnClickListener(this);
        }

        public void bindData(Article article) {
            //bind Data với bài báo thường
            if (article == null) {
                return;
            }
            mTextSource.setText(article.getSource().getName());
            mTextTitle.setText(article.getTitle());
            mTextTimeReadable.setText(article.getReadableTime());

            String mediumSummary = GeneralTool.getSummaryOfArticle(article, ConstParam.MEDIUM);
            String upperString = mediumSummary.substring(0, 1).toUpperCase() + mediumSummary.substring(1);
            mTextSummary.setText(upperString);

//            mTextSummary.setText(GeneralTool.getSummaryOfArticle(article, ConstParam.MEDIUM));
            if (article.getImage() != null) {
                if (!article.getImage().equals("")) {
                    Glide.with(itemView.getContext()).load(article.getImage()).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(mImageCoverArticle);
                }
            }
            if (article.getSource() != null) {
                if (article.getSource().getIcon() != null) {
                    if (!article.getSource().getIcon().equals("")) {
                        Glide.with(itemView.getContext()).load(/*"https://" +*/ article.getSource().getIcon()).
                                apply(new RequestOptions().override(400, 0).
                                        placeholder(R.drawable.image_default).error(R.drawable.image_default))
                                .into(mImageCoverSource);
                    }
                }
            }

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Article article = mCurrentArrayArticles.get(position);
            if (v.getId() == R.id.image_more_item_article) {
                //click more button
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View sheetView = inflater.inflate(R.layout.fragment_bottom_sheet_book_mark_article, null);
                LinearLayout bookmark = sheetView.findViewById(R.id.layout_bookmark_menu_bottom);
                LinearLayout cancel = sheetView.findViewById(R.id.layout_cancel_menu_bottom);
                TextView textBookmark = sheetView.findViewById(R.id.text_bookmark_menu_bottom);
                mBottomSheetDialog.setContentView(sheetView);
                if (article.isBookmarked()) {
                    //Đã lưu thì "bỏ lưu"
                    textBookmark.setText("Bỏ lưu");
                } else {
                    //Chưa lưu thì "Lưu lại"
                    textBookmark.setText("Lưu lại");
                }
                mBottomSheetDialog.show();
                bookmark.setOnClickListener(v12 -> {
                    mBottomSheetDialog.dismiss();
                    if (article.isBookmarked()) {
                        //Bỏ lưu
                        article.setBookmarked(false);
                        ReadRealmToolForBookmarkArticle.deleteArticleBookmark(mContext, article);
                        addFragmentCallback.updateListArticleBookmarkInAddFrag(false, article.getId(), article);
//                            Toast.makeText(mContext, "Bỏ lưu!", Toast.LENGTH_SHORT).show();
                    } else {
                        //Lưu lại
                        article.setBookmarked(true);
                        ReadRealmToolForBookmarkArticle.addArticleToRealm(mContext, article);
                        addFragmentCallback.updateListArticleBookmarkInAddFrag(true, article.getId(), article);
                        Toast.makeText(mContext, "Đã lưu!", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setOnClickListener(v1 -> mBottomSheetDialog.dismiss());
            } else {
                //Sự kiện khi kích vào một bài báo thường
                //Chuyển sang màn hình Chi tiết các bài báo
                Gson gson = new Gson();
                String jsonListArticles = gson.toJson(mArrayArticles);
                DetailNewsFragment detailNewsFragment = DetailNewsFragment.newInstance(jsonListArticles, position, true);
                detailNewsFragment.setAddFragmentCallback(addFragmentCallback);

                addFragmentCallback.addFrgCallback(detailNewsFragment);
            }
        }
    }

    //    public void updateListArticles(ArrayList<Article> articles) {
//        mCurrentArrayArticles.addAll(articles);
//        notifyDataSetChanged();
//    }
    public void setLoaded() {
        isLoading = false;
    }

    //======Chỉ sử dụng các hàm này trong việc LoadMore ở DetailEventFragment======
    //Thực hiện việc nhét 3 thằng đầu vào trong RecyclerView
    public void initUpdateThresHoldArticles() {
        //Thực tế hàm này chỉ được gọi đúng 1 lần
        //Trong hàm khởi tạo, mArrayArticles là cả list full rồi
        if (mArrayArticles == null) {
            return;
        }
        Article articleFooter = new Article();
        articleFooter.setId(DEFAULT_ID_ARTICLE_FOOTER);
        mCurrentArrayArticles.add(articleFooter);
        if (mArrayArticles.size() <= NUMBER_VISIBLE_ARTICLES) {
            mCurrentArrayArticles.addAll(0, mArrayArticles);
            currentItemCount = mCurrentArrayArticles.size();
        } else {
            //List có nhiều hơn NUMBER_VISIBLE_ARTICLES
            for (int i = 0; i < NUMBER_VISIBLE_ARTICLES; i++) {
                mCurrentArrayArticles.add(mCurrentArrayArticles.size() - 1, mArrayArticles.get(i));
                currentItemCount = NUMBER_VISIBLE_ARTICLES;
            }
        }
        notifyDataSetChanged();
    }

    public void addItemLoading() {
        //Thêm vào trước Footer
//        arrayDatums.add(null);
//        notifyItemInserted(arrayDatums.size() - 1);
        mCurrentArrayArticles.add(mCurrentArrayArticles.size() - 1, null);
        notifyItemInserted(mCurrentArrayArticles.size() - 2);
    }

    public void removeItemLoading() {
        //Thêm vào trước Footer
        if (mCurrentArrayArticles.size() < 2) {
            return;
        }
        mCurrentArrayArticles.remove(mCurrentArrayArticles.size() - 2);
        notifyItemRemoved(mCurrentArrayArticles.size() - 1);
    }

    public void updateListArticles(ArrayList<Article> listArticles) {
        //Dùng cho load lần đầu và refresh
        if (listArticles == null) {
            //Đây là listArticles tổng thể
            return;
        }
        mArrayArticles.clear();
        mArrayArticles.addAll(listArticles);
        initUpdateThresHoldArticles();
        notifyDataSetChanged();
    }


    //LoadMore Articles
    public void loadMoreArticles() {
        if (mArrayArticles.size() <= NUMBER_VISIBLE_ARTICLES) {
            this.setLoaded();
            return;
        }
        if (mArrayArticles.size() <= currentItemCount + NUMBER_VISIBLE_ARTICLES) {
            //Ví dụ: mListNews.size() = 5, currentItemCount = 3, NUMBER_VISIBLE_ARTICLES = 3
            for (int i = currentItemCount; i < mArrayArticles.size(); i++) {
                mCurrentArrayArticles.add(mCurrentArrayArticles.size() - 1, mArrayArticles.get(i));
            }
            currentItemCount = mCurrentArrayArticles.size();

            //Load all done
        } else {
            for (int i = currentItemCount; i < currentItemCount + NUMBER_VISIBLE_ARTICLES; i++) {
                mCurrentArrayArticles.add(mCurrentArrayArticles.size() - 1, mArrayArticles.get(i));
            }
            currentItemCount = currentItemCount + NUMBER_VISIBLE_ARTICLES;
        }
        this.setLoaded();
        notifyDataSetChanged();
    }

    public void addNewArticleBookmarked(Article article) {
        mCurrentArrayArticles.add(0, article);
        notifyDataSetChanged();
    }

    public void removeArticleBookmarked(int idArticle) {
        for (int i = 0; i < mCurrentArrayArticles.size(); i++) {
            Article article = mCurrentArrayArticles.get(i);
            if (article.getId() == idArticle) {
                mCurrentArrayArticles.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void clearAndReloadInSearching(List<Article> articles) {
        mCurrentArrayArticles.clear();
        mCurrentArrayArticles.addAll(0, articles);
        notifyDataSetChanged();
    }

    public void clearList() {
        mCurrentArrayArticles.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Article> getmCurrentArrayArticles() {
        return mCurrentArrayArticles;
    }
}
