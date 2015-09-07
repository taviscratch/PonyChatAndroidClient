package com.taviscratch.ponychatandroidclient.services;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.ui.MainActivity;
import com.taviscratch.ponychatandroidclient.utility.Constants;

public class NotificationService extends IntentService{


    // Notification Stuff
    NotificationManager notificationManager;
    TaskStackBuilder stackBuilder;



    public NotificationService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        stackBuilder = TaskStackBuilder.create(this);

        Intent activityIntent = new Intent(this,MainActivity.class);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(activityIntent);
        PendingIntent notificationIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("PonyChat Message")
                        .setContentText("PonyChat Service Started")
                        .setContentIntent(notificationIntent);
        startForeground(6667, mBuilder.build());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String text = extras.getString(Constants.NOTIFICATION);
        pushNotification(text);
    }



    public void pushNotification(String text) {
        Intent activityIntent = new Intent(this,MainActivity.class);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(activityIntent);
        PendingIntent notificationIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("PonyChat Message")
                        .setContentText(text)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setContentIntent(notificationIntent);

        notificationManager.notify(6667, mBuilder.build());
    }

}
