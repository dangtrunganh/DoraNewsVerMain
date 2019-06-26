package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.autoviewpager.StoryAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.DetailStoryFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HotNewsAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements UpdateUIFollowBookmarkChild {
    private ArrayList<Datum> arrayDatums;
    private ILoadMore loadMore;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    //    private boolean flagFinishLoadData = false; //true, false ám chỉ lần load này đã phải cuối chưa? true tức là cuối rồi
    private boolean isLoading; //Load xong thì tắt ProgressBar đi
    //true là đang load
    //false là load xong rồi, tắt đi thôi

    private float newTopRecyclerView;

    private final int VIEW_TYPE_ARTICLE = 1, VIEW_TYPE_EVENT = 2, VIEW_TYPE_STORY = 3, ERROR_VIEW = 4, VIEW_TYPE_LOADING = 5, VIEW_TYPE_FOOTER = 6;

    private AddFragmentCallback addFragmentCallback;

    public HotNewsAdapter2(ArrayList<Datum> mArrayDatums, Context mContext, RecyclerView recyclerView, /*FragmentManager fragmentManager, int typeTabContent,*/ AddFragmentCallback addFragmentCallback) {
        //===Tạo footer===
        this.arrayDatums = new ArrayList<>();
        Datum datumFooter = new Datum();
        datumFooter.setFooter(true);
        this.arrayDatums.add(datumFooter);
        //======

        this.arrayDatums.addAll(0, mArrayDatums);
        this.mContext = mContext;
//        this.typeTabContent = typeTabContent;
        this.addFragmentCallback = addFragmentCallback;

        float yTopRecyclerView = recyclerView.getY();
        int heightStoryView = (int) (GeneralTool.getWidthScreen(mContext) * 0.9f + GeneralTool.convertDpsToPixels(mContext, 50.0f));
        this.newTopRecyclerView = yTopRecyclerView - (1.0f / 3) * heightStoryView;

        //handle khi có sự kiện loadmore
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int totalItemCount = linearLayoutManager.getItemCount();


                //====list rỗng====
                if (arrayDatums.size() == 0 || arrayDatums.size() == 1) {
                    return;
                }
                //======
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                int positionFirstVisible = linearLayoutManager.findFirstVisibleItemPosition();
//                Log.e("ff-first", String.valueOf(positionFirstVisible));
                int positionLastVisible = linearLayoutManager.findLastVisibleItemPosition();
//                Log.e("ff-last", String.valueOf(positionLastVisible));
                int size = positionLastVisible - positionFirstVisible;


                //Quy hết ra đơn vị pixels
                float height = recyclerView.getHeight();
//                float width = recyclerView.getWidth();

                float yBottomRecyclerView = yTopRecyclerView + height;

//                Log.e("mm-yTop", String.valueOf(yTopRecyclerView));
//                Log.e("mm-yTop-new", String.valueOf(newTopRecyclerView));
//                Log.e("mm-height", String.valueOf(height));

                for (int i = 0; i <= size; i++) {
                    int position = positionFirstVisible + i;
//                    Log.e("11-main-posi=", String.valueOf(position));
                    View view = linearLayoutManager.findViewByPosition(position);
                    float yTopView = view.getY();
                    float yBottomView = yTopView + heightStoryView;
                    Datum datum = arrayDatums.get(position);
                    if (datum == null) {
                        continue;
                    }
                    if (datum.isFooter()) {
                        return;
                    }
                    if (datum.getType() == TypeNewsConst.STORY) {
                        RecyclerView.ViewHolder viewHolderStory = recyclerView.findViewHolderForAdapterPosition(position);
                        if (viewHolderStory instanceof HotNewsAdapter2.StoryViewHolder) {
                            HotNewsAdapter2.StoryViewHolder storyViewHolder = (StoryViewHolder) viewHolderStory;
//                            Log.e("11-enter-yTop", "Position: " + String.valueOf(position) + " - " + String.valueOf(yTopView));
//                            Log.e("11-enter-yTopParent", "Position: " + String.valueOf(position) + " - " + String.valueOf(newTopRecyclerView));
//                            Log.e("11-enter-yBottom", "Position: " + String.valueOf(position) + " - " + String.valueOf(yBottomView));
//                            Log.e("11-enter-yBottomParent", "Position: " + String.valueOf(position) + " - " + String.valueOf(yBottomRecyclerView));
                            if (GeneralTool.checkIfChildOutIParent(yTopView, yBottomView, newTopRecyclerView, yBottomRecyclerView)) {
                                //Thỏa mãn nằm ngoài
                                //Chạy animation
                                //Lấy View Holder ra ^^
//                                if (!isAnimatingStory) {
//                                    //có 1 cái đang chạy, isAnimatingStory = true nhưng ko phải nó --> ko dừng đc trừ
//                                    continue;
//                                }
                                storyViewHolder.stopAnimation();
//                                isAnimatingStory = false;
                            } else {
                                //Nằm trong, chạy animation thôi ^^, tại một thời điểm chỉ cho 1 cái chạy
//                                if (isAnimatingStory) {
//                                    //Đang có cái khác chạy rồi, reject mn đi
//                                    continue;
//                                }
                                storyViewHolder.startAnimation();
//                                isAnimatingStory = true;
                            }
                        }
                    }
                }

                if (!isLoading /*&& totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)*/
                        && linearLayoutManager != null
                        && lastVisibleItem >= arrayDatums.size() - 3) {
                    if (loadMore != null)
                        loadMore.onLoadMore();
                    isLoading = true;
                }
            }
        });
    }

