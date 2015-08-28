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
    private static String defaultChannel = "#octaviaisbestpony";
    private String currentChannel = defaultChannel;

    private boolean keepThreadAlive;
    BroadcastReceiver receiver;

    public IRCConnection() {
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

        //comm.sendMessage(currentChannel, "\001ACTION says hello to everypony.\001");
    }

    public void stopThread() {
        keepThreadAlive = false;
    }

/*    public void sendMessage(String message) {
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
                parseCommand(command.toLowerCase(), payload);
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


        if(command.equals("me")) {
            comm.sendMessage(currentChannel, "\001ACTION" + message + "\001");
        } *//*else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        }*//*




        throw new Exception("Command not supported");
    }*/

}
