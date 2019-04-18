package com.anhdt.doranewsvermain.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.EventAdapterHorizontal;
import com.anhdt.doranewsvermain.adapter.recyclerview.StoryItemAdapter;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.ItemDetailStory;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DetailStoryFragment extends BaseFragment implements View.OnClickListener {
    private static String ARG_TYPE_TAB = "ARG_TYPE_TAB";
    private static String ARG_LIST_EVENT = "ARG_LIST_EVENT";
    private static String ARG_ID_STORY = "ARG_ID_STORY";

    private RecyclerView recyclerViewEvents;
    private Toolbar mToolbar;
    private Button btnFollow;
    private StoryItemAdapter storyItemAdapter;
    private AddFragmentCallback addFragmentCallback;
    private int typeTabHomeOrLatest;
    private boolean isFollowed = false;
    private String idStory;
    private ArrayList<ItemDetailStory> arrayItemDetailStories;

    public void setAddFragmentCallback(AddFragmentCallback addFragmentCallback) {
        this.addFragmentCallback = addFragmentCallback;
    }

    public static DetailStoryFragment newInstance(int typeTab, String jsonListEvents, String idStory) {
        Bundle args = new Bundle();
        DetailStoryFragment fragment = new DetailStoryFragment();
        args.putInt(ARG_TYPE_TAB, typeTab);
        args.putString(ARG_LIST_EVENT, jsonListEvents);
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
        mToolbar = view.findViewById(R.id.toolbar_detail_story);

        //Set back icon to toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> addFragmentCallback.popBackStack());

        recyclerViewEvents = view.findViewById(R.id.recycler_detail_story);
        btnFollow = view.findViewById(R.id.btn_follow_story);

        //===GetData====
        Bundle bundle = getArguments();
        typeTabHomeOrLatest = bundle.getInt(ARG_TYPE_TAB);
        idStory = bundle.getString(ARG_ID_STORY);
        String jsonListEvent = bundle.getString(ARG_LIST_EVENT);

        Gson gson = new Gson();
        ArrayList<Event> mArrayEvents = gson.fromJson(jsonListEvent, new TypeToken<List<Event>>() {
        }.getType());

        //===Đổ Data sang RecyclerView===
        if (mArrayEvents != null) {
            arrayItemDetailStories = GeneralTool.convertToListDatumStory(mArrayEvents);
            //Làm tiếp trong này nhá
            LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);
            recyclerViewEvents.setLayoutManager(linearLayoutManagerHorizontal);
            recyclerViewEvents.setHasFixedSize(true);
            recyclerViewEvents.setNestedScrollingEnabled(false);

            btnFollow.setOnClickListener(this);

            //===Adapter===
            storyItemAdapter = new StoryItemAdapter(arrayItemDetailStories, getContext(),
                    typeTabHomeOrLatest, idStory, addFragmentCallback);
            recyclerViewEvents.setAdapter(storyItemAdapter);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_detail_story;
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_follow_story) {
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

        }
    }
}
