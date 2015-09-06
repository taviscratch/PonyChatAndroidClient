package com.taviscratch.ponychatandroidclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.jibble.pircbot.PircBot;

import java.util.Set;


public class IRCMessenger extends PircBot {


    private static String defaultRealname = "Ponychat Android Client";
    private static String currentChannel;

    BroadcastReceiver outgoingMessageReceiver;

    public IRCMessenger(String username) {
        this(username, defaultRealname);
    }

    public IRCMessenger(String username, String realname) {
        this.setName(username);
        this.setVersion(realname);

        registerReceiver();
    }

    private void registerReceiver() {
        // Setup and register the broadcast receiver
        outgoingMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString(Constants.IntentExtrasConstants.MESSAGE);
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
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.CHANNEL, channel);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.NORMAL);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.NORMAL);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, notice);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sourceNick);
        msgIntent.putExtra(Constants.IntentExtrasConstants.CHANNEL, Constants.NETWORK_LOBBY);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.NORMAL);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onServerResponse(int code, String response) {
        if(code == 372 || code == 375 || code == 376) {
            Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
            msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, response);
            msgIntent.putExtra(Constants.IntentExtrasConstants.CHANNEL, Constants.NETWORK_LOBBY);
            msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.NORMAL);
            LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
        }
    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, action);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.CHANNEL, Constants.NETWORK_LOBBY);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.ACTION);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
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




        else
            throw new Exception("Command not supported");
    }



    private void handleIncomingMessage(String message) {

    }

    private void handleOutgoingMessage(String message) {

    }

}
