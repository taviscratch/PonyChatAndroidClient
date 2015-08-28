package com.taviscratch.ponychatandroidclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.jibble.pircbot.PircBot;


public class IRCCommunicator extends PircBot {

    private String username;
    private static String hostname;
    private String currentChannel;

    BroadcastReceiver outgoingMessageReceiver;


    public IRCCommunicator(String hostname, String channel, String username) {
        this.hostname = hostname;
        this.currentChannel = channel;
        this.username = username;
        this.setName(username);
        this.setVersion("PonyChat Android Client");




        outgoingMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString("message");
                parseMessage(message);
            }
        };

        IntentFilter outgoingMessageFilter = new IntentFilter(Constants.MESSAGE_TO_SEND);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(outgoingMessageReceiver, outgoingMessageFilter);
    }

    @Override
    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra("message", message);
        msgIntent.putExtra("sender", sender);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
        //pushNotification(sender + ": " + message);
    }





    private void parseMessage(String message) {
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
            sendMessage(currentChannel, message);
        }
    }

    private void parseCommand(String command, String message) throws Exception {


        if(command.equals("me")) {
            sendMessage(currentChannel, "\001ACTION" + message + "\001");
        } /*else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        }*/




        throw new Exception("Command not supported");
    }



}
