package com.example.hklist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */


    private void sendNotification(String title, String messageBody) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        //  fullScreenPendingIntent = PendingIntent.getActivity(getContext(), 0, push, PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setFullScreenIntent(fullScreenPendingIntent, true);
       // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.noti_move_dragon));
        Intent intent=new Intent(this,LoginActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSmallIcon(R.drawable.bg_dragon);
        builder.setContentTitle(title);
        builder.setContentText(messageBody);
        builder.setColor(Color.DKGRAY);
        builder.setContentIntent(pendingIntent);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_HIGH));
        }
        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(0, builder.build());
    }
}