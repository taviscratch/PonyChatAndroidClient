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
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Handler;

public class MainActivity extends Activity implements Chatroom.OnFragmentInteractionListener,
        LeftDrawer.OnFragmentInteractionListener,
        RightDrawer.OnFragmentInteractionListener,
        ConnectionSettingsPopup.OnFragmentInteractionListener {


    // Fragments
    Chatroom chatroom;
    //LeftDrawer leftDrawer;
    //RightDrawer rightDrawer;
    //ConnectionSettingsPopup connectionSettingsPopup;

    IRCService ircService;

    private ServiceConnection ircServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRCService.IRCServiceBinder ircServiceBinder = (IRCService.IRCServiceBinder) service;
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


        bindToService();

        // Making the fragments
        if(chatroom==null) {
            chatroom = new Chatroom();
            chatroom.setRetainInstance(true);
        }

        //leftDrawer = LeftDrawer.newInstance(null,null);
        //rightDrawer = RightDrawer.newInstance(null,null);
        //connectionSettingsPopup = ConnectionSettingsPopup.newInstance(null,null);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.Chatroom_container, chatroom);
        //ft.add(R.id.ConnectionSettingsPopup_container, connectionSettingsPopup);
        //ft.add(R.id.LeftDrawer_container, leftDrawer);
        //ft.add(R.id.RightDrawer_container, rightDrawer);

        //ft.hide(chatroom);
        //ft.hide(leftDrawer);
        //ft.hide(rightDrawer);
        //ft.hide(connectionSettingsPopup);
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
        Intent intent = new Intent(this, IRCService.class);
        stopService(intent);
    }


    public void bindToService() {
        Intent intent = new Intent(this, IRCService.class);
        bindService(intent, ircServiceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    // TODO
    public void onFragmentInteraction(Uri uri) {

    }


}
