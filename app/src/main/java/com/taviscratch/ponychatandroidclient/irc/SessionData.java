package com.taviscratch.ponychatandroidclient.irc;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.ui.IRCMessageAdapter;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;
//import java.util.logging.Handler;
import android.os.Handler;


public class SessionData {

    private int maxMessageLogSize;

    private Hashtable<String, Conversation> conversations;
    private Hashtable<String, IRCMessageAdapter> messageAdapters;

    Handler mDataSetChangedHandler;

    public SessionData(int maxChannelSize) {
        this.maxMessageLogSize = maxChannelSize;

        // Initialize the adapter hashtable
        messageAdapters = new Hashtable<String, IRCMessageAdapter>();

        conversations = new Hashtable<String, Conversation>();

    }




    // Standard data operations
    public Conversation addConversation(String key) {
        return addConversation(key, new Conversation(key, maxMessageLogSize));
    }
    public Conversation addConversation(String key, Conversation value) {
        if(!conversationExists(key)) {
            conversations.put(key, value);
            IRCMessageAdapter adapter = new IRCMessageAdapter(PonyChatApplication.getAppContext(), R.layout.irc_message_action, value);
            messageAdapters.put(key, adapter);
        } else
            throw new IllegalArgumentException("conversation already exists");
        return value;
    }


    public Conversation removeConversation(String key) {
        messageAdapters.remove(key);
        return conversations.remove(key);
    }


    public Conversation getConversation(String key){
        if(conversationExists(key))
            return conversations.get(key);
        else
            throw new IllegalArgumentException("conversation does not exist");
    }

    public String[] getAllConversationNames() {
        String[] names = new String[conversations.size()];

        Enumeration<String> keys = conversations.keys();

        for(int i = 0; keys.hasMoreElements(); i++)
            names[i] = keys.nextElement();

        return names;
    }


    // Adding a message to a conversation, creating the conversation if it doesn't exist.
    public void putMessage(final String conversationName, IRCMessage message) {
        if(conversations.containsKey(conversationName)) { // add the message to the existing conversation
            conversations.get(conversationName).addMessage(message);
        }
        else {
            Conversation conversation = new Conversation(conversationName, maxMessageLogSize, message);
            addConversation(conversationName,conversation);
        }

        // use handler to update data set in UI thread
        final Runnable notifyDataSetChanged = new Runnable() {
            @Override
            public void run() {
                messageAdapters.get(conversationName).notifyDataSetChanged();
            }
        };
        mDataSetChangedHandler.post(notifyDataSetChanged);

    }


    public void setDataChangedHandler(Handler handler) {
        mDataSetChangedHandler = handler;
    }


    // Changes the maximum number of messages that are allowed in the MessageLogs
    public void changeMaxMessageLogSize(int newMax) {
        if(newMax < 1) throw new IllegalArgumentException("Max size must be at least 1");

        Conversation[] conversations = new Conversation[0];
        this.conversations.values().toArray(conversations);
        for (int i = 0; i < conversations.length; i++) {
            conversations[i].setSizeLimit(newMax);
        }
    }


    public boolean conversationExists(String key){
        return conversations.containsKey(key);
    }


    public String[] getChannelNames() {
        PriorityQueue<String> channelNames = new PriorityQueue<String>();

        Enumeration e = conversations.keys();
        while(e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if(Util.isChannel(name))
                channelNames.add(name);;
        }


        String[] names = new String[channelNames.size()];
        names = channelNames.toArray(names);


        return names;
    }

    public String[] getPrivateMessageNames() {
        PriorityQueue<String> privateMessageNames = new PriorityQueue<String>();

        Enumeration e = conversations.keys();
        while(e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if(!Util.isChannel(name) && !name.equals(Constants.NETWORK_LOBBY))
                privateMessageNames.add(name);
        }

        String[] names = new String[privateMessageNames.size()];
        names = privateMessageNames.toArray(names);


        return names;
    }


    // username queries and list operations
    public String[] getUserList(String conversationName) {
        Conversation conversation = getConversation(conversationName);
        ArrayList<String> userArrayList = conversation.getUsers();
        String[] userlist = new String[userArrayList.size()];
        userlist = userArrayList.toArray(userlist);
        return userlist;
    }
    public void addUserTo(String conversationName, String username) {
        Conversation conversation = getConversation(conversationName);
        conversation.addUser(username);
    }
    public void removeUserFrom(String conversationName, String username) {
        Conversation conversation = getConversation(conversationName);
        conversation.removeUser(username);
    }
    public boolean isUserIn(String conversationName, String username) {
        Conversation conv = conversations.get(conversationName);
        if(conv.userExists(username))
            return true;
        else
            return false;
    }



    public IRCMessageAdapter getAdapter(String conversationName) {
        if(messageAdapters.containsKey(conversationName))
            return messageAdapters.get(conversationName);
        else
            throw new IllegalArgumentException("conversation adapter does not exist");
    }

    public void setConversationTopic(String convsationName, String topic) {
        Conversation conversation = getConversation(convsationName);
        conversation.setTopic(topic);
    }

}
