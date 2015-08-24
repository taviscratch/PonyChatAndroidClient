package com.taviscratch.ponychatandroidclient;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import org.jibble.pircbot.PircBot;
import com.taviscratch.ponychatandroidclient.IRCService.IRCCommunicator;

/**
 * Created by tavi on 8/22/15.
 */
public class IRCConnection extends Thread {

    IRCCommunicator comm;

    private static String defaultHostname = "irc.ponychat.net";
    private static String defaultChannel = "#ponychatandroidclient";
    private String currentChannel = defaultChannel;

    private boolean keepThreadAlive;

    public IRCConnection(IRCCommunicator comm) {
        this.comm = comm;
    }


    @Override
    public void run() {
        synchronized (this) {
            keepThreadAlive = true;

            try {
                setupIRCConnection();
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }

            while (keepThreadAlive) {
                // TODO
            }
        }
    }

    public void setupIRCConnection() throws Exception {
        // Now start our comm up.
        //comm = new IRCCommunicator();

        // Enable debugging output.
        comm.setVerbose(true);

        // Connect to the IRC server.
        comm.connect(defaultHostname);

        // Join the #pircbot channel.
        comm.joinChannel(defaultChannel);
    }

    public void stopThread() {
        keepThreadAlive = false;
    }

    public void sendMessage(String message) {
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
                parseCommand(command, payload);
            } catch(Exception e) {
                System.out.println(e.toString());
            }
        } else {
            if(comm != null) {
                comm.sendMessage(currentChannel, message);
            }
        }
    }

    private void parseCommand(String command, String message) throws Exception {
        throw new Exception("Command not supported");
    }

}
