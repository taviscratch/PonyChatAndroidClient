package com.taviscratch.ponychatandroidclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class IRCService extends Service {



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
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Service Bound", Toast.LENGTH_LONG).show();
        return ircServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "Service Unbound", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }









}