//    public boolean isFlagFinishLoadData() {
//        return flagFinishLoadData;
//    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayDatums.get(position) == null) {
            //Thằng cuối cùng, kiểm tra trước xem có phải thằng Loading ko?
            return VIEW_TYPE_LOADING;
        }
        //Ko phải Loading, xem có phải Footer ko?
        if (arrayDatums.get(position).isFooter()) {
            return VIEW_TYPE_FOOTER;
        }
        Datum datum = arrayDatums.get(position);
        int typeItem = datum.getType();
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
                View viewArticle = mLayoutInflater.inflate(R.layout.item_article_hot_news_in_recycler_view, viewGroup, false);
                return new HotNewsAdapter2.ArticleViewHolder(viewArticle);
            case VIEW_TYPE_EVENT:
                View viewEvent = mLayoutInflater.inflate(R.layout.item_event_hot_news, viewGroup, false);
                return new HotNewsAdapter2.EventViewHolder(viewEvent);
            case VIEW_TYPE_STORY:
                View viewStory = mLayoutInflater.inflate(R.layout.item_stories_hot_news, viewGroup, false);
                return new HotNewsAdapter2.StoryViewHolder(viewStory);
            case VIEW_TYPE_LOADING:
                View viewLoading = mLayoutInflater.inflate(R.layout.item_loading, viewGroup, false);
                return new HotNewsAdapter2.LoadingViewHolder(viewLoading);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view, viewGroup, false);
                return new HotNewsAdapter2.FooterViewHolder(viewFooter);
        }
        return null; //Với ERROR_VIEW
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Datum datum = arrayDatums.get(position);
        if (viewHolder instanceof ArticleViewHolder && datum.getType() == TypeNewsConst.ARTICLE) {
            //Liệu type có đúng?
            //Lấy ra list các articles
            Article article = datum.getArticle();
            if (article == null) {
                return;
            }
//            ArrayList<Article> articleArrayList = (ArrayList<Article>) datum.getArticles();
//            if (articleArrayList == null) {
//                return;
//            }
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) viewHolder;
            articleViewHolder.bindData(article);
        } else if (viewHolder instanceof EventViewHolder && datum.getType() == TypeNewsConst.EVENT) {
            Event event_ = datum.getEvent();
            if (event_ == null) {
                return;
            }
            EventViewHolder eventViewHolder = (EventViewHolder) viewHolder;
            eventViewHolder.bindData(event_);
        } else if (viewHolder instanceof StoryViewHolder && datum.getType() == TypeNewsConst.STORY) {
            Stories stories = datum.getStories();
            if (stories == null) {
                return;
            }
            ArrayList<Event> arrayListEvents = (ArrayList<Event>) stories.getEvents();
            StoryViewHolder storyViewHolder = (StoryViewHolder) viewHolder;

            String idStory = datum.getStories().getStoryId();

            //Chỉ chạy trên android M trở lên
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (idStory != null) {
//                    Log.e("uu-id-out-story", idStory);
                    storyViewHolder.bindData(arrayListEvents, idStory);
                } else {
                    Toast.makeText(mContext, "Error - idStory null!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (viewHolder instanceof LoadingViewHolder) {
//            if (flagFinishLoadData) {
//                //true - hết rồi, dừng load, không hiển thị LoadingProgressBar nữa
//                return;
//            }
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.setIndeterminate();
        } else if (viewHolder instanceof FooterViewHolder) {
            //FooterView
        }
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {

    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //do nothing, cần làm lại adapter này cho riêng màn NewsInCategoryFragment
//        for (int i = 0; i < arrayDatums.size(); i++) {
//            boolean k = false;
//            Datum datum = arrayDatums.get(i);
//            if (datum == null) {
//                continue;
//            }
//            if (datum.getType() == null) {
//                continue;
//            }
//            if (datum.getType() == TypeNewsConst.ARTICLE) {
//                ArrayList<Article> tempArticles = (ArrayList<Article>) datum.getArticles();
//                if (tempArticles == null) {
//                    continue;
//                }
//                if (tempArticles.size() == 0) {
//                    continue;
//                }
//                Log.e("pkll-", tempArticles.toString());
//                Log.e("pkll-idmain", idArticle + "");
//                for (int j = 0; j < tempArticles.size(); j++) {
//                    //quét cả list articles này
//                    int i1 = tempArticles.get(i).getId();
//                    int i2 = article.getId();
//                    if (i1 == i2) {
//                        tempArticles.get(i).setBookmarked(isBookmarked);
//                        notifyDataSetChanged();
//                        Log.e("pkll-xx", "done");
//                        k = true;
//                        break;
//                    }
//                }
//            }
//            if (k) {
//                break;
//            }
//        }
    }

    @Override
    public void addNotificationFragment() {

    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar_news);
        }

        public void setIndeterminate() {
            progressBar.setIndeterminate(true);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private RecyclerView recyclerArticle;
        private ImageView mImageCoverArticle;
        private TextView mTextSource;
        private TextView mTextTitle;
        private TextView mTextTimeReadable;
        private TextView mTextSummary;
        private ImageView mImageCoverSource;
        private ImageView mImageMore;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
//            recyclerArticle = itemView.findViewById(R.id.recycler_articles_hot_news);
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
            //=====set up bookmark=====
            if (article == null) {
                return;
            }
            ReadRealmToolForBookmarkArticle.setArticleBookmarked(mContext, article);
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
                        Glide.with(itemView.getContext()).load("https://" + article.getSource().getIcon()).
                                apply(new RequestOptions().override(400, 0).
                                        placeholder(R.drawable.image_default).error(R.drawable.image_default))
                                .into(mImageCoverSource);
                    }
                }
            }
