package com.taviscratch.ponychatandroidclient;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Created by sam on 8/23/15.
 */
public class PonyChatApplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();

        // Start Service
        Intent serviceIntent = new Intent(this,IRCService.class);
        startService(serviceIntent);

        // Start the launch activiy
        Intent activityIntent = new Intent(this,MainActivity.class);
        startActivity(activityIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public Application getApplication() {
        return this;
    };

}
