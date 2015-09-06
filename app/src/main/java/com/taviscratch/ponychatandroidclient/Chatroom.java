package com.taviscratch.ponychatandroidclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class Chatroom extends Fragment {

    IRCBackgroundService ircService;

    EditText inputBox;
    Button sendMessageButton;
    ListView listView;

    private OnFragmentInteractionListener mListener;

    float xStart,yStart,xEnd,yEnd;

    private String currentConversation;


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

        // Create widgets
        inputBox = (EditText) theview.findViewById(R.id.chatInput);
        sendMessageButton = (Button) theview.findViewById(R.id.sendMessage_button);
        listView = (ListView) theview.findViewById(R.id.chatroomListView);

        // Set up the listview array adapter
        IRCMessageAdapter messageAdapter = IRCSession.getInstance().getMessageAdapter(currentConversation);
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
                    msgIntent.putExtra(Constants.IntentExtrasConstants.CHANNEL, currentConversation);
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

        return theview;

    }


    // Hides the soft keyboard and clears the focus from the EditText widget
    private void hideKeyboardAndClearFocus(View v, EditText inputbox) {
        inputbox.clearFocus();
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        public void onFragmentInteraction(Uri uri);
    }





}
