package com.taviscratch.ponychatandroidclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.irc.Conversation;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.services.IRCBackgroundService;
import com.taviscratch.ponychatandroidclient.irc.IRCSession;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Util;


public class Chatroom extends Fragment {

    IRCBackgroundService ircService;


    EditText inputBox;
    Button sendMessageButton;
    static ListView listView;
    static TextView topicMarquee;
    IRCMessageAdapter messageAdapter;

    private OnFragmentInteractionListener mListener;

    float xStart,yStart,xEnd,yEnd;

    private static String currentConversation;


    public Chatroom() {
        // Required empty public constructor
    }


    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //createReceivers();

        // Invalidate touch coordinates
        xStart = yStart = xEnd = yEnd = -1.0f;

        currentConversation = Constants.NETWORK_LOBBY;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_chatroom, container, false);

        IRCSession session = IRCSession.getInstance();

        // Create widgets
        inputBox = (EditText) theview.findViewById(R.id.chatInput);
        sendMessageButton = (Button) theview.findViewById(R.id.sendMessage_button);
        listView = (ListView) theview.findViewById(R.id.chatroomListView);
        topicMarquee = (TextView) theview.findViewById(R.id.topicMarquee);

        // Set up the listview array adapter
        messageAdapter = session.getMessageAdapter(currentConversation);
        listView.setAdapter(messageAdapter);


        // Set button's onClick
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputBox.getText().toString();
                if (!message.isEmpty()) {
                    inputBox.setText("");
                    Intent msgIntent = new Intent(Constants.MESSAGE_TO_SEND);
                    msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE, message);
                    msgIntent.putExtra(Constants.IntentExtrasConstants.MESSAGE_TARGET, currentConversation);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
                }
            }
        });


        // touch listener
        theview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return true;
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });

        topicMarquee.setSelected(true);
        topicMarquee.setText(session.getTopic(currentConversation));

        applyTheme();

        return theview;

    }

    private void applyTheme() {
        int backgroundPrimary, backgroundSecondary, accent,
                menuTitle1, menuTitle2, menuItem,
                chatName, chatMessage, chatAction, chatEvent;

        // get the theme preferences
        SharedPreferences themePreferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.ThemeColorPreferenceConstants.PREFS_NAME,0);

        // get the hex color codes
        backgroundPrimary = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_PRIMARY, -1);
        menuItem = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.MENU_ITEM, -1);
        chatName = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_NAME, -1);
        chatMessage = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_MESSAGE, -1);
        chatAction = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_ACTION, -1);
        chatEvent = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_EVENT, -1);

        // check for invalid values
        if(backgroundPrimary==-1 || menuItem==-1 || chatName==-1 || chatMessage==-1 || chatAction==-1 || chatEvent==-1)
            throw new IllegalArgumentException("error in retrieving theme preferences");

        // apply the colors
        topicMarquee.setBackgroundColor(backgroundPrimary);
        topicMarquee.setTextColor(menuItem);
        messageAdapter.setChatNameColor(chatName);
        messageAdapter.setChatMessageColor(chatMessage);
        messageAdapter.setChatActionColor(chatAction);
        messageAdapter.setChatEventColor(chatEvent);
        inputBox.setTextColor(chatMessage);
        listView.setBackgroundColor(getResources().getColor(R.color.background_material_light));


    }

    public static String getCurrentConversation() {
        return currentConversation;
    }


    // Hides the soft keyboard and clears the focus from the EditText widget
    private void hideKeyboardAndClearFocus(View v, EditText inputbox) {
        inputbox.clearFocus();
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }





    public void setConversation(Conversation conversation, IRCMessageAdapter adapter) {



        // Topic formatting
        String conversationName = conversation.getName();
        String topic = conversation.getTopic();

        String formattedTopic= conversationName;
        if(topic.equals(conversationName) || topic.equals("")){
            if(!Util.isChannel(conversationName) && !conversationName.equals(Constants.NETWORK_LOBBY))
                formattedTopic = "[Private Message] " + conversationName;
        } else
            formattedTopic = conversationName + " |>> " + topic;

        topicMarquee.setText(formattedTopic);
        currentConversation = conversationName;
        messageAdapter = adapter;
        listView.setAdapter(messageAdapter);

        applyTheme();
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onMessageInput(String rawMessage) {
        if (mListener != null) {
            mListener.onProcessUserInput(rawMessage);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onProcessUserInput(String rawMessage);
    }


}
