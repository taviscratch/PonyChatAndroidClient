package com.taviscratch.ponychatandroidclient;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ChannelData {

    String channelName;
    LinkedList<IRCMessage> messageLog;
    int sizeLimit;





    // Constructor
    public ChannelData(String channelName, int sizeLimit) {
        this.channelName = channelName;
        this.sizeLimit = sizeLimit;
        this.messageLog = new LinkedList<IRCMessage>();
    }

    // Posts the message to the list. If the list is already at capacity, then it removes
    // the last entry before it posts the new one.
    public void postMessage(IRCMessage message) {
        if(messageLog.size() >= sizeLimit)
            messageLog.removeLast();
        messageLog.add(message);
    }

    // Changes the size limit. If the new limit is less than the current one, then it removes
    // the last X entries, where X is the difference between the two numbers.
    public void setSizeLimit(int newSizeLimit) throws IllegalArgumentException {
        if(newSizeLimit < 1) throw new IllegalArgumentException("new size limit must be >=1");
        if(newSizeLimit < sizeLimit) {
            int X = sizeLimit-newSizeLimit;
            for(int i=0;i<X;i++)
                messageLog.removeLast();
        }
        sizeLimit = newSizeLimit;
    }


}
