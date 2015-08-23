package com.taviscratch.ponychatandroidclient;

import org.jibble.pircbot.PircBot;

/**
 * Created by tavi on 8/22/15.
 */
public class IRCCommunicator extends PircBot {

    public IRCCommunicator() {
        this.setName("PonyChatAndroidClient");
    }

    @Override
    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {

    }



}
