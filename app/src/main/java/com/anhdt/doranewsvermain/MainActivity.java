package com.anhdt.doranewsvermain;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.broadcastreceiver.NetworkChangeReceiver;
import com.anhdt.doranewsvermain.constant.ConstGeneralTypeTab;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.ConstServiceFirebase;
import com.anhdt.doranewsvermain.fragment.ArticleFramentInDetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.FavoriteFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.MoreFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralHomeFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralLatestNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.service.voice.VoicePlayerService;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActionNetworkStateChange, VoicePlayerService.OnListenerActivity, ControlVoice {
    private static final String TAG = MainActivity.class.getName();
    private BottomNavigationView navigation;
    private TextView textConnectionState;

    //=====View-Notice=====
    private ConstraintLayout constraintLayoutViewNotice;
    private TextView textContentViewNotice, textTitleHot;
    private ImageView imageViewNotice, imageCloseViewNotice;
    //=====================

    //=====ViewControlMusic=====
    private TextView textTitleArticle, textStartTime, textEndTime;
    private ConstraintLayout constraintLayoutControlVoice;
    private SeekBar seekBarVoice;
    private CircleImageView imageCoverControlVoice;
    private ImageView imagePreviousVoice, imagePlayVoice, imageNextVoice, imageExitVoice;
    //=====================

    private final GeneralHomeFragment homeFragment = GeneralHomeFragment.newInstance();
    private final GeneralLatestNewsFragment latestNewsFragment = GeneralLatestNewsFragment.newInstance();
    private final FavoriteFragment favoriteFragment = FavoriteFragment.newInstance();
    private final MoreFragment moreFragment = MoreFragment.newInstance();

    private final FragmentManager fm = getSupportFragmentManager();

    private Fragment activeFragment = homeFragment;

    //Biến này dùng để điều khiển tab hiện tại bật ra Fragment, dùng cả cho sau này điều khiển nhạc nữa
    //Bật ra Fragment ở tab hiện tại mong muốn, hiện tại chỉ có tác dụng khi App chạy
    //Khi đó GeneralHomeFragment tồn tại, đã add vào một Fragment rồi? Right?
    private AddFragmentCallback activeAddFragmentCallback = homeFragment;

    private static final String NAME_HOME_FRAGMENT = "HOME_FRAGMENT";
    private static final String NAME_LATEST_NEWS_FRAGMENT = "LATEST_NEWS_FRAGMENT";
    private static final String NAME_FAVORITE_FRAGMENT = "FAVORITE_FRAGMENT";
    private static final String NAME_MORE_FRAGMENT = "MORE_FRAGMENT";
    private boolean doubleBackToExitPressedOnce = false;
    private NetworkChangeReceiver receiver;

    private String idEventFromBroadcast;
    private String idStoryFromBroadcast;
    private String urlImageFromBroadcast;

    private String contentNotice;
    private String titleHot;

    //========
    private VoicePlayerService mPlayerService;
    private ServiceConnection mConnection;
    private boolean mIsConnect;
    //========

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("llj-", intent.getAction());
//            if (intent.getAction().equals("MyData")) {
            idEventFromBroadcast = intent.getExtras().getString(ConstServiceFirebase.PARAM_ID_EVENT);
            idStoryFromBroadcast = intent.getExtras().getString(ConstServiceFirebase.PARAM_ID_LONG_EVENT);
            urlImageFromBroadcast = intent.getExtras().getString(ConstServiceFirebase.PARAM_URL_IMAGE);
            titleHot = intent.getExtras().getString(ConstServiceFirebase.PARAM_TITLE_HOT);
            contentNotice = intent.getExtras().getString(ConstServiceFirebase.PARAM_CONTENT_NOTICE);
            loadDataToNotice();
//            }

        }
    };

    private void loadDataToNotice() {
        constraintLayoutViewNotice.setVisibility(View.VISIBLE);
        textTitleHot.setText(titleHot);
        textContentViewNotice.setText(contentNotice);
        Glide.with(this).load(this.urlImageFromBroadcast).
                apply(new RequestOptions().override(400, 0).
                        placeholder(R.drawable.image_default).error(R.drawable.image_default))
                .into(imageViewNotice);
        Handler handler = new Handler();
        Runnable delayrunnable = () -> constraintLayoutViewNotice.setVisibility(View.GONE);
        handler.postDelayed(delayrunnable, 4000);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_hot:
                if (activeFragment == homeFragment) {
                    homeFragment.popAllBackStack();
                    return true;
                }
                fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                activeAddFragmentCallback = homeFragment;
                return true;
            case R.id.navigation_latest_news:
                if (activeFragment == latestNewsFragment) {
                    latestNewsFragment.popAllBackStack();
                    return true;
                }
                fm.beginTransaction().hide(activeFragment).show(latestNewsFragment).commit();
                activeFragment = latestNewsFragment;
                activeAddFragmentCallback = latestNewsFragment;
                return true;
            case R.id.navigation_favorite:
                fm.beginTransaction().hide(activeFragment).show(favoriteFragment).commit();
                activeFragment = favoriteFragment;
                activeAddFragmentCallback = favoriteFragment;
                return true;
            case R.id.navigation_more:
                fm.beginTransaction().hide(activeFragment).show(moreFragment).commit();
                activeFragment = moreFragment;
                activeAddFragmentCallback = moreFragment;
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("main-", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        receiver = new NetworkChangeReceiver(this);
        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);

        //=====bind service=====
        boundService();
    }

    private void boundService() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (iBinder instanceof VoicePlayerService.ArticleBinder) {
                    mIsConnect = true;
                    VoicePlayerService.ArticleBinder binder = (VoicePlayerService.ArticleBinder) iBinder;
                    mPlayerService = binder.getService();
                    mPlayerService.setmListenerActivity(MainActivity.this);
//                    updateUI();
                    if (mPlayerService != null) {
                        Log.e("m-", "Service is boned successfully!");
                    } else {
                        Log.e("m-", "null mnr");
                    }
                } else {
                    mIsConnect = false;
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mIsConnect = false;
            }
        };

        Intent intent = new Intent(this, VoicePlayerService.class);
        this.bindService(intent, mConnection, Service.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStart() {
        Log.d("main-", "onStart()");
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData")
        );
    }

    @Override
    protected void onResume() {
        Log.d("main-", "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("main-", "onPause()");
        super.onPause();
    }


    @Override
    protected void onStop() {
        Log.d("main-", "onStop()");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("main-", "onDestroy()");
        super.onDestroy();
    }

    private void initViews() {
        navigation = findViewById(R.id.navigation);
        textConnectionState = findViewById(R.id.text_connection_state);
        textTitleHot = findViewById(R.id.text_title_hot_view_notice);
        constraintLayoutViewNotice = findViewById(R.id.constraint_view_notice);
        textContentViewNotice = findViewById(R.id.text_content_view_notice);
        imageViewNotice = findViewById(R.id.image_cover_view_notice);
        imageCloseViewNotice = findViewById(R.id.image_close_view_notice);

        //====View-Control-Voice====
        textTitleArticle = findViewById(R.id.text_name_article_control_voice);
        textTitleArticle.setSelected(true);
        textTitleArticle.setOnClickListener(this);
        constraintLayoutControlVoice = findViewById(R.id.constraint_control_voice);
//        constraintLayoutControlVoice.setOnClickListener(this);
        textStartTime = findViewById(R.id.text_time_start_control_voice);
        textEndTime = findViewById(R.id.text_time_end_control_music);
        seekBarVoice = findViewById(R.id.seek_bar_control_voice);

        imageCoverControlVoice = findViewById(R.id.circle_image_cover_control_voice);
        imageCoverControlVoice.setOnClickListener(this);
        imagePreviousVoice = findViewById(R.id.image_previous_control_voice);
        imagePlayVoice = findViewById(R.id.image_play_pause_control_voice);
        imageNextVoice = findViewById(R.id.image_next_control_voice);
        imageExitVoice = findViewById(R.id.image_stop_control_music);
        //============


        constraintLayoutViewNotice.setVisibility(View.GONE);
        //Nhận list Category để truyền Params cho fragment LatestNewsInFragment
        //Chỉ return về listCategories string, vì 2 thông số idEvent và idLongEvent chỉ để truyển cho DetailEventAct
        Intent result = getIntent();
        if (result == null) {
            return;
        }
        String idEvent = result.getStringExtra(ConstParamTransfer.TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN);
        String idLongEvent = result.getStringExtra(ConstParamTransfer.TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN);

        String jsonCategories = result.getStringExtra(ConstParamTransfer.TRANSFER_LIST_CATEGORY_FR_SPLASH_TO_MAIN);
        String uId = result.getStringExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_MAIN);

        if (jsonCategories == null) {
            return;
        }

        //set up args cho HomeFragment
        Bundle argsHomeFrg = new Bundle();
        argsHomeFrg.putString(GeneralHomeFragment.PARAM_U_ID_GENERAL_HOME_FRG, uId);
        if (idEvent != null && idLongEvent != null) {
            argsHomeFrg.putString(GeneralHomeFragment.ARG_EVENT_ID, idEvent);
            argsHomeFrg.putString(GeneralHomeFragment.ARG_STORY_ID, idLongEvent);
        } else {
            argsHomeFrg.putString(GeneralHomeFragment.ARG_EVENT_ID, GeneralHomeFragment.DEFAULT_ID_ARG);
            argsHomeFrg.putString(GeneralHomeFragment.ARG_STORY_ID, GeneralHomeFragment.DEFAULT_ID_ARG);
        }
        homeFragment.setArguments(argsHomeFrg);

        //set up category
        Bundle args = new Bundle();
        args.putString(GeneralLatestNewsFragment.PARAM_LIST_CATEGORY_GENERAL_LATEST_NEWS_FRG, jsonCategories);
        args.putString(GeneralLatestNewsFragment.PARAM_U_ID_GENERAL_LATEST_NEWS_FRG, uId);
        latestNewsFragment.setArguments(args);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.container, moreFragment, NAME_MORE_FRAGMENT).hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.container, favoriteFragment, NAME_FAVORITE_FRAGMENT).hide(favoriteFragment).commit();
        fm.beginTransaction().add(R.id.container, latestNewsFragment, NAME_LATEST_NEWS_FRAGMENT).hide(latestNewsFragment).commit();
        fm.beginTransaction().add(R.id.container, homeFragment, NAME_HOME_FRAGMENT).commit();

