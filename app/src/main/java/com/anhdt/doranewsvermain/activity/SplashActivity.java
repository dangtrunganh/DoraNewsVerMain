package com.anhdt.doranewsvermain.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.MainActivity;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstLocalCaching;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.userresult.UserResult;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    private static final String TAG = SplashActivity.class.getName();

    private ImageView imageAppIcon;
    private ImageView imageAppName;
    private ImageView imageAppFooter;
//    private TextView textAppName;

//    private SpinKitView spinKitView;

    private Animation animationLogo;

    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_2);
        initViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        StatusBarNotification[] sbns = nMgr.getActiveNotifications();
//        for (StatusBarNotification sbn : sbns) {
//            if (sbn == null) {
//                Log.i(TAG, "sbn is null");
//                continue;
//            }
//            Notification n = sbn.getNotification();
//            Bundle bundle = n.extras;
//            String eventId = bundle.getString("event_id");
//            String storyId = bundle.getString("long_event_id");
//            String urlImage = bundle.getString("urlImage");
//            if (eventId != null && storyId != null && urlImage != null) {
//                Log.e("iii1-eventId", eventId);
//                Log.e("iii1-storyId", storyId);
//                Log.e("iii1-urlImage", urlImage);
//                Log.e("===========", "===========");
//            } else {
//                Log.e("iii1-", "null");
//            }
//        }
////        String y = Arrays.toString(sbns);
////        Log.e("iii-", y);
////        Toast.makeText(this, "iii--" + Arrays.toString(x), Toast.LENGTH_SHORT).show();
        nMgr.cancelAll();
    }

    private void initViews() {
        imageAppIcon = findViewById(R.id.image_logo_app_splash);
        imageAppName = findViewById(R.id.image_name_app_splash);
        imageAppFooter = findViewById(R.id.image_footer_app_splash);
//        textAppName = findViewById(R.id.text_splash_screen);

//        spinKitView = findViewById(R.id.spin_kit);
//        Style style = Style.values()[9];
//        Sprite drawable = SpriteFactory.create(style);
//        spinKitView.setIndeterminateDrawable(drawable);

        animationLogo = AnimationUtils.loadAnimation(this, R.anim.push_down);
        imageAppIcon.setAnimation(animationLogo);

        animationLogo = AnimationUtils.loadAnimation(this, R.anim.push_right);
        imageAppName.setAnimation(animationLogo);
//        animationLogo.setFillAfter(true);

        animationLogo = AnimationUtils.loadAnimation(this, R.anim.push_right);
        imageAppFooter.setAnimation(animationLogo);


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    //Đọc data ra
                    setUpUserId(); //- Chỉ thực hiện khi đọc list category chưa có, vì nếu không, mỗi lần splash chạy là lại phải đọc uId lên?
                    //Hay là đọc uId ở đây để MainAct đỡ phải gọi?

                    setUpDataCategory();
                    //Sau đó navigate sang màn hình tiếp theo, đã navigate trong setUpDataCategory() rồi
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        thread.start();
//        animationLogo.setAnimationListener(this);
    }

    private String getDeviceId() {
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
//        Log.e("k-android_id", android_id);
        return android_id;
    }

    private void setUpUserId() {
        //Đọc userId trong Local, nếu không có tức là chưa đăng nhập lần nào --> Đăng ký mới
        uId = ReadCacheTool.getUId(SplashActivity.this);
//        String jsonCategory = ReadCacheTool.getListCategoryInCache(SplashActivity.this);
        if (uId.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_UID_DEFAULT) /*&&
                jsonCategory.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_LIST_CATEGORY_DEFAULT)*/) {
            //Tức là đang rỗng, chưa có uId trong SharedPreference
            getTokenFirebaseAndSendToServer(); //Lấy token từ firebase, trong đó gọi hàm sendInfo lên Server
        }
    }

    private void getTokenFirebaseAndSendToServer() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this, instanceIdResult -> {
            String mToken = instanceIdResult.getToken();
//            Log.e("k-newToken", mToken);
            sendInfoToServer(mToken);
        });
    }

    private void sendInfoToServer(String mToken) {
        //Gửi thông tin User lên server để đăng ký lần đầu

        //1. Lấy device id
//        String deviceId = getDeviceId();
        String deviceId = GeneralTool.getDeviceId(this);
        if (deviceId != null && mToken != null) {
            //Gửi thông tin lên server
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.URL_GET_ROOT_LOG_IN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            ServerAPI apiService = retrofit.create(ServerAPI.class);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("deviceid", deviceId)
                    .addFormDataPart("token", mToken)
                    .build();

            Call<UserResult> call = apiService.login(requestBody);
            call.enqueue(new Callback<UserResult>() {
                @Override
                public void onResponse(Call<UserResult> call, Response<UserResult> response) {
//                    ResponseBody responseBody = response.body();
                    UserResult userResult = response.body();
                    uId = userResult.getUId();
                    if (uId == null) {
//                        Log.e("su-login", "fail: uId null");
                    } else {
                        Log.e("su-login", "mToken: " + mToken);
                        Log.e("su-login", "deviceId: " + deviceId);
                        Log.e("su-login", "uuid: " + userResult.getUId());
                        // store to local cache
                        ReadCacheTool.storeUIdMTokenDeviceId(SplashActivity.this, userResult.getUId(), mToken, deviceId);
                    }
                }

                @Override
                public void onFailure(Call<UserResult> call, Throwable t) {
                    Toast.makeText(SplashActivity.this, "Fail to login!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
//        textAppName.setVisibility(View.VISIBLE);
//        spinKitView.setVisibility(View.VISIBLE);

//        Animation animationAppName = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_app_name);
//        animationAppName.setFillAfter(true);
//        textAppName.setAnimation(animationAppName);
//        animationAppName.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                //Đọc data ra
//                setUpUserId(); //- Chỉ thực hiện khi đọc list category chưa có, vì nếu không, mỗi lần splash chạy là lại phải đọc uId lên?
//                //Hay là đọc uId ở đây để MainAct đỡ phải gọi?
//
//                setUpDataCategory();
//                //Sau đó navigate sang màn hình tiếp theo, đã navigate trong setUpDataCategory() rồi
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void setUpDataCategory() {
        //Đọc List category trong Local xem đã có chưa? Nếu có rồi thì navigate sang MainActivity, nén data list category đọc được này cho MainActivity
        ArrayList<Category> categoryArrayList = getListCategoriesChosen();
        if (categoryArrayList == null) {
            //Nothing in pref tức là lần đầu mở app
//            Toast.makeText(this, "Nothing in pref - category - null", Toast.LENGTH_SHORT).show();
            Intent intentPick = new Intent(SplashActivity.this, PickCategoryActivity.class);
            intentPick.putExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_PICK_CATEGORY, uId);
            startActivity(intentPick);
            finish();
        } else if (categoryArrayList.size() == 0) {
            //Nothing in pref tức là lần đầu mở app
//            Toast.makeText(this, "Nothing in pref - category - 0", Toast.LENGTH_SHORT).show();
            Intent intentPick = new Intent(SplashActivity.this, PickCategoryActivity.class);
            intentPick.putExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_PICK_CATEGORY, uId);
            startActivity(intentPick);
            finish();
        } else {
            //2 -- Đã có category trong local, thực hiện mở MainActivity
            //Tuy nhiên ở đây có thể có intent bắn từ notification vào
            //MainActivity nhận được nếu có 2 tham số event_id và long_event_id thì navigate sang màn DetailEventAct
            //Còn nếu không thì chỉ nhận được list Category, từ SplashActivity hoặc PickCategoryActivity
            Intent intentResult = new Intent(SplashActivity.this, MainActivity.class);
            //===getIntentExtras====
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String eventId = bundle.getString(ConstParamTransfer.KEY_EVENT_ID_NOTIFICATION);
                String longEventId = bundle.getString(ConstParamTransfer.KEY_LONG_EVENT_ID_NOTIFICATION);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN, eventId);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN, longEventId);
            }
            Gson gson = new Gson();
            String json = gson.toJson(categoryArrayList);
            intentResult.putExtra(ConstParamTransfer.TRANSFER_LIST_CATEGORY_FR_SPLASH_TO_MAIN, json);

            //Mặc định setUser chạy trước, do đó, ta sẽ truyền hết uID đã có sang các màn hình khác nhau
            if (!uId.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_UID_DEFAULT)) {
                intentResult.putExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_MAIN, uId);
            }
            startActivity(intentResult);
            finish();
        }
    }

    private ArrayList<Category> getListCategoriesChosen() {
        String json = ReadCacheTool.getListCategoryInCache(SplashActivity.this);
        if (json.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_LIST_CATEGORY_DEFAULT)) {
//            Toast.makeText(this, "Ep-Nothing in pref", Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<Category>>() {
        }.getType());
    }
}
