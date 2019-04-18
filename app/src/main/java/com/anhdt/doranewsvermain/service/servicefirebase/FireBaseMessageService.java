package com.anhdt.doranewsvermain.service.servicefirebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.anhdt.doranewsvermain.MainActivity;
import com.anhdt.doranewsvermain.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBaseMessageService/* extends FirebaseMessagingService */{
//    private static final String NOTIFICATION_NUMBER = "NOTIFICATION_NUMBER";
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (remoteMessage.getData().size() > 0) { // khi nhận được dữ liệu
//            final String messageResponse = remoteMessage.getData().get("message");
//            sendNotification(messageResponse); //Tiến hành gửi thông báo, tạo notification
//            //.... hoặc tải dữ liệu, update dữ liệu, ra bất cứ lệnh gì cho app
//        }
//    }
//    // ví dụ tạo notification
//    private void sendNotification(String messageBody/*, String requestType*/) {
//        SharedPreferences prefs =
//                getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//        int notificationNumber = prefs.getInt(NOTIFICATION_NUMBER, 0);
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, intent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);  //dữ liệu dẽ được truyền đi khi nhấn vào notification
//
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this).setCategory(NotificationCompat.CATEGORY_PROMO)
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                                R.mipmap.ic_launcher_round))
//                        .setAutoCancel(true)
//                        .setSmallIcon(R.drawable.image_icon_app_dora)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setColor(Color.parseColor("#00bcd4"))
//                        .setSound(uri)
//                        .setContentIntent(pendingIntent)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL);
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(notificationNumber, notificationBuilder.build());
//
//        // Thông thường, chỉ với đoạn code trên, các notification khi được tạo sẽ che khuất lấy nhau, cái mới đè lên trên cái cũ (One line) ,
//        // đoạn code tiếp sau đây sẽ giải quyết chuyện đó, các thanh thông báo sẽ xếp chồng lên nhau, nhưng ko che khuất lẫn nhau,
//        // thông báo mới sẽ xếp lên trên thông báo cũ (Multi line), thuận tiện cho việc click chọn notification cụ thể
//        SharedPreferences.Editor editor = prefs.edit();
//        notificationNumber++;
//        editor.putInt(NOTIFICATION_NUMBER, notificationNumber);
//        editor.apply();
//    }

}

