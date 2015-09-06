package com.taviscratch.ponychatandroidclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.taviscratch.ponychatandroidclient.Constants.PreferenceConstants;
import com.taviscratch.ponychatandroidclient.Constants.PreferenceDefaults;

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

        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        if(preferences.getString("firstRun","true").equals("true")){
            editor.remove(PreferenceConstants.DEFAULT_CHANNELS);
            editor.commit();
            checkPreferences(preferences);
        }

        editor.remove(PreferenceConstants.DEFAULT_CHANNELS);


        if(preferences.getString(PreferenceConstants.RANDOMIZE_USERNAME, "true").equals("true")){
            editor = preferences.edit();
            editor.putString(PreferenceConstants.USERNAME, Util.getRandomUsername());

        }
        editor.commit();

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


        editor.commit();
    }





}
