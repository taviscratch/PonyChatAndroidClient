package com.taviscratch.ponychatandroidclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity implements Chatroom.OnFragmentInteractionListener,
        LeftDrawer.OnFragmentInteractionListener,
        RightDrawer.OnFragmentInteractionListener,
        ConnectionSettingsPopup.OnFragmentInteractionListener {


    // Fragments
    Chatroom chatroom;
    LeftDrawer leftDrawer;
    RightDrawer rightDrawer;
    //ConnectionSettingsPopup connectionSettingsPopup;


    // animation stuff
    int ANIMATION_DURATION;
    float xStart, yStart, xEnd, yEnd = -1.0f;
    int dpi;


    IRCBackgroundService ircService;

    private ServiceConnection ircServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRCBackgroundService.IRCServiceBinder ircServiceBinder = (IRCBackgroundService.IRCServiceBinder) service;
            ircService = ircServiceBinder.getService();
            Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        dpi = getResources().getDisplayMetrics().densityDpi;

        bindToService();

        // animation stuff
        ANIMATION_DURATION = getResources().getInteger(R.integer.animation_duration);
        // set all to an invalid position
        xStart = yStart = xEnd = yEnd = -1.0f;

        // Making the fragments
        if(chatroom==null) {
            chatroom = new Chatroom();
            chatroom.setRetainInstance(true);
        }

        leftDrawer = LeftDrawer.newInstance(null,null);
        rightDrawer = RightDrawer.newInstance(null,null);
        //connectionSettingsPopup = ConnectionSettingsPopup.newInstance(null,null);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.Chatroom_container, chatroom, "CHATROOM");
        //ft.add(R.id.ConnectionSettingsPopup_container, connectionSettingsPopup);
        ft.add(R.id.LeftDrawer_container, leftDrawer, "LEFT DRAWER");
        ft.add(R.id.RightDrawer_container, rightDrawer, "RIGHT DRAWER");



        ft.hide(leftDrawer);

        ft.show(rightDrawer);
        ft.hide(rightDrawer);

        ft.show(chatroom);


        ft.commit();


    }

    @Override
    protected void onResume() {
        super.onResume();

        bindToService();


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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void stopIRCService() {
        Intent intent = new Intent(this, IRCBackgroundService.class);
        stopService(intent);
    }

    public void bindToService() {
        Intent intent = new Intent(this, IRCBackgroundService.class);
        bindService(intent, ircServiceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    // TODO
    public void onFragmentInteraction(Uri uri) {

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

            Fragment leftDrawer =getFragmentManager().findFragmentByTag("LEFT DRAWER");
            Fragment rightDrawer = getFragmentManager().findFragmentByTag("RIGHT DRAWER");

            switch (swipe) {
                case LEFT:
                    if(leftDrawer.isVisible()) {
                        hideFragment(leftDrawer);
                    }
                    else if(rightDrawer.isVisible()) {
                        // Do nothing
                    }
                    else {
                        if(Util.isChannel(Chatroom.getCurrentConversation()))
                            showFragment(rightDrawer);
                    }
                    break;

                case RIGHT:
                    if(leftDrawer.isVisible()) {
                        // Do nothing
                    }
                    else if(rightDrawer.isVisible()) {
                        hideFragment(rightDrawer);
                    }
                    else {
                        showFragment(leftDrawer);
                    }
                    break;

                case UP:
                    // not supported
                    break;
                case DOWN:
                    // not supported
                    break;
            }
        }
    }

    public void showFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.show(frag);
        ft.commit();
    }
    public void hideFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(frag);
        ft.commit();
    }


}
