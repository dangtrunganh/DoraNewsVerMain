package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.viewpagersearch.SearchViewPagerAdapter;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.ArticleInTypeSearchFragment;
import com.anhdt.doranewsvermain.fragment.secondchildfragment.EventInTypeSearchFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SearchFragment extends BaseFragmentNeedUpdateUI implements SearchView.OnQueryTextListener {
    private static final String CONNECTED = "CONNECTED";
    private static final String DISCONNECTED = "DISCONNECTED";


    //Các state này ko quan tâm đến tabLayout
    private static final int INIT_SEARCH_DO_NOTHING_BEFORE = 0; //Bật màn này, hiển thị luôn là tìm kiếm abc, xyz,...

    //Tìm kiếm lần đầu mà ko có mạng thì ko bật list, tab lên, chỉ hiển thị cái layout mất mạng lên thôi
    private static final int SEARCH_NO_NETWORK = 1; //Tìm kiếm lần đầu mà ko có mạng, các list chỉ bật lên khi lần đầu có mạng và tìm kiếm ok, trả về 0 or null ko sao?

    //Tìm kiếm lần đầu mà có mạng, bật list lên, tắt constraint layout đi
    private static final int SEARCH_NETWORK_AVAILABLE = 2;

    private Context mContext;
    private FragmentActivity fragmentActivity;

    //Gọi về General để call Main gọi hàm addFrg()
    private AddFragmentCallback addFragmentCallback;

    //Các View
    private RecyclerView recyclerViewSearchResult;
    private SearchView searchView;
    private ProgressDialog dialog;
    private ImageView imageState;
    private TextView textStateInfo;
    private TextView textStateAction;
    //    private SwipeRefreshLayout swipeContainer;
    private ConstraintLayout constraintLayoutNoWifi;

    private DisplaySearchResults searchEvents;
    private DisplaySearchResults searchArticles;

//    private SearchResultAdapter searchResultAdapter;

    private String oldStateNetWork = DISCONNECTED; //ban đầu sẽ là mất mạng

//    private ShimmerFrameLayout mShimmerViewContainer;
//    private TextView textNoNetwork;

    private SearchViewPagerAdapter searchViewPagerAdapter;
    private ViewPager viewPagerTwoTypeTab;
    private TabLayout tabLayoutTwoTypeTab;

    //Gọi đến thằng con số 1, yêu cầu nó theo dõi/bỏ theo dõi, vì tab 1 có thể bật ra màn Detail Event, Detail Story
    private UpdateUIFollowBookmarkChild updateUIFollowFirstChildTab;

    //Gọi đến thằng con số 2, yêu cầu nó bookmark/ bỏ bookmark
    private UpdateUIFollowBookmarkChild updateUIBookmarkSecondChildTab;

    public AddFragmentCallback getAddFragmentCallback() {
        return addFragmentCallback;
    }

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public SearchFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
        fragmentActivity = getActivity();
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateUIStateSearch(int type) {
        switch (type) {
            case INIT_SEARCH_DO_NOTHING_BEFORE:
                //do nothing
                break;
            case SEARCH_NO_NETWORK:
                //Tắt hết các view đi
                viewPagerTwoTypeTab.setVisibility(View.GONE);
                tabLayoutTwoTypeTab.setVisibility(View.GONE);

                imageState.setVisibility(View.VISIBLE);
                textStateInfo.setVisibility(View.VISIBLE);
                textStateAction.setVisibility(View.VISIBLE);
                imageState.setImageResource(R.drawable.ic_wifi_yellow);
                textStateInfo.setText("Không có kết nối mạng");
                textStateAction.setText("Hãy kết nối mạng và thử lại");
                break;
            case SEARCH_NETWORK_AVAILABLE:
                viewPagerTwoTypeTab.setVisibility(View.VISIBLE);
                tabLayoutTwoTypeTab.setVisibility(View.VISIBLE);
                imageState.setVisibility(View.GONE);
                textStateInfo.setVisibility(View.GONE);
                textStateAction.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        constraintLayoutNoWifi = view.findViewById(R.id.constraint_state_wifi_off_frg_search);
        constraintLayoutNoWifi.setVisibility(View.GONE);
        imageState = view.findViewById(R.id.image_wifi_off_frg_search);
        textStateInfo = view.findViewById(R.id.text_no_network_frg_search);
        textStateAction = view.findViewById(R.id.text_try_refresh_network_frg_search);
//        textNoNetwork = view.findViewById(R.id.text_no_network_frg_search);
//        textNoNetwork.setVisibility(View.GONE);
//        swipeContainer = view.findViewById(R.id.swipe_container_frg_search);
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Searching..");
        dialog.setCancelable(false);

//        recyclerViewSearchResult = view.findViewById(R.id.recycler_frg_search);
//        recyclerViewSearchResult.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
//                LinearLayoutManager.VERTICAL, false);
//        recyclerViewSearchResult.setLayoutManager(linearLayoutManager);

//        searchResultAdapter = new SearchResultAdapter(mContext, new ArrayList<>(), addFragmentCallback);
//        recyclerViewSearchResult.setAdapter(searchResultAdapter);
        viewPagerTwoTypeTab = view.findViewById(R.id.view_pager_type_frg_search);
        viewPagerTwoTypeTab.setOffscreenPageLimit(1);
        tabLayoutTwoTypeTab = view.findViewById(R.id.tab_layout_type_frg_search);

        searchView = view.findViewById(R.id.sv_search_frg_search);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.clearFocus();
        searchView.setOnQueryTextListener(this);

//        swipeContainer.setOnRefreshListener(() -> {
//            String query = searchView.getQuery().toString();
//            if (!query.equals("")) {
//                search(query);
//            } else {
//                swipeContainer.setRefreshing(false);
//            }
//        });
        setUpViewPager(viewPagerTwoTypeTab);
        tabLayoutTwoTypeTab.setupWithViewPager(viewPagerTwoTypeTab);

        constraintLayoutNoWifi.setVisibility(View.VISIBLE);
        tabLayoutTwoTypeTab.setVisibility(View.GONE);
        viewPagerTwoTypeTab.setVisibility(View.GONE);
    }

    private void setUpViewPager(ViewPager viewPager) {
        //set up 2 pager of viewpager
        EventInTypeSearchFragment eventInTypeSearchFragment = EventInTypeSearchFragment.newInstance();
        eventInTypeSearchFragment.setAddFragmentCallback(addFragmentCallback);
        updateUIFollowFirstChildTab = eventInTypeSearchFragment;

        searchEvents = eventInTypeSearchFragment;

        ArticleInTypeSearchFragment articleInTypeSearchFragment = ArticleInTypeSearchFragment.newInstance();
        articleInTypeSearchFragment.setAddFragmentCallback(addFragmentCallback);
        updateUIBookmarkSecondChildTab = articleInTypeSearchFragment;

        searchArticles = articleInTypeSearchFragment;
        searchViewPagerAdapter = new SearchViewPagerAdapter(getChildFragmentManager(),
                eventInTypeSearchFragment,
                articleInTypeSearchFragment);
        viewPager.setAdapter(searchViewPagerAdapter);
    }

    private void actionDisableLoadBaseOnNetworkState(boolean state) {
        if (state) {
            //Có mạng
            //Bật hết các View lên
            recyclerViewSearchResult.setVisibility(View.VISIBLE);
            constraintLayoutNoWifi.setVisibility(View.GONE);
//            textNoNetwork.setVisibility(View.GONE);
        } else {
            //Mất mạng
            //Bật hết các View lên
            recyclerViewSearchResult.setVisibility(View.GONE);
            constraintLayoutNoWifi.setVisibility(View.VISIBLE);
//            textNoNetwork.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void initProgressbar() {

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //do nothing
        updateUIFollowFirstChildTab.updateUIFollow(isFollowed, idStory, stories);
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //do nothing
        updateUIBookmarkSecondChildTab.updateUIBookmark(isBookmarked, idArticle, article);
    }

    @Override
    public void addNotificationFragment() {

    }

    @Override
    public void scrollToTop() {
        //do nothing
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_search;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Query search
        if (fragmentActivity == null) {
            searchView.clearFocus();
            return false;
        }
        View view = fragmentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
            //Có mạng
            if (query != null) {
                if (!query.equals("")) {
                    //keywords khác rỗng
                    search(query);
                } else {
                    //keywords rỗng
                }
            } else {
                //keywords null
            }
            updateUIStateSearch(SEARCH_NETWORK_AVAILABLE);
        } else {
            //Mất mạng
            updateUIStateSearch(SEARCH_NO_NETWORK);
        }
        searchView.clearFocus();
        return false;
    }

    private void search(String keywords) {
        dialog.show();

        //Gọi đến 2 fragment con để thực hiện tìm kiếm
        searchArticles.displayArticleResults(keywords, dialog);
        searchEvents.displayEventResults(keywords, dialog);
    }

//    private void search(String query) {
//        //Gửi thông tin lên server
//        dialog.show();
//        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
//            if (oldStateNetWork.equals(DISCONNECTED)) {
//                //Nếu trạng thái trước đó là Disconnected thì bật lại shimmer
////                mShimmerViewContainer.setVisibility(View.VISIBLE);
////                mShimmerViewContainer.startShimmerAnimation();
//                oldStateNetWork = CONNECTED;
//            }
//            actionDisableLoadBaseOnNetworkState(true);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(RootAPIUrlConst.URL_SEARCH_POST)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .build();
//            ServerAPI apiService = retrofit.create(ServerAPI.class);
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("keywords", query)
//                    .build();
//
//            Call<DatumSearchResult> call = apiService.searchEvent(requestBody);
//            call.enqueue(new Callback<DatumSearchResult>() {
//                @Override
//                public void onResponse(Call<DatumSearchResult> call, Response<DatumSearchResult> response) {
//                    DatumSearchResult datumSearchResult = response.body();
//
//                    if (datumSearchResult == null) {
//                        Toast.makeText(mContext, "Error: Call API successfully, but datumSearchResult is null!", Toast.LENGTH_SHORT).show();
//                        swipeContainer.setRefreshing(false);
//                        dialog.dismiss();
//                        return;
//                    }
//                    DataSearchResult dataSearchResult = datumSearchResult.getDataSearchResult();
//                    if (dataSearchResult == null) {
//                        Toast.makeText(mContext, "Error: Call API successfully, but dataSearchResult is null!", Toast.LENGTH_SHORT).show();
//                        swipeContainer.setRefreshing(false);
//                        dialog.dismiss();
//                        return;
//                    }
//                    List<EventSearchResult> eventSearchResults = dataSearchResult.getEventSearchResults();
//                    if (eventSearchResults == null) {
//                        Toast.makeText(mContext, "Error: Call API successfully, but list eventSearchResults is null!", Toast.LENGTH_SHORT).show();
//                        swipeContainer.setRefreshing(false);
//                        dialog.dismiss();
//                        return;
//                    }
//
//                    if (eventSearchResults.size() == 0) {
//                        Toast.makeText(mContext, "Không tìm được sự kiện, bài viết nào!", Toast.LENGTH_SHORT).show();
//                        swipeContainer.setRefreshing(false);
//                        dialog.dismiss();
//                        return;
//                    }
//                    searchResultAdapter.updateListResults(eventSearchResults);
//                    swipeContainer.setRefreshing(false);
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void onFailure(Call<DatumSearchResult> call, Throwable t) {
//                    Toast.makeText(mContext, "Fail to search data!", Toast.LENGTH_SHORT).show();
//                    swipeContainer.setRefreshing(false);
//                    dialog.dismiss();
//                }
//            });
//        } else {
//            //Mất mạng
//            actionDisableLoadBaseOnNetworkState(false);
////            mShimmerViewContainer.setVisibility(View.GONE);
////            mShimmerViewContainer.stopShimmerAnimation();
//            oldStateNetWork = DISCONNECTED;
//            swipeContainer.setRefreshing(false);
//            dialog.dismiss();
//        }
//    }

    public void showOrHideListResult(boolean isShowed) {
        //true --> show, false --> hide
        if (isShowed) {

        } else {

        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
