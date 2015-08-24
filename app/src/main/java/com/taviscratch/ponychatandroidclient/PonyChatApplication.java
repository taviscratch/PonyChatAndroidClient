package com.taviscratch.ponychatandroidclient;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;


public class PonyChatApplication extends Application{


    private static Context context;


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


        Intent serviceIntent = new Intent(this, IRCService.class);
        startService(serviceIntent);



    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }


    public static Context getAppContext() {
        return context;
    }


}
