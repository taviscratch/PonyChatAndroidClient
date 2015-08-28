package com.taviscratch.ponychatandroidclient;

import java.util.Date;

//  A simple data structure for storing IRC messages.
public class IRCMessage {

    private String user;
    private String message;
    private String channelPostedOn;
    private Date timePosted;

    public IRCMessage(String user, String message, String channelPostedOn, Date timePosted) {
        this.user = user;
        this.message = message;
        this.channelPostedOn = channelPostedOn;
        this.timePosted = timePosted;
    }
}
