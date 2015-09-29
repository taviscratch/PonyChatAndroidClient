package com.taviscratch.ponychatandroidclient.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.irc.Conversation;
import com.taviscratch.ponychatandroidclient.services.IRCBackgroundService;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.services.NotificationService;
import com.taviscratch.ponychatandroidclient.ui.settings.IRCSettingsActivity;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.SwipeControls;
import com.taviscratch.ponychatandroidclient.utility.Util;

public class MainActivity extends Activity implements Chatroom.OnFragmentInteractionListener,
        LeftDrawer.OnFragmentInteractionListener,
        RightDrawer.OnFragmentInteractionListener {


    // Fragments
    Chatroom chatroom;
    LeftDrawer leftDrawer;
    RightDrawer rightDrawer;


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
            if(Constants.DEBUG) Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(Constants.DEBUG) Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
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

        registerReceivers();
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

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.Chatroom_container, chatroom, "CHATROOM");
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void switchToConversation(String conversationName) {
        hideFragment(leftDrawer);
        hideFragment(rightDrawer);

        // if the conversation doesn't exist
        if(!ircService.doesConversationExist(conversationName)) {
            if(Util.isPrivateConversation(conversationName))
                ircService.addPrivateConversation(conversationName);
            else
                throw new IllegalArgumentException("invalid conversation name submitted");
        }

        Conversation conversation = ircService.getConversation(conversationName);
        IRCMessageAdapter messageAdapter = ircService.getIRCMessageAdapter(conversationName);


        // set adapter
        chatroom.setConversationAdapter(messageAdapter);

        // set topic
        String topicText = formatTopicText(conversationName, conversation.getTopic());
        chatroom.setTopicMarqueeText(topicText);

        // ensure that theme is properly loaded
        chatroom.invalidateTheme();
    }

    private String formatTopicText(String conversationName, String topic) {

        // Topic formatting
        String formattedTopic= conversationName;
        if(topic.equals(conversationName) || topic.equals("")){
            if(!Util.isChannel(conversationName) && !conversationName.equals(Constants.NETWORK_LOBBY))
                formattedTopic = "[Private Message] " + conversationName;
        } else
            formattedTopic = conversationName + " |>> " + topic;

        return formattedTopic;
    }




    @Override
    protected void onDestroy() {

        unbindService(ircServiceConnection);
        SharedPreferences preferences = getSharedPreferences(Constants.AppPreferenceConstants.PREFS_NAME, 0);
        boolean keepServiceRunning = preferences.getBoolean(Constants.AppPreferenceConstants.KEEP_IRC_SERVICE_RUNNING_IN_BACKGROUND, false);
        if(!keepServiceRunning) {
            Intent ircBackgroundServiceIntent = new Intent(this,IRCBackgroundService.class);
            stopService(ircBackgroundServiceIntent);

            Intent notificationServiceIntent = new Intent(this, NotificationService.class);
            stopService(notificationServiceIntent);
        }

        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void bindToService() {
        Intent intent = new Intent(this, IRCBackgroundService.class);
        bindService(intent, ircServiceConnection, Context.BIND_AUTO_CREATE);
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


    public void settingsButtonOnClick(View v) {
        Intent intent = new Intent(PonyChatApplication.getAppContext(), IRCSettingsActivity.class);
        startActivity(intent);
        hideFragment(getFragmentManager().findFragmentByTag("LEFT DRAWER"));
    }

    public void registerReceivers() {
        // A receiver for messages sent from the server
        BroadcastReceiver joinedNewConversationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String conversationName = extras.getString(Constants.IntentExtrasConstants.CONVERSATION_NAME);
                switchToConversation(conversationName);
            }
        };
        IntentFilter joinedNewConversationFilter = new IntentFilter(Constants.JOINED_NEW_CONVERSATION);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(joinedNewConversationReceiver, joinedNewConversationFilter);
    }

    @Override
    public void onConversationSelected(String conversationName) {

    }

    @Override
    public void onUserNameSelected(String username) {

    }

    @Override
    public void onProcessUserInput(String rawMessage) {

    }
}
