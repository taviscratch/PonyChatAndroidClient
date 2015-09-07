package com.taviscratch.ponychatandroidclient;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

// A singleton class for the IRC session
public class IRCSession extends Thread {


    SessionData sessionData;
    Hashtable<String, IRCMessageAdapter> messageAdapters;
    BroadcastReceiver incomingMessageReceiver, outgoingMessageReceiver;



    private String username;
    private boolean keepRunning;

    public String getUsername() {
        return username;
    }


    private IRCSession() {
        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME, 0);

        // Get the max length for message logs
        int maxMessageLogSize = preferences.getInt(Constants.PreferenceConstants.MAX_MESSAGE_LOG_SIZE, -1);

        // Initialize the data structures
        sessionData = new SessionData(maxMessageLogSize);

        // Initialize the adapter hashtable
        messageAdapters = new Hashtable<String, IRCMessageAdapter>();

        IRCMessageAdapter lobbyAdapter = new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, sessionData.getConversation(Constants.NETWORK_LOBBY));

        messageAdapters.put(Constants.NETWORK_LOBBY, lobbyAdapter);
    }

    // A holder class for the static reference of the only instance of IRCSession
    private static class IRCSessionHolder {
        public static final IRCSession instance = new IRCSession();
    }

    public static IRCSession getInstance() {
        return IRCSessionHolder.instance;
    }

    @Override
    public void run() {
        synchronized (this){
            keepRunning = true;

            createReceivers();
            connect();

/*            while(keepRunning) {
                // TODO
            }*/
        }


    }

    public void connect() {
        String hostname, realname;
        Set<String> defaultChannels;
        int port;


        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME, 0);

        // Get the preferences
        username = preferences.getString(Constants.PreferenceConstants.USERNAME, null);
        hostname = preferences.getString(Constants.PreferenceConstants.HOSTNAME, null);
        defaultChannels = preferences.getStringSet(Constants.PreferenceConstants.DEFAULT_CHANNELS, null);
        port = preferences.getInt(Constants.PreferenceConstants.PORT, -1);
        realname = preferences.getString(Constants.PreferenceConstants.REALNAME, null);


        // Initialize the messenger
        IRCMessenger messenger =  new IRCMessenger(username, realname);
        messenger.setVerbose(true);

        // Attempt a connection to the host
        try {
            if(port == -1)
                messenger.connect(hostname);
            else
                messenger.connect(hostname,port);
        } catch(Exception e) {
            System.out.println(e);
        }


        // Join all the default channels
        Iterator<String> channelIterator = defaultChannels.iterator();
        while(channelIterator.hasNext()) {
            // Add the channel's data structure
            String channelName = channelIterator.next();
            sessionData.addConversation(channelName);

            // Create and add the adapter for the new channel's data
            IRCMessageAdapter newAdapter = new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, sessionData.getConversation(channelName));
            messageAdapters.put(channelName, newAdapter);

            // Join the channel
            messenger.joinChannel(channelName);
        }
    }



    public void stopThread() {
        synchronized (this) {
            keepRunning = false;
        }
    }


    private void handleIncomingMessage(Bundle bundle) {
        String channelName, sender, message, messageType;
        IRCMessage.MessageType type = IRCMessage.MessageType.NORMAL;
        String key = null;

        channelName = bundle.getString(Constants.IntentExtrasConstants.CHANNEL);
        sender = bundle.getString(Constants.IntentExtrasConstants.SENDER);
        message = bundle.getString(Constants.IntentExtrasConstants.MESSAGE);
        messageType = bundle.getString(Constants.IntentExtrasConstants.MESSAGE_TYPE);


        if(messageType.equals(Constants.MessageType.ACTION))
            type = IRCMessage.MessageType.ACTION;


        IRCMessage msg = new IRCMessage(sender, message, System.currentTimeMillis(), type);

        if(channelName!=null) {
            sessionData.postToConversation(channelName,msg);
            key = channelName;
        }

        else {
            sessionData.postToConversation(sender, msg);
            key = sender;
        }

        messageAdapters.get(key).notifyDataSetChanged();
    }

    private void handleOutgoingMessage(Bundle bundle) {

    }


    // Instantiates all the receivers for this object
    private void createReceivers() {
        // A receiver for messages sent from the server
        incomingMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleIncomingMessage(intent.getExtras());
            }
        };
        IntentFilter serverMessageFilter = new IntentFilter(Constants.MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(incomingMessageReceiver, serverMessageFilter);

        // A receiver for messages made by the current user
        outgoingMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleOutgoingMessage(intent.getExtras());
            }
        };
        IntentFilter myMessageFilter = new IntentFilter(Constants.MESSAGE_TO_SEND);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(outgoingMessageReceiver, myMessageFilter);
    }


    public MessageLog getChannelData(String channelName) {
        return getMessageLog(channelName);
    }
    public MessageLog getPrivateMessageData(String sender) {
        return getMessageLog(sender);
    }
    public MessageLog getNetworkLobbyData() {
        return getMessageLog(Constants.NETWORK_LOBBY);
    }
    private MessageLog getMessageLog(String key) {
        return sessionData.getConversation(key);
    }


    public IRCMessageAdapter getMessageAdapter(String key) {
        if(messageAdapters.containsKey(key))
            return messageAdapters.get(key);
        else if(sessionData.conversationExists(key)) {
            MessageLog log = sessionData.getConversation(key);
            return messageAdapters.put(key ,new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, log));
        }

        else
            throw new IllegalArgumentException("key does not exist");
    }

    public String[] getChannelNames() {
        return sessionData.getChannelNames();
    }

    public String[] getPrivateMessageNames() {
        return sessionData.getPrivateMessageNames();
    }

}
