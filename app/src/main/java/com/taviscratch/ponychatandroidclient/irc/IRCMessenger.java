package com.taviscratch.ponychatandroidclient.irc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.services.IRCBackgroundService;
import com.taviscratch.ponychatandroidclient.utility.Constants;

import org.jibble.pircbot.PircBot;

import java.util.StringTokenizer;

public class IRCMessenger extends PircBot {

    BroadcastReceiver outgoingMessageReceiver, connectReceiver, disconnectReceiver;
    private SessionData sessionData;




    public IRCMessenger(String username, SessionData sessionData) {
        this(username, Constants.PreferenceDefaults.REALNAME, sessionData);
    }
    public IRCMessenger(String username, String realname, SessionData sessionData) {
        this.setName(username);
        this.setVersion(realname);

        this.sessionData = sessionData;

        registerReceivers();
    }




    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        IRCMessage msg = new IRCMessage(sender, message, System.currentTimeMillis(), IRCMessage.MessageType.PRIVMSG);
        sessionData.putMessage(channel, msg);
    }


    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        String message = null;

        if(oldNick.equals(IRCBackgroundService.currentUsername))
            IRCBackgroundService.currentUsername = newNick;
        message = oldNick + " is now known as " + newNick;


        IRCMessage msg = new IRCMessage(oldNick, message, System.currentTimeMillis(), IRCMessage.MessageType.EVENT);

        // get the names for every conversation
        String[] names = sessionData.getAllConversationNames();

        // check each conversation for the username and update appropriately
        for(int i=0;i<names.length;i++) {
            String conversationName = names[i];
            if(sessionData.isUserIn(conversationName, oldNick)) {
                sessionData.removeUserFrom(conversationName, oldNick);
                sessionData.addUserTo(conversationName,newNick);
                sessionData.putMessage(conversationName, msg);
            }
        }

        // check for existing conversations that are with the user who's nick is being changed
        if(sessionData.conversationExists(oldNick)) {
            // create a new conversation with the new nick, but based on the old nick's conversation
            Conversation privConv = new Conversation(newNick,sessionData.getConversation(oldNick));
            sessionData.removeConversation(oldNick);
            sessionData.addConversation(newNick, privConv);
        }

    }


    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        IRCMessage msg = new IRCMessage(sender, message, System.currentTimeMillis(), IRCMessage.MessageType.PRIVMSG);
        sessionData.putMessage(sender, msg);
    }


    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        IRCMessage msg = new IRCMessage(sourceNick, notice, System.currentTimeMillis(), IRCMessage.MessageType.PRIVMSG);
        sessionData.putMessage(Constants.NETWORK_LOBBY, msg);
        // TODO add support for properly dealing with notices from users and channels
    }


    @Override
    protected void onServerResponse(int code, String response) {
        if(code == 372 || code == 375 || code == 376) {
            IRCMessage msg = new IRCMessage(Constants.NETWORK_LOBBY, response, System.currentTimeMillis(), IRCMessage.MessageType.PRIVMSG);
            sessionData.putMessage(Constants.NETWORK_LOBBY, msg);

        } else if(code == 437) {
            // 437 = "Nick/channel is temporarily unavailable"
            setName(getName()+"_");
            try {
                connect(getServer(),getPort(),getPassword());
            } catch(Exception e) {
                // TODO
            }
        }
    }


    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        IRCMessage msg = new IRCMessage(sender, action, System.currentTimeMillis(), IRCMessage.MessageType.ACTION);
        sessionData.putMessage(target, msg);
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        String message = null;

        // set the message
        if(sender.equals(IRCBackgroundService.currentUsername))
            message = "You have joined the channel";
        else
            message = sender + " has joined the channel";

        // add the IRCMessage to the correct conversation
        IRCMessage msg = new IRCMessage(sender, message, System.currentTimeMillis(), IRCMessage.MessageType.EVENT);
        sessionData.putMessage(channel, msg);
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
        sessionData.setConversationTopic(channel, topic);
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
        String message = null;

        // set the message
        if(sender.equals(IRCBackgroundService.currentUsername))
            message = "You have left the channel";
        else
            message = sender + " has left the channel";


        // add the IRCMessage to the correct conversation
        IRCMessage msg = new IRCMessage(sender, message, System.currentTimeMillis(), IRCMessage.MessageType.EVENT);
        sessionData.putMessage(channel, msg);

        // if the person leaving the channel is the user
        if(sender.equals(IRCBackgroundService.currentUsername)) {
            // then remove that conversation
            /*sessionData.removeConversation(channel);*/
        }


    }

    /**
     * This method is called whenever someone (possibly us) is kicked from
     * any of the channels that we are in.
     * <p/>
     * The implementation of this method in the PircBot abstract class
     * performs no actions and may be overridden as required.
     *
     * @param channel        The channel from which the recipient was kicked.
     * @param kickerNick     The nick of the user who performed the kick.
     * @param kickerLogin    The login of the user who performed the kick.
     * @param kickerHostname The hostname of the user who performed the kick.
     * @param recipientNick  The unfortunate recipient of the kick.
     * @param reason         The reason given by the user who performed the kick.
     */
    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        String message = null;

        // set the message
        if(recipientNick.equals(IRCBackgroundService.currentUsername))
            message = "You have been kicked from the channel";
        else
            message = recipientNick + " has been kicked from the channel";

        // if the person that is getting kicked is the user
        if(recipientNick.equals(IRCBackgroundService.currentUsername)) {
            // then remove that conversation
            /*sessionData.removeConversation(channel);*/
        }
        else { // add the IRCMessage to the correct conversation
            IRCMessage msg = new IRCMessage(recipientNick, message, System.currentTimeMillis(), IRCMessage.MessageType.EVENT);
            sessionData.putMessage(channel, msg);
        }
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

    private TypeOfMessage parseCommand(String command) throws IllegalArgumentException {

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

        else throw new IllegalArgumentException("Command not supported");
    }


    private String parseTarget(String target) {
        if(target.equals(Constants.NETWORK_LOBBY))
            return "";

        return target;
    }




    private void handleIncomingMessage(String message) {

    }

    private void handleOutgoingMessage(String command, String target, String message) {
        IRCMessage msg;

        TypeOfMessage type = null;
        String currentUsername = IRCBackgroundService.currentUsername;

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
                msg = new IRCMessage(currentUsername,message,System.currentTimeMillis(), IRCMessage.MessageType.PRIVMSG);
                sessionData.putMessage(target, msg);
                break;
            case ACTION:
                sendAction(target, message);
                msg = new IRCMessage(currentUsername,message,System.currentTimeMillis(), IRCMessage.MessageType.ACTION);
                sessionData.putMessage(target, msg);
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
