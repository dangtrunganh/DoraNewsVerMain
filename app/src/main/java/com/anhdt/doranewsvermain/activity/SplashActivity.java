package com.anhdt.doranewsvermain.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.MainActivity;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstLocalCaching;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.LoadPageConst;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.News;
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
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.anhdt.doranewsvermain.constant.LoadPageConst.RELOAD_INIT_CURRENT_PAGE;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    private static final String TAG = SplashActivity.class.getName();
    private static final int DEACTIVE = 0;
    private static final int ACTIVE = 1;

    private ImageView imageAppIcon, imageLoading1, imageLoading2, imageLoading3, imageLoading4;
    private TextView imageAppName;
    private ImageView imageAppFooter;
    //    private TextView textAppName;
    private TextView textLoading;
    private TableRow tableRowLoading;
//    private SpinKitView spinKitView;

    private Animation animationLogo;
//    private Handler mHandler;
//    private ProgressBar progressBar;

    private String uId;

    private long startTime, endTime;

    private int /*currentPosition,*/ previousPosition;

    private int currentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_3);
        initViews();

//        Log.e("pp-ANDROID", GeneralTool.getVersionAndroid());
//        Log.e("pp-MANUFACTURER", Build.MANUFACTURER);
//        Log.e("pp-PRODUCT", Build.PRODUCT);
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
        startTime = System.currentTimeMillis();
//        currentPosition = 0;
        previousPosition = -1;
        currentCount = 0;
//        mHandler = new Handler(Looper.getMainLooper());
        imageAppIcon = findViewById(R.id.image_logo_app_splash);
        imageAppName = findViewById(R.id.image_name_app_splash);
        imageLoading1 = findViewById(R.id.image_loading_1);
        imageLoading2 = findViewById(R.id.image_loading_2);
        imageLoading3 = findViewById(R.id.image_loading_3);
        imageLoading4 = findViewById(R.id.image_loading_4);
//        imageAppFooter = findViewById(R.id.image_footer_app_splash);
//        progressBar = findViewById(R.id.progress_bar_splash_screen);
//        progressBar.setVisibility(View.GONE);
//        textAppName = findViewById(R.id.text_splash_screen);
        textLoading = findViewById(R.id.text_loading_splash);
        tableRowLoading = findViewById(R.id.table_row_loading_splash);
        tableRowLoading.setVisibility(View.GONE);
//        spinKitView = findViewById(R.id.spin_kit);
//        Style style = Style.values()[9];
//        Sprite drawable = SpriteFactory.create(style);
//        spinKitView.setIndeterminateDrawable(drawable);

//        animationLogo = AnimationUtils.loadAnimation(this, R.anim.push_down);
//        imageAppIcon.setAnimation(animationLogo);
//
//        animationLogo = AnimationUtils.loadAnimation(this, R.anim.push_right);
//        imageAppName.setAnimation(animationLogo);

        animationLogo = AnimationUtils.loadAnimation(this, R.anim.fade_app_name);
        imageAppIcon.setAnimation(animationLogo);

        animationLogo = AnimationUtils.loadAnimation(this, R.anim.fade_app_name);
        imageAppName.setAnimation(animationLogo);


//        animationLogo.setFillAfter(true);

//        animationLogo = AnimationUtils.loadAnimation(this, R.anim.push_right);
//        imageAppFooter.setAnimation(animationLogo);

        animationLogo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                tableRowLoading.setVisibility(View.VISIBLE);
                textLoading.setVisibility(View.VISIBLE);
//                imageLoading1.setImageLevel(ACTIVE);

                //Đọc data ra
                setUpUserId(); //- Chỉ thực hiện khi đọc list category chưa có, vì nếu không, mỗi lần splash chạy là lại phải đọc uId lên?
                //Hay là đọc uId ở đây để MainAct đỡ phải gọi?

                setUpDataCategory();
                //Sau đó navigate sang màn hình tiếp theo, đã navigate trong setUpDataCategory() rồi


