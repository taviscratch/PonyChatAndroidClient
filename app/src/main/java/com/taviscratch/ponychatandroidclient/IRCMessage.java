package com.taviscratch.ponychatandroidclient;

import java.text.DateFormat;
import java.util.Date;

//  A simple data structure for storing IRC messages.
public class IRCMessage {

    private String sender;
    private String message;
    private long timePosted;
    private String formattedTime;
    private MessageType type;


    public IRCMessage(String sender, String message, long timePosted, MessageType type) {
        this.sender = sender;
        this.message = message;
        this.timePosted = timePosted;
        this.type = type;
    }



    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimePosted() {
        return timePosted;
    }

    public String getFormattedTime() {
        if(formattedTime==null) {
            Date date = new Date(timePosted);
            formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        }
        return formattedTime;
    }




    public enum MessageType {
        PRIVMSG,
        ACTION
    }
}
