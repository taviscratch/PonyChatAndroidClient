package com.taviscratch.ponychatandroidclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.jibble.pircbot.PircBot;


public class IRCMessenger extends PircBot {


    private static String defaultRealname = "Ponychat Android Client";
    private TypeOfMessage tag;
    BroadcastReceiver outgoingMessageReceiver;

    public IRCMessenger(String username) {
        this(username, defaultRealname);
    }

    public IRCMessenger(String username, String realname) {
        this.setName(username);
        this.setVersion(realname);

        registerReceiver();
    }

    private void registerReceiver() {
        // Setup and register the broadcast receiver
        outgoingMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString(Constants.IntentExtrasConstants.MESSAGE);
                String target = extras.getString(Constants.IntentExtrasConstants.MESSAGE_TARGET);
                handleOutgoingMessage(target,message);
            }
        };
        IntentFilter outgoingMessageFilter = new IntentFilter(Constants.MESSAGE_TO_SEND);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(outgoingMessageReceiver, outgoingMessageFilter);
    }



    @Override
    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, channel);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.PRIVMSG);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }



    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.PRIVMSG);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, notice);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sourceNick);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, Constants.NETWORK_LOBBY);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.PRIVMSG);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onServerResponse(int code, String response) {
        if(code == 372 || code == 375 || code == 376) {
            Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
            msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, response);
            msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, Constants.NETWORK_LOBBY);
            msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.PRIVMSG);
            LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
        }
    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, action);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, target);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE,Constants.MessageType.ACTION);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        IRCSession session = IRCSession.getInstance();
        if(sender.equals(session.getUsername()))
            session.joinedChannel(channel);
    }

    /**
     * This method is called whenever someone (possibly us) parts a channel
     * which we are on.
     * <p/>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel  The channel which somebody parted from.
     * @param sender   The nick of the user who parted from the channel.
     * @param login    The login of the user who parted from the channel.
     * @param hostname The hostname of the user who parted from the channel.
     */
    @Override
    protected void onPart(String channel, String sender, String login, String hostname) {
        IRCSession session = IRCSession.getInstance();
        if(sender.equals(session.getUsername()))
            session.leftChannel(channel);
    }

    private String parseTarget(String target) {
        if(target.equals(Constants.NETWORK_LOBBY))
            return "";

        return target;
    }

    private String parseMessage(String message) {

        if(message.charAt(0) == '/') {
            int firstSpacePosition = message.indexOf(" ");
            String command, payload;
            if(firstSpacePosition != -1) {

                command = message.substring(1, firstSpacePosition);
                payload = message.substring(firstSpacePosition);
            }
            else {
                command = message.substring(1);
                payload = "";
            }

            try {
                return parseCommand(command.toLowerCase(), payload);
            } catch(Exception e) {
                Toast.makeText(PonyChatApplication.getAppContext(), "Service Started", Toast.LENGTH_SHORT).show();
            }
        }
        tag = TypeOfMessage.PRIVMSG;
        return message;
    }

    private String parseCommand(String command, String message) throws Exception {


        if(command.equals("me")) {
            tag = TypeOfMessage.ACTION;
            return message;
        } else if(command.equals("join")) {
            tag = TypeOfMessage.JOIN;
            return message;
        } else if(command.equals("leave")) {
            tag = TypeOfMessage.PART;
            return message;
        } /*else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        }*/

        else
            throw new Exception("Command not supported");
    }



    private void handleIncomingMessage(String message) {

    }

    private void handleOutgoingMessage(String target, String message) {
        IRCSession session = IRCSession.getInstance();
        IRCMessage msg;

        target = parseTarget(target);
        message = parseMessage(message);

        switch(tag) {
            case PRIVMSG:
                sendMessage(target, message);
                msg = new IRCMessage(session.getUsername(),message,System.currentTimeMillis(), IRCMessage.MessageType.PRIVMSG);
                session.postOutgoingMessage(target,msg);
                break;
            case ACTION:
                sendAction(target, message);
                msg = new IRCMessage(session.getUsername(),message,System.currentTimeMillis(), IRCMessage.MessageType.ACTION);
                session.postOutgoingMessage(target,msg);
                break;
            case JOIN:
                joinChannel(message);
                break;
            case PART:
                partChannel(message);
                break;
        }

        tag = null;


    }


    enum TypeOfMessage {
        PRIVMSG,
        ACTION,
        JOIN,
        PART

    }


}
