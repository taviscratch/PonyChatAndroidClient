package com.taviscratch.ponychatandroidclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IRCService extends Service {
    public IRCService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
