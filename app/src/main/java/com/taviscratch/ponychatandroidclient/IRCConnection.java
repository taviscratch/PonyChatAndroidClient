package com.taviscratch.ponychatandroidclient;

import android.os.Handler;
import android.os.Message;

/**
 * Created by tavi on 8/22/15.
 */
public class IRCConnection implements Runnable{

    IRCCommunicator bot;

    private static String defaultHostname = "irc.ponychat.net";
    private static String defaultChannel = "#ponychat";


    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public IRCConnection() {}


    @Override
    public void run() {


        synchronized (this) {
            try {
                //setupIRCHandler();
            }
            catch (Exception e) {
                System.out.println(e.getStackTrace());
            }




            //while(true) {}


        }


    }

    public void setupIRCHandler() throws Exception {
        // Now start our bot up.
        bot = new IRCCommunicator();

        // Enable debugging output.
        bot.setVerbose(true);

        // Connect to the IRC server.
        bot.connect(defaultHostname);

        // Join the #pircbot channel.
        bot.joinChannel(defaultChannel);

        bot.sendMessage(defaultChannel, "test");
    }





}
