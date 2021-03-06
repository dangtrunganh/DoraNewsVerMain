package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.ArticleItemAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.ArticleItemAdapter2;
import com.anhdt.doranewsvermain.adapter.recyclerview.EventAdapterHorizontal;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.config.ConfigSettings;
import com.anhdt.doranewsvermain.constant.ConstAPIYoutube;
import com.anhdt.doranewsvermain.constant.ConstParamLogging;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.Logging;
import com.anhdt.doranewsvermain.model.OSGroup;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.LogTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForBookmarkArticle;
import com.anhdt.doranewsvermain.util.ReadRealmToolForLogging;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.anhdt.doranewsvermain.config.ConfigSettings.TIME_SESSION;

public class DetailEventFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener {
    private static String ARG_TYPE_TAB = "ARG_TYPE_TAB";
    private static String ARG_EVENT_ID = "ARG_EVENT_ID";
    private static String ARG_EVENT_TITLE = "ARG_EVENT_TITLE";
    private static String ARG_LIST_OF_STORY = "ARG_LIST_OF_STORY";
    private static String ARG_CATEGORY_ID = "ARG_CATEGORY_ID";

    //    private static String ARG_TYPE_SINGLE_OR_IN = "ARG_TYPE_SINGLE_OR_IN";
    private static String ARG_LONG_EVENT_ID = "ARG_LONG_EVENT_ID";
    private ProgressDialog dialog;
    private static String DEFAULT_USER_ID = "8def96c8-5f32-4fcf-9c0f-ce76ca719d65";

    public static String DEFAULT_ID_STORY = "";
    public static String DEFAULT_LIST_OF_STORY = "";
    private AlertDialog alertDialog;

//    public static final int TYPE_SINGLE = 0; //Event single, not in any stories
//    public static final int TYPE_IN = 1; //Event in stories

    private ImageView mImageViewCover;
    private ImageView mImageShowStory;
    private TextView mTextTitleEvent, mTextNameCategory, mTextNumberNews/*, mTextLoadMoreNews*/;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerViewArticle;
    private ShimmerFrameLayout mShimmerViewContainer;

    private TextView textShowAllEvent;
    private RecyclerView recyclerListEventInStoryHorizontal;
    private ConstraintLayout constraintLayoutListEventInStory;
    private Button btnFollow;
    private String idStoryForUpdateUI;

    private ArticleItemAdapter2 articleItemAdapter;
    private EventAdapterHorizontal eventAdapterHorizontal;

    private AddFragmentCallback addFragmentCallback;

    private String ipAddress;
    private String versionAndroid;
    private String userAgent;
    private OSGroup osGroup;

    //    private int typeTabHomeOrLatest;
    private String eventId;
    private String categoryId;

    //    private String eventTitle;
    private String idStory; //Chỉ những TYPE_IN thì mới kiểm tra lấy thông tin idStory
    private ArrayList<Article> articles;
    private ArrayList<Event> arrayListEvent;
    private ArrayList<Event> arrayGeneralForUpdateHorizontalListEvent; //List này để

//    private boolean isFollowed = false;

