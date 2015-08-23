package com.taviscratch.ponychatandroidclient;


import org.jibble.pircbot.PircBot;

public class IRCCommunicator extends PircBot {

    private static String defaultUsername = "PonyChatAndroidClient";


    public IRCCommunicator() {
        this.setName(defaultUsername);
    }

    @Override
    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {

    }
}
