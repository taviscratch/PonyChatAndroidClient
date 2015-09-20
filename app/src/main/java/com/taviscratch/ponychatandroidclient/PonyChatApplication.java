package com.taviscratch.ponychatandroidclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.taviscratch.ponychatandroidclient.services.IRCBackgroundService;
import com.taviscratch.ponychatandroidclient.services.NotificationService;
import com.taviscratch.ponychatandroidclient.ui.ThemeColors;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Constants.AppPreferenceConstants;
import com.taviscratch.ponychatandroidclient.utility.Constants.PreferenceDefaults;
import com.taviscratch.ponychatandroidclient.utility.Util;

public class PonyChatApplication extends Application{


    private static Context context;
    private static SharedPreferences themeColorPreferences;
    public static boolean I_JUST_DONT_KNOW_WHAT_WENT_WRONG = false;

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

        // set up themes



        context = getApplicationContext();


        SharedPreferences appPreferences = getSharedPreferences(AppPreferenceConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = appPreferences.edit();

        boolean firstRun = appPreferences.getBoolean(AppPreferenceConstants.IS_FIRST_RUN,true);

        if(firstRun) {
            editor.putBoolean(AppPreferenceConstants.IS_FIRST_RUN, true);
            editor.remove(AppPreferenceConstants.DEFAULT_CHANNELS);
            editor.commit();
            checkPreferences(appPreferences);
        } else {
            if(appPreferences.getBoolean(AppPreferenceConstants.ALWAYS_RANDOMIZE_USERNAME, false)){
                editor.putString(AppPreferenceConstants.USERNAME, Util.getRandomUsername());

            }
        }

        editor.apply();


        Intent ircBackgroundServiceIntent = new Intent(this, IRCBackgroundService.class);
        startService(ircBackgroundServiceIntent);

        Intent notificationServiceIntent = new Intent(this, NotificationService.class);
        startService(notificationServiceIntent);

    }


    public static Context getAppContext() {
        return context;
    }
    public SharedPreferences getAppPreferences() {
        return getSharedPreferences(AppPreferenceConstants.PREFS_NAME, 0);
    }

    public SharedPreferences getCurrentThemePreferences() {
        return getSharedPreferences(Constants.ThemeColorPreferenceConstants.PREFS_NAME,0);
    }


    private void checkPreferences(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();


        if(!preferences.contains(AppPreferenceConstants.USERNAME)) {
            editor.putString(AppPreferenceConstants.USERNAME, Util.getRandomUsername());
        }
        if(!preferences.contains(AppPreferenceConstants.REALNAME)) {
            editor.putString(AppPreferenceConstants.REALNAME,PreferenceDefaults.REALNAME);
        }
        if(!preferences.contains(AppPreferenceConstants.HOSTNAME)) {
            editor.putString(AppPreferenceConstants.HOSTNAME,PreferenceDefaults.HOSTNAME);
        }
        if(!preferences.contains(AppPreferenceConstants.PORT)) {
            editor.putInt(AppPreferenceConstants.PORT, PreferenceDefaults.PORT);
        }
        if(!preferences.contains(AppPreferenceConstants.DEFAULT_CHANNELS)) {
            Set<String> defaultChannels = (Set<String>) new HashSet<String>(java.util.Arrays.asList(PreferenceDefaults.DEFAULT_CHANNELS));
            editor.putStringSet(AppPreferenceConstants.DEFAULT_CHANNELS, defaultChannels);
        }
        if(!preferences.contains(AppPreferenceConstants.MAX_MESSAGE_LOG_SIZE)) {
            editor.putInt(AppPreferenceConstants.MAX_MESSAGE_LOG_SIZE, PreferenceDefaults.MAX_MESSAGE_LOG_SIZE);
        }


        editor.apply();
    }




    public void switchAppTheme(String theme) {
        loadTheme(theme);
    }

    // Loads the theme constants into the shared preferences
    private void loadTheme(String theme) throws IllegalArgumentException {
        SharedPreferences themeColorPreferences = getSharedPreferences(Constants.ThemeColorPreferenceConstants.PREFS_NAME,0);
        SharedPreferences.Editor editor = themeColorPreferences.edit();

        if(theme.equals(ThemeColors.Default.themeName)) {

            // TODO

        } else if(theme.equals(ThemeColors.Magic.themeName)) {
            editor.putInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_PRIMARY, ThemeColors.Magic.backgroundPrimary);
            editor.putInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_SECONDARY, ThemeColors.Magic.backgroundSecondary);
            editor.putInt(Constants.ThemeColorPreferenceConstants.ACCENT, ThemeColors.Magic.accent);
            editor.putInt(Constants.ThemeColorPreferenceConstants.MENU_TITLE_1, ThemeColors.Magic.menuTitle1);
            editor.putInt(Constants.ThemeColorPreferenceConstants.MENU_TITLE_2, ThemeColors.Magic.menuTitle2);
            editor.putInt(Constants.ThemeColorPreferenceConstants.MENU_ITEM, ThemeColors.Magic.menuItem);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_NAME, ThemeColors.Magic.chatName);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_MESSAGE, ThemeColors.Magic.chatMessage);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_ACTION, ThemeColors.Magic.chatAction);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_EVENT, ThemeColors.Magic.chatEvent);

        } else if(theme.equals(ThemeColors.Rustic.themeName)) {

            // TODO

        } else if(theme.equals(ThemeColors.Rainbow.themeName)) {

            // TODO

        } else if(theme.equals(ThemeColors.Shy.themeName)) {

            // TODO

        } else if(theme.equals(ThemeColors.Party.themeName)) {
            editor.putInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_PRIMARY, ThemeColors.Party.backgroundPrimary);
            editor.putInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_SECONDARY, ThemeColors.Party.backgroundSecondary);
            editor.putInt(Constants.ThemeColorPreferenceConstants.ACCENT, ThemeColors.Party.accent);
            editor.putInt(Constants.ThemeColorPreferenceConstants.MENU_TITLE_1, ThemeColors.Party.menuTitle1);
            editor.putInt(Constants.ThemeColorPreferenceConstants.MENU_TITLE_2, ThemeColors.Party.menuTitle2);
            editor.putInt(Constants.ThemeColorPreferenceConstants.MENU_ITEM, ThemeColors.Party.menuItem);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_NAME, ThemeColors.Party.chatName);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_MESSAGE, ThemeColors.Party.chatMessage);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_ACTION, ThemeColors.Party.chatAction);
            editor.putInt(Constants.ThemeColorPreferenceConstants.CHAT_EVENT, ThemeColors.Party.chatEvent);


        } else if(theme.equals(ThemeColors.FashionHorse.themeName)) {

            // TODO

        } else {
            loadTheme(ThemeColors.Default.themeName);
            throw new IllegalArgumentException("that horse's theme is not supported");
        }



        editor.commit();

    }


}
