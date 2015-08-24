package com.taviscratch.ponychatandroidclient;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.jibble.pircbot.PircBot;

public class IRCService extends Service {

    // The irc network connection thread
    IRCConnection ircConnectionThread;


    // Notification Stuff
    NotificationManager notificationManager;
    TaskStackBuilder stackBuilder;






    // Binder
    private final IBinder ircServiceBinder = new IRCServiceBinder();
    public class IRCServiceBinder extends Binder{
        public IRCService getService() {
            return IRCService.this;
        }
    }




    public IRCService() { }


    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        stackBuilder = TaskStackBuilder.create(this);

    }

    @Override
    public IBinder onBind(Intent intent) {

        return ircServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        ircConnectionThread = new IRCConnection();
        ircConnectionThread.start();



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

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        ircConnectionThread.stopThread();
        stopForeground(true);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

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
