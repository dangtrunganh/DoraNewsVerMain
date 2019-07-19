//package com.anhdt.doranewsvermain.fragment.firstchildfragment;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.constraint.ConstraintLayout;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.anhdt.doranewsvermain.R;
//import com.anhdt.doranewsvermain.activity.SettingsActivity;
//import com.anhdt.doranewsvermain.adapter.recyclerview.VideoAdapter;
//import com.anhdt.doranewsvermain.api.ServerAPI;
//import com.anhdt.doranewsvermain.constant.ConstAPIYoutube;
//import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
//import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
//import com.anhdt.doranewsvermain.model.newsresult.Article;
//import com.anhdt.doranewsvermain.model.newsresult.Stories;
//import com.anhdt.doranewsvermain.model.notificationresult.NotificationResult;
//import com.anhdt.doranewsvermain.util.GeneralTool;
//import com.facebook.shimmer.ShimmerFrameLayout;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class VideoFragment extends BaseFragmentNeedUpdateUI implements UpdateListNotification, View.OnClickListener {
//    private static final String CONNECTED = "CONNECTED";
//    private static final String DISCONNECTED = "DISCONNECTED";
//    private RecyclerView recyclerViewVideos;
//    private SwipeRefreshLayout swipeContainer;
//    private ImageView imageSettings;
//    private ImageView imageNotifications;
//
//    private ImageView imageWifiOff;
//    private TextView textNoNetwork, textTryRefresh;
//    private ShimmerFrameLayout mShimmerViewContainer;
//
//    private ConstraintLayout constraintLayoutNoNetwork;
////    private ArrayList<Item> arrayListVideos;
//
//    private VideoAdapter videoAdapter;
//    private Context mContext;
//
//    private String nextPageToken = ConstAPIYoutube.PAGE_TOKEN_DEFAULT; //token tiếp theo
//
//    private AddFragmentCallback addFragmentCallback;
//
//    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng
//
//    public AddFragmentCallback getAddFragmentCallback() {
//        return addFragmentCallback;
//    }
//
//    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
//        this.addFragmentCallback = addFragmentCallback;
//    }
//
//    public VideoFragment() {
//    }
//
//    public static VideoFragment newInstance() {
//        Bundle args = new Bundle();
//
//        VideoFragment fragment = new VideoFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mContext = getContext();
//    }
//
//    @Override
//    protected void initializeComponents() {
//        View view = getView();
//        if (view == null) {
//            return;
//        }
//        constraintLayoutNoNetwork = view.findViewById(R.id.constraint_state_wifi_off_frg_video);
//        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container_frg_video);
//
//        imageWifiOff = view.findViewById(R.id.image_wifi_off_frg_video);
//        textNoNetwork = view.findViewById(R.id.text_no_network_frg_video);
//        textTryRefresh = view.findViewById(R.id.text_try_refresh_network_frg_video);
//
//        constraintLayoutNoNetwork.setVisibility(View.GONE);
//        imageSettings = view.findViewById(R.id.circle_button_person_frg_video);
//        imageSettings.setOnClickListener(this);
//        imageNotifications = view.findViewById(R.id.iv_search_frg_video);
//        imageNotifications.setOnClickListener(this);
//        swipeContainer = view.findViewById(R.id.swipe_container_frg_video);
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//
//        recyclerViewVideos = view.findViewById(R.id.recycler_frg_video);
//        recyclerViewVideos.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
//                LinearLayoutManager.VERTICAL, false);
//        recyclerViewVideos.setLayoutManager(linearLayoutManager);
////        arrayListVideos = new ArrayList<>();
//        videoAdapter = new VideoAdapter(getContext(), new ArrayList<>(), addFragmentCallback, recyclerViewVideos);
//        recyclerViewVideos.setAdapter(videoAdapter);
//        //===Adapter====LoadData======
//
////        arrayListVideos = CreateVideoData.createVideos();
////        if (arrayListVideos.size() == 0) {
////            //Hiển thị màn hình chưa có thông báo nào
////            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
////            recyclerViewVideos.setVisibility(View.GONE);
////        } else {
////            constraintLayoutNoNetwork.setVisibility(View.GONE);
////            recyclerViewVideos.setVisibility(View.VISIBLE);
////        }
////        videoAdapter = new VideoAdapter(getContext(), arrayListVideos, addFragmentCallback, this);
////        recyclerViewVideos.setAdapter(videoAdapter);
//        loadData(ConstAPIYoutube.PAGE_TOKEN_DEFAULT);
//        swipeContainer.setOnRefreshListener(() -> loadData(ConstAPIYoutube.PAGE_TOKEN_DEFAULT));
//        setUpLoadMore();
//    }
//
//    private void setUpLoadMore() {
//        videoAdapter.setLoadMore(() -> {
//            videoAdapter.addItemLoading();
//            new Handler().postDelayed(() -> {
//                videoAdapter.removeItemLoading();
//                if (nextPageToken == null) {
//                    Toast.makeText(mContext, "Đã tải đến video cuối cùng!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!nextPageToken.equals(ConstAPIYoutube.PAGE_TOKEN_DEFAULT)) {
//                    loadData(nextPageToken);
//                }
//            }, 2000); // Time out to load
//        });
//    }
//
//    private void loadData(String token) {
//        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
//            if (oldStateNetWork.equals(DISCONNECTED)) {
//                //Nếu trạng thái trước đó là Disconnected thì bật lại shimmer
//                mShimmerViewContainer.setVisibility(View.VISIBLE);
//                mShimmerViewContainer.startShimmerAnimation();
//                oldStateNetWork = CONNECTED;
//            }
//            actionDisableLoadBaseOnNetworkState(true);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(ConstAPIYoutube.ROOT_URL_VIDEO)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            final ServerAPI apiService = retrofit.create(ServerAPI.class);
//            Call<VideoResult> call = apiService.getListVideos(ConstAPIYoutube.PARAM_PART,
//                    ConstAPIYoutube.ORDER,
//                    ConstAPIYoutube.CHANNEL_ID,
//                    ConstAPIYoutube.MAX_RESULTS,
//                    ConstAPIYoutube.KEY,
//                    token,
//                    ConstAPIYoutube.TIME_PUBLISHED_AFTER);
//            call.enqueue(new Callback<VideoResult>() {
//                @Override
//                public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
//                    VideoResult videoResult = response.body();
//                    if (videoResult == null) {
////                        Toast.makeText(mContext, "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
//                        //Chưa theo dõi sự kiện nào
//                        mShimmerViewContainer.stopShimmerAnimation();
//                        mShimmerViewContainer.setVisibility(View.GONE);
//                        swipeContainer.setRefreshing(false);
//
//                        //Tạm thời, cần yêu cầu server trả về mảng rỗng
//                        if (videoAdapter.getArrayVideos().size() == 1) {
//                            //Hiển thị màn hình chưa có sự kiện nào được theo dõi
//                            //Luôn luôn tồn tại một phần tử null làm footer
//                            imageWifiOff.setImageResource(R.drawable.ic_no_video_youtube);
//                            textNoNetwork.setText("Không có video nào");
//                            textTryRefresh.setText("Thử làm mới lại trang bằng cách vuốt xuống để cập nhật");
//                            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
//                            recyclerViewVideos.setVisibility(View.GONE);
//                        } else {
//                            //Không load được thêm video nào từ Server, tuy nhiên list hiện tại đã có video (xảy ra khi thực hiện refresh) từ list hiện tại đã có
//                            constraintLayoutNoNetwork.setVisibility(View.GONE);
//                            recyclerViewVideos.setVisibility(View.VISIBLE);
//                        }
//                        return;
//                    }
//                    ArrayList<Item> arrayListVideosTemp = videoResult.getItems();
////                    if (videoAdapter.getArrayVideos().size() == 1) {
////                        //Hiển thị màn hình chưa có sự kiện nào được theo dõi
////                        //Luôn luôn tồn tại một phần tử null làm footer
////                        imageWifiOff.setImageResource(R.drawable.ic_no_video_youtube);
////                        textNoNetwork.setText("Không có video nào");
////                        textTryRefresh.setText("Thử làm mới lại trang bằng cách vuốt xuống để cập nhật");
////                        constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
////                        recyclerViewVideos.setVisibility(View.GONE);
////                    } else {
//                    constraintLayoutNoNetwork.setVisibility(View.GONE);
//                    recyclerViewVideos.setVisibility(View.VISIBLE);
//                    //update
//                    if (token.equals(ConstAPIYoutube.PAGE_TOKEN_DEFAULT)) {
//                        //load lần đầu
//                        nextPageToken = videoResult.getNextPageToken();
//                        videoAdapter.updateListVideos(arrayListVideosTemp);
//                    } else /*if (token.equals(videoResult.getPrevPageToken()))*/ {
//                        //load lần tiếp theo
//                        //token hiện tại trùng với cái previous
//                        //ok, thực hiện load more
//                        nextPageToken = videoResult.getNextPageToken();
//                        videoAdapter.addLoadMoreListVideos(arrayListVideosTemp);
//                    }
////                        videoAdapter.notifyDataSetChanged();
////                    }
//                    mShimmerViewContainer.stopShimmerAnimation();
//                    mShimmerViewContainer.setVisibility(View.GONE);
//                    swipeContainer.setRefreshing(false);
//                }
//
//                @Override
//                public void onFailure(Call<VideoResult> call, Throwable t) {
////                    Toast.makeText(mContext, "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
//                    Log.e("youtube-", "error");
//                    mShimmerViewContainer.stopShimmerAnimation();
//                    mShimmerViewContainer.setVisibility(View.GONE);
//                    swipeContainer.setRefreshing(false);
//                }
//            });
//        } else {
//            //Mất mạng
//            actionDisableLoadBaseOnNetworkState(false);
//            mShimmerViewContainer.setVisibility(View.GONE);
//            mShimmerViewContainer.stopShimmerAnimation();
//            oldStateNetWork = DISCONNECTED;
//            swipeContainer.setRefreshing(false);
//        }
//    }
//
//    private void actionDisableLoadBaseOnNetworkState(boolean state) {
//        if (state) {
//            //Có mạng
//            //Bật hết các View lên
//            constraintLayoutNoNetwork.setVisibility(View.GONE);
//            recyclerViewVideos.setVisibility(View.VISIBLE);
//        } else {
//            //Mất mạng
//            //Bật hết các View lên
//            imageWifiOff.setImageResource(R.drawable.ic_wifi_yellow);
//            textNoNetwork.setText("Không có kết nối mạng");
//            textTryRefresh.setText("Hãy kết nối mạng và vuốt xuống để thử lại");
//            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
//            recyclerViewVideos.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void initProgressbar() {
//        //do nothing
//    }
//
//    @Override
//    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
//        //Không có gì để update
//        //do nothing
//    }
//
//    @Override
//    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
//        //Không có gì để update
//        //do nothing
//    }
//
//    @Override
//    public void addNotificationFragment() {
//
//    }
//
//    @Override
//    public void scrollToTop() {
//        //do nothing
//    }
//
//    @Override
//    protected int getFragmentLayout() {
//        return R.layout.fragment_video;
//    }
//
//    @Override
//    public void addNotification(NotificationResult notificationResult) {
//        //do nothing
//    }
//
//    @Override
//    public void removeNotification(String idNotification) {
//        //do nothing
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.circle_button_person_frg_video:
//                //Mở màn settings
//                startActivity(new Intent(getContext(), SettingsActivity.class));
//                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.enter, R.anim.exit);
//                break;
//            case R.id.iv_search_frg_video:
//                addFragmentCallback.addNotificationFragment();
//                break;
//            default:
//                break;
//        }
//    }
//}
