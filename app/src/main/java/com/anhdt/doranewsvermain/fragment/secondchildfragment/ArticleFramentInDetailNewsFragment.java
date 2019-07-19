package com.anhdt.doranewsvermain.fragment.secondchildfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.activity.ReadOriginalArticleActivity;
import com.anhdt.doranewsvermain.config.ConfigSettings;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseNormalFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

public class ArticleFramentInDetailNewsFragment extends BaseNormalFragment implements View.OnClickListener {
    public static final String PARAM_DETAIL_NEWS = "param_news";
    public static final String PARAM_LIST_NEWS = "param_list_news";
    public static final String PARAM_POSITION = "param_position";

    private ImageView btnPlay, imageCoverNews;
    private TextView txtTitleNews, textCategory, txtContentNews, textSource;
    private Button btnReadMore;
    private View viewCoverage;

    //Bài báo ứng với Fragment này
    private Article currentArticles;
    private ControlVoice controlVoice;

    public ControlVoice getControlVoice() {
        return controlVoice;
    }

    public void setControlVoice(ControlVoice controlVoice) {
        this.controlVoice = controlVoice;
    }

    //voice đang chạy?
    private boolean mIsPlaying = false;

    private ArrayList<Article> listTotalArticles;

    private int position;
    private Context mContext;

    public ArrayList<Article> getListTotalArticles() {
        return listTotalArticles;
    }

    public ArticleFramentInDetailNewsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    public static ArticleFramentInDetailNewsFragment newInstance(String jsonStringCurrentArticle, String jsonListTotalArticles, int position) {
        ArticleFramentInDetailNewsFragment articleFramentInDetailNewsFragment = new ArticleFramentInDetailNewsFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_DETAIL_NEWS, jsonStringCurrentArticle);
        args.putString(PARAM_LIST_NEWS, jsonListTotalArticles);
        args.putInt(PARAM_POSITION, position);
        articleFramentInDetailNewsFragment.setArguments(args);
        return articleFramentInDetailNewsFragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initializeComponents() {
        View view = getView();
        if (view == null) {
            return;
        }
        imageCoverNews = view.findViewById(R.id.image_cover_fr_detail_news);
//        textFirstSummary = view.findViewById(R.id.text_content_first_fr_detail_news);
        txtTitleNews = view.findViewById(R.id.text_fr_title_detail_news);
        txtContentNews = view.findViewById(R.id.text_content_fr_detail_news);
        btnReadMore = view.findViewById(R.id.btn_read_more_fr_detail_news);
//        TextView txtSourceSummary = view.findViewById(R.id.text_source_summary);
        viewCoverage = view.findViewById(R.id.view_coverage_fr_detail_news);
        btnPlay = view.findViewById(R.id.image_play_fr_detail_news);


        //====
        Typeface customFontNormal = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/calibril.ttf");
//        Typeface customFontItalic = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/calibriitalic.ttf");
//        textSource.setTypeface(customFontItalic);
        txtContentNews.setTypeface(customFontNormal);
//        textFirstSummary.setTypeface(customFontNormal);

        //load view
        if (getArguments() == null
                || getArguments().getString(PARAM_DETAIL_NEWS) == null
                || getArguments().getString(PARAM_LIST_NEWS) == null) {
//            Log.e(this.getClass().getName(), "NULL CMNR");
            return;
        }

        String jsonStringNews = getArguments().getString(PARAM_DETAIL_NEWS);
        String jsonTotalNews = getArguments().getString(PARAM_LIST_NEWS);
        position = getArguments().getInt(PARAM_POSITION);

        Gson gson = new Gson();

        //Bài báo hiện tại của Fragment này
        currentArticles = gson.fromJson(jsonStringNews, Article.class);
        if (currentArticles == null) {
            return;
        }

        listTotalArticles = gson.fromJson(jsonTotalNews, new TypeToken<ArrayList<Article>>() {
        }.getType());

        if (listTotalArticles == null) {
            return;
        }

        loadData(view);
    }

    @SuppressLint("SetTextI18n")
    private void loadData(View view) {
        Glide.with(mContext).load(currentArticles.getImage()).
                apply(new RequestOptions().override(400, 0).
                        placeholder(R.drawable.image_default).error(R.drawable.image_default))
                .into(imageCoverNews);
        txtTitleNews.setText(currentArticles.getTitle());
        //Summary
        String mediumSummary = GeneralTool.getSummaryOfArticle(currentArticles, ConstParam.MEDIUM);
//        String upperString = mediumSummary.substring(0, 1).toUpperCase() + mediumSummary.substring(1);
        //=======
//        upperString = upperString.replaceAll("\\. ", "\\.");
//        String[] arrayString = upperString.split("\n");
//        if (arrayString.length == 1) {
//            textFirstSummary.setText("");
//            txtContentNews.setText(upperString);
//        } else if (arrayString.length > 1) {
//            textFirstSummary.setText(arrayString[0]);
//            String x = upperString.replace(arrayString[0] + "\n", "");
//            txtContentNews.setText(x);
//        }
        txtContentNews.setText(mediumSummary);
        btnReadMore.setOnClickListener(view1 -> {
            //Xử lý sự kiện khi click vào button xem chi tiết bài báo, mở ra 1 activity riêng, tạm thời show
            //cmn web view ra :v
            if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
                Intent intent = new Intent(view1.getContext(), ReadOriginalArticleActivity.class);
                intent.putExtra(ConstParamTransfer.TRANSFER_URL_FR_DETAIL_ARTICLE_TO_READ_ORIGINAL_ACT, currentArticles.getUrl());
                startActivity(intent);
            } else {
                //Mất mạng
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Không có kết nối mạng");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }

        });
        if (ConfigSettings.isPlayedVoice) {
            btnPlay.setVisibility(View.VISIBLE);
            viewCoverage.setVisibility(View.VISIBLE);
            btnPlay.setOnClickListener(this);
        } else {
            btnPlay.setVisibility(View.GONE);
            viewCoverage.setVisibility(View.GONE);
        }

        if (ReadCacheTool.getDetailArticleTest(mContext)) {
            textCategory = view.findViewById(R.id.text_category_fr_detail_news);
            textSource = view.findViewById(R.id.text_source_fr_detail_news);
            textSource.setText("Theo " + currentArticles.getSource().getName() + " - " + currentArticles.getReadableTime());
            textCategory.setText(currentArticles.getCategory().getName());
        }
    }

    @Override
    protected int getFragmentLayout() {
//        boolean isTested = ReadCacheTool.getDetailArticleTest(mContext);
        return R.layout.fragment_article_in_frg_detail_news_3;
//        if (isTested) {
//            return R.layout.fragment_article_in_frg_detail_news_3;
//        } else {
//            return R.layout.fragment_article_in_frg_detail_news;
//        }
    }

    @Override
    protected void initProgressbar() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_play_fr_detail_news:
                if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
                    if (mIsPlaying) {
                        btnPlay.setPressed(true);
                        mIsPlaying = false;
                    } else {
                        btnPlay.setPressed(false);
                        mIsPlaying = true;
                    }
                    //handle khi click vào play voice
                    if (this.controlVoice != null) {
                        controlVoice.playVoiceAtPosition(listTotalArticles, position);
                    }
                } else {
                    //Mất mạng
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage("Không có kết nối mạng để nghe bài báo này");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
                break;
            default:
                break;
        }
    }
}
