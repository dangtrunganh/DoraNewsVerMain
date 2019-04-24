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
import com.anhdt.doranewsvermain.fragment.DetailEventFragment;
import com.anhdt.doranewsvermain.fragment.DetailNewsFragment;
import com.anhdt.doranewsvermain.fragment.basefragment.BaseFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralFavoriteFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralMoreFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.AddFragmentCallback;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralHomeFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralLatestNewsFragment;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.service.voice.VoicePlayerService;
import com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity.ControlVoice;
import com.anhdt.doranewsvermain.util.VoiceTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

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
    private ArrayList<Article> listCurrentOnTopArticles = new ArrayList<>();
    private ArrayList<Article> listCurrentOnServiceArticles = new ArrayList<>();
    //=====================

    private final GeneralHomeFragment homeFragment = GeneralHomeFragment.newInstance();
    private final GeneralLatestNewsFragment latestNewsFragment = GeneralLatestNewsFragment.newInstance();
    private final GeneralFavoriteFragment generalFavoriteFragment = GeneralFavoriteFragment.newInstance();
    private final GeneralMoreFragment generalMoreFragment = GeneralMoreFragment.newInstance();

    private final FragmentManager fm = getSupportFragmentManager();

    private BaseFragment activeFragment = homeFragment;

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


//    ====Interface for

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
        Runnable delayrunnable = () -> constraintLayoutViewNotice.setVisibility(GONE);
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
                fm.beginTransaction().hide(activeFragment).show(generalFavoriteFragment).commit();
                activeFragment = generalFavoriteFragment;
                activeAddFragmentCallback = generalFavoriteFragment;
                return true;
            case R.id.navigation_notification:
                fm.beginTransaction().hide(activeFragment).show(generalMoreFragment).commit();
                activeFragment = generalMoreFragment;
                activeAddFragmentCallback = generalMoreFragment;
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
        if (!mIsConnect) {
            unbindService(mConnection);
            listCurrentOnServiceArticles = new ArrayList<>();
        }
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
        constraintLayoutControlVoice = findViewById(R.id.constraint_control_voice);
        constraintLayoutControlVoice.setVisibility(GONE);
        constraintLayoutControlVoice.setOnClickListener(this);

        imageCoverControlVoice = findViewById(R.id.circle_image_cover_control_voice);
        imagePlayVoice = findViewById(R.id.image_play_pause_control_voice);
        imagePlayVoice.setOnClickListener(this);
        imageNextVoice = findViewById(R.id.image_next_control_voice);
        imageNextVoice.setOnClickListener(this);
        imageExitVoice = findViewById(R.id.image_stop_control_music);
        imageExitVoice.setOnClickListener(this);
        //============


        constraintLayoutViewNotice.setVisibility(GONE);
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

        //set up args cho HomeFragment====All General Fragment======
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

        homeFragment.setControlVoice(this);
        latestNewsFragment.setControlVoice(this);
        generalFavoriteFragment.setControlVoice(this);
        generalMoreFragment.setControlVoice(this);

        fm.beginTransaction().add(R.id.container, generalMoreFragment, NAME_MORE_FRAGMENT).hide(generalMoreFragment).commit();
        fm.beginTransaction().add(R.id.container, generalFavoriteFragment, NAME_FAVORITE_FRAGMENT).hide(generalFavoriteFragment).commit();
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
                    constraintLayoutViewNotice.setVisibility(GONE);
                    activeAddFragmentCallback.addFrgCallback(detailEventFragment);
                } else if (activeFragment == latestNewsFragment) {
                    DetailEventFragment detailEventFragment = DetailEventFragment.newInstance(ConstGeneralTypeTab.TYPE_TAB_LATEST_HOME, idEventFromBroadcast, idStoryFromBroadcast, DetailEventFragment.DEFAULT_LIST_OF_STORY);
                    detailEventFragment.setAddFragmentCallback(activeAddFragmentCallback);
                    constraintLayoutViewNotice.setVisibility(GONE);
                    activeAddFragmentCallback.addFrgCallback(detailEventFragment);
                }
                break;
            case R.id.image_close_view_notice:
                constraintLayoutViewNotice.setVisibility(GONE);
                break;
