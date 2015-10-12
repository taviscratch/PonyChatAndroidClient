package com.taviscratch.ponychatandroidclient.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.irc.Conversation;
import com.taviscratch.ponychatandroidclient.irc.IRCMessage;
import com.taviscratch.ponychatandroidclient.irc.IRCMessenger;
import com.taviscratch.ponychatandroidclient.irc.SessionData;
import com.taviscratch.ponychatandroidclient.ui.IRCMessageAdapter;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Util;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import android.os.Handler;

public class IRCBackgroundService extends Service {

    private IRCMessenger messenger;
    private SessionData sessionData;
//    private Hashtable<String, IRCMessageAdapter> messageAdapters;
    public static String currentUsername;

    // Binder
    private final IBinder ircServiceBinder = new IRCServiceBinder();
    public class IRCServiceBinder extends Binder{
        public IRCBackgroundService getService() {
            return IRCBackgroundService.this;
        }
    }


    final Handler mDataSetChangedHandler = new Handler();


    public IRCBackgroundService() {
        initCoreComponents();
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        /*IRCSession.getInstance().start();*/
        if(Constants.DEBUG) Toast.makeText(this, "Service Bound", Toast.LENGTH_SHORT).show();
        return ircServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(Constants.DEBUG) Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Constants.DEBUG) Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        // TODO start ircMessenger and connect
        Runnable r = new Runnable() {
            @Override
            public void run() {
                String password, hostname, username;
                Set<String> defaultChannels;
                int port;

                // TODO initialize sessionData inside new thread
                //initCoreComponents();


                SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.AppPreferenceConstants.PREFS_NAME,0);

                // Get the preferences
                username = currentUsername;
                password = preferences.getString(Constants.AppPreferenceConstants.PASSWORD, null);
                hostname = preferences.getString(Constants.AppPreferenceConstants.HOSTNAME, null);
                defaultChannels = preferences.getStringSet(Constants.AppPreferenceConstants.DEFAULT_CHANNELS, null);
                port = preferences.getInt(Constants.AppPreferenceConstants.PORT, -1);

                // Initialize the messenger
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
        };


        Thread ircThread = new Thread(r);
        ircThread.start();

        return START_STICKY;
    }




    @Override
    public void onDestroy() {
        stopForeground(true);
        if(Constants.DEBUG) Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    private void initCoreComponents() {
        // get shared preferences
        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.AppPreferenceConstants.PREFS_NAME, 0);
        // Get the max length for message logs
        int maxMessageLogSize = preferences.getInt(Constants.AppPreferenceConstants.MAX_MESSAGE_LOG_SIZE, -1);
        // Initialize the data structures
        sessionData = new SessionData(maxMessageLogSize);
        sessionData.setDataChangedHandler(mDataSetChangedHandler);

        // get the username
        currentUsername = preferences.getString(Constants.AppPreferenceConstants.USERNAME, null);

        String realname = preferences.getString(Constants.AppPreferenceConstants.REALNAME, null);

        // create the IRCMessenger, using the realname if it isn't null
        messenger = (realname!=null) ? new IRCMessenger(currentUsername,realname,sessionData) : new IRCMessenger(currentUsername, sessionData);

        // Instantiate the network lobby conversation, and add it's topic
        sessionData.addConversation(Constants.NETWORK_LOBBY);
        Conversation networkLobby = sessionData.getConversation(Constants.NETWORK_LOBBY);
        networkLobby.setTopic(Constants.NETWORK_LOBBY);
    }


    // Setup and register the broadcast receivers
    private void registerReceivers() {
        BroadcastReceiver connectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter connectFilter = new IntentFilter(Constants.CONNECT_TO_IRC_NETWORK);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(connectReceiver, connectFilter);

        BroadcastReceiver disconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter disconnectFilter = new IntentFilter(Constants.CONNECT_TO_IRC_NETWORK);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(disconnectReceiver, disconnectFilter);
    }


    public IRCMessageAdapter getIRCMessageAdapter(String conversationName) {
        return sessionData.getAdapter(conversationName);
    }


    public void addPrivateConversation(String conversationName){
        sessionData.addConversation(conversationName);
    }

    public void processUserInput(String rawMessage, String targetConversationName) {
        // TODO change to an actual call to the IRCMessenger
        Intent msgIntent = new Intent(Constants.MESSAGE_TO_SEND);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, rawMessage);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, targetConversationName);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
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
        // emit an intent letting the application know that a new conversation has been joined;
        emitJoinedConversationIntent(channelName);
    }







    public Conversation getConversationData(String conversationName) {
        return getConversation(conversationName);
    }
    public Conversation getPrivateMessageData(String sender) {
        return getConversation(sender);
    }
    public Conversation getNetworkLobbyData() {
        return getConversation(Constants.NETWORK_LOBBY);
    }
    public Conversation getConversation(String key) {
        return sessionData.getConversation(key);
    }





    public IRCMessageAdapter getMessageAdapter(String key) {
        if(sessionData.conversationExists(key))
            return sessionData.getAdapter(key);
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


    private void addConversation(String target) {
        // throw an exception if the conversation already exists
        if(doesConversationExist(target)) throw new IllegalArgumentException("conversation already exists");

        // add the conversation
        sessionData.addConversation(target);
        Conversation conversation = sessionData.getConversation(target);
    }

    public void emitJoinedConversationIntent(String target) {
        Intent msgIntent = new Intent(Constants.JOINED_NEW_CONVERSATION);
        msgIntent.putExtra(Constants.IntentExtrasConstants.CONVERSATION_NAME, target);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }
/*    public void emitLeftConversationIntent(String target) {
        Intent msgIntent = new Intent(Constants.JOINED_NEW_CONVERSATION);
        msgIntent.putExtra(Constants.IntentExtrasConstants.CONVERSATION_NAME, target);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }*/



    public void postOutgoingMessage(String target, IRCMessage message) {
        sessionData.putMessage(target, message);
        /*messageAdapters.get(target).notifyDataSetChanged();*/
    }

    public String[] getUserList(String channelName) {
        User[] users = messenger.getUsers(channelName);
        String[] usernames = new String[users.length];

        for(int i = 0; i < users.length; i++)
            usernames[i] = users[i].getNick();

        return usernames;
    }

    public boolean doesConversationExist(String key) {
        /*return messageAdapters.containsKey(key);*/
        return sessionData.conversationExists(key);
    }


}
