package com.taviscratch.ponychatandroidclient;


import java.util.Collection;
import java.util.LinkedList;

public class MessageLog extends LinkedList<IRCMessage> {


    private int maxSize;

    // Creates a new empty message log
    public MessageLog(int maxSize) {
        this(maxSize, null);
    }

    // Creates a new message log with a single message in it
    public MessageLog(int maxSize, IRCMessage message) {
        if(message != null) addMessage(message);
        this.maxSize = maxSize;
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


/*    @Override
    public int size() {
        return super.size()-1;
    }*/
}