//            case R.id.circle_image_cover_control_voice:
            case R.id.constraint_control_voice:
                //Mở fragment chi tiết list bài báo lên
                //Sự kiện khi kích vào một bài báo thường
                showDetailListArticles();
                break;
            case R.id.image_stop_control_music:
                //Exit nhạc
                constraintLayoutControlVoice.setVisibility(GONE);
                if (mPlayerService != null) {
                    mPlayerService.stopArticle();
//                    mPlayerService.setArticleList(new ArrayList<>());
                }
                break;
            case R.id.image_next_control_voice:
                if (mPlayerService != null) {
                    mPlayerService.nextArticle();
                    Article article = mPlayerService.getCurrentArticle();
                    textTitleArticle.setText(article.getTitle());
                    Glide.with(this).load(article.getImage()).
                            apply(new RequestOptions().override(400, 0).
                                    placeholder(R.drawable.image_default).error(R.drawable.image_default))
                            .into(imageCoverControlVoice);
                    imagePlayVoice.setImageResource(R.drawable.ic_pause_black);
//                    textEndTime.setText(mPlayerService.getTotalTime());
                }
                break;
            case R.id.image_play_pause_control_voice:
                if (mPlayerService == null) {
                    return;
                }
                if (mPlayerService.isOnlyPlaying()) {
                    //pause
                    mPlayerService.pauseArticle();
                    imagePlayVoice.setImageResource(R.drawable.ic_play_black);
                } else {
                    //play
                    mPlayerService.playArticle();
                    imagePlayVoice.setImageResource(R.drawable.ic_pause_black);
                }
                break;
//            case R.id.image_previous_control_voice:
//                if (mPlayerService == null) {
//                    return;
//                }
//                mPlayerService.previousArticle();
//                Article article = mPlayerService.getCurrentArticle();
//                textTitleArticle.setText(article.getTitle());
//                Glide.with(this).load(article.getImage()).
//                        apply(new RequestOptions().override(400, 0).
//                                placeholder(R.drawable.image_default).error(R.drawable.image_default))
//                        .into(imageCoverControlVoice);
//                imagePlayVoice.setImageResource(R.drawable.ic_pause_black);
//                break;
            default:
                break;
        }

    }

    private void showDetailListArticles() {
        if (!mIsConnect) {
            return;
        }
        int position = mPlayerService.getIndexCurrentArticle();
        //Chuyển sang màn hình Chi tiết các bài báo
        if (VoiceTool.checkIfTwoListEqual(listCurrentOnTopArticles, listCurrentOnServiceArticles)) {
            return;
        }
        Gson gson = new Gson();
        String jsonListArticles = gson.toJson(mPlayerService.getCurrentArticlesList());
        DetailNewsFragment detailNewsFragment = DetailNewsFragment.newInstance(jsonListArticles, position);
        detailNewsFragment.setAddFragmentCallback(activeAddFragmentCallback);

        //===
        if (activeFragment == homeFragment) {
            detailNewsFragment.setFragmentManager(GeneralHomeFragment.fragmentManagerHome);
        } else if (activeFragment == latestNewsFragment) {
            detailNewsFragment.setFragmentManager(GeneralLatestNewsFragment.fragmentManagerLatest);
        }
        //===
        activeAddFragmentCallback.addFrgCallback(detailNewsFragment);
    }

    @Override
    public void actionWhenNetworkChange(String newState) {
        if (newState.equals(NetworkChangeReceiver.CONNECTED)) {
            textConnectionState.setText("Quay lại trực tuyến");
            textConnectionState.setTextColor(Color.WHITE);
            textConnectionState.setBackgroundColor(Color.GREEN);
            Handler handler = new Handler();
            Runnable delayrunnable = () -> textConnectionState.setVisibility(GONE);
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
        if (mPlayerService != null) {
            textTitleArticle.setText(article.getTitle());
            Glide.with(this).load(article.getImage()).
                    apply(new RequestOptions().override(400, 0).
                            placeholder(R.drawable.image_default).error(R.drawable.image_default))
                    .into(imageCoverControlVoice);
//            textEndTime.setText(mPlayerService.getTotalTime());
        }
    }

    @Override
    public void playVoiceAtPosition(ArrayList<Article> articles, int position) {
//        Toast.makeText(this, "Play voice at main!5", Toast.LENGTH_SHORT).show();
        if (!mIsConnect) {
            return;
        }
        if (articles != null) {
            mPlayerService.setArticleList(articles);
            listCurrentOnServiceArticles = articles;
        } else {
            return;
        }
//        mPlayerService.setIndexCurrentArticle(position);
        constraintLayoutControlVoice.setVisibility(View.VISIBLE);
        textTitleArticle.setText(articles.get(position).getTitle());
        Glide.with(this).load(articles.get(position).getImage()).
                apply(new RequestOptions().override(400, 0).
                        placeholder(R.drawable.image_default).error(R.drawable.image_default))
                .into(imageCoverControlVoice);
        mPlayerService.playArticle(position);
        imagePlayVoice.setImageResource(R.drawable.ic_pause_black);
//        textEndTime.setText(mPlayerService.getTotalTime());
    }

    @Override
    public void setCurrentListVoiceOnTopStack(ArrayList<Article> articles) {
        this.listCurrentOnTopArticles = articles;
    }

    @Override
    public void deleteCurrentListVoiceOnTopStack() {
        this.listCurrentOnTopArticles = new ArrayList<>();
    }
}
