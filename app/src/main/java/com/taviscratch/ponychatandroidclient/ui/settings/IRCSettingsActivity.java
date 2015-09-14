package com.taviscratch.ponychatandroidclient.ui.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.services.IRCBackgroundService;
import com.taviscratch.ponychatandroidclient.ui.Chatroom;
import com.taviscratch.ponychatandroidclient.ui.MainActivity;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.SwipeControls;
import com.taviscratch.ponychatandroidclient.utility.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IRCSettingsActivity extends Activity {

    Fragment currentlyViewing;
    float xStart, yStart, xEnd, yEnd;

    Fragment settingsDrawerFragment, connectionSettings, servicesSettings, uiSettings, miscSettings;

    public static final String drawerTag = "SETTINGS DRAWER";
    public static final String connectionTag = "CONNECTION SETTINGS";
    public static final String servicesTag = "SERVICES SETTINGS";
    public static final String uiTag = "UI SETTINGS";
    public static final String miscTag = "MISC SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircsettings);

        // set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // invalidate touch input values
        xStart = yStart = xEnd = yEnd = -1.0f;



        // Fragment Stuff
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        settingsDrawerFragment = new SettingsDrawerFragment();
        connectionSettings = new SettingsFragment_Connection();
        servicesSettings = new SettingsFragment_Services();
        uiSettings = new SettingsFragment_UI();
        miscSettings = new SettingsFragment_Misc();

        // add all
        ft.add(R.id.settings_drawer_fragment, settingsDrawerFragment, drawerTag);
        ft.add(R.id.connection_settings_fragment,connectionSettings, connectionTag);
        ft.add(R.id.services_settings_fragment,servicesSettings, servicesTag);
        ft.add(R.id.ui_settings_fragment,uiSettings, uiTag);
        ft.add(R.id.misc_settings_fragment,miscSettings, miscTag);

        // hide all
        ft.hide(settingsDrawerFragment);
        ft.hide(connectionSettings);
        ft.hide(servicesSettings);
        ft.hide(uiSettings);
        ft.hide(miscSettings);

        // start off viewing the connection settings
        ft.show(connectionSettings);
        currentlyViewing = connectionSettings;

        ft.commit();

    }


    // Switches to the fragment that's passed in, and hides the drawer.
    public void switchToFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.hide(currentlyViewing);
        ft.show(frag);
        ft.hide(getFragmentManager().findFragmentByTag(drawerTag));
        ft.commit();

        currentlyViewing = frag;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void handleTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                xStart = event.getX();
                yStart = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                xEnd = event.getX();
                yEnd = event.getY();
                if(xStart >= 0.0f && yStart >= 0.0f)
                    handleInput(xStart,xEnd,yStart,yEnd);
                // set all to an invalid position
                xStart = yStart = xEnd = yEnd = -1.0f;
                break;
        }
    }

    private void handleInput(float xStart, float xEnd, float yStart, float yEnd) {

        float x = Math.abs(xStart - xEnd);
        float y = Math.abs(yStart-yEnd);

        // if the input is a swipe
        if(x>1.0f || y>1.0f) {
            SwipeControls.SWIPE_DIRECTION swipe = SwipeControls.interpretSwipe(xStart,xEnd,yStart,yEnd);

            Fragment drawer = getFragmentManager().findFragmentByTag(drawerTag);

            switch (swipe) {
                case LEFT:
                    if(drawer.isVisible()) {
                        hideFragment(drawer);
                    }

                    break;

                case RIGHT:
                    if(drawer.isHidden()) {
                        showFragment(drawer);
                        View currentSettingsView = currentlyViewing.getView();
                        currentSettingsView.clearFocus();
                    }

                    break;
            }
        }
    }

    private void showFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.show(frag);
        ft.commit();
    }
    private void hideFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(frag);
        ft.commit();
    }


    public void openConnectionSettingsFragment(View v) {
        switchToFragment(getFragmentManager().findFragmentByTag(IRCSettingsActivity.connectionTag));
    }
    public void openServicesSettingsFragment(View v) {
        switchToFragment(getFragmentManager().findFragmentByTag(IRCSettingsActivity.servicesTag));
    }
    public void openUISettingsFragment(View v) {
        switchToFragment(getFragmentManager().findFragmentByTag(IRCSettingsActivity.uiTag));
    }
    public void openMiscSettingsFragment(View v) {
        switchToFragment(getFragmentManager().findFragmentByTag(IRCSettingsActivity.miscTag));
    }
    public void applySettingsButtonOnClick(View v) {

        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        // get the views of all the fragments
        View connectionSettingsView = connectionSettings.getView();
        View serviceSettingsView = servicesSettings.getView();
        View uiSettingsView = uiSettings.getView();
        View miscSettingsView = miscSettings.getView();




        EditText usernameEdit, passwordEdit, hostnameEdit, portEdit, realnameEdit, defaultChannelsEdit;
        CheckBox randomizeUsernameCheckBox, keepIRCServiceRunningCheckBox, notificationsEnabledCheckBox;

        // get all the widgets from the connection settings fragment
        usernameEdit = (EditText) connectionSettingsView.findViewById(R.id.settings_usernameEdit);
        passwordEdit = (EditText) connectionSettingsView.findViewById(R.id.settings_passwordEdit);
        hostnameEdit = (EditText) connectionSettingsView.findViewById(R.id.settings_hostnameEdit);
        portEdit = (EditText) connectionSettingsView.findViewById(R.id.settings_portEdit);
        realnameEdit = (EditText) connectionSettingsView.findViewById(R.id.settings_realnameEdit);
        defaultChannelsEdit = (EditText) connectionSettingsView.findViewById(R.id.settings_defaultChannelsEdit);
        randomizeUsernameCheckBox = (CheckBox) connectionSettingsView.findViewById(R.id.settings_randomusername_checkbox);

        //get all the widgets from the services settings fragment
        keepIRCServiceRunningCheckBox = (CheckBox) serviceSettingsView.findViewById(R.id.settings_keep_service_running_checkbox);
        notificationsEnabledCheckBox = (CheckBox) serviceSettingsView.findViewById(R.id.settings_enable_notifications_checkbox);



        // get the data from the widgets
        String username, password, hostname, realname;
        int port;
        Set<String> defaultChannels;
        boolean alwaysRandomizeUsername, notificationsEnabled, keepIRCServiceRunning;

        username = usernameEdit.getText().toString();
        password = passwordEdit.getText().toString();
        hostname = hostnameEdit.getText().toString();
        realname = realnameEdit.getText().toString();
        port = Integer.valueOf(portEdit.getText().toString());
        alwaysRandomizeUsername = randomizeUsernameCheckBox.isChecked();
        notificationsEnabled = notificationsEnabledCheckBox.isChecked();
        keepIRCServiceRunning = keepIRCServiceRunningCheckBox.isChecked();
        defaultChannels = parseDefaultChannels(defaultChannelsEdit.getText().toString());


        // edit the shared preferences
        editor.putString(Constants.PreferenceConstants.USERNAME, username);
        editor.putString(Constants.PreferenceConstants.PASSWORD, password);
        editor.putString(Constants.PreferenceConstants.HOSTNAME, hostname);
        editor.putInt(Constants.PreferenceConstants.PORT, port);
        editor.putString(Constants.PreferenceConstants.REALNAME, realname);
        editor.putStringSet(Constants.PreferenceConstants.DEFAULT_CHANNELS, defaultChannels);
        editor.putBoolean(Constants.PreferenceConstants.ALWAYS_RANDOMIZE_USERNAME, alwaysRandomizeUsername);
        editor.putBoolean(Constants.PreferenceConstants.KEEP_IRC_SERVICE_RUNNING_IN_BACKGROUND, keepIRCServiceRunning);
        editor.putBoolean(Constants.PreferenceConstants.NOTIFICATIONS_ENABLED, notificationsEnabled);

        editor.commit();

        Intent startMainActivityIntent = new Intent(PonyChatApplication.getAppContext(), MainActivity.class);
        startActivity(startMainActivityIntent);
    }


    private Set<String> parseDefaultChannels(String defaultChannelsEntry) {
        defaultChannelsEntry = defaultChannelsEntry.replaceAll(" ", "");
        String[] defaultChannels = defaultChannelsEntry.split(",");
        return new HashSet<String>(new ArrayList<String>(Arrays.asList(defaultChannels)));
    }




}
