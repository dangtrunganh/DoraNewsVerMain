package com.anhdt.doranewsvermain.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.anhdt.doranewsvermain.api.ServerAPI;
import com.anhdt.doranewsvermain.constant.RootAPIUrlConst;
import com.anhdt.doranewsvermain.model.Logging;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Context.WIFI_SERVICE;

public class LogTool {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static String getSessionId(Context mContext) {
//        Date date = new Date();
//
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
//        long dateLong = myFormat.parse(startDayFormatted).getTime();
        Calendar aGMTCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
        return ReadCacheTool.getUId(mContext) + "_" + aGMTCalendar.getTimeInMillis();
    }

    public static long getTimeCreate() {
        Calendar aGMTCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
        return aGMTCalendar.getTimeInMillis();
    }

    public static String getIpAddress(Context mContext) {
        WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static void sendLogging(Context context) {
        ArrayList<Logging> loggings = ReadRealmToolForLogging.getListLoggingInLocal(context);
        if (loggings == null) {
            return;
        }

        if (loggings.size() == 0) {
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(loggings);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RootAPIUrlConst.IP_ADDRESS_LOGGING)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ServerAPI apiService = retrofit.create(ServerAPI.class);
        Log.e("send-log", json);
        Call<ResponseBody> sendLogging = apiService.postRawJSON(loggings);
        sendLogging.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("send-log", "ok-welcome");
                String data = null;
                try {
                    data = response.body().string();
                    Log.e("send-log", "ok-response: " + data);
                    Log.e("send-log", "ok-url: " + response.raw().request().url());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("send-log", "fail-welcome");
            }
        });
    }
}