//            //=========================
//            ArticleItemAdapter articleItemAdapter = new ArticleItemAdapter(mContext,
//                    articleArrayList, addFragmentCallback,
//                    null, ArticleItemAdapter.IN_HOME);
//            recyclerArticle.setHasFixedSize(true);
//            recyclerArticle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//            recyclerArticle.setAdapter(articleItemAdapter);
//            recyclerArticle.setNestedScrollingEnabled(false);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Datum datum = arrayDatums.get(position);
            Article article = datum.getArticle();
            if (article == null) {
                return;
            }
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
                            Toast.makeText(mContext, "Bỏ lưu!", Toast.LENGTH_SHORT).show();
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
                ArrayList<Article> articleArrayList = new ArrayList<>();
                articleArrayList.add(article);
                String jsonListArticles = gson.toJson(articleArrayList);
                DetailNewsFragment detailNewsFragment = DetailNewsFragment.newInstance(jsonListArticles, position);
                detailNewsFragment.setAddFragmentCallback(addFragmentCallback);

                addFragmentCallback.addFrgCallback(detailNewsFragment);
            }
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView lnlCoverEvent;
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
        public void bindData(Event event) {
            tvCategory.setText(event.getCategory().getName());
            tvTitleEvent.setText(event.getTitle());
            tvTimeReadable.setText(event.getReadableTime());
            tvNumberArticles.setText(String.valueOf(event.getNumArticles()) + " bài báo");


            if (event.getImage() != null) {
                if (!event.getImage().equals("")) {
//                    Picasso.get().load(event.getImage()).into(lnlCoverEvent);
                    Glide.with(itemView.getContext()).load(event.getImage()).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(lnlCoverEvent);
                }
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //Sự kiện khi click vào một event, mở màn hình chi tiết event đó lên
            //...
            Datum datum = arrayDatums.get(position);
            Event event = datum.getEvent();
            if (event == null) {
                return;
            }
            String idEvent = event.getId();
//            String titleEvent = event.getTitle();

            //Là sự kiện hiển thị chi tiết bài báo đơn lẻ, nên sẽ truyền idStory là DEFAULT
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(/*typeTabContent,*/ idEvent/*, titleEvent*/, DetailEventFragment.DEFAULT_ID_STORY, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                detailEventFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailEventFragment);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Không có kết nối mạng");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        }
    }

    private void loadDetailStory(String idStory/*,*//* String uId*/) {
        String uId = ReadCacheTool.getUId(mContext);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServerAPI apiService = retrofit.create(ServerAPI.class);

        Call<Stories> call = apiService.getDetailStory(idStory, uId);

        call.enqueue(new Callback<Stories>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {
                Stories stories = response.body();
                if (stories == null) {
                    Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int follow = stories.getFollow();
                //Kiểm tra biến này để bật/tắt follow
                ArrayList<Event> arrayListEvent = (ArrayList<Event>) stories.getEvents();
                if (arrayListEvent == null) {
                    return;
                }
                if (arrayListEvent.size() == 0) {
                    return;
                }
                //load data to recyclerView
//                eventAdapterHorizontal.updateListEvents(arrayListEvent);
                Gson gson = new Gson();
                String jsonListEvents = gson.toJson(arrayListEvent);
                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(/*typeTabContent*//*, jsonListEvents*//*,*/ idStory);
                detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailStoryFragment);
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        private AutoScrollViewPager autoScrollViewPagerStories;
        private CircleIndicator indicator;
        private TextView textViewStory;
        private ArrayList<Event> arrayListEvents;
        private String idStory;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            autoScrollViewPagerStories = itemView.findViewById(R.id.viewpager_item_stories);
            textViewStory = itemView.findViewById(R.id.text_view_full_story_item_stories);


//            ConstraintLayout constraintLayout = itemView.findViewById(R.id.constraint_layout_item_event_vpg);
//        constraintLayout.getHeight() = constraintLayout.getWidth();

            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();

            int width = metrics.widthPixels;
//            int height = metrics.heightPixels;
//            Log.e("xxx-width", String.valueOf(width));
//            Log.e("xxx-height", String.valueOf(height));
//            autoScrollViewPagerStories.getLayoutParams().width = metrics.widthPixels;
            ViewGroup.LayoutParams layoutParams = autoScrollViewPagerStories.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = (int) (width * 0.9);
            autoScrollViewPagerStories.setLayoutParams(layoutParams);

            indicator = itemView.findViewById(R.id.indicator_vp_item_stories);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bindData(ArrayList<Event> arrayEvents, String idStory) {
            this.arrayListEvents = arrayEvents;
            this.idStory = idStory;
            StoryAdapter storyAdapterVP = new StoryAdapter(arrayEvents, mContext, autoScrollViewPagerStories, idStory/*, typeTabContent*/, addFragmentCallback);
            autoScrollViewPagerStories.setAdapter(storyAdapterVP);
            indicator.setViewPager(autoScrollViewPagerStories);

            //Chỉ khi bindData() xong thì mới click xem thông tin toàn cảnh được ^^
            textViewStory.setOnClickListener(this);

            //Nếu cần tắt animation đi
//            autoScrollViewPagerStories.stopAutoScroll();
            autoScrollViewPagerStories.setScrollDurationFactor(4);
            autoScrollViewPagerStories.setInterval(5000);
            autoScrollViewPagerStories.setOnTouchListener(this);
        }

        public void stopAnimation() {
            autoScrollViewPagerStories.stopAutoScroll();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        public void startAnimation() {
            autoScrollViewPagerStories.startAutoScroll();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.text_view_full_story_item_stories) {
                //Mở Chi tiết story lên
                if (arrayListEvents == null) {
                    return;
                }
                if (arrayListEvents.size() == 0) {
                    return;
                }
                if (idStory == null) {
                    return;
                }
//                loadDetailStory(idStory, GeneralTool.getDeviceId(mContext));
//                Log.e("pipi-", idStory);
                if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                    DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(/*typeTabContent*//*, jsonListEvents*//*,*/ idStory);
                    detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
                    addFragmentCallback.addFrgCallback(detailStoryFragment);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage("Không có kết nối mạng");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }

//                Gson gson = new Gson();
//                String jsonListEvents = gson.toJson(arrayListEvents);
//                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(typeTabContent, jsonListEvents, idStory);
//                detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
//                addFragmentCallback.addFrgCallback(detailStoryFragment);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                Toast.makeText(mContext, "release!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "press!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    @Override
    public int getItemCount() {
        if (arrayDatums == null) {
            return 0;
        }
        return arrayDatums.size();
    }

    public void addItemLoading() {
        //Thêm vào trước Footer
//        arrayDatums.add(null);
//        notifyItemInserted(arrayDatums.size() - 1);

        arrayDatums.add(arrayDatums.size() - 1, null);
        notifyItemInserted(arrayDatums.size() - 2);
    }

    public void removeItemLoading() {
        //Thêm vào trước Footer
        if (arrayDatums.size() < 2) {
            return;
        }
        arrayDatums.remove(arrayDatums.size() - 2);
        notifyItemRemoved(arrayDatums.size() - 1);
    }

    public void updateListNews(List<Datum> listDatums) {
        if (GeneralTool.checkIfParentHasChild(arrayDatums, (ArrayList<Datum>) listDatums)) {
            //List cha chứa cả list con sau khi load more
            this.setLoaded();
            return;
        }
        arrayDatums.addAll(arrayDatums.size() - 1, listDatums);
        notifyDataSetChanged();
        this.setLoaded();
    }

//    public void updateSpecialListNews(List<Datum> listDatums) {
//        arrayDatums.clear();
//        arrayDatums.addAll(listDatums);
//        notifyDataSetChanged();
//        this.setLoaded();
//    }


    //Thực hiện nếu vuốt xuống refresh mà list bị trùng id thì bỏ qua, ko update nữa
    //Nếu mà 2 list ko trùng thì clear rôi update
    //Dùng được cho cả khi list đang ở chế độ offline mà online lại
    public void reloadInNormalState(List<Datum> listDatums) {
//        if (arrayDatums.equals(listDatums)) {
//            //equals thì chắc chỉ khi mới load lần đầu, xong ấn refresh luôn mới vào, khi ấy 2 list trùng nhau
//            return;
//        }
        if (GeneralTool.checkIfParentHasChild(arrayDatums, (ArrayList<Datum>) listDatums)) {
            //Sau khi load một hồi, nếu list hiện tại đã có nhiều data
            //Lần load mới trả về trùng với mấy thằng đầu của list thì ko loadMore nữa
            this.setLoaded();
            return;
        }
        arrayDatums.clear();

        //===Tạo footer===
        Datum datumFooter = new Datum();
        datumFooter.setFooter(true);
        this.arrayDatums.add(datumFooter);
        //======

        arrayDatums.addAll(0, listDatums);
        notifyDataSetChanged();
        this.setLoaded();
    }

    public void clearList() {
        arrayDatums.clear();

        //===Tạo footer===
        Datum datumFooter = new Datum();
        datumFooter.setFooter(true);
        this.arrayDatums.add(datumFooter);
        notifyDataSetChanged();
        this.setLoaded();
    }

    public void setLoaded() {
        isLoading = false;
    }

//    public void setFlagFinishLoadData(boolean flagFinishLoadData) {
//        this.flagFinishLoadData = flagFinishLoadData;
//    }
}
