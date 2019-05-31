package com.anhdt.doranewsvermain.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.autoviewpager.StoryAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.constant.TypeNewsConst;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.DetailStoryFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HotNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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

//    public static final int VISIBLE_THRESHOLD = 6;

//    private boolean isAnimatingStory = false;

//    private FragmentManager fragmentManager;

//    private int typeTabContent; // 1 or 2 - tab home hay tab latest

    private AddFragmentCallback addFragmentCallback;

    public HotNewsAdapter(ArrayList<Datum> mArrayDatums, Context mContext, RecyclerView recyclerView, /*FragmentManager fragmentManager, int typeTabContent,*/ AddFragmentCallback addFragmentCallback) {
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
                Log.e("ff-first", String.valueOf(positionFirstVisible));
                int positionLastVisible = linearLayoutManager.findLastVisibleItemPosition();
                Log.e("ff-last", String.valueOf(positionLastVisible));
                int size = positionLastVisible - positionFirstVisible;


                //Quy hết ra đơn vị pixels
                float height = recyclerView.getHeight();
//                float width = recyclerView.getWidth();

                float yBottomRecyclerView = yTopRecyclerView + height;

                Log.e("mm-yTop", String.valueOf(yTopRecyclerView));
                Log.e("mm-yTop-new", String.valueOf(newTopRecyclerView));
                Log.e("mm-height", String.valueOf(height));

                for (int i = 0; i <= size; i++) {
                    int position = positionFirstVisible + i;
                    Log.e("11-main-posi=", String.valueOf(position));
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
                        if (viewHolderStory instanceof HotNewsAdapter.StoryViewHolder) {
                            HotNewsAdapter.StoryViewHolder storyViewHolder = (StoryViewHolder) viewHolderStory;
                            Log.e("11-enter-yTop", "Position: " + String.valueOf(position) + " - " + String.valueOf(yTopView));
                            Log.e("11-enter-yTopParent", "Position: " + String.valueOf(position) + " - " + String.valueOf(newTopRecyclerView));
                            Log.e("11-enter-yBottom", "Position: " + String.valueOf(position) + " - " + String.valueOf(yBottomView));
                            Log.e("11-enter-yBottomParent", "Position: " + String.valueOf(position) + " - " + String.valueOf(yBottomRecyclerView));
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
//        if (datum == null) {
//        Log.e("ppl-", position + "");
//        Log.e("ppl-", arrayDatums.toString());
//        }
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
                View viewArticle = mLayoutInflater.inflate(R.layout.item_article_hot_news, viewGroup, false);
                return new HotNewsAdapter.ArticleViewHolder(viewArticle);
            case VIEW_TYPE_EVENT:
                View viewEvent = mLayoutInflater.inflate(R.layout.item_event_hot_news, viewGroup, false);
                return new HotNewsAdapter.EventViewHolder(viewEvent);
            case VIEW_TYPE_STORY:
                View viewStory = mLayoutInflater.inflate(R.layout.item_stories_hot_news, viewGroup, false);
                return new HotNewsAdapter.StoryViewHolder(viewStory);
            case VIEW_TYPE_LOADING:
                View viewLoading = mLayoutInflater.inflate(R.layout.item_loading, viewGroup, false);
                return new HotNewsAdapter.LoadingViewHolder(viewLoading);
            case VIEW_TYPE_FOOTER:
                View viewFooter = mLayoutInflater.inflate(R.layout.item_footer_recycler_view, viewGroup, false);
                return new HotNewsAdapter.FooterViewHolder(viewFooter);
        }
        return null; //Với ERROR_VIEW
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Datum datum = arrayDatums.get(position);
        if (viewHolder instanceof ArticleViewHolder && datum.getType() == TypeNewsConst.ARTICLE) {
            //Liệu type có đúng?
            //Lấy ra list các articles
            ArrayList<Article> articleArrayList = (ArrayList<Article>) datum.getArticles();
            if (articleArrayList == null) {
                return;
            }
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) viewHolder;
            articleViewHolder.bindData(articleArrayList);
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

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerArticle;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerArticle = itemView.findViewById(R.id.recycler_articles_hot_news);
        }

        public void bindData(ArrayList<Article> articleArrayList) {
            //=====set up bookmark=====
            //Không biết như này có được không?
            ReadRealmToolForBookmarkArticle.setListBookmark(mContext, articleArrayList);
            //=========================
            ArticleItemAdapter articleItemAdapter = new ArticleItemAdapter(mContext,
                    articleArrayList, addFragmentCallback,
                    null, ArticleItemAdapter.IN_HOME);
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
            DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(/*typeTabContent,*/ idEvent/*, titleEvent*/, DetailEventFragment.DEFAULT_ID_STORY, DetailEventFragment.DEFAULT_LIST_OF_STORY);
            detailEventFragment.setAddFragmentCallback(addFragmentCallback);
            addFragmentCallback.addFrgCallback(detailEventFragment);
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

    public class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        }

        public void stopAnimation() {
            autoScrollViewPagerStories.stopAutoScroll();
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
                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(/*typeTabContent*//*, jsonListEvents*//*,*/ idStory);
                detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
                addFragmentCallback.addFrgCallback(detailStoryFragment);
//                Gson gson = new Gson();
//                String jsonListEvents = gson.toJson(arrayListEvents);
//                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(typeTabContent, jsonListEvents, idStory);
//                detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
//                addFragmentCallback.addFrgCallback(detailStoryFragment);
            }
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

    public void setLoaded() {
        isLoading = false;
    }

//    public void setFlagFinishLoadData(boolean flagFinishLoadData) {
//        this.flagFinishLoadData = flagFinishLoadData;
//    }
}
