package com.anhdt.doranewsvermain;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.fragment.FavoriteFragment;
import com.anhdt.doranewsvermain.fragment.LatestNewsFragment;
import com.anhdt.doranewsvermain.fragment.MoreFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralHomeFragment;
import com.anhdt.doranewsvermain.fragment.generalfragment.GeneralLatestNewsFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private BottomNavigationView navigation;

    private final GeneralHomeFragment homeFragment = GeneralHomeFragment.newInstance();
    private final GeneralLatestNewsFragment latestNewsFragment = GeneralLatestNewsFragment.newInstance();
    private final FavoriteFragment favoriteFragment = FavoriteFragment.newInstance();
    private final MoreFragment moreFragment = MoreFragment.newInstance();

    private final FragmentManager fm = getSupportFragmentManager();

    private Fragment activeFragment = homeFragment;

    private static final String NAME_HOME_FRAGMENT = "HOME_FRAGMENT";
    private static final String NAME_LATEST_NEWS_FRAGMENT = "LATEST_NEWS_FRAGMENT";
    private static final String NAME_FAVORITE_FRAGMENT = "FAVORITE_FRAGMENT";
    private static final String NAME_MORE_FRAGMENT = "MORE_FRAGMENT";

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
                return true;
            case R.id.navigation_latest_news:
                if (activeFragment == latestNewsFragment) {
                    latestNewsFragment.popAllBackStack();
                    return true;
                }
                fm.beginTransaction().hide(activeFragment).show(latestNewsFragment).commit();
                activeFragment = latestNewsFragment;
                return true;
            case R.id.navigation_favorite:
                fm.beginTransaction().hide(activeFragment).show(favoriteFragment).commit();
                activeFragment = favoriteFragment;
                return true;
            case R.id.navigation_more:
                fm.beginTransaction().hide(activeFragment).show(moreFragment).commit();
                activeFragment = moreFragment;
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void getIntentFromSplashAct() {

    }

    private void initViews() {
        navigation = findViewById(R.id.navigation);

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

        //Sau khi setup xong xuôi thì bắn sang màn hình DetailEvent nếu thỏa mãn
        if (idEvent != null && idLongEvent != null) {
            if (!idEvent.equals("") && !idLongEvent.equals("")) {
//                Intent intentToDetailEventAct = new Intent(MainActivity.this, DetailEventActivity.class);
//                intentToDetailEventAct.putExtra(ConstParamTransfer.PARAM_ID_EVENT, idEvent);
//                intentToDetailEventAct.putExtra(ConstParamTransfer.PARAM_ID_LONG_EVENT, idLongEvent);
//                startActivity(intentToDetailEventAct);
            }
        }
    }

//    private void callFragment(Fragment fragment) {
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.commit();
//    }
}
