package com.taviscratch.ponychatandroidclient;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.taviscratch.ponychatandroidclient.MyFrameLayout;


public class Chatroom extends Fragment {

    IRCService ircService;

    EditText inputBox;
    Button sendMessageButton;
    ListView listView;

    ArrayAdapter<String> adapter;

    private OnFragmentInteractionListener mListener;

    BroadcastReceiver serverMessageReceiver;
    BroadcastReceiver myMessageReceiver;

    float xStart,yStart,xEnd,yEnd = -1.0f;




    public Chatroom() {
        // Required empty public constructor
    }


    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createReceivers();
        xStart = yStart = xEnd = yEnd = -1.0f;

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
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.mytextview);
        listView.setAdapter(adapter);

        // Set button's onClick
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputBox.getText().toString();
                if (!message.isEmpty()) {
                    inputBox.setText("");
                    Intent msgIntent = new Intent(Constants.MESSAGE_TO_SEND);
                    msgIntent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
                }
                //hideKeyboardAndClearFocus(v, inputBox);
            }
        });



        // touch listener

        theview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //((MainActivity) getActivity()).handleTouchEvent(event);
                getActivity().onTouchEvent(event);
                return true;
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //((MainActivity) getActivity()).handleTouchEvent(event);
                getActivity().onTouchEvent(event);
                return false;
            }
        });

        return theview;

    }


    @Override
    public void onInflate(AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(attrs, savedInstanceState);
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





    // Instantiates all the receivers for this object
    private void createReceivers() {
        // A receiver for messages sent from the server
        serverMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString("message");
                String sender = extras.getString("sender");
                adapter.add(sender + ": " + message);
            }
        };
        IntentFilter serverMessageFilter = new IntentFilter(Constants.MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(serverMessageReceiver, serverMessageFilter);

        // A reciever for messages made by the current user
        myMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString("message");
                adapter.add("Me" + ": " + message); // TODO change so this references the username in the shared preferences
            }
        };
        IntentFilter myMessageFilter = new IntentFilter(Constants.MESSAGE_TO_SEND);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myMessageReceiver, myMessageFilter);
    }





    public void handleTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                xStart = event.getX();
                yStart = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                xEnd = event.getX();
                yEnd = event.getY();
                if(xStart >= 0.0f && yStart >= 0.0f)
                    handleInput(xStart,xEnd,yStart,yEnd);
                // set all to an invalid position
                xStart = yStart = xEnd = yEnd = -1.0f;
                break;
        }
    }





    public void handleInput(float xStart, float xEnd, float yStart, float yEnd) {

        float x = Math.abs(xStart - xEnd);
        float y = Math.abs(yStart-yEnd);

        // if the input is a swipe
        if(x>1.0f || y>1.0f) {
            SwipeControls.SWIPE_DIRECTION swipe = SwipeControls.interpretSwipe(xStart,xEnd,yStart,yEnd);


            Fragment leftDrawer =getFragmentManager().findFragmentByTag("LEFT DRAWER");
            Fragment rightDrawer = getFragmentManager().findFragmentByTag("RIGHT DRAWER");

            switch (swipe) {
                case LEFT:
                    if(leftDrawer.isVisible()) {
                        closeLeftDrawer(leftDrawer);
                    }
                    else if(rightDrawer.isVisible()) {
                        // Do nothing
                    }
                    else {
                        openRightDrawer(rightDrawer);
                    }
                    break;

                case RIGHT:
                    if(leftDrawer.isVisible()) {
                        // Do nothing
                    }
                    else if(rightDrawer.isVisible()) {
                        closeRightDrawer(rightDrawer);
                    }
                    else {
                        openLeftDrawer(leftDrawer);
                    }
                    break;

                case UP:
                    // not supported
                    break;
                case DOWN:
                    // not supported
                    break;
            }


        }


    }




    private void openLeftDrawer(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();


        ft.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_left);
        ft.show(frag);
        //ft.replace(R.id.LeftDrawer_container,frag);
        ft.commit();
    }
    private void closeLeftDrawer(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_left);
        //ft.replace(R.id.LeftDrawer_container, new Fragment());
        ft.hide(frag);
        ft.commit();
    }
    private void openRightDrawer(Fragment frag) {
/*        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.in_from_left,R.animator.out_to_left);
        ft.show(frag);
        ft.commit();*/
    }
    private void closeRightDrawer(Fragment frag) {
/*        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.in_from_left,R.animator.out_to_left);
        ft.show(frag);
        ft.commit();*/
    }





}
