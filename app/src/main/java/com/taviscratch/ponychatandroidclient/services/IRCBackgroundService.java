package com.taviscratch.ponychatandroidclient.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.irc.Conversation;
import com.taviscratch.ponychatandroidclient.ui.IRCMessageAdapter;
import com.taviscratch.ponychatandroidclient.irc.IRCSession;
import com.taviscratch.ponychatandroidclient.utility.Constants;

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
        IRCSession.getInstance().start();
        if(Constants.DEBUG) Toast.makeText(this, "Service Bound", Toast.LENGTH_SHORT).show();
        return ircServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(Constants.DEBUG) Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Constants.DEBUG) Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }




    @Override
    public void onDestroy() {
        stopForeground(true);
        if(Constants.DEBUG) Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    // Setup and register the broadcast receivers
    private void registerReceivers() {
        BroadcastReceiver connectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter connectFilter = new IntentFilter(Constants.CONNECT_TO_IRC_NETWORK);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(connectReceiver, connectFilter);

        BroadcastReceiver disconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter disconnectFilter = new IntentFilter(Constants.CONNECT_TO_IRC_NETWORK);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(disconnectReceiver, disconnectFilter);
    }


    public IRCMessageAdapter getIRCMessageAdapter(String conversationName) {
        return IRCSession.getInstance().getMessageAdapter(conversationName);
    }

    public Conversation getConversation(String conversationName) {
        return IRCSession.getInstance().getConversationData(conversationName);
    }

    public boolean doesConversationExist(String conversationName) {
        return IRCSession.getInstance().doesConversationExist(conversationName);
    }

    public void addPrivateConversation(String conversationName){
        IRCSession.getInstance().startNewPrivateConversation(conversationName);
    }


}
