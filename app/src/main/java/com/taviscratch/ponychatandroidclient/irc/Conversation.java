package com.taviscratch.ponychatandroidclient.irc;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Conversation extends LinkedList<IRCMessage> {


    private int maxSize;

    public int getMaxSize() {
        return maxSize;
    }

    private ArrayList<String> users;



    private String name;
    public String getName() {
        return name;
    }

    private String topic = "";
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }



    // Creates a new empty message log
    public Conversation(String name, int maxSize) {
        this(name, maxSize, null);
    }

    // Creates a new message log with a single message in it
    public Conversation(String name, int maxSize, IRCMessage message) {
        this.name = name;
        this.maxSize = maxSize;
        this.users = new ArrayList<String>();
        if(message != null) addMessage(message);
    }

    public Conversation(String name, Conversation oldConversation) {
        this.name = name;
        this.maxSize = oldConversation.getMaxSize();
        this.users = oldConversation.getUsers();
        this.addAll(oldConversation);
    }


    // Posts the message to the list. If the list is already at capacity, then it removes
    // the last entry before it posts the new one.
    public void addMessage(IRCMessage message) {
        if(size() >= maxSize)
            removeLast();
        add(message);
    }

    // Changes the size limit. If the new limit is less than the current one, then it removes
    // the last X entries, where X is the difference between the two numbers.
    public void setSizeLimit(int newMaxSize) throws IllegalArgumentException {
        if(newMaxSize < 1) throw new IllegalArgumentException("new size limit must be >=1");
        if(newMaxSize < maxSize) {
            int X = maxSize-newMaxSize;
            for(int i=0;i<X;i++)
                removeLast();
        }
        maxSize = newMaxSize;
    }

    public void addUser(String username) {
        if(!userExists(username))
            users.add(username);
        else
            throw new IllegalArgumentException("user already exists");
    }

    public void addUsers(String[] usernames) {
        for(int i=0;i<usernames.length;i++) {
            addUser(usernames[i]);
        }
    }

    public void removeUser(String username) {
        if(userExists(username))
            remove(username);
        else
            throw new IllegalArgumentException(("user does not exist"));
    }

    public ArrayList<String> getUsers() {
        /*String[] userlist = new String[users.size()];
        userlist = users.toArray(userlist);
        return userlist;*/
        return users;
    }

    public boolean userExists(String username) {
        if(users.contains(username))
            return true;
        else
            return false;
    }

}
