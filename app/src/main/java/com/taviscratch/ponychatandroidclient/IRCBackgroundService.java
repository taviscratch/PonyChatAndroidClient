package com.taviscratch.ponychatandroidclient;

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

public class IRCBackgroundService extends Service {

    // Binder
    private final IBinder ircServiceBinder = new IRCServiceBinder();
    public class IRCServiceBinder extends Binder{
        public IRCBackgroundService getService() {
            return IRCBackgroundService.this;
        }
    }




    public IRCBackgroundService() { }


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


        IRCSession.getInstance().start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //ircConnectionThread.stopThread();
        IRCSession.getInstance().stopThread();
        stopForeground(true);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
