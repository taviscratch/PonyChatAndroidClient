package com.taviscratch.ponychatandroidclient.irc;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.utility.Constants;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;


public class SessionData {

    private int maxMessageLogSize;

    private Hashtable<String, Conversation> conversations;

    public SessionData(int maxChannelSize) {
        this.maxMessageLogSize = maxChannelSize;
        conversations = new Hashtable<String, Conversation>();
        conversations.put(Constants.NETWORK_LOBBY, new Conversation(Constants.NETWORK_LOBBY, maxChannelSize));
    }

    // Standard data operations
    public Conversation addConversation(String key) {
        return addConversation(key, new Conversation(key, maxMessageLogSize));
    }
    public Conversation addConversation(String key, Conversation value) {
        return conversations.put(key, value);
    }
    public Conversation removeConversation(String key) {
        return conversations.remove(key);
    }
    public Conversation getConversation(String key){
        return conversations.get(key);
    }

    // Adding a message to a conversation
    public void postToConversation(String key, IRCMessage message) {
        if(conversations.containsKey(key)) // add the message to the existing conversation
            conversations.get(key).addMessage(message);
        else // Create a new conversation with the message added to it
            conversations.put(key, new Conversation(key, maxMessageLogSize, message));

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
            if(name.startsWith("#"))
                channelNames.add(name);
        }
        return channelNames.toArray(new String[channelNames.size()]);
    }

    public String[] getPrivateMessageNames() {
        PriorityQueue<String> privateMessageNames = new PriorityQueue<String>();

        Enumeration e = conversations.keys();
        while(e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if(!name.startsWith("#") && !name.equals(Constants.NETWORK_LOBBY))
                privateMessageNames.add(name);
        }
        return (String[]) privateMessageNames.toArray(new String[privateMessageNames.size()]);
    }

}
