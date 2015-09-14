package com.taviscratch.ponychatandroidclient.irc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.utility.Constants;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import com.taviscratch.ponychatandroidclient.utility.Constants.PreferenceConstants;
import com.taviscratch.ponychatandroidclient.utility.Util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class IRCMessenger extends PircBot {

    BroadcastReceiver outgoingMessageReceiver, connectReceiver, disconnectReceiver;

    public IRCMessenger(String username) {
        this(username, Constants.PreferenceDefaults.REALNAME);
    }

    public IRCMessenger(String username, String realname) {
        this.setName(username);
        this.setVersion(realname);

        registerReceivers();
    }


    @Override
    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, channel);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE, Constants.MessageType.PRIVMSG);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }



    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        IRCSession.getInstance().setUsername(newNick);
    }


    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, sender);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE, Constants.MessageType.PRIVMSG);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).sendBroadcast(msgIntent);
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        Intent msgIntent = new Intent(Constants.MESSAGE_RECEIVED);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, notice);
        msgIntent.putExtra(Constants.IntentExtrasConstants.SENDER, sourceNick);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, Constants.NETWORK_LOBBY);
        msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TYPE, Constants.MessageType.PRIVMSG);
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
        } else if(code == 437) {
            // 437 = "Nick/channel is temporarily unavailable"
            setName(getName()+"_");
            try {
                connect(getServer(),getPort(),getPassword());
            } catch(Exception e) {

            }
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
     * This method is called whenever a user sets the topic, or when
     * PircBot joins a new channel and discovers its topic.
     * <p/>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel The channel that the topic belongs to.
     * @param topic   The topic for the channel.
     * @param setBy   The nick of the user that set the topic.
     * @param date    When the topic was set (milliseconds since the epoch).
     * @param changed True if the topic has just been changed, false if
     */
    @Override
    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        IRCSession.getInstance().setChannelTopic(channel, topic);
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



    // Setup and register the broadcast receiver
    private void registerReceivers() {
        // Setup and register the broadcast receiver
        outgoingMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command, target, message;
                command = null;

                Bundle extras = intent.getExtras();
                message = extras.getString(Constants.IntentExtrasConstants.MESSAGE);
                target = extras.getString(Constants.IntentExtrasConstants.MESSAGE_TARGET);


                if(message.startsWith("/")) {
                    String[] messageParts = parseCommandMessage(message);
                    command = messageParts[0];
                    if(messageParts[1]!=null)
                        target = messageParts[1];
                    message = messageParts[2];
                } else {
                    command = "/msg";
                }

                // special case for /connect
                if(command.equals("/connect")) {
                    try{
                        disconnect();
                        connect(getServer(),getPort(),getPassword());
                    } catch(Exception e) {

                    }
                } else
                    handleOutgoingMessage(command, target, message);
            }
        };
        IntentFilter outgoingMessageFilter = new IntentFilter(Constants.MESSAGE_TO_SEND);
        LocalBroadcastManager.getInstance(PonyChatApplication.getAppContext()).registerReceiver(outgoingMessageReceiver, outgoingMessageFilter);
    }


    private String[] parseCommandMessage(String message) {
        String command, target, messagePayload;
        command = target = null;


        StringTokenizer tokenizer = new StringTokenizer(message);
        command = tokenizer.nextToken().toLowerCase();
        if(command.equals("/msg")) {
            target = tokenizer.nextToken();
        } else if(command.equals("/nick")) {
            target = null;
        } else if(command.equals("/me")) {
            target = null;
        } else if(command.equals("/join")) {
            target = null;
        } else if(command.equals("/part")) {
            target = null;
        }

        messagePayload = "";
        while(tokenizer.hasMoreTokens()) messagePayload += " " + tokenizer.nextToken();

        return new String[] {command,target,messagePayload};
    }

    private TypeOfMessage parseCommand(String command) throws Exception {

        if(command.equals("/me")) {
            return TypeOfMessage.ACTION;
        } else if(command.equals("/join")) {
            return TypeOfMessage.JOIN;
        } else if(command.equals("/leave")) {
            return TypeOfMessage.PART;
        } else if(command.equals("/nick")) {
            return TypeOfMessage.NICK;
        } else if(command.equals("/msg")) {
            return TypeOfMessage.PRIVMSG;
        } /*else if(command.equals()) {

        } else if(command.equals()) {

        } else if(command.equals()) {

        }*/

        else throw new Exception("Command not supported");
    }
    private String parseTarget(String target) {
        if(target.equals(Constants.NETWORK_LOBBY))
            return "";

        return target;
    }




    private void handleIncomingMessage(String message) {

    }

    private void handleOutgoingMessage(String command, String target, String message) {
        IRCSession session = IRCSession.getInstance();
        IRCMessage msg;

        TypeOfMessage type = null;

        try {
            type = parseCommand(command);
        } catch(Exception e) {
            System.err.println(e.getStackTrace());
            return;
        }


        switch(type) {
            case PRIVMSG:
                sendMessage(target, message);
                if(target.toLowerCase().equals("nickserv")) target = Constants.NETWORK_LOBBY;
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
            case NICK:
                changeNick(message);
                break;
        }
    }


    enum TypeOfMessage {
        PRIVMSG,
        ACTION,
        JOIN,
        PART,
        NICK
    }

    public void setNewName(String name) {
        setName(name);
    }

}
