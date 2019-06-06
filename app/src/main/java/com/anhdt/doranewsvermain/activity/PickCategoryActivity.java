package com.anhdt.doranewsvermain.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.MainActivity;
import com.anhdt.doranewsvermain.R;
import com.anhdt.doranewsvermain.adapter.recyclerview.CategoryAdapter;
import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.ConstLocalCaching;
import com.anhdt.doranewsvermain.constant.ConstParamTransfer;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.categoryresult.CategoryResult;
import com.anhdt.doranewsvermain.model.newsresult.Category;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.ReadCacheTool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PickCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnPick;
    private TextView txtPickDone;
    private RecyclerView mRecyclerPick;
    private ConstraintLayout constraintLayoutNoNetwork;
    private Button btnTryReconnect;
    private ProgressDialog dialog;

    private CategoryAdapter mCategoryAdapter;
    private CategoryResult mCategoryResult;

    private ArrayList<Category> mCategoryListTest; //All from api
    private ArrayList<Category> mCategoryListInLocal;

    private String uId;
    private boolean isExistListCategoriesInLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_category);

        getUIdFromIntent();

        initViews();
    }

    private void getUIdFromIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            uId = ReadCacheTool.getUId(this);
        } else {
            uId = intent.getStringExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_PICK_CATEGORY);
        }
    }

    private void initViews() {
        btnPick = findViewById(R.id.btn_pick_done);
        txtPickDone = findViewById(R.id.text_pick_done);
        mRecyclerPick = findViewById(R.id.recycler_pick_category);
        constraintLayoutNoNetwork = findViewById(R.id.constraint_state_wifi_off_pick_category);
        btnTryReconnect = findViewById(R.id.button_try_refresh_network_pick_category);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        btnPick.setOnClickListener(this);
        btnTryReconnect.setOnClickListener(this);

        setUpAdapter();
    }

    private void setUpAdapter() {
        mCategoryListTest = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(mCategoryListTest, this, txtPickDone, btnPick);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerPick.setLayoutManager(mLayoutManager);
        mRecyclerPick.setAdapter(mCategoryAdapter);
        isExistListCategoriesInLocal = getListCategoriesChosen();

        getDataFromServer();
    }

    private void getDataFromServer() {
        if (GeneralTool.isNetworkAvailable(Objects.requireNonNull(this))) {
            //Có mạng
            dialog.show();
            mRecyclerPick.setVisibility(View.VISIBLE);
            constraintLayoutNoNetwork.setVisibility(View.GONE);
            getListCategoryFromAPI(isExistListCategoriesInLocal);
        } else {
            //Mất mạng
            mRecyclerPick.setVisibility(View.GONE);
            constraintLayoutNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    private boolean getListCategoriesChosen() {
        mCategoryListInLocal = new ArrayList<>();
        SharedPreferences pre = getSharedPreferences
                (ConstLocalCaching.FILE_NAME_PREF_LIST_CATEGORY, MODE_PRIVATE);
        String json = pre.getString(ConstLocalCaching.KEY_PREF_LIST_CATEGORY, ConstLocalCaching.DEFAULT_VALUE_PREF_LIST_CATEGORY_DEFAULT);
        assert json != null;
        if (json.equals(ConstLocalCaching.DEFAULT_VALUE_PREF_LIST_CATEGORY_DEFAULT)) {
            return false;
        }

        //Convert ngược từ jsonString sang List<Category> categories
        Gson gson = new Gson();
        mCategoryListInLocal = gson.fromJson(json, new TypeToken<List<Category>>() {
        }.getType());
//        Log.e("ListCategories-Local", mCategoryListInLocal.toString());
        return true;
    }

    private void getListCategoryFromAPI(final boolean flag) {
        //flag = true: Đã có trong local list các category đã chọn
        //flag = false: Không có trong local
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.ROOT_GET_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerAPI apiService = retrofit.create(ServerAPI.class);
        Call<CategoryResult> call = apiService.getResultCategory();
        call.enqueue(new Callback<CategoryResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                mCategoryResult = response.body();
                if (mCategoryResult == null) {
                    Toast.makeText(getApplicationContext(), "Failed to load data - null", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                mCategoryListTest = (ArrayList<Category>) mCategoryResult.getData();
                if (flag) {
                    //flag = true: Đã có trong local list các category đã chọn, thì khởi tạo, tick đã chọn cho các category đó
                    for (Category category : mCategoryListTest) {
                        for (Category categoryLocal : mCategoryListInLocal) {
                            if (category.getId().equals(categoryLocal.getId())) {
                                //Set selected cho các category đã chọn trong Local
                                category.setSelected(true);
                                break;
                            }
                        }
                    }
                    mCategoryAdapter.getListCategoryChosen().clear();
                    mCategoryAdapter.setListCategoryChosen(mCategoryListInLocal);
                    btnPick.setEnabled(true);
                    btnPick.setText("OK");

                    //Set màu khi đã chọn đủ, cho phép user ấn "OK"
                    btnPick.setBackgroundColor(0xff64DD17);
                    txtPickDone.setText("(Đã chọn " + mCategoryListInLocal.size() + "/" + mCategoryListTest.size() + ")");
                } else {
                    txtPickDone.setText("(Đã chọn 0" + "/" + mCategoryListTest.size() + ")");
                }

                mCategoryAdapter.updateListCategories(mCategoryListTest);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load category - onFailure", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
//        startActivity(new Intent(PickCategoryActivity.this, MainActivity.class));
//        finish();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pick_done:
                //Lấy ra list các category đã chọn từ adapter
                List<Category> categoriesChosen = mCategoryAdapter.getListCategoryChosen();
                if (categoriesChosen == null) {
                    return;
                }
                if (categoriesChosen.size() == 0) {
                    return;
                }
                //===Lưu xuống Local===
                ReadCacheTool.storeCategory(PickCategoryActivity.this, categoriesChosen);

                //1. Navigate tới màn hình MainActivity, trong MainActivity sẽ đọc trc tiên danh sách category này trong
                //2. share preference để load lên.
                Intent intentResult = new Intent(PickCategoryActivity.this, MainActivity.class);

                Gson gson = new Gson();
                String json = gson.toJson(categoriesChosen);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_LIST_CATEGORY_FR_SPLASH_TO_MAIN, json);
                intentResult.putExtra(ConstParamTransfer.TRANSFER_U_ID_FR_SPLASH_TO_MAIN, uId);

                startActivity(intentResult);
                finish();
                break;
            case R.id.button_try_refresh_network_pick_category:
                //try to reconnect network
                getDataFromServer();
                break;
            default:
                break;
        }
    }
}
