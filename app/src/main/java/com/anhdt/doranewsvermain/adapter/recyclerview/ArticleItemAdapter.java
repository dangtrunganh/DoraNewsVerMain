package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ArticleItemAdapter extends RecyclerView.Adapter<ArticleItemAdapter.ArticleItemViewHolder> implements UpdateUIFollowBookmarkChild {
    public static final int LOAD_MORE_DETAIL_EVENT = 0;
    public static final int IN_HOME = 1;

    //Số bài viết tối đa mỗi lần load thêm là bao nhiêu
    private static final int NUMBER_VISIBLE_ARTICLES = 3;

    private Context mContext;
    private ArrayList<Article> mArrayArticles;
    private LayoutInflater mLayoutInflater;
    private FragmentManager fragmentManager;
    //    private int typeTabContent;
    private AddFragmentCallback addFragmentCallback;

    private ArrayList<Article> mCurrentArrayArticles;
    private TextView textLoadMore;
    private int currentItemCount;

    public ArticleItemAdapter(Context mContext, ArrayList<Article> mArrayArticles/*, FragmentManager fragmentManager*/,
            /*int typeTabContent,*/ AddFragmentCallback addFragmentCallback,
                              TextView textLoadMore, int typeLoadMore) {
        this.mContext = mContext;
        this.mArrayArticles = mArrayArticles;
//        this.typeTabContent = typeTabContent;
        this.addFragmentCallback = addFragmentCallback;

        //Nếu ngoài home thì trường này là null, ví ko có load more
        this.textLoadMore = textLoadMore;
        if (typeLoadMore == IN_HOME) {
            //Trong màn home thì chính là nó luôn
            this.mCurrentArrayArticles = this.mArrayArticles;
        } else {
            //Không phải trong màn Home thì phải LoadMore
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
                        Glide.with(itemView.getContext()).load(/*"https://" + */article.getSource().getIcon()).
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
                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
            } else {
                //Sự kiện khi kích vào một bài báo thường
                //Chuyển sang màn hình Chi tiết các bài báo
                Gson gson = new Gson();
                String jsonListArticles = gson.toJson(mCurrentArrayArticles);
                DetailNewsFragment detailNewsFragment = DetailNewsFragment.newInstance(jsonListArticles, position, false);
                detailNewsFragment.setAddFragmentCallback(addFragmentCallback);

                addFragmentCallback.addFrgCallback(detailNewsFragment);
            }
//            if (typeTabContent == ConstGeneralTypeTab.TYPE_TAB_HOME) {
//                detailNewsFragment.setFragmentManager(GeneralHomeFragment.fragmentManagerHome);
//                addFragmentCallback.addFrgCallback(detailNewsFragment);
//            } else if (typeTabContent == ConstGeneralTypeTab.TYPE_TAB_LATEST_HOME) {
//                detailNewsFragment.setFragmentManager(GeneralLatestNewsFragment.fragmentManagerLatest);
//                addFragmentCallback.addFrgCallback(detailNewsFragment);
//            }
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
