package com.anhdt.doranewsvermain.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.CategoryAdapter;
import com.anhdt.doranewsvermain.adapter.recyclerview.NewsSourceAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.categoryresult.CategoryResult;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newsresult.News;
import com.anhdt.doranewsvermain.model.newssourceresult.GeneralDataNewsSource;
import com.anhdt.doranewsvermain.model.newssourceresult.NewsSource;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PickNewsSourceActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textPickDone, textPickAll;
    private IndexFastScrollRecyclerView mRecyclerPick;
    private ProgressDialog dialog;
    private NewsSourceAdapter mNewsSourceAdapter;
    private GeneralDataNewsSource mGeneralDataNewsSource;

    private ArrayList<NewsSource> mListAllNewsSource; //All from api
    private ArrayList<NewsSource> mListPickedNewsSource; //news sources picked

    private String uId;
    private ServerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_news_source);
        getUIdFromIntent();

        initViews();
    }

    private void getUIdFromIntent() {
        uId = ReadCacheTool.getUId(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ServerAPI.class);
    }

    private void initViews() {
        textPickDone = findViewById(R.id.text_pick_done_source_act);
        textPickAll = findViewById(R.id.text_pick_all_source_act);
        mRecyclerPick = findViewById(R.id.recycler_source_act);

        textPickAll.setVisibility(View.GONE);
        textPickDone.setVisibility(View.GONE);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending...");
        dialog.setCancelable(false);

        setUpAdapter();
        textPickDone.setOnClickListener(this);
    }

    private void getListSourcePicked() {
        mListPickedNewsSource = new ArrayList<>();
        Call<GeneralDataNewsSource> call = apiService.getNewsSourcePicked(uId);
        Log.e("-uuu", uId);
        call.enqueue(new Callback<GeneralDataNewsSource>() {
            @Override
            public void onResponse(Call<GeneralDataNewsSource> call, Response<GeneralDataNewsSource> response) {
                GeneralDataNewsSource generalDataNewsSource = response.body();
                if (generalDataNewsSource == null) {
                    Toast.makeText(getApplicationContext(), "Failed to load data - null", Toast.LENGTH_SHORT).show();
                    return;
                }
                mListPickedNewsSource = (ArrayList<NewsSource>) generalDataNewsSource.getData();
                if (mListPickedNewsSource == null) {
                    Toast.makeText(getApplicationContext(), "Failed to load mListPickedNewsSource - null", Toast.LENGTH_SHORT).show();
                    return;
                }
                mNewsSourceAdapter.setmListSourceChosen(mListPickedNewsSource);
            }

            @Override
            public void onFailure(Call<GeneralDataNewsSource> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load news sources picked - onFailure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpAdapter() {
        mListAllNewsSource = new ArrayList<>();
        mNewsSourceAdapter = new NewsSourceAdapter(mListAllNewsSource,
                this, textPickDone, textPickAll);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerPick.setLayoutManager(linearLayoutManager);
        mRecyclerPick.setAdapter(mNewsSourceAdapter);
        loadSources();
        getListSourcePicked();
    }

    private void loadSources() {
        Call<GeneralDataNewsSource> call = apiService.getListNewsSource();
        call.enqueue(new Callback<GeneralDataNewsSource>() {
            @Override
            public void onResponse(Call<GeneralDataNewsSource> call, Response<GeneralDataNewsSource> response) {
                mGeneralDataNewsSource = response.body();
                if (mGeneralDataNewsSource == null) {
                    Toast.makeText(getApplicationContext(), "Failed to load data - null", Toast.LENGTH_SHORT).show();
                    return;
                }
                mListAllNewsSource = (ArrayList<NewsSource>) mGeneralDataNewsSource.getData();
                if (mListAllNewsSource == null) {
                    Toast.makeText(getApplicationContext(), "Failed to load mListAllNewsSource - null", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mListAllNewsSource.size() > 0) {
                    Collections.sort(mListAllNewsSource, (object1, object2) -> object1.getId().compareTo(object2.getId()));
                }
                mNewsSourceAdapter.updateListSources(mListAllNewsSource);
                textPickAll.setVisibility(View.VISIBLE);
                textPickDone.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<GeneralDataNewsSource> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load news sources - onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_pick_done_source_act:
                List<NewsSource> newsSourcesPicked = mNewsSourceAdapter.getmListSourceChosen();
                if (newsSourcesPicked == null) {
                    return;
                }
                if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(this))) {
                    sendSourcesToServer(newsSourcesPicked);
                } else {
                    //Mất mạng
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage("Không có kết nối mạng");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
                break;
            default:
                break;
        }
    }

    private void sendSourcesToServer(List<NewsSource> newsSourcesPicked) {
        dialog.show();
        String picklist = "";
        for (int i = 0; i < newsSourcesPicked.size(); i++) {
            NewsSource newsSource = newsSourcesPicked.get(i);
            if (i == newsSourcesPicked.size() - 1) {
                picklist = picklist + newsSource.getId();
            } else {
                picklist = picklist + newsSource.getId() + ",";
            }
        }
        if (picklist.equals("")) {
            picklist = "\"" + "\"";
        }
        Log.e("lop-", picklist);
        Call<GeneralDataNewsSource> call = apiService.sendNewsSourcePicked(uId, picklist);

        //show url request
        Log.e("urlx-", call.request().url().toString());
        call.enqueue(new Callback<GeneralDataNewsSource>() {
            @Override
            public void onResponse(Call<GeneralDataNewsSource> call, Response<GeneralDataNewsSource> response) {
                GeneralDataNewsSource mGeneralSentSource = response.body();
                if (mGeneralSentSource == null) {
                    Toast.makeText(getApplicationContext(), "Failed to send data - null", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                ArrayList<NewsSource> mListSentSource = (ArrayList<NewsSource>) mGeneralSentSource.getData();
                if (mListSentSource == null) {
                    Toast.makeText(getApplicationContext(), "Failed to load mListSentSource - null", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                dialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<GeneralDataNewsSource> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to send data - onFailure", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
