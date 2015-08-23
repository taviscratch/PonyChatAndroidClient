package com.taviscratch.ponychatandroidclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Handler;

public class MainActivity extends Activity implements Chatroom.OnFragmentInteractionListener,
        LeftDrawer.OnFragmentInteractionListener,
        RightDrawer.OnFragmentInteractionListener,
        ConnectionSettingsPopup.OnFragmentInteractionListener {

    IRCService ircService;

    private ServiceConnection ircServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRCService.IRCServiceBinder ircServiceBinder = (IRCService.IRCServiceBinder) service;
            ircService = ircServiceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };




    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);



        // Starts the IRCService
        startIRCService();


        // Making the fragments
        Fragment chatroom = Chatroom.newInstance(null,null);
        Fragment leftDrawer = LeftDrawer.newInstance(null,null);
        Fragment rightDrawer = RightDrawer.newInstance(null,null);
        //Fragment connectionSettingsPopup = ConnectionSettingsPopup.newInstance(null,null);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.Chatroom_container, chatroom);
        //ft.add(R.id.ConnectionSettingsPopup_container, connectionSettingsPopup);
        ft.add(R.id.LeftDrawer_container, leftDrawer);
        ft.add(R.id.RightDrawer_container, rightDrawer);

        //ft.hide(chatroom);
        ft.hide(leftDrawer);
        ft.hide(rightDrawer);
        //ft.hide(connectionSettingsPopup);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, IRCService.class);
        bindService(intent, ircServiceConnection, Context.BIND_AUTO_CREATE);
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

    public void startIRCService() {
        Intent intent = new Intent(this, IRCService.class);
        startService(intent);
        bindService(intent, ircServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopIRCService() {
        Intent intent = new Intent(this, IRCService.class);
        stopService(intent);
    }


    @Override
    // TODO
    public void onFragmentInteraction(Uri uri) {

    }





}
