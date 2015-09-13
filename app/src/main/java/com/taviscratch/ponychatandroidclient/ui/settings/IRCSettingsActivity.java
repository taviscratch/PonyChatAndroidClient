package com.taviscratch.ponychatandroidclient.ui.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.ui.Chatroom;
import com.taviscratch.ponychatandroidclient.ui.MainActivity;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.SwipeControls;
import com.taviscratch.ponychatandroidclient.utility.Util;

public class IRCSettingsActivity extends Activity {

    Fragment currentlyViewing;
    float xStart, yStart, xEnd, yEnd;

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

        Fragment settingsDrawerFragment, connectionSettings, servicesSettings, uiSettings, miscSettings;
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










        editor.commit();


        Intent intent = new Intent(PonyChatApplication.getAppContext(), MainActivity.class);
        startActivity(intent);
    }
}
