package com.taviscratch.ponychatandroidclient;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import android.widget.Toast;

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


    public IRCService() {



    }


    @Override
    public void onCreate() {
        super.onCreate();

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

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        ircConnectionThread.stopThread();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }


    public void sendMessage(String message){
        ircConnectionThread.sendMessage(message);
        //pushNotification(message);
    }

    public void pushNotification(String text) {
        Intent activityIntent = new Intent(this,MainActivity.class);

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

        notificationManager.notify(0,mBuilder.build());
    }


    public void setupNotificationHandling(Activity activity) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(activity);
    }

}
