package com.anhdt.doranewsvermain.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.StoryItemAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragmentNeedUpdateUI;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.UpdateUIFollowBookmarkChild;
import com.anhdt.doranewsvermain.model.ItemDetailStory;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.model.newsresult.Stories;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailStoryFragment extends BaseFragmentNeedUpdateUI implements View.OnClickListener, UpdateUIFollowBookmarkChild {
    private static String ARG_TYPE_TAB = "ARG_TYPE_TAB";
    private static String ARG_LIST_EVENT = "ARG_LIST_EVENT";
    private static String ARG_ID_STORY = "ARG_ID_STORY";
    private static String DEFAULT_USER_ID = "8def96c8-5f32-4fcf-9c0f-ce76ca719d65";

    private RecyclerView recyclerViewEvents;
    private Toolbar mToolbar;
    private Button btnFollow;
    private ProgressDialog dialog;
    private StoryItemAdapter storyItemAdapter;
    private AddFragmentCallback addFragmentCallback;
    private AlertDialog alertDialog;
//    private int typeTabHomeOrLatest;
    //    private boolean isFollowed = false;
    private int stateFollow = -1;


    //    private boolean isBlueButton = false; //Biến này chỉ để updateUI thôi, ko được dùng
    //Nếu Đã theo dõi -> Chuyển thành true, trước đó là false
    //Nếu bỏ theo dõi -> Chuyển thành false, trước đó là true
    private String idStory;
    private ArrayList<ItemDetailStory> arrayItemDetailStories;

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static DetailStoryFragment newInstance(/*int typeTab*//*, String jsonListEvents*//*,*/ String idStory) {
        Bundle args = new Bundle();
        DetailStoryFragment fragment = new DetailStoryFragment();
//        args.putInt(ARG_TYPE_TAB, typeTab);
//        args.putString(ARG_LIST_EVENT, jsonListEvents);
        args.putString(ARG_ID_STORY, idStory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        mToolbar = view.findViewById(R.id.toolbar_detail_story);

        //Set back icon to toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> addFragmentCallback.popBackStack());

        recyclerViewEvents = view.findViewById(R.id.recycler_detail_story);
        LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewEvents.setLayoutManager(linearLayoutManagerHorizontal);
        recyclerViewEvents.setHasFixedSize(true);
//        recyclerViewEvents.setNestedScrollingEnabled(false);

        btnFollow = view.findViewById(R.id.btn_follow_story);

        //===GetData====
        Bundle bundle = getArguments();
//        typeTabHomeOrLatest = bundle.getInt(ARG_TYPE_TAB);
        this.idStory = bundle.getString(ARG_ID_STORY);


//        Log.e("pipi-receiver", this.idStory);

//        if (idStory == null) {
//            return;
//        }
//        if (!idStory.equals("")) {
//            return;
//        }
        String uId = ReadCacheTool.getUId(getContext());
        loadData(this.idStory, uId);
//        String jsonListEvent = bundle.getString(ARG_LIST_EVENT);

//        Gson gson = new Gson();
//        ArrayList<Event> mArrayEvents = gson.fromJson(jsonListEvent, new TypeToken<List<Event>>() {
//        }.getType());

//        //===Đổ Data sang RecyclerView===
//        if (mArrayEvents != null) {
//            arrayItemDetailStories = GeneralTool.convertToListDatumStory(mArrayEvents);
//            //Làm tiếp trong này nhá
//            LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getActivity(),
//                    LinearLayoutManager.VERTICAL, false);
//            recyclerViewEvents.setLayoutManager(linearLayoutManagerHorizontal);
//            recyclerViewEvents.setHasFixedSize(true);
//            recyclerViewEvents.setNestedScrollingEnabled(false);
//
//            btnFollow.setOnClickListener(this);
//
//            //===Adapter===
//            storyItemAdapter = new StoryItemAdapter(arrayItemDetailStories, getContext(),
//                    typeTabHomeOrLatest, idStory, addFragmentCallback, mArrayEvents);
//            recyclerViewEvents.setAdapter(storyItemAdapter);
//        }
    }

    @Override
    protected void initProgressbar() {

    }

    private void loadData(String idStory, String uId) {
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
                    Log.e("ee-idStory", idStory);
                    if (idStory.equals("")) {
                        Log.e("ee-idStory", "empty");
                    }
                    return;
                }
//                int follow = stories.getFollow();
                //Kiểm tra biến này để bật/tắt follow
                ArrayList<Event> mArrayEvents = (ArrayList<Event>) stories.getEvents();
                Log.e("p0-", mArrayEvents.toString());
                if (mArrayEvents == null) {
                    return;
                }
                if (mArrayEvents.size() == 0) {
                    return;
                }
                //load data to recyclerView
                arrayItemDetailStories = GeneralTool.convertToListDatumStory(mArrayEvents);
                btnFollow.setOnClickListener(DetailStoryFragment.this);
                //===Adapter===
                storyItemAdapter = new StoryItemAdapter(arrayItemDetailStories, getContext(),
                        /*typeTabHomeOrLatest,*/ idStory, addFragmentCallback, mArrayEvents);
                recyclerViewEvents.setAdapter(storyItemAdapter);

                stateFollow = stories.getFollow();
                if (stateFollow == RootAPIUrlConst.FOLLOW_INTEGER) {
                    updateUIWhenFollow(true);
                } else if (stateFollow == RootAPIUrlConst.UN_FOLLOW_INTEGER) {
                    updateUIWhenFollow(false);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load data - onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUIWhenFollow(boolean isFollowed) {
        if (isFollowed) {
            //Theo dõi
            final int sdk = android.os.Build.VERSION.SDK_INT;
            stateFollow = RootAPIUrlConst.FOLLOW_INTEGER;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                btnFollow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.border_un_follow_button));
            } else {
                btnFollow.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_un_follow_button));
            }
            btnFollow.setText("Bỏ theo dõi");
        } else {
            //Không theo dõi
            final int sdk = android.os.Build.VERSION.SDK_INT;
            stateFollow = RootAPIUrlConst.UN_FOLLOW_INTEGER;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                btnFollow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.border_follow_button));
            } else {
                btnFollow.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_follow_button));
            }
            btnFollow.setText("Theo dõi");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_follow_story) {
            //Khi click vào follow 1 story thì có nút này hiện lên
//            updateButtonUI();
            followEvent();
        }
    }

    private void initDialog(String uId) {
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Bỏ theo dõi?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Tớ đồng ý", (dialogg, which) -> {
            //Noi dung xu ly khi click vao button, mac dinh dialog se close sau khi click vao
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            final ServerAPI apiService = retrofit.create(ServerAPI.class);

            Call<Stories> call = apiService.followStory(uId, idStory, RootAPIUrlConst.UN_FOLLOW);

            call.enqueue(new Callback<Stories>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {
                    Stories stories = response.body();
                    if (stories == null) {
                        Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    stateFollow = stories.getFollow();
                    if (stateFollow == RootAPIUrlConst.UN_FOLLOW_INTEGER) {
                        //success, bỏ follow thành công
                        //Thông báo cho MainActivity thay đổi UI button
                        addFragmentCallback.updateListEventFollowInAddFrag(false, idStory, stories);
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Stories> call, Throwable t) {
                    Toast.makeText(getContext(), "Bỏ theo dõi thất bại!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            //Noi dung xu ly khi click vao button, mac dinh dialog se close sau khi click vao
        });
        alertDialog.setCanceledOnTouchOutside(false); //Vo hieu hoa khong cho kich ra ngoai de tat dialog
        alertDialog.setCancelable(false); //Vo hieu hoa khong cho an back de tat dialog
    }

    private void followEvent() {
        String uId = ReadCacheTool.getUId(getContext());
        if (stateFollow == RootAPIUrlConst.FOLLOW_INTEGER) {
            //Trạng thái hiện tại đang là Follow
            //Khi Click vào sẽ Unfollow - Chú ý là UNFOLLOW!!!
            initDialog(uId);
            alertDialog.show();
        } else if (stateFollow == RootAPIUrlConst.UN_FOLLOW_INTEGER) {
            //Trạng thái hiện tại đang là UnFollow, click vào sẽ là Follow
            //Trạng thái hiện tại đang là Follow
            //Khi Click vào sẽ follow - Chú ý là FOLLOW!!!
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            final ServerAPI apiService = retrofit.create(ServerAPI.class);

            Call<Stories> call = apiService.followStory(uId, idStory, RootAPIUrlConst.FOLLOW);

            call.enqueue(new Callback<Stories>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {
                    Stories stories = response.body();
                    if (stories == null) {
                        Toast.makeText(getContext(), "Error: Call API successfully, but data is null!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    stateFollow = stories.getFollow();
                    if (stateFollow == RootAPIUrlConst.FOLLOW_INTEGER) {
                        //success, follow thành công
                        //Thông báo cho MainActivity thay đổi UI button
                        addFragmentCallback.updateListEventFollowInAddFrag(true, idStory, stories);
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Stories> call, Throwable t) {
                    Toast.makeText(getContext(), "Bỏ theo dõi thất bại!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "State is undefined!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void updateUIFollow(boolean isFollowed, String idStory, Stories stories) {
        //Update lại UI
        if (this.idStory.equals(idStory)) {
            updateUIWhenFollow(isFollowed);
        }
    }

    @Override
    public void updateUIBookmark(boolean isBookmarked, int idArticle, Article article) {
        //Không cần update bookmark
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_detail_story;
    }
}
