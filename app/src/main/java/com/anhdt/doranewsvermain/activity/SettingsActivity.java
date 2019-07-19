package com.anhdt.doranewsvermain.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private LinearLayout lnlPickCategory, lnlPickSourceNews, lnlPickLanguage,
            lnlChooseSize, lnlTheme, lnlShare, lnlRate, lnlContact,
            lnlPolicy, lnlPerson;
    //    private AlertDialog alertDialog;
//    private TextView textInfoUser;
//    private boolean isTested;
    private TextView textInfoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        initViews();
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar_act_settings);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> finish());

//        alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
//        alertDialog.setTitle("Thông báo");
//        alertDialog.setMessage("Tính năng chưa ra mắt");
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                (dialog, which) -> dialog.dismiss());

        lnlPerson = findViewById(R.id.lnl_inf_person_settings);
        lnlPickCategory = findViewById(R.id.lnl_category_news_settings);
        lnlPickSourceNews = findViewById(R.id.lnl_pick_source_settings);
        lnlPickLanguage = findViewById(R.id.lnl_pick_language_settings);
        lnlChooseSize = findViewById(R.id.lnl_size_display_settings);
        lnlTheme = findViewById(R.id.lnl_theme_settings);
        lnlShare = findViewById(R.id.lnl_share_settings);
        lnlRate = findViewById(R.id.lnl_rate_settings);
        lnlContact = findViewById(R.id.lnl_contact_settings);
//        lnlhelp = findViewById(R.id.lnl_help_settings);
        lnlPolicy = findViewById(R.id.lnl_policy_settings);

        textInfoApp = findViewById(R.id.text_info_user);
//        lnlSecurity = findViewById(R.id.lnl_security_settings);
//        lnlIntroduction = findViewById(R.id.lnl_about_settings);
//        lnlUpdate = findViewById(R.id.lnl_update_settings);
//        lnlDeleteCache = findViewById(R.id.lnl_delete_cache_settings);
//        lnlLogOut = findViewById(R.id.lnl_log_out_settings);

//        textInfoUser = findViewById(R.id.text_info_user);

//        String info = "+deviceId: " + ReadCacheTool.getDeviceId(this) + "\n" +
//                "+uId: " + ReadCacheTool.getUId(this) + "\n" +
//                "+mToken: " + ReadCacheTool.getMToken(this) + "\n";
//        textInfoUser.setText(info);

        //OnClickListener
        lnlPickCategory.setOnClickListener(this);
        lnlPickSourceNews.setOnClickListener(this);
        lnlPickLanguage.setOnClickListener(this);
        lnlChooseSize.setOnClickListener(this);
        lnlTheme.setOnClickListener(this);
        lnlShare.setOnClickListener(this);
        lnlRate.setOnClickListener(this);
        lnlContact.setOnClickListener(this);
//        lnlhelp.setOnClickListener(this);
        lnlPolicy.setOnClickListener(this);
//        lnlSecurity.setOnClickListener(this);
//        lnlIntroduction.setOnClickListener(this);
//        lnlUpdate.setOnClickListener(this);
//        lnlDeleteCache.setOnClickListener(this);
//        lnlLogOut.setOnClickListener(this);
        lnlPerson.setOnClickListener(this);

        //===Test=====
//        isTested = ReadCacheTool.getDetailArticleTest(this);
        String infoApp = "Sứ mệnh của Dora là thu hoạch, chế biến và trình bày thông tin cho mọi người một cách toàn diện nhất để họ có thể hành động vì cuộc sống tốt đẹp hơn.";
        SpannableString s = new SpannableString(infoApp);
        s.setSpan(new ForegroundColorSpan(Color.BLUE), 19, 29, 0);
        s.setSpan(new ForegroundColorSpan(Color.RED), 31, 40, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#9C27B0")), 43, 52, 0);
        textInfoApp.setText(s);
    }

    private void rate() {
        String str = "https://play.google.com/store/apps/details?id=com.anhdt.doranewsvermain";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    private void share() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ ứng dụng \"Tin tức Dora\"");
        email.putExtra(Intent.EXTRA_TEXT, "Cùng download về sử dụng nhé mọi người. \n https://play.google.com/store/apps/details?id=com.anhdt.doranewsvermain");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Chọn ứng dụng để chia sẻ:"));
    }

    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, "[Tin tức Dora] Góp ý cho phiên bản " + GeneralTool.getVersionApp() + " (trên " + GeneralTool.getNameOfDevice() + ", Android: " + GeneralTool.getVersionAndroid() + ")");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"doraserviceacc@gmail.com"});
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Chọn ứng dụng để chia sẻ:"));
    }

    @Override
    public void onClick(View v) {
//        boolean isNetworkAvailable = false;
//        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(this))) {
//
//        }
        switch (v.getId()) {
            case R.id.lnl_category_news_settings:
                //Màn pick category
                if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(this))) {
                    Intent intent = new Intent(this, PickCategoryActivity.class);
                    intent.putExtra(PickCategoryActivity.ARGS_TYPE_PICK_CATEGORY, PickCategoryActivity.TYPE_HAVE_CANCEL);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage("Không có kết nối mạng");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
                break;
            case R.id.lnl_pick_source_settings:
                if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(this))) {
                    Intent intentSource = new Intent(this, PickNewsSourceActivity.class);
                    intentSource.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentSource);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage("Không có kết nối mạng");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
                break;
            case R.id.lnl_pick_language_settings:
            case R.id.lnl_size_display_settings:
            case R.id.lnl_theme_settings:
                Toast.makeText(this, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
//                alertDialog.show();
                break;
            case R.id.lnl_policy_settings:
                startActivity(new Intent(SettingsActivity.this, PrivacyActivity.class));
                break;
            case R.id.lnl_share_settings:
                share();
                break;
//                if (isTested) {
//                    Toast.makeText(this, "Trở vể chế độ xem DetailArticle mặc định!", Toast.LENGTH_SHORT).show();
//                    isTested = false;
//                } else {
//                    Toast.makeText(this, "Vào chế độ xem DetailArticle thử nghiệm!", Toast.LENGTH_SHORT).show();
//                    isTested = true;
//                }
//                ReadCacheTool.storeDetailArticleTest(this, isTested);
//                break;
            case R.id.lnl_rate_settings:
                rate();
                break;
            case R.id.lnl_contact_settings:
                sendEmail();
                break;
//            case R.id.lnl_help_settings:
//            case R.id.lnl_security_settings:
//            case R.id.lnl_about_settings:
//            case R.id.lnl_update_settings:
            case R.id.lnl_inf_person_settings:
//                ReadCacheTool.clearPreferenceWelcome(this);
//                Toast.makeText(this, "Đã xóa dữ liệu màn Welcome!", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
//                alertDialog.show();
                break;
//            case R.id.lnl_delete_cache_settings:
////                ReadCacheTool.clearPreferenceWelcome(this);
////                Toast.makeText(this, "Đã xóa dữ liệu màn Welcome!", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.lnl_log_out_settings:
//                Toast.makeText(this, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
////                alertDialog.show();
//                break;
            default:
//                alertDialog.show();
                Toast.makeText(this, "Tính năng chưa ra mắt", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
