package com.taviscratch.ponychatandroidclient.irc;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.services.NotificationService;
import com.taviscratch.ponychatandroidclient.ui.Chatroom;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Util;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

// A singleton class for the IRC session
public class IRCSession extends Thread {


    SessionData sessionData;
    Hashtable<String, IRCMessageAdapter> messageAdapters;
    BroadcastReceiver incomingMessageReceiver, outgoingMessageReceiver;
    IRCMessenger messenger;


    private String username;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    private IRCSession() {
        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME, 0);

        // Get the max length for message logs
        int maxMessageLogSize = preferences.getInt(Constants.PreferenceConstants.MAX_MESSAGE_LOG_SIZE, -1);

        // Initialize the data structures
        sessionData = new SessionData(maxMessageLogSize);

        // Initialize the adapter hashtable
        messageAdapters = new Hashtable<String, IRCMessageAdapter>();

        MessageLog networkLobby = sessionData.getConversation(Constants.NETWORK_LOBBY);
        networkLobby.setTopic(Constants.NETWORK_LOBBY);

        IRCMessageAdapter lobbyAdapter = new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, networkLobby);

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
            createReceivers();
            connect();
        }
    }

    public void connect() {
        String password, hostname, realname;
        Set<String> defaultChannels;
        int port;

        SharedPreferences preferences = PonyChatApplication.getAppPreferences();

        // Get the preferences
        username = preferences.getString(Constants.PreferenceConstants.USERNAME, null);
        password = preferences.getString(Constants.PreferenceConstants.PASSWORD, null);
        hostname = preferences.getString(Constants.PreferenceConstants.HOSTNAME, null);
        defaultChannels = preferences.getStringSet(Constants.PreferenceConstants.DEFAULT_CHANNELS, null);
        port = preferences.getInt(Constants.PreferenceConstants.PORT, -1);
        realname = preferences.getString(Constants.PreferenceConstants.REALNAME, null);

        // Initialize the messenger
        messenger =  new IRCMessenger(username, realname);
        messenger.setVerbose(true);
        messenger.setAutoNickChange(false);


        // Attempt a connection to the host
        try {
            if(port==-1)
                messenger.connect(hostname, Constants.PreferenceDefaults.PORT, password);
            else
                messenger.connect(hostname,port,password);

        } catch (NickAlreadyInUseException e) {
            messenger.setNewName(Util.getRandomUsername());
            messenger.setAutoNickChange(true);

            try {
                if(port==-1)
                    messenger.connect(hostname, Constants.PreferenceDefaults.PORT, password);
                else
                    messenger.connect(hostname,port,password);

            } catch(Exception ex) {
                // TODO
            }

            // getting your name back
            messenger.sendMessage("nickserv","id " + username + " " + password);
            messenger.sendMessage("nickserv","regain " + username);

        } catch (IrcException e) {
            int x = 0;
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }

        // Join all the default channels
        Iterator<String> channelIterator = defaultChannels.iterator();
        while(channelIterator.hasNext()) {
            String channelName = channelIterator.next();
            messenger.joinChannel(channelName);
        }
    }





    private void handleIncomingMessage(Bundle bundle) {
        String channelName, sender, message, messageType, target;
        IRCMessage.MessageType type = IRCMessage.MessageType.PRIVMSG;

        sender = bundle.getString(Constants.IntentExtrasConstants.SENDER);
        message = bundle.getString(Constants.IntentExtrasConstants.MESSAGE);
        target = bundle.getString(Constants.IntentExtrasConstants.MESSAGE_TARGET);
        messageType = bundle.getString(Constants.IntentExtrasConstants.MESSAGE_TYPE);


        if(messageType.equals(Constants.MessageType.ACTION))
            type = IRCMessage.MessageType.ACTION;

        IRCMessage msg = new IRCMessage(sender, message, System.currentTimeMillis(), type);
        sessionData.postToConversation(target, msg);

        if(!messageAdapters.containsKey(target)) {
            MessageLog log = sessionData.getConversation(target);
            IRCMessageAdapter adapter = new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, log);
            messageAdapters.put(target, adapter);
        }
        messageAdapters.get(target).notifyDataSetChanged();

        // Notify the user
        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME,0);
        if(preferences.getBoolean(Constants.PreferenceConstants.NOTIFICATIONS_ENABLED,true)) {
            Intent notificationIntent = new Intent(Constants.NOTIFICATION);
            notificationIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
            notificationIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
            notificationIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, target);
            notificationIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE, messageType);
            LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(notificationIntent);
        }


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




    public void startNewPrivateConversation(String username) {
        addConversation(username);
    }

    public void joinedChannel(String channelName) {
        addConversation(channelName);
    }

    public void leftChannel(String channelName) {
        sessionData.removeConversation(channelName);
        messageAdapters.remove(channelName);
        if(channelName.equals(Chatroom.getCurrentConversation()))
            Chatroom.switchConversationInView(Constants.NETWORK_LOBBY);
    }



    private void addConversation(String target) {
        sessionData.addConversation(target);
        MessageLog log = sessionData.getConversation(target);
        IRCMessageAdapter adapter = new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, log);
        messageAdapters.put(target, adapter);
    }




    public void postOutgoingMessage(String target, IRCMessage message) {
        sessionData.postToConversation(target, message);
        messageAdapters.get(target).notifyDataSetChanged();
    }

    public String[] getUserList(String channelName) {
        User[] users = messenger.getUsers(channelName);
        String[] usernames = new String[users.length];

        for(int i = 0; i < users.length; i++)
            usernames[i] = users[i].getNick();

        return usernames;
    }

    public boolean doesConversationExist(String key) {
        return messageAdapters.containsKey(key);
    }

    // If channelName is infact a channel name, then it return's its topic.
    // Otherwise, it returns ChannelName
    public String getTopic(String channelName) {
        if (Util.isChannel(channelName))
            return sessionData.getConversation(channelName).getTopic();

        return channelName;
    }

    public void setChannelTopic(String channelName, String topic) {
        sessionData.getConversation(channelName).setTopic(topic);
    }





}
