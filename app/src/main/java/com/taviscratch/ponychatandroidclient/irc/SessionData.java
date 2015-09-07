package com.taviscratch.ponychatandroidclient.irc;

import com.taviscratch.ponychatandroidclient.utility.Constants;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;


public class SessionData {

    private int maxMessageLogSize;

    private Hashtable<String, MessageLog> conversations;

    public SessionData(int maxChannelSize) {
        this.maxMessageLogSize = maxChannelSize;
        conversations = new Hashtable<String, MessageLog>();
        conversations.put(Constants.NETWORK_LOBBY, new MessageLog(maxChannelSize));
    }

    // Standard data operations
    public MessageLog addConversation(String key) {
        return addConversation(key, new MessageLog(maxMessageLogSize));
    }
    public MessageLog addConversation(String key, MessageLog value) {
        return conversations.put(key, value);
    }
    public MessageLog removeConversation(String key) {
        return conversations.remove(key);
    }
    public MessageLog getConversation(String key){
        return conversations.get(key);
    }

    // Adding a message to a converstation
    public void postToConversation(String key, IRCMessage message) {
        if(conversations.containsKey(key)) // add the message to the existing conversation
            conversations.get(key).addMessage(message);
        else // Create a new conversation with the message added to it
            conversations.put(key, new MessageLog(maxMessageLogSize,message));

    }

    // Changes the maximum number of messages that are allowed in the MessageLogs
    public void changeMaxMessageLogSize(int newMax) {
        if(newMax < 1) throw new IllegalArgumentException("Max size must be at least 1");

        MessageLog[] messageLogs = new MessageLog[0];
        conversations.values().toArray(messageLogs);
        for (int i = 0; i < messageLogs.length; i++) {
            messageLogs[i].setSizeLimit(newMax);
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
