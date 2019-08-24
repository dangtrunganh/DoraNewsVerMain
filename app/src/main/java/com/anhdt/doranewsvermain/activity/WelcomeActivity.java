package com.anhdt.doranewsvermain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.anhdt.doranewsvermain.MainActivity;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.viewpagerwelcome.WelcomePagerAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.config.ConfigSettings;
import com.anhdt.doranewsvermain.constant.ConstParamLogging;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.Logging;
import com.anhdt.doranewsvermain.model.OSGroup;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.LogTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.anhdt.doranewsvermain.util.ReadRealmToolForLogging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

import static com.anhdt.doranewsvermain.config.ConfigSettings.TIME_SESSION;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mPager;
    private ScrollingPagerIndicator indicator;
    private Button btnSkip, btnNext;

    private String ipAddress;
    private String versionAndroid;
    private String userAgent;
    private OSGroup osGroup;

    private int[] layouts = {R.layout.fragment_slider_item_1, R.layout.fragment_slider_item_2,
            R.layout.fragment_slider_item_3, R.layout.fragment_slider_item_4};

    private WelcomePagerAdapter welcomePagerAdapter;
    //    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private ImageView[] dots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInfoLogging();

        ArrayList<Logging> loggings = ReadRealmToolForLogging.getListLoggingInLocal(this);
        if (loggings != null) {
            if (loggings.size() > 0) {
                ConfigSettings.numberOfLogging = loggings.size();
                ConfigSettings.lastSessionId = loggings.get(loggings.size() - 1).getSessionId();
                ConfigSettings.lastTimeCreated = loggings.get(loggings.size() - 1).getTimeCreate();
//                Log.e("pil-detail", loggings.toString());
            } else {
                //default init
                ConfigSettings.numberOfLogging = 0;
                ConfigSettings.lastTimeCreated = 0;
                ConfigSettings.lastSessionId = "-1";
            }
        } else {
            //default init
            ConfigSettings.numberOfLogging = 0;
            ConfigSettings.lastTimeCreated = 0;
            ConfigSettings.lastSessionId = "-1";
        }

//        Log.e("pil-", ConfigSettings.numberOfLogging + "");
        //===create log, if number of loggings == 10, send to server.
        cacheLog();
//        sendLoggingStartApp(new ArrayList<>());

        if (ReadCacheTool.checkPreferenceWelcomeActivity(this)) {
            //data ở notification truyền về màn này đầu tiên
            Intent intentResult = new Intent(WelcomeActivity.this, SplashActivity.class);
            //===getIntentExtras====
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String eventId = bundle.getString(ConstParamTransfer.KEY_EVENT_ID_NOTIFICATION);
                String longEventId = bundle.getString(ConstParamTransfer.KEY_LONG_EVENT_ID_NOTIFICATION);
//                Log.e("iii-event_id", eventId);
//                Log.e("iii-story_id", longEventId);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN, eventId);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN, longEventId);
            }
            startActivity(intentResult);
            finish();
//            loadHomeActivity();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_welcome_2);

        mPager = findViewById(R.id.view_pager_welcome_activity);
        welcomePagerAdapter = new WelcomePagerAdapter(layouts, this);
        mPager.setAdapter(welcomePagerAdapter);
        mPager.setOffscreenPageLimit(4);

        indicator = findViewById(R.id.indicator_welcome_activity);
        indicator.attachToPager(mPager);

        btnSkip = findViewById(R.id.button_skip_welcome_activity);
        btnNext = findViewById(R.id.button_next_welcome_activity);
        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == layouts.length - 1) {
                    btnNext.setText("Start");
                    btnSkip.setVisibility(View.INVISIBLE);
                } else {
                    btnNext.setText("Next");
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
                -1,
                ipAddress,
                osGroup,
                ConstParamLogging.EVENT_ID_START_APP,
                "default_event_id",
                "default_cat_id", ConfigSettings.lastTimeCreated);
        ReadRealmToolForLogging.addLoggingToRealm(this, logging);
        ConfigSettings.numberOfLogging++;
//        ConfigSettings.lastSessionId = sessionId;

        if (ConfigSettings.numberOfLogging >= ConfigSettings.NUMBER_LOGGING_TO_SEND) {
            //sendLoggingStartApp
//            Log.e("polp-", ConfigSettings.numberOfLogging + "");
            LogTool.sendLogging(this);
//            sendLogging();
            //Delete cache log
            ReadRealmToolForLogging.deleteAllLogging(this);
            //reset numberOfLogging
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
//        Log.e("send-log", json);
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

//    private void sendLoggingStartApp(ArrayList<Logging> loggings) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RootAPIUrlConst.IP_ADDRESS_LOGGING)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        final ServerAPI apiService = retrofit.create(ServerAPI.class);
//
//        String sessionId = LogTool.getSessionId(getApplicationContext());
//        String ipAddress = LogTool.getIpAddress(getApplicationContext());
//        String versionAndroid = GeneralTool.getVersionAndroid();
//        String userAgent = GeneralTool.getNameOfDevice();
//
//        Log.e("ILO-sessionId", sessionId);
//        Log.e("ILO-ipAddress", ipAddress);
//        Log.e("ILO-versionAndroid", versionAndroid);
//        Log.e("ILO-userAgent", userAgent);
//
//        Call<Void> call = apiService.getLoggingStartApp(sessionId, ipAddress,
//                ConstParamLogging.OS_CODE_ANDROID, versionAndroid, userAgent, ConstParamLogging.EVENT_ID_START_APP);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_next_welcome_activity:
                loadNextSlide();
                break;
            case R.id.button_skip_welcome_activity:
                loadHomeActivity();
                ReadCacheTool.storeWelcomeScreen(this);
                break;
            default:
                break;
        }
    }

    private void loadHomeActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void loadNextSlide() {
        int nextSlide = mPager.getCurrentItem() + 1;
        if (nextSlide < layouts.length) {
            mPager.setCurrentItem(nextSlide);
        } else {
            loadHomeActivity();
            ReadCacheTool.storeWelcomeScreen(this);
        }
    }
}