    private int stateFollow = -1;
    private Context mContext;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static DetailEventFragment newInstance(/*int typeTab,*/ String eventId/*, String eventTitle*/, String idStory, String jsonListOfStory, String categoryId) {
        Bundle args = new Bundle();
        DetailEventFragment fragment = new DetailEventFragment();
//        args.putInt(ARG_TYPE_TAB, typeTab);
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_LONG_EVENT_ID, idStory);
        args.putString(ARG_LIST_OF_STORY, jsonListOfStory);
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d("main-ff", "onAttach()");
        super.onAttach(activity);
        mContext = getContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("main-ff", "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("main-ff", "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("main-ff", "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("main-ff", "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d("main-ff", "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("main-ff", "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("main-ff", "onPause()");
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        Log.d("main-ff", "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("main-ff", "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("main-ff", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d("main-ff", "onDetach()");
        super.onDetach();
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        getInfoLogging();

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        mShimmerViewContainer = view.findViewById(R.id.skeleton_detail_event);
        mImageViewCover = view.findViewById(R.id.image_cover_detail_event);
        mImageShowStory = view.findViewById(R.id.image_show_all_event_in_stories);
        mTextTitleEvent = view.findViewById(R.id.text_title_detail_event);
        mTextNameCategory = view.findViewById(R.id.text_category_detail_event);
        mTextNumberNews = view.findViewById(R.id.text_number_news_detail_event);
//        mTextLoadMoreNews = view.findViewById(R.id.text_load_more_news);

        textShowAllEvent = view.findViewById(R.id.text_show_all_event_in_stories);
        constraintLayoutListEventInStory = view.findViewById(R.id.constraint_layout_frg_home);

        recyclerViewArticle = view.findViewById(R.id.recycler_news_detail_event);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewArticle.setLayoutManager(linearLayoutManager);
        recyclerViewArticle.setHasFixedSize(true);
//11        recyclerViewArticle.setNestedScrollingEnabled(false);

//        mTextLoadMoreNews.setOnClickListener(this);
        mToolbar = view.findViewById(R.id.toolbar_detail_event);
        collapsingToolbarLayout = view.findViewById(R.id.collab_toolbar_layout_detail_event);
        collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        //Set back icon to toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_white);
        mToolbar.setNavigationOnClickListener(v -> addFragmentCallback.popBackStack());

        btnFollow = view.findViewById(R.id.btn_follow_story_in_detail_event);
        btnFollow.setOnClickListener(this);

        //===GetData====
        Bundle bundle = getArguments();
//        typeTabHomeOrLatest = bundle.getInt(ARG_TYPE_TAB);
        eventId = bundle.getString(ARG_EVENT_ID);
        categoryId = bundle.getString(ARG_CATEGORY_ID);
//        eventTitle = bundle.getString(ARG_EVENT_TITLE);
        idStory = bundle.getString(ARG_LONG_EVENT_ID);
        String jsonListEventsOfStory = bundle.getString(ARG_LIST_OF_STORY);
        if (jsonListEventsOfStory != null && idStory != null) {
            if (!idStory.equals(DEFAULT_ID_STORY) && !jsonListEventsOfStory.equals(DEFAULT_LIST_OF_STORY)) {
//                idStoryForUpdateUI = idStory;
                //Nếu idStory khác rỗng, thực hiện load data lên recycler view - list các story
                //recyclerView - List các event trong story horizontal
                constraintLayoutListEventInStory.setVisibility(View.VISIBLE);
                recyclerListEventInStoryHorizontal = view.findViewById(R.id.recycler_list_articles_in_stories_detail_event);
                LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL, false);
                recyclerListEventInStoryHorizontal.setLayoutManager(linearLayoutManagerHorizontal);
                recyclerListEventInStoryHorizontal.setHasFixedSize(true);
//11                recyclerListEventInStoryHorizontal.setNestedScrollingEnabled(false);

//                eventAdapterHorizontal = new EventAdapterHorizontal(new ArrayList<>(), mContext,
//                        /*typeTabHomeOrLatest, */idStory, addFragmentCallback, eventId);
//                recyclerListEventInStoryHorizontal.setAdapter(eventAdapterHorizontal);

                //===Lấy ra List Event từ json===
                Gson gson = new Gson();
                arrayListEvent = gson.fromJson(jsonListEventsOfStory, new TypeToken<List<Event>>() {
                }.getType());

                eventAdapterHorizontal = new EventAdapterHorizontal(arrayListEvent, mContext,
                        /*typeTabHomeOrLatest, */idStory, addFragmentCallback, eventId);
                recyclerListEventInStoryHorizontal.setAdapter(eventAdapterHorizontal);

//                eventAdapterHorizontal.updateListEvents(arrayListEvent);
                textShowAllEvent.setOnClickListener(this);
                mImageShowStory.setOnClickListener(this);
            } else {
//                idStoryForUpdateUI = eventId;
                //Không hiển thị list các event trong story vì idStory là rỗng
//            constraintLayoutListEventInStory.setVisibility(View.GONE);
            }
        } else {
//            idStoryForUpdateUI = eventId;
        }

        boolean idStoryUIHasSet = false;
        if (idStory != null) {
            if (!idStory.equals(DEFAULT_ID_STORY)) {
                idStoryForUpdateUI = idStory;
            } else {
                idStoryForUpdateUI = eventId;
            }
        } else {
            idStoryForUpdateUI = eventId;
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
        articleItemAdapter = new ArticleItemAdapter2(mContext,
                new ArrayList<>(), addFragmentCallback, recyclerViewArticle);
        recyclerViewArticle.setAdapter(articleItemAdapter);

        //===Adapter====
//        articleItemAdapter = new ArticleItemAdapter(getContext(), new ArrayList<>(), typeTabHomeOrLatest, addFragmentCallback);
//        recyclerViewArticle.setAdapter(articleItemAdapter);

        String uId = ReadCacheTool.getUId(mContext);
        loadData(eventId, uId);
        setUpLoadMore();

        //====send==logging====
        //====send====on===click====event======
//        ipAddress = LogTool.getIpAddress(mContext);
//        versionAndroid = GeneralTool.getVersionAndroid();
//        userAgent = GeneralTool.getNameOfDevice();
        cacheLog();

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RootAPIUrlConst.IP_ADDRESS_LOGGING)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        final ServerAPI apiService = retrofit.create(ServerAPI.class);
//        String sessionId = LogTool.getSessionId(mContext);
//        String ipAddress = LogTool.getIpAddress(mContext);
//        String versionAndroid = GeneralTool.getVersionAndroid();
//        String userAgent = GeneralTool.getNameOfDevice();
//
//        Log.e("ELO-sessionId", sessionId);
//        Log.e("ELO-ipAddress", ipAddress);
//        Log.e("ELO-versionAndroid", versionAndroid);
//        Log.e("ELO-userAgent", userAgent);
//
//        Call<Void> call = apiService.getLoggingClickEvent(sessionId, ipAddress,
//                ConstParamLogging.OS_CODE_ANDROID, versionAndroid, userAgent, eventId);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//
//            }
//        });
    }

    private void getInfoLogging() {
        ipAddress = LogTool.getIpAddress(mContext);
        versionAndroid = GeneralTool.getVersionAndroid();
        userAgent = GeneralTool.getNameOfDevice();
        osGroup = new OSGroup(ConstParamLogging.OS_CODE_ANDROID, versionAndroid, userAgent);
    }

    private void cacheLog() {
        long currentTime = LogTool.getTimeCreate();
        String sessionId;
        if (currentTime - ConfigSettings.lastTimeCreated < TIME_SESSION) {
            sessionId = ConfigSettings.lastSessionId;
        } else {
            sessionId = LogTool.getSessionId(mContext);
            ConfigSettings.lastSessionId = sessionId;
        }
        ConfigSettings.lastTimeCreated = LogTool.getTimeCreate();

        Logging logging = new Logging(sessionId,
                -1,
                ipAddress,
                osGroup,
                ConstParamLogging.EVENT_ID_START_APP,
                eventId,
                categoryId, ConfigSettings.lastTimeCreated);
        ReadRealmToolForLogging.addLoggingToRealm(mContext, logging);
        ConfigSettings.numberOfLogging++;

        if (ConfigSettings.numberOfLogging >= ConfigSettings.NUMBER_LOGGING_TO_SEND) {
            LogTool.sendLogging(mContext);
            ReadRealmToolForLogging.deleteAllLogging(mContext);
            ConfigSettings.numberOfLogging = 0;
        }
    }


//    private void sendLoggingStartApp(ArrayList<Logging> loggings) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RootAPIUrlConst.IP_ADDRESS_LOGGING)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        final ServerAPI apiService = retrofit.create(ServerAPI.class);
//        String sessionId = LogTool.getSessionId(mContext);
//        String ipAddress = LogTool.getIpAddress(mContext);
//        String versionAndroid = GeneralTool.getVersionAndroid();
//        String userAgent = GeneralTool.getNameOfDevice();
//
//        Log.e("ELO-sessionId", sessionId);
//        Log.e("ELO-ipAddress", ipAddress);
//        Log.e("ELO-versionAndroid", versionAndroid);
//        Log.e("ELO-userAgent", userAgent);
//
//        Call<Void> call = apiService.getLoggingClickEvent(sessionId, ipAddress,
//                ConstParamLogging.OS_CODE_ANDROID, versionAndroid, userAgent, eventId);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//
//            }
//        });
//    }

    private void setUpLoadMore() {
        articleItemAdapter.setLoadMore(() -> {
            articleItemAdapter.addItemLoading();
            new Handler().postDelayed(() -> {
                articleItemAdapter.removeItemLoading();
                articleItemAdapter.loadMoreArticles();
            }, 2000); // Time out to load
        });
    }

//    private void loadDataToRecyclerEventHorizontal(String idStory, String uId) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        final ServerAPI apiService = retrofit.create(ServerAPI.class);
//
//        Call<Stories> call = apiService.getDetailStory(idStory, uId);
//
//        call.enqueue(new Callback<Stories>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {
//                Stories stories = response.body();
//                if (stories == null) {
//                    Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                int follow = stories.getFollow();
//                //Kiểm tra biến này để bật/tắt follow
//                arrayListEvent = (ArrayList<Event>) stories.getEvents();
//                if (arrayListEvent == null) {
//                    return;
//                }
//                if (arrayListEvent.size() == 0) {
//                    return;
//                }
//                //load data to recyclerView
//                eventAdapterHorizontal.updateListEvents(arrayListEvent);
//            }
//
//            @Override
//            public void onFailure(Call<Stories> call, Throwable t) {
//                Toast.makeText(mContext, "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
                    Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    return;
                }
                Glide.with(mContext).load(event.getImage())
                        .apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(mImageViewCover);
                mTextTitleEvent.setText(event.getTitle());
                mTextNameCategory.setText(event.getCategory().getName());

                if (event.getListArticles() != null) {
                    mTextNumberNews.setText(event.getListArticles().size() + " bài báo / " + event.getReadableTime());
                    ArrayList<Article> articlesArray = (ArrayList<Article>) event.getListArticles();
                    //=====set up bookmark=====
                    //Không biết như này có được không?
                    ReadRealmToolForBookmarkArticle.setListBookmark(mContext, articlesArray);
                    //=========================
//                    articleItemAdapter = new ArticleItemAdapter2(mContext,
//                            articlesArray, addFragmentCallback, recyclerViewArticle);
//                    recyclerViewArticle.setAdapter(articleItemAdapter);

                    articleItemAdapter.updateListArticles(articlesArray);
                }
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                stateFollow = event.getFollow();
                if (stateFollow == RootAPIUrlConst.FOLLOW_INTEGER) {
                    updateUIWhenFollow(true);
                } else if (stateFollow == RootAPIUrlConst.UN_FOLLOW_INTEGER) {
                    updateUIWhenFollow(false);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
//                Toast.makeText(mContext, "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
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

    public void updateUIWhenFollow(boolean isFollowed) {
        //true - theo dõi
        if (isFollowed) {
            //Theo dõi
            final int sdk = android.os.Build.VERSION.SDK_INT;
            stateFollow = RootAPIUrlConst.FOLLOW_INTEGER;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                btnFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.border_un_follow_button));
            } else {
                btnFollow.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_un_follow_button));
            }
            btnFollow.setText("Bỏ theo dõi");
        } else {
            //Không theo dõi
            final int sdk = android.os.Build.VERSION.SDK_INT;
            stateFollow = RootAPIUrlConst.UN_FOLLOW_INTEGER;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                btnFollow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.border_follow_button));
            } else {
                btnFollow.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_follow_button));
            }
            btnFollow.setText("Theo dõi");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_follow_story_in_detail_event) {
            //Khi click vào follow, gửi request về Server, nếu thành công thì
            //Gọi interface về MainActivity để update, hehe, ko gọi hàm trực tiếp
            followEvent();
        } else if (v.getId() == R.id.text_show_all_event_in_stories) {
            //Sự kiện Click vào xem tất cả story
            if (arrayListEvent == null) {
                return;
            }
            if (arrayListEvent.size() == 0) {
                return;
            }
//            Gson gson = new Gson();
//            String jsonListEvents = gson.toJson(arrayListEvent);
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(/*typeTabHomeOrLatest*//*, jsonListEvents*//*,*/ idStory);
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

        } else if (v.getId() == R.id.image_show_all_event_in_stories) {
            //Sự kiện Click vào xem tất cả story
            if (arrayListEvent == null) {
                return;
            }
            if (arrayListEvent.size() == 0) {
                return;
            }
//            Gson gson = new Gson();
//            String jsonListEvents = gson.toJson(arrayListEvent);
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                DetailStoryFragment detailStoryFragment = DetailStoryFragment.newInstance(/*typeTabHomeOrLatest*//*, jsonListEvents*//*,*/ idStory);
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
        } else if (v.getId() == R.id.text_load_more_news) {
            //Khi ấn xem thêm thì load thêm ra
//            Toast.makeText(getContext(), "Clicked load more!", Toast.LENGTH_SHORT).show();
            articleItemAdapter.loadMoreArticles();
        }
    }

    private void followEvent() {
        String uId = ReadCacheTool.getUId(mContext);
        if (stateFollow == RootAPIUrlConst.FOLLOW_INTEGER) {
            //Trạng thái hiện tại đang là Follow
            //Khi Click vào sẽ Unfollow - Chú ý là UNFOLLOW!!!
            initDialog(uId);
            alertDialog.show();
        } else if (stateFollow == RootAPIUrlConst.UN_FOLLOW_INTEGER) {
            //Trạng thái hiện tại đang là UnFollow, click vào sẽ là Follow
            //Trạng thái hiện tại đang là Follow
            //Khi Click vào sẽ follow - Chú ý là FOLLOW!!!
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                dialog.show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();


                final ServerAPI apiService = retrofit.create(ServerAPI.class);

                Call<Stories> call = apiService.followEvent(uId, eventId, RootAPIUrlConst.FOLLOW);

                call.enqueue(new Callback<Stories>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {
                        Stories stories = response.body();
                        if (stories == null) {
                            Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        stateFollow = stories.getFollow();
                        String idStoryResult = stories.getStoryId();
                        if (stateFollow == RootAPIUrlConst.FOLLOW_INTEGER) {
                            //success, follow thành công
                            //Thông báo cho MainActivity thay đổi UI button
                            addFragmentCallback.updateListEventFollowInAddFrag(true, idStoryResult, stories);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Stories> call, Throwable t) {
                        Toast.makeText(mContext, "Theo dõi thất bại!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Không có kết nối mạng");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }

        } else {
            Toast.makeText(mContext, "State is undefined!", Toast.LENGTH_SHORT).show();
        }

    }

    public void initDialog(String uId) {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Bỏ theo dõi?");
        alertDialog.setCanceledOnTouchOutside(false); //Vo hieu hoa khong cho kich ra ngoai de tat dialog
        alertDialog.setCancelable(false); //Vo hieu hoa khong cho an back de tat dialog

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialogg, which) -> {
            //Noi dung xu ly khi click vao button, mac dinh dialog se close sau khi click vao

            //Kiểm tra state mạng========
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                ///====
                dialog.show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final ServerAPI apiService = retrofit.create(ServerAPI.class);

                Call<Stories> call = apiService.followEvent(uId, eventId, RootAPIUrlConst.UN_FOLLOW);

                call.enqueue(new Callback<Stories>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {
                        Stories stories = response.body();
                        if (stories == null) {
                            Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        stateFollow = stories.getFollow();
                        String idStoryResult = stories.getStoryId();
                        if (stateFollow == RootAPIUrlConst.UN_FOLLOW_INTEGER) {
                            //success, bỏ follow thành công
                            //Thông báo cho MainActivity thay đổi UI button
                            addFragmentCallback.updateListEventFollowInAddFrag(false, idStoryResult, stories);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Stories> call, Throwable t) {
                        Toast.makeText(mContext, "Bỏ theo dõi thất bại!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Không có kết nối mạng");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, whichNew) -> dialog.dismiss());
                alertDialog.show();
            }
            //Kiểm tra state mạng========
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hủy", (dialog, which) -> {
            //Noi dung xu ly khi click vao button, mac dinh dialog se close sau khi click vao
        });
    }


    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Update lại UI
        if (this.idStoryForUpdateUI.equals(idStory)) {
            updateUIWhenFollow(isFollowed);
        }
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //Kiểm tra xem list article có thằng nào trong list bookmark ko thì update ở đây
        articleItemAdapter.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }
}
