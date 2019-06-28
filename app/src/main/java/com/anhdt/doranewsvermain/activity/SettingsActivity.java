package com.anhdt.doranewsvermain.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
            lnlChooseSize, lnlTheme, lnlShare, lnlRate, lnlContact, lnlhelp,
            lnlPolicy, lnlSecurity, lnlIntroduction, lnlUpdate, lnlDeleteCache, lnlLogOut;
    private AlertDialog alertDialog;
    private TextView textInfoUser;

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

        alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle("Thông báo");
        alertDialog.setMessage("Tính năng chưa ra mắt");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());

        lnlPickCategory = findViewById(R.id.lnl_category_news_settings);
        lnlPickSourceNews = findViewById(R.id.lnl_pick_source_settings);
        lnlPickLanguage = findViewById(R.id.lnl_pick_language_settings);
        lnlChooseSize = findViewById(R.id.lnl_size_display_settings);
        lnlTheme = findViewById(R.id.lnl_theme_settings);
        lnlShare = findViewById(R.id.lnl_share_settings);
        lnlRate = findViewById(R.id.lnl_rate_settings);
        lnlContact = findViewById(R.id.lnl_contact_settings);
        lnlhelp = findViewById(R.id.lnl_help_settings);
        lnlPolicy = findViewById(R.id.lnl_policy_settings);
        lnlSecurity = findViewById(R.id.lnl_security_settings);
        lnlIntroduction = findViewById(R.id.lnl_about_settings);
        lnlUpdate = findViewById(R.id.lnl_update_settings);
        lnlDeleteCache = findViewById(R.id.lnl_delete_cache_settings);
        lnlLogOut = findViewById(R.id.lnl_log_out_settings);

        textInfoUser = findViewById(R.id.text_info_user);

        String info = "+deviceId: " + ReadCacheTool.getDeviceId(this) + "\n" +
                "+uId: " + ReadCacheTool.getUId(this) + "\n" +
                "+mToken: " + ReadCacheTool.getMToken(this) + "\n";
        textInfoUser.setText(info);

        //OnClickListener
        lnlPickCategory.setOnClickListener(this);
        lnlPickSourceNews.setOnClickListener(this);
        lnlPickLanguage.setOnClickListener(this);
        lnlChooseSize.setOnClickListener(this);
        lnlTheme.setOnClickListener(this);
        lnlShare.setOnClickListener(this);
        lnlRate.setOnClickListener(this);
        lnlContact.setOnClickListener(this);
        lnlhelp.setOnClickListener(this);
        lnlPolicy.setOnClickListener(this);
        lnlSecurity.setOnClickListener(this);
        lnlIntroduction.setOnClickListener(this);
        lnlUpdate.setOnClickListener(this);
        lnlDeleteCache.setOnClickListener(this);
        lnlLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isNetworkAvailable = false;
        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(this))) {

        }
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
            case R.id.lnl_share_settings:
            case R.id.lnl_rate_settings:
            case R.id.lnl_contact_settings:
            case R.id.lnl_help_settings:
            case R.id.lnl_policy_settings:
            case R.id.lnl_security_settings:
            case R.id.lnl_about_settings:
            case R.id.lnl_update_settings:
            case R.id.lnl_delete_cache_settings:
                ReadCacheTool.clearPreference(this);
                Toast.makeText(this, "Đã xóa dữ liệu màn Welcome!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lnl_log_out_settings:
                alertDialog.show();
                break;
            default:
                alertDialog.show();
                break;
        }
    }
}
