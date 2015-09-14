package com.taviscratch.ponychatandroidclient.services;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.irc.IRCMessage;
import com.taviscratch.ponychatandroidclient.ui.MainActivity;
import com.taviscratch.ponychatandroidclient.utility.Constants;

public class NotificationService extends Service {


    // Notification Stuff
    NotificationManager notificationManager;
    TaskStackBuilder stackBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public NotificationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString(Constants.IntentExtrasConstants.MESSAGE);
                String sender = extras.getString(Constants.IntentExtrasConstants.SENDER);
                String messageType = extras.getString(Constants.IntentExtrasConstants.MESSAGE_TYPE);
                String target = extras.getString(Constants.IntentExtrasConstants.MESSAGE_TARGET);
                parseMessage(message, sender, target, messageType);
            }
        };
        IntentFilter connectFilter = new IntentFilter(Constants.NOTIFICATION);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(notificationReceiver, connectFilter);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        stackBuilder = TaskStackBuilder.create(this);

        Intent activityIntent = new Intent(this,MainActivity.class);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(activityIntent);
        PendingIntent notificationIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("PonyChat Message")
                        .setContentText("PonyChat Service Started")
                        .setContentIntent(notificationIntent);
        startForeground(6667, mBuilder.build());
    }

    private void parseMessage(String message, String sender, String target, String messageType) {
        String notification = "";

        if(messageType!=null) {
            if (messageType.equals(Constants.MessageType.PRIVMSG)) {
                notification = sender + ": " + message;
                pushNotification(notification);
            } else if (messageType.equals(Constants.MessageType.ACTION)) {
                notification = sender + " " + message;
                pushNotification(notification);
            }
        } else {
            // do nothing
        }
    }

    public void pushNotification(String text) {
        Intent activityIntent = new Intent(PonyChatApplication.getAppContext(),MainActivity.class);

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
