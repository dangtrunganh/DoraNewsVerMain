package com.anhdt.doranewsvermain.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.config.ConfigSettings;
import com.anhdt.doranewsvermain.constant.ConstParamLogging;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.Logging;
import com.anhdt.doranewsvermain.model.OSGroup;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.LogTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForLogging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.anhdt.doranewsvermain.config.ConfigSettings.TIME_SESSION;

public class ReadOriginalArticleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webView;
    private int articleId = -1;

    private String ipAddress;
    private String versionAndroid;
    private String userAgent;
    private OSGroup osGroup;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_original_article);
        initViews();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        getInfoLogging();
        webView = findViewById(R.id.web_view_wv);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        toolbar = findViewById(R.id.toolbar_news_wv);
        setSupportActionBar(toolbar);
        this.setTitle("Đọc chi tiết");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        loadLink();

        //====send==logging====
        //====send====on===click====article==summary======
        cacheLog();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RootAPIUrlConst.IP_ADDRESS_LOGGING)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        final ServerAPI apiService = retrofit.create(ServerAPI.class);
//        String sessionId = LogTool.getSessionId(getApplicationContext());
//        String ipAddress = LogTool.getIpAddress(getApplicationContext());
//        String versionAndroid = GeneralTool.getVersionAndroid();
//        String userAgent = GeneralTool.getNameOfDevice();
//
//        Log.e("ADLO-sessionId", sessionId);
//        Log.e("ADLO-ipAddress", ipAddress);
//        Log.e("ADLO-versionAndroid", versionAndroid);
//        Log.e("ADLO-userAgent", userAgent);
//
//        Call<Void> call = apiService.getLoggingClickArticle(sessionId, articleId, ipAddress,
//                ConstParamLogging.OS_CODE_ANDROID, versionAndroid, userAgent, ConstParamLogging.EVENT_ID_READ_DETAIL);
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
        ipAddress = LogTool.getIpAddress(getApplicationContext());
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
            sessionId = LogTool.getSessionId(getApplicationContext());
            ConfigSettings.lastSessionId = sessionId;
        }
        ConfigSettings.lastTimeCreated = LogTool.getTimeCreate();

//        OSGroup osGroup = new OSGroup(ConstParamLogging.OS_CODE_ANDROID, versionAndroid, userAgent);
        Logging logging = new Logging(sessionId,
                articleId,
                ipAddress,
                osGroup,
                ConstParamLogging.EVENT_ID_READ_DETAIL,
                "default_event_id",
                "default_cat_id", ConfigSettings.lastTimeCreated);
        ReadRealmToolForLogging.addLoggingToRealm(this, logging);
        ConfigSettings.numberOfLogging++;

        if (ConfigSettings.numberOfLogging >= ConfigSettings.NUMBER_LOGGING_TO_SEND) {
            LogTool.sendLogging(this);
            ReadRealmToolForLogging.deleteAllLogging(this);
            ConfigSettings.numberOfLogging = 0;
        }
    }

//    private void sendLogging() {
//        ArrayList<Logging> loggings = ReadRealmToolForLogging.getListLoggingInLocal(this);
//        if (loggings == null) {
//            return;
//        }
//
//        if (loggings.size() == 0) {
//            return;
//        }
//
//        Gson gson = new Gson();
//        String json = gson.toJson(loggings);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RootAPIUrlConst.IP_ADDRESS_LOGGING)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient.build())
//                .build();
//
//        final ServerAPI apiService = retrofit.create(ServerAPI.class);
//        Call<Void> sendLogging = apiService.postRawJSON(json);
//        sendLogging.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Log.e("send-log", "ok-welcome");
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e("send-log", "fail-welcome");
//            }
//        });
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    private void loadLink() {
        Intent intent = getIntent();
        if (intent != null) {
            String urll = intent.getStringExtra(ConstParamTransfer.TRANSFER_URL_FR_DETAIL_ARTICLE_TO_READ_ORIGINAL_ACT);
            articleId = intent.getIntExtra(ConstParamTransfer.TRANSFER_ARTICLE_ID_FR_DETAIL_ARTICLE_TO_READ_ORIGINAL_ACT, -1);
//            urll = urll.replace("/r/", "/c/");
            if (urll == null) {
                urll = "https://baomoi.com/thua-dau-indonesia-hlv-u22-viet-nam-chi-trich-trong-tai/c/29768439.epi";
            }
            webView.loadUrl(urll);
        } else {
            Toast.makeText(this, "Error, nothing in intent url news", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
