package com.taviscratch.ponychatandroidclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.jibble.pircbot.PircBot;
import com.taviscratch.ponychatandroidclient.IRCCommunicator;


public class IRCConnection extends Thread {

    IRCCommunicator comm;

    private static String defaultUsername = "PonyChatAndroid";
    private static String defaultHostname = "irc.ponychat.net";
    private static String defaultChannel = "#ponychatandroidclient";
    private String currentChannel = defaultChannel;

    private boolean keepThreadAlive;
    BroadcastReceiver receiver;

    public IRCConnection() {
        /*receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString("message");
                sendMessage(message);
            }
        };

        IntentFilter passMessageFilter = new IntentFilter(Constants.MESSAGE_TO_SEND);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(receiver, passMessageFilter);*/
    }


    @Override
    public void run() {
        synchronized (this) {
            keepThreadAlive = true;

            try {
                setupIRCConnection();
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }

            while (keepThreadAlive) {
                // TODO
            }
        }
    }

    public void setupIRCConnection() throws Exception {
        // Start the comm up.
        comm = new IRCCommunicator(defaultHostname, defaultChannel, defaultUsername);

        // Enable debugging output.
        comm.setVerbose(true);

        // Connect to the IRC server.
        comm.connect(defaultHostname);

        // Join the default channel.
        comm.joinChannel(defaultChannel);
    }

    public void stopThread() {
        keepThreadAlive = false;
    }

    public void sendMessage(String message) {
        if(message.charAt(0) == '/') {
            int firstSpacePosition = message.indexOf(" ");
            String command, payload;
            if(firstSpacePosition != -1) {

                command = message.substring(1, firstSpacePosition);
                payload = message.substring(firstSpacePosition);
            }
            else {
                command = message.substring(1);
                payload = "";
            }

            try {
                parseCommand(command, payload);
            } catch(Exception e) {
                System.out.println(e.toString());
            }
        } else {
            if(comm != null) {
                comm.sendMessage(currentChannel, message);
            }
        }
    }

    private void parseCommand(String command, String message) throws Exception {
        throw new Exception("Command not supported");
    }

}
