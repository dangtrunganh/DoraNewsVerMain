package com.anhdt.doranewsvermain.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anhdt.doranewsvermain.ActionNetworkStateChange;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String CONNECTED = "CONNECTED";
    public static final String DISCONNECTED = "DISCONNECTED";
    private TextView textViewNetworkState;

    private ActionNetworkStateChange actionNetworkStateChange;
//    public NetworkChangeReceiver(TextView textViewNetworkState) {
//        this.textViewNetworkState = textViewNetworkState;
//    }

    public NetworkChangeReceiver(ActionNetworkStateChange actionNetworkStateChange) {
        this.actionNetworkStateChange = actionNetworkStateChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {

                actionNetworkStateChange.actionWhenNetworkChange(CONNECTED);
            } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

                actionNetworkStateChange.actionWhenNetworkChange(DISCONNECTED);
            }
        }
    }
}
