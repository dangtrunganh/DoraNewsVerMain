package com.anhdt.doranewsvermain.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.viewpager_welcome.WelcomePagerAdapter;
import com.anhdt.doranewsvermain.util.ReadCacheTool;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mPager;
    private ScrollingPagerIndicator indicator;
    private Button btnSkip, btnNext;

    private int[] layouts = {R.layout.fragment_slider_item_1, R.layout.fragment_slider_item_2,
            R.layout.fragment_slider_item_3, R.layout.fragment_slider_item_4};

    private WelcomePagerAdapter welcomePagerAdapter;
    private ImageView[] dots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ReadCacheTool.checkPreferenceWelcomeActivity(this)) {
            loadHomeActivity();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_welcome);

        mPager = findViewById(R.id.view_pager_welcome_activity);
        welcomePagerAdapter = new WelcomePagerAdapter(layouts, this);
        mPager.setAdapter(welcomePagerAdapter);

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
