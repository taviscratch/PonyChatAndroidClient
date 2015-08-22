package com.taviscratch.ponychatandroidclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class MainActivity extends Activity {

    IRCService ircService;

    private ServiceConnection ircServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRCService.IRCServiceBinder ircServiceBinder = (IRCService.IRCServiceBinder) service;
            ircService = ircServiceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startIRCService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, IRCService.class);
        bindService(intent, ircServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(ircServiceConnection);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopIRCService();
    }

    public void startIRCService() {
        Intent intent = new Intent(this, IRCService.class);
        startService(intent);
        bindService(intent, ircServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopIRCService() {
        Intent intent = new Intent(this, IRCService.class);
        stopService(intent);
    }








}
