package com.example.harishshanker.yourfault;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.google.android.gms.wearable.WearableListenerService;

public class WearListService extends WearableListenerService {
    public WearListService() {
    }


    private static final String START_ACTIVITY = "/start_activity";
    private static final String START_INFO = "/start_info";
    private static final String START_PHOTO = "/start_photo";
    private static final String TAG = "/LOCATION SERVICES";


    private static GoogleApiClient mGoogleApiClient;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            makeNotif(value);
        }
        else if( messageEvent.getPath().equalsIgnoreCase( START_INFO) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent("WE GOT SOME INFO");
            intent.putExtra("message", value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }

    }

    public void makeNotif(String message){
        int notificationId = 001;

        Bitmap bitmap = Bitmap.createBitmap(320,320, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.parseColor("#999919"));
        String eventTitle = message.split(":::")[1];
        NumberFormat formatter = new DecimalFormat("#0.00");
        System.out.println(formatter.format(4.0));
        String eventDistance = formatter.format(Double.parseDouble(message.split(":::")[4]));
        String eventMag = message.split(":::")[0];

        Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);

        viewIntent.putExtra("message", message);
        viewIntent.addFlags(viewIntent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_full_sad)
                        .setContentTitle(eventTitle)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText(eventMag + "M\n" + eventDistance + " km")
                        .setContentIntent(viewPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(bitmap));

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, notificationBuilder.build());

    }

}
