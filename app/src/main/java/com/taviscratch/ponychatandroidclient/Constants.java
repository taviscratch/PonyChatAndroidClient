package com.taviscratch.ponychatandroidclient;


import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class Constants {

    // Intent filters
    public static final String MESSAGE_TO_SEND = "com.taviscratch.ponychatandroidclient.MESSAGE_TO_SEND";
    public static final String MESSAGE_RECEIVED = "com.taviscratch.ponychatandroidclient.MESSAGE_RECEIVED";
    public static final String NOTIFICATION = "com.taviscratch.ponychatandroidclient.NOTIFICATION";


    // The channel name fot the network lobby
    public static final String NETWORK_LOBBY = "Network Lobby";

    // Shared preferences stuff
    public static final class PreferenceConstants {
        public static final String PREFS_NAME = "pone";
        public static final String USERNAME = "USERNAME";
        public static final String HOSTNAME = "HOSTNAME";
        public static final String PORT = "PORT";
        public static final String DEFAULT_CHANNELS = "DEFAULT_CHANNELS";
        public static final String REALNAME = "REALNAME";
        public static final String MAX_MESSAGE_LOG_SIZE = "MAX_MESSAGE_LOG_SIZE";
        public static final String RANDOMIZE_USERNAME = "RANDOMIZE_USERNAME";
    }

    public static final class PreferenceDefaults {
        public static final String HOSTNAME = "irc.ponychat.net";
        public static final int PORT = 6667;
        public static final String REALNAME = "Ponychat Android Client";
        public static final int MAX_MESSAGE_LOG_SIZE = 100;
        public static final String[] DEFAULT_CHANNELS = {"#ponyville","#octaviaisbestpony"};
    }



    // Bundle keys for the message passing intent's extras
    public static final class IntentExtrasConstants {
        public static final String MESSAGE = "MESSAGE";
        public static final String CHANNEL = "CHANNEL";
        public static final String SENDER = "SENDER";
        public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
        public static final String MESSAGE_TARGET = "MESSAGE_TARGET";
    }

    public static final class MessageType {
        public static final String NORMAL = "NORMAL";
        public static final String ACTION = "ACTION";
    }

}
