package com.taviscratch.ponychatandroidclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import com.taviscratch.ponychatandroidclient.services.IRCBackgroundService;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Constants.PreferenceConstants;
import com.taviscratch.ponychatandroidclient.utility.Constants.PreferenceDefaults;
import com.taviscratch.ponychatandroidclient.utility.Util;

public class PonyChatApplication extends Application{


    private static Context context;

    private static final String[] defaultPreferenceNames = {
            PreferenceConstants.DEFAULT_CHANNELS,
            PreferenceConstants.HOSTNAME,
            PreferenceConstants.MAX_MESSAGE_LOG_SIZE,
            PreferenceConstants.PORT,
            PreferenceConstants.REALNAME,
            PreferenceConstants.USERNAME };



    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        SharedPreferences preferences = getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        if(preferences.getString("firstRun","true").equals("true")){
            editor.remove(PreferenceConstants.DEFAULT_CHANNELS);
            editor.apply();
            checkPreferences(preferences);
        };


        if(preferences.getString(PreferenceConstants.RANDOMIZE_USERNAME, "true").equals("true")){
            editor = preferences.edit();
            editor.putString(PreferenceConstants.USERNAME, Util.getRandomUsername());

        }
        editor.apply();

        Intent serviceIntent = new Intent(this, IRCBackgroundService.class);
        startService(serviceIntent);



    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }


    public static Context getAppContext() {
        return context;
    }



    private void checkPreferences(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();


        if(!preferences.contains(PreferenceConstants.USERNAME)) {
            editor.putString(PreferenceConstants.USERNAME, Util.getRandomUsername());
        }
        if(!preferences.contains(PreferenceConstants.REALNAME)) {
            editor.putString(PreferenceConstants.REALNAME,PreferenceDefaults.REALNAME);
        }
        if(!preferences.contains(PreferenceConstants.HOSTNAME)) {
            editor.putString(PreferenceConstants.HOSTNAME,PreferenceDefaults.HOSTNAME);
        }
        if(!preferences.contains(PreferenceConstants.PORT)) {
            editor.putInt(PreferenceConstants.PORT, PreferenceDefaults.PORT);
        }
        if(!preferences.contains(PreferenceConstants.DEFAULT_CHANNELS)) {
            Set<String> defaultChannels = (Set<String>) new HashSet<String>(java.util.Arrays.asList(PreferenceDefaults.DEFAULT_CHANNELS));
            editor.putStringSet(PreferenceConstants.DEFAULT_CHANNELS, defaultChannels);
        }
        if(!preferences.contains(PreferenceConstants.MAX_MESSAGE_LOG_SIZE)) {
            editor.putInt(PreferenceConstants.MAX_MESSAGE_LOG_SIZE, PreferenceDefaults.MAX_MESSAGE_LOG_SIZE);
        }


        editor.apply();
    }





}
