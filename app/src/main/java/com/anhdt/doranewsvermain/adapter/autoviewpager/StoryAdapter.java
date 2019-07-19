package com.anhdt.doranewsvermain.adapter.autoviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class StoryAdapter extends PagerAdapter {
    private ArrayList<Event> arrayEvents;
    private LayoutInflater inflater;
    private Context mContext;
    private AutoScrollViewPager viewPager;
    private boolean isTextAnimating; //true nếu animation đang chạy, false nếu animation tắt rồi

    private String idStory;
//    private int typeTabContent;
    private AddFragmentCallback addFragmentCallback;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public StoryAdapter(ArrayList<Event> arrayEvents, Context context, AutoScrollViewPager viewPager, String idStory, /*int typeTabContent,*/ AddFragmentCallback addFragmentCallback) {
        this.arrayEvents = arrayEvents;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.viewPager = viewPager;
        this.idStory = idStory;
//        this.typeTabContent = typeTabContent;
        this.addFragmentCallback = addFragmentCallback;

//        ViewPager view = new ViewPager(mContext);
//        this.viewPager.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
////            int posi = this.viewPager.getCurrentItem();
////            Log.e("posi-", String.valueOf(posi));
////            View view = viewPager.findViewWithTag(String.valueOf(posi));
////            view.
//        });
    }

    @Override
    public int getCount() {
        return arrayEvents.size();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = inflater.inflate(
                R.layout.item_event_in_vpg_stories_2, container, false);

        ImageView newsBackgroundLayout = itemView.findViewById(R.id.lnl_background_text_event_item_event_in_vpg);
//        TextView tvSum = itemView.findViewById(R.id.text_sum_content_item_event_in_vpg);
        TextView tvCategory = itemView.findViewById(R.id.text_category_item_event_in_vpg);
        TextView tvNumberArticles = itemView.findViewById(R.id.text_number_articles_item_event_in_vpg);
        TextView tvTimeReadable = itemView.findViewById(R.id.text_time_item_event_in_vpg);
        TextView tvTitleEvent = itemView.findViewById(R.id.text_title_event_item_event_in_vpg);
//        ConstraintLayout constraintLayout = itemView.findViewById(R.id.constraint_layout_item_event_vpg);

        //Tạo chiều rộng của Viewpager = width màn hình
        //Tạo chiều cao của ViewPager = height * 0.9

        //=======change====set cứng============
//        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
//        int width = metrics.widthPixels;
//        ViewGroup.LayoutParams layoutParams = constraintLayout.getLayoutParams();
//        layoutParams.width = width;
//        layoutParams.height = (int) (width * 0.65);
//        constraintLayout.setLayoutParams(layoutParams);
        //=======change====set cứng============


        Event event = arrayEvents.get(position);

//        CustomView customView = new CustomView(mContext, itemView);
        if (event.getImage() != null) {
            if (!event.getImage().equals("")) {
//                Picasso.get().load(event.getImage()).into(newsBackgroundLayout);

                Glide.with(itemView.getContext()).load(event.getImage()).
                        apply(new RequestOptions().override(400, 0).
                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
                        .into(newsBackgroundLayout);
            }
        }
        tvCategory.setText(event.getCategory().getName());
        tvNumberArticles.setText(String.valueOf(event.getNumArticles()) + " bài báo");
        tvTimeReadable.setText(event.getReadableTime());
        tvTitleEvent.setText(event.getTitle());

        //Summary
//        BackgroundColorSpan backgroundColorSpanBlack = new BackgroundColorSpan(Color.parseColor("#B3000000"));
////        BackgroundColorSpan backgroundColorSpanNull = new BackgroundColorSpan(Color.parseColor("#00000000"));
//
//        String summaryContent = event.getContent();
//        SpannableString str = new SpannableString(summaryContent);
//        if (!summaryContent.equals("")) {
//            str.setSpan(backgroundColorSpanBlack, 0, summaryContent.length(), 0);
//        }

//        Animation mAnimation;
//        //Nếu cần tắt animation đi
//        mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.move_summary);
//        mAnimation.setFillAfter(true);
//        tvSum.setText(str);
//        tvSum.setTextColor(0xffffffff);
//        tvSum.setAnimation(mAnimation);
//        mAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
////                tvSum.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
////                tvSum.setTextColor(0x00000000);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        container.addView(itemView);
        itemView.setOnClickListener(v -> {
            //Có position rồi này
            //Bật lên DetailEventFrg, chú ý truyền vào Type = DetailEventFragment.TYPE_IN
            String idEvent = event.getId();
//            String titleEvent = event.getTitle();

            Log.e("uu-id-out-story", idStory);
            //Là sự kiện hiển thị chi tiết bài báo đơn lẻ, nên sẽ truyền idStory là idStory
            String jsonListEvent = new Gson().toJson(arrayEvents);

            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(mContext))) {
                DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(/*typeTabContent,*/ idEvent/*, titleEvent*/, idStory, jsonListEvent);
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
        });
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof View) {
            return;
        }
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return o.equals(view);
    }
}
