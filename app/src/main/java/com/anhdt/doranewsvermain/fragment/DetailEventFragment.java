package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.ArticleItemAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.EventAdapterHorizontal;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollow;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailEventFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener {
    private static String ARG_TYPE_TAB = "ARG_TYPE_TAB";
    private static String ARG_EVENT_ID = "ARG_EVENT_ID";
    private static String ARG_EVENT_TITLE = "ARG_EVENT_TITLE";
    private static String ARG_LIST_OF_STORY = "ARG_LIST_OF_STORY";

    //    private static String ARG_TYPE_SINGLE_OR_IN = "ARG_TYPE_SINGLE_OR_IN";
    private static String ARG_LONG_EVENT_ID = "ARG_LONG_EVENT_ID";

    private static String DEFAULT_USER_ID = "8def96c8-5f32-4fcf-9c0f-ce76ca719d65";

    public static String DEFAULT_ID_STORY = "";
    public static String DEFAULT_LIST_OF_STORY = "";

//    public static final int TYPE_SINGLE = 0; //Event single, not in any stories
//    public static final int TYPE_IN = 1; //Event in stories

    private ImageView mImageViewCover;
    private ImageView mImageShowStory;
    private TextView mTextTitleEvent, mTextNameCategory, mTextNumberNews, mTextLoadMoreNews;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerViewArticle;
    private ShimmerFrameLayout mShimmerViewContainer;

    private TextView textShowAllEvent;
    private RecyclerView recyclerListEventInStoryHorizontal;
    private ConstraintLayout constraintLayoutListEventInStory;
    private Button btnFollow;

    private ArticleItemAdapter articleItemAdapter;
    private EventAdapterHorizontal eventAdapterHorizontal;

    private AddFragmentCallback addFragmentCallback;

    private int typeTabHomeOrLatest;
    private String eventId;
    //    private String eventTitle;
    private String idStory; //Chỉ những TYPE_IN thì mới kiểm tra lấy thông tin idStory
    private ArrayList<Article> articles;
    private ArrayList<Event> arrayListEvent;

    private boolean isFollowed = false;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static DetailEventFragment newInstance(int typeTab, String eventId/*, String eventTitle*/, String idStory, String jsonListOfStory) {
        Bundle args = new Bundle();
        DetailEventFragment fragment = new DetailEventFragment();
        args.putInt(ARG_TYPE_TAB, typeTab);
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_LONG_EVENT_ID, idStory);
        args.putString(ARG_LIST_OF_STORY, jsonListOfStory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        mShimmerViewContainer = view.findViewById(R.id.skeleton_detail_event);
//        mShimmerViewContainer.startShimmerAnimation();
        mImageViewCover = view.findViewById(R.id.image_cover_detail_event);
        mImageShowStory = view.findViewById(R.id.image_show_all_event_in_stories);
        mTextTitleEvent = view.findViewById(R.id.text_title_detail_event);
        mTextNameCategory = view.findViewById(R.id.text_category_detail_event);
        mTextNumberNews = view.findViewById(R.id.text_number_news_detail_event);
        mTextLoadMoreNews = view.findViewById(R.id.text_load_more_news);

        textShowAllEvent = view.findViewById(R.id.text_show_all_event_in_stories);
        constraintLayoutListEventInStory = view.findViewById(R.id.constraint_layout_frg_home);

        recyclerViewArticle = view.findViewById(R.id.recycler_news_detail_event);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewArticle.setLayoutManager(linearLayoutManager);
        recyclerViewArticle.setHasFixedSize(true);
        recyclerViewArticle.setNestedScrollingEnabled(false);

        mTextLoadMoreNews.setOnClickListener(this);

        mToolbar = view.findViewById(R.id.toolbar_detail_event);

        //====Collasping====Tạm thời bỏ đi======
        collapsingToolbarLayout = view.findViewById(R.id.collab_toolbar_layout_detail_event);
        collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        //====Collasping====Tạm thời bỏ đi======

        //Set back icon to toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_white);
        mToolbar.setNavigationOnClickListener(v -> addFragmentCallback.popBackStack());

        btnFollow = view.findViewById(R.id.btn_follow_story_in_detail_event);
        btnFollow.setOnClickListener(this);

        //===GetData====
        Bundle bundle = getArguments();
        typeTabHomeOrLatest = bundle.getInt(ARG_TYPE_TAB);
        eventId = bundle.getString(ARG_EVENT_ID);
//        eventTitle = bundle.getString(ARG_EVENT_TITLE);
        idStory = bundle.getString(ARG_LONG_EVENT_ID);
        String jsonListEventsOfStory = bundle.getString(ARG_LIST_OF_STORY);
        if (jsonListEventsOfStory != null && idStory != null) {
            if (!idStory.equals(DEFAULT_ID_STORY) && !jsonListEventsOfStory.equals(DEFAULT_LIST_OF_STORY)) {
                //Nếu idStory khác rỗng, thực hiện load data lên recycler view - list các story
                //recyclerView - List các event trong story horizontal
                constraintLayoutListEventInStory.setVisibility(View.VISIBLE);
                recyclerListEventInStoryHorizontal = view.findViewById(R.id.recycler_list_articles_in_stories_detail_event);
                LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL, false);
                recyclerListEventInStoryHorizontal.setLayoutManager(linearLayoutManagerHorizontal);
                recyclerListEventInStoryHorizontal.setHasFixedSize(true);
                recyclerListEventInStoryHorizontal.setNestedScrollingEnabled(false);

                eventAdapterHorizontal = new EventAdapterHorizontal(new ArrayList<>(), getContext(),
                        typeTabHomeOrLatest, idStory, addFragmentCallback, eventId);
                recyclerListEventInStoryHorizontal.setAdapter(eventAdapterHorizontal);

                //===Lấy ra List Event từ json===
                Gson gson = new Gson();
                arrayListEvent = gson.fromJson(jsonListEventsOfStory, new TypeToken<List<Event>>() {
                }.getType());
                eventAdapterHorizontal.updateListEvents(arrayListEvent);
//            loadDataToRecyclerEventHorizontal(idStory, DEFAULT_USER_ID);
                textShowAllEvent.setOnClickListener(this);
                mImageShowStory.setOnClickListener(this);
            } /*else {
            //Không hiển thị list các event trong story vì idStory là rỗng
//            constraintLayoutListEventInStory.setVisibility(View.GONE);
        }*/
        }

//        mToolbar.setTitle(eventTitle);

        articles = new ArrayList<>();

        //====Fix collapsing===
        recyclerViewArticle.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        //===Adapter====
//        articleItemAdapter = new ArticleItemAdapter(getContext(), new ArrayList<>(), typeTabHomeOrLatest, addFragmentCallback);
//        recyclerViewArticle.setAdapter(articleItemAdapter);

        loadData(eventId, DEFAULT_USER_ID);
    }

    private void loadDataToRecyclerEventHorizontal(String idStory, String uId) {
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
                    Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int follow = stories.getFollow();
                //Kiểm tra biến này để bật/tắt follow
                arrayListEvent = (ArrayList<Event>) stories.getEvents();
                if (arrayListEvent == null) {
                    return;
                }
                if (arrayListEvent.size() == 0) {
                    return;
                }
                //load data to recyclerView
                eventAdapterHorizontal.updateListEvents(arrayListEvent);
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData(String eventId, String uId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServerAPI apiService = retrofit.create(ServerAPI.class);

        Call<Event> call = apiService.getDetailEvent(eventId, uId);

        call.enqueue(new Callback<Event>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                Event event = response.body();
                if (event == null) {
                    Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    return;
                }
                Log.e("xxy-", event.toString());
                Glide.with(getContext()).load(event.getImage())
                        .apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(mImageViewCover);
                mTextTitleEvent.setText(event.getTitle());
                mTextNameCategory.setText(event.getCategory().getName());

                if (event.getListArticles() != null) {
                    mTextNumberNews.setText(event.getListArticles().size() + " bài báo / " + event.getReadableTime());
                    Log.e("xxy-1", event.getListArticles().toString());
                    articleItemAdapter = new ArticleItemAdapter(getContext(),
                            (ArrayList<Article>) event.getListArticles(),
                            typeTabHomeOrLatest, addFragmentCallback, mTextLoadMoreNews, ArticleItemAdapter.LOAD_MORE_DETAIL_EVENT);
                    recyclerViewArticle.setAdapter(articleItemAdapter);
                }
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_detail_event;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_follow_story_in_detail_event) {
            //Khi click vào follow 1 story thì có nút này hiện lên
            if (!isFollowed) {
                Toast.makeText(getContext(), "Bạn đã theo dõi dòng sự kiện này ^^", Toast.LENGTH_SHORT).show();
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnFollow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.border_un_follow_button));
                } else {
                    btnFollow.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_un_follow_button));
                }
                btnFollow.setText("Bỏ theo dõi");
                isFollowed = true;
            } else {
                Toast.makeText(getContext(), "Bỏ theo dõi dòng sự kiện này :(", Toast.LENGTH_SHORT).show();
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnFollow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.border_follow_button));
                } else {
                    btnFollow.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_follow_button));
                }
                btnFollow.setText("Theo dõi");
                isFollowed = false;
            }

        } else if (v.getId() == R.id.text_show_all_event_in_stories) {
            //Sự kiện Click vào xem tất cả story
            if (arrayListEvent == null) {
                return;
            }
            if (arrayListEvent.size() == 0) {
                return;
            }
            Gson gson = new Gson();
            String jsonListEvents = gson.toJson(arrayListEvent);
            DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(typeTabHomeOrLatest/*, jsonListEvents*/, idStory);
            detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
            addFragmentCallback.addFrgCallback(detailStoryFragment);
        } else if (v.getId() == R.id.image_show_all_event_in_stories) {
            //Sự kiện Click vào xem tất cả story
            if (arrayListEvent == null) {
                return;
            }
            if (arrayListEvent.size() == 0) {
                return;
            }
            Gson gson = new Gson();
            String jsonListEvents = gson.toJson(arrayListEvent);
            DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(typeTabHomeOrLatest/*, jsonListEvents*/, idStory);
            detailStoryFragment.setAddFragmentCallback(addFragmentCallback);
            addFragmentCallback.addFrgCallback(detailStoryFragment);
        } else if (v.getId() == R.id.text_load_more_news) {
            //Khi ấn xem thêm thì load thêm ra
//            Toast.makeText(getContext(), "Clicked load more!", Toast.LENGTH_SHORT).show();
            articleItemAdapter.loadMoreArticles();
        }
    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory) {
        //Update lại UI
    }
}