//                textLoading.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
//                new PrefetchData().execute();
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            sleep(1500);
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.setIndeterminate(true);
////                    //Đọc data ra
////                    setUpUserId(); //- Chỉ thực hiện khi đọc list category chưa có, vì nếu không, mỗi lần splash chạy là lại phải đọc uId lên?
////                    //Hay là đọc uId ở đây để MainAct đỡ phải gọi?
////
////                    setUpDataCategory();
////                    //Sau đó navigate sang màn hình tiếp theo, đã navigate trong setUpDataCategory() rồi
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        super.run();
//                    }
//                };
//                thread.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


//        new PrefetchData().execute();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setUpUserId(); //- Chỉ thực hiện khi đọc list category chưa có, vì nếu không, mỗi lần splash chạy là lại phải đọc uId lên?
//                //Hay là đọc uId ở đây để MainAct đỡ phải gọi?
//                setUpDataCategory();
//            }
//        }, 500);


//        animationLogo.setAnimationListener(this);
    }

    //========

    /**
     * Async Task to make http call
     */
    @SuppressLint("StaticFieldLeak")
    class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
//            progressBar.setIndeterminate(true);
//            super.onPreExecute();
//            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */
//            //Đọc data ra
//            setUpUserId(); //- Chỉ thực hiện khi đọc list category chưa có, vì nếu không, mỗi lần splash chạy là lại phải đọc uId lên?
//            //Hay là đọc uId ở đây để MainAct đỡ phải gọi?
//
//            setUpDataCategory();
//            //Sau đó navigate sang màn hình tiếp theo, đã navigate trong setUpDataCategory() rồi

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            new Handler().postDelayed(new Runnable() {
//
//                /*
//                 * Showing splash screen with a timer. This will be useful when you
//                 * want to show case your app logo / company
//                 */
//
//                @Override
//                public void run() {
//                    // After completing http call
//                    // will close this activity and lauch main activity
//                    Intent i = new Intent(SplashScreen.this, SplashScreenActivity.class);
//                    i.putExtra("name", name);
//                    i.putExtra("state", state);
//                    startActivity(i);
//                    // close this activity
//                    finish();
//                }
//            }, SPLASH_TIME_OUT);
        }
    }
    //========

    private void setUpUserId() {
        //Đọc userId trong Local, nếu không có tức là chưa đăng nhập lần nào --> Đăng ký mới
        uId = ReadCacheTool.getUId(SplashActivity.this);
        //uId có thể là rỗng ("")
//        if (uId.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_UID_DEFAULT)) {
        //Tức là đang rỗng, chưa có uId trong SharedPreference
        getTokenFirebaseAndSendToServer(); //Lấy token từ firebase, trong đó gọi hàm sendInfo lên Server
//        }
    }

    private void getTokenFirebaseAndSendToServer() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this, instanceIdResult -> {
            String mToken = instanceIdResult.getToken();
            sendInfoToServer(mToken);
        });
    }

    private void deActive(int position) {
        switch (position) {
            case 0:
                imageLoading1.setImageLevel(DEACTIVE);
            case 1:
                imageLoading2.setImageLevel(DEACTIVE);
            case 2:
                imageLoading3.setImageLevel(DEACTIVE);
            case 3:
                imageLoading4.setImageLevel(DEACTIVE);
            default:
                break;
        }
    }

    private void activie(int position) {
        switch (position) {
            case 0:
                imageLoading1.setImageLevel(ACTIVE);
            case 1:
                imageLoading2.setImageLevel(ACTIVE);
            case 2:
                imageLoading3.setImageLevel(ACTIVE);
            case 3:
                imageLoading4.setImageLevel(ACTIVE);
            default:
                break;
        }
    }

    private void loadingActive(int position) {
        switch (position) {
            case 0:
                imageLoading1.setImageLevel(ACTIVE);
                imageLoading2.setImageLevel(DEACTIVE);
                imageLoading3.setImageLevel(DEACTIVE);
                imageLoading4.setImageLevel(DEACTIVE);
            case 1:
                imageLoading1.setImageLevel(DEACTIVE);
                imageLoading2.setImageLevel(ACTIVE);
                imageLoading3.setImageLevel(DEACTIVE);
                imageLoading4.setImageLevel(DEACTIVE);
            case 2:
                imageLoading1.setImageLevel(DEACTIVE);
                imageLoading2.setImageLevel(DEACTIVE);
                imageLoading3.setImageLevel(ACTIVE);
                imageLoading4.setImageLevel(DEACTIVE);
            case 3:
                imageLoading1.setImageLevel(DEACTIVE);
                imageLoading2.setImageLevel(DEACTIVE);
                imageLoading3.setImageLevel(DEACTIVE);
                imageLoading4.setImageLevel(ACTIVE);
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
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
                    UserResult userResult = response.body();
                    if (userResult == null) {
                        Toast.makeText(SplashActivity.this, "Error when login to server - null!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    uId = userResult.getUId();
                    if (uId == null) {
                        Toast.makeText(SplashActivity.this, "Error when login to server!", Toast.LENGTH_SHORT).show();
                    } else {
                        // store to local cache
//                        Log.e("mnx-UId:", uId);
//                        Log.e("mnx-mToken:", mToken);
//                        Log.e("mnx-deviceId:", deviceId);
                        ReadCacheTool.storeUIdMTokenDeviceId(SplashActivity.this, userResult.getUId(), mToken, deviceId);
                    }
                }

                @Override
                public void onFailure(Call<UserResult> call, Throwable t) {
                    Log.e("login-", "fail");
//                    Toast.makeText(SplashActivity.this, "Fail to login!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

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
            intentPick.putExtra(PickCategoryActivity.ARGS_TYPE_PICK_CATEGORY, PickCategoryActivity.TYPE_NO_CANCEL);
            startActivity(intentPick);
            finish();
        } else if (categoryArrayList.size() == 0) {
            //Nothing in pref tức là lần đầu mở app
//            Toast.makeText(this, "Nothing in pref - category - 0", Toast.LENGTH_SHORT).show();
            Intent intentPick = new Intent(SplashActivity.this, PickCategoryActivity.class);
            intentPick.putExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_PICK_CATEGORY, uId);
            intentPick.putExtra(PickCategoryActivity.ARGS_TYPE_PICK_CATEGORY, PickCategoryActivity.TYPE_NO_CANCEL);
            startActivity(intentPick);
            finish();
        } else {
            //2 -- Đã có category trong local, thực hiện mở MainActivity
            //Tuy nhiên ở đây có thể có intent bắn từ notification vào
            //MainActivity nhận được nếu có 2 tham số event_id và long_event_id thì navigate sang màn DetailEventAct
            //Còn nếu không thì chỉ nhận được list Category, từ SplashActivity hoặc PickCategoryActivity
            if (uId == null) {
                uId = ReadCacheTool.getUId(SplashActivity.this);
            }
            Intent intentResult = new Intent(SplashActivity.this, MainActivity.class);
            //===getIntentExtras====
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String eventId = bundle.getString(ConstParamTransfer.TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN);
                String longEventId = bundle.getString(ConstParamTransfer.TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN);

//                Log.e("iii-event_id", eventId);
//                Log.e("iii-story_id", longEventId);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_EVENT_ID_FR_SPLASH_TO_MAIN, eventId);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_LONG_EVENT_ID_FR_SPLASH_TO_MAIN, longEventId);
            }
            Gson gson = new Gson();
            String json = gson.toJson(categoryArrayList);
            intentResult.putExtra(ConstParamTransfer.TRANSFER_LIST_CATEGORY_FR_SPLASH_TO_MAIN, json);
            intentResult.putExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_MAIN, uId);
            //=======Load Data to Local=========
            loadDataLatestNewsScreen(categoryArrayList, intentResult);
            //=======Load Data to Local=========
//            startActivity(intentResult);
//            finish();
        }
    }

    private void loadDataLatestNewsScreen(ArrayList<Category> categoryArrayList, Intent intentResult) {
        //Không có mạng hoặc là onFailure thì ko lưu vào trong local --> get lên sẽ là null
        //Không có mạng thì sẽ ko có intent bắn về
        if (GeneralTool.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ServerAPI apiService = retrofit.create(ServerAPI.class);
            String uId = ReadCacheTool.getUId(this);
            String deviceId = ReadCacheTool.getDeviceId(this);
            for (int i = 0; i < categoryArrayList.size(); i++) {
                //Load từng cái và lưu xuống db
                Category category = categoryArrayList.get(i);
                int currentIndex = i;
                Call<News> call = apiService.getNewsInEachCategory(String.valueOf(RELOAD_INIT_CURRENT_PAGE),
                        deviceId,
                        category.getId(),
                        uId
                );
                call.enqueue(new Callback<News>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                        currentCount += 1;
                        double ratio = currentCount * 1.0 / categoryArrayList.size() * 100;
                        int ratioUse = (int) Math.round(ratio);
                        textLoading.setText("Đang tải..." + ratioUse + "%");
                        News news = response.body();
                        if (news == null) {
                            if (currentIndex == categoryArrayList.size() - 1) {
                                startActivity(intentResult);
                                finish();
                            }
                            return;
                        }
//                        int posi = currentCount % 4;
//                        loadingItemCount(categoryArrayList.size());
//                        Log.e("uu-index", currentIndex + "");
//                        currentPosition = currentIndex % 4;
//
////                        Log.e("uu-currentPosition", currentPosition + "");
////                        Log.e("uu-previousPosition", previousPosition + "");
////                        Log.e("uu-===", "==============");
//                        currentCount += 1;
//                        double ratio = currentCount * 1.0 / categoryArrayList.size() * 100;
//                        double ratioUse = Math.round(ratio * 100) / 100;
//                        textLoading.setText("Đang tải..." + ratioUse + "%");
//                        mHandler.post(() -> {
//                            textLoading.setText("Đang tải..." + ratioUse + "%");
//                            loadingActive(posi);
//                        });

//                                loadingActive(posi);

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                textLoading.setText("Đang tải..." + ratioUse + "%");
////                                loadingActive(posi);
//                            }
//                        });
//                        activie(posi);
//                        deActive(previousPosition);
//                        previousPosition = currentCount % 4;
                        ReadCacheTool.storeNewsByCategory(SplashActivity.this, category.getId(), (ArrayList<Datum>) news.getData());

                        if (currentIndex == categoryArrayList.size() - 1) {
                            long stopTime = System.currentTimeMillis();
                            long elapsedTime = (stopTime - startTime) / 1000;
                            Log.e("time-process", elapsedTime + " seconds");

                            startActivity(intentResult);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {
                        textLoading.setText("Đang tải...100%");
                        if (currentIndex == categoryArrayList.size() - 1) {
                            startActivity(intentResult);
                            finish();
                        }
                    }
                });
            }
        } else {
            //bật màn MainActivity ngay lập tức khi ko có mạng, trong Local sẽ ko có gì cả
            //Giả hiệu ứng chạy
//            for (int i = 0; i < categoryArrayList.size(); i++) {
//                currentPosition = i % 4;
//                activie(currentPosition);
//                deActive(previousPosition);
//                previousPosition = currentPosition;
//            }
            startActivity(intentResult);
            finish();
        }
    }

    private void loadingItemCount(int sizeCategories) {
        double ratio = currentCount * 1.0 / sizeCategories;
        Log.e("uu-ratio", ratio + "");
        if (ratio >= 0.25 && ratio < 0.5) {
            imageLoading2.setImageLevel(ACTIVE);
            imageLoading1.setImageLevel(DEACTIVE);
//            activie(1);
//            deActive(0);
            Log.e("uu-active", "1");
        } else if (ratio >= 0.5 && ratio < 0.75) {
            imageLoading3.setImageLevel(ACTIVE);
            imageLoading2.setImageLevel(DEACTIVE);
//            activie(2);
//            deActive(1);
            Log.e("uu-active", "2");
        } else if (ratio >= 0.75 && ratio <= 1) {
            imageLoading4.setImageLevel(ACTIVE);
            imageLoading3.setImageLevel(DEACTIVE);
//            activie(3);
//            deActive(2);
            Log.e("uu-active", "3");
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
