package com.taviscratch.ponychatandroidclient.utility;


import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class Constants {

    public static boolean DEBUG = false;

    // Intent filters
    public static final String MESSAGE_TO_SEND = "com.taviscratch.ponychatandroidclient.MESSAGE_TO_SEND";
    public static final String MESSAGE_RECEIVED = "com.taviscratch.ponychatandroidclient.MESSAGE_RECEIVED";
    public static final String NOTIFICATION = "com.taviscratch.ponychatandroidclient.NOTIFICATION";
    public static final String CONNECT_TO_IRC_NETWORK = "com.taviscratch.ponychatandroidclient.CONNECT_TO_IRC_NETWORK";
    public static final String DISCONNECT_FROM_IRC_NETWORK = "com.taviscratch.ponychatandroidclient.DISCONNECT_FROM_IRC_NETWORK";
    public static final String IRC_CONNECTION_SUCCESS = "com.taviscratch.ponychatandroidclient.IRC_CONNECTION_SUCCESS";
    public static final String IRC_CONNECTION_FAILURE = "com.taviscratch.ponychatandroidclient.IRC_CONNECTION_FAILURE";
    public static final String JOINED_NEW_CONVERSATION = "com.taviscratch.ponychatandroidclient.JOINED_NEW_CONVERSATION";


    // The channel name fot the network lobby
    public static final String NETWORK_LOBBY = "Network Lobby";

    // Shared preferences stuff
    public static final class AppPreferenceConstants {
        public static final String PREFS_NAME = "pone";
        public static final String USERNAME = "USERNAME";
        public static final String PASSWORD = "PASSWORD";
        public static final String HOSTNAME = "HOSTNAME";
        public static final String PORT = "PORT";
        public static final String DEFAULT_CHANNELS = "DEFAULT_CHANNELS";
        public static final String REALNAME = "REALNAME";
        public static final String MAX_MESSAGE_LOG_SIZE = "MAX_MESSAGE_LOG_SIZE";
        public static final String ALWAYS_RANDOMIZE_USERNAME = "ALWAYS_RANDOMIZE_USERNAME";
        public static final String IS_FIRST_RUN = "IS_FIRST_RUN";
        public static final String NOTIFICATIONS_ENABLED = "NOTIFICATIONS_ENABLED";
        public static final String KEEP_IRC_SERVICE_RUNNING_IN_BACKGROUND = "KEEP_IRC_SERVICE_RUNNING_IN_BACKGROUND";
    }

    // Shared preference defaults
    public static final class PreferenceDefaults {
        public static final String HOSTNAME = "irc.ponychat.net";
        public static final int PORT = 6667;
        public static final String REALNAME = "Ponychat Android Client";
        public static final int MAX_MESSAGE_LOG_SIZE = 100;
        public static final String[] DEFAULT_CHANNELS = {"#ponyville","#yolo"};
        public static final boolean ALWAYS_RANDOMIZE_USERNAME = false;
        public static final boolean NOTIFICATIONS_ENABLED = true;
        public static final boolean KEEP_IRC_SERVICE_RUNNING_IN_BACKGROUND = true;
    }


    public static final class ThemeColorPreferenceConstants {
        public static final String PREFS_NAME = "theme colors";
        public static final String THEME_NAME = "THEME_NAME";
        public static final String BACKGROUND_PRIMARY = "BACKGROUND_PRIMARY";
        public static final String BACKGROUND_SECONDARY = "BACKGOURND_SECONDARY";
        public static final String ACCENT = "ACCENT";
        public static final String MENU_TITLE_1 = "MENU_TITLE_1";
        public static final String MENU_TITLE_2 = "MENU_TITLE_2";
        public static final String MENU_ITEM = "MENU_ITEM";
        public static final String CHAT_NAME = "CHAT_NAME";
        public static final String CHAT_MESSAGE = "CHAT_MESSAGE";
        public static final String CHAT_ACTION = "CHAT_ACTION";
        public static final String CHAT_EVENT = "CHAT_EVENT";
    }


    // Bundle keys for the message passing intent's extras
    public static final class IntentExtrasConstants {
        public static final String MESSAGE = "MESSAGE";
        public static final String SENDER = "SENDER";
        public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
        public static final String MESSAGE_TARGET = "MESSAGE_TARGET";
        public static final String CONVERSATION_NAME = "CONVERSATION_NAME";
    }

    public static final class MessageType {
        public static final String PRIVMSG = "PRIVMSG";
        public static final String ACTION = "ACTION";
        public static final String RAW_MESSAGE = "RAW_MESSAGE";
    }

}