//        if (GeneralTool.isNetworkAvailable(this)) {
//            Log.e("nn-", "available");
//            sayHi();
//        }

        //setOnClick all view
        constraintLayoutViewNotice.setOnClickListener(this);
        imageCloseViewNotice.setOnClickListener(this);
    }

    public void sayHi() {
//        textConnectionState.setVisibility(View.VISIBLE);
//        textConnectionState.setText("Xin chào Virgo Trung Anh");
//        textConnectionState.setTextColor(Color.WHITE);
//        textConnectionState.setBackgroundColor(Color.GREEN);
//        Handler handler = new Handler();
//        Runnable delayrunnable = () -> textConnectionState.setVisibility(View.GONE);
//        handler.postDelayed(delayrunnable, 3000);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn back thêm 1 lần nữa để thoát", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.constraint_view_notice:
                //Bật lên chi tiết màn hình đó
                if (activeFragment == homeFragment) {
                    DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(ConstGeneralTypeTab.TYPE_TAB_HOME, idEventFromBroadcast, idStoryFromBroadcast, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                    detailEventFragment.setAddFragmentCallback(activeAddFragmentCallback);
                    constraintLayoutViewNotice.setVisibility(View.GONE);
                    activeAddFragmentCallback.addFrgCallback(detailEventFragment);
                } else if (activeFragment == latestNewsFragment) {
                    DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(ConstGeneralTypeTab.TYPE_TAB_LATEST_HOME, idEventFromBroadcast, idStoryFromBroadcast, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                    detailEventFragment.setAddFragmentCallback(activeAddFragmentCallback);
                    constraintLayoutViewNotice.setVisibility(View.GONE);
                    activeAddFragmentCallback.addFrgCallback(detailEventFragment);
                }
                break;
            case R.id.image_close_view_notice:
                constraintLayoutViewNotice.setVisibility(View.GONE);
                break;
            case R.id.circle_image_cover_control_voice:
            case R.id.text_name_article_control_voice:
                //Mở fragment chi tiết list bài báo lên
                break;
            default:
                break;
        }

    }

    @Override
    public void actionWhenNetworkChange(String newState) {
        if (newState.equals(NetworkChangeReceiver.CONNECTED)) {
            textConnectionState.setText("Quay lại trực tuyến");
            textConnectionState.setTextColor(Color.WHITE);
            textConnectionState.setBackgroundColor(Color.GREEN);
            Handler handler = new Handler();
            Runnable delayrunnable = () -> textConnectionState.setVisibility(View.GONE);
            handler.postDelayed(delayrunnable, 3000);
        } else if (newState.equals(NetworkChangeReceiver.DISCONNECTED)) {
            textConnectionState.setVisibility(View.VISIBLE);
            textConnectionState.setText("Không có kết nối");
            textConnectionState.setTextColor(Color.WHITE);
            textConnectionState.setBackgroundColor(Color.DKGRAY);
        }
    }

    @Override
    public void updateArticle(Article article) {

    }

    @Override
    public void playVoiceAtPosition(ArrayList<Article> articles, int position) {
        Toast.makeText(this, "Play voice at main!5", Toast.LENGTH_SHORT).show();
    }
}
