package com.taviscratch.ponychatandroidclient.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.irc.IRCSession;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LeftDrawer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LeftDrawer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeftDrawer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    float drawerWidth, screenWidth;
    private static final int animationDuration = 300;
    private static float DERPYS_CONSTANT = 1f;
    private static float ANIMATION_TRANSLATION_SCALE = 0.95f;

    static IRCSession session;

    TextView currentConversationView;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeftDrawer.
     */
    // TODO: Rename and change types and number of parameters
    public static LeftDrawer newInstance(String param1, String param2) {
        LeftDrawer fragment = new LeftDrawer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LeftDrawer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_left_drawer, container, false);
        session = IRCSession.getInstance();
        
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        theview.setX(screenWidth);

        drawerWidth = Util.convertDpToPixel(240f, this.getActivity());

        ScrollView conversationsScrollView = (ScrollView) theview.findViewById(R.id.conversationsScrollView);
        TextView networkLobbyText = (TextView) theview.findViewById(R.id.networkLobbyText);
        LinearLayout channelsList = (LinearLayout) theview.findViewById(R.id.channelsList);
        LinearLayout privateMessagesList = (LinearLayout) theview.findViewById(R.id.privateMessagesList);
        Button settingsButton = (Button) theview.findViewById(R.id.settings_button);

        settingsButton.setText("\u00A4");

        conversationsScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });

        networkLobbyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).switchToConversation(Constants.NETWORK_LOBBY);
                currentConversationView = null;
            }
        });

        String[] channelNames = session.getChannelNames();
        String[] privateMessageNames = session.getPrivateMessageNames();

        updateLists(theview);

        theview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });


        if(PonyChatApplication.I_JUST_DONT_KNOW_WHAT_WENT_WRONG) theview.setRotation(DERPYS_CONSTANT);
        return theview;
    }

    public void updateLists(View view) {
        LinearLayout channelsList = (LinearLayout) view.findViewById(R.id.channelsList);
        LinearLayout privateMessagesList = (LinearLayout) view.findViewById(R.id.privateMessagesList);

        String[] channelNames = session.getChannelNames();
        String[] privateMessageNames = session.getPrivateMessageNames();

        // Sorting the lists
        List chNames = Arrays.asList(channelNames);
        List pmNames = Arrays.asList(privateMessageNames);
        Collections.sort(chNames, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(pmNames, String.CASE_INSENSITIVE_ORDER);
        channelNames = (String[]) chNames.toArray();
        privateMessageNames = (String[]) pmNames.toArray();


        String currentConversation = Chatroom.getCurrentConversation();


        // clears the channel and private message lists
        channelsList.removeAllViewsInLayout();
        privateMessagesList.removeAllViewsInLayout();


        // Check the current textviews for channels, and add new ones if necessary
        for(int i = 0; i < channelNames.length; i++) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView = (TextView) inflater.inflate(R.layout.left_drawer_list_item, (ViewGroup) view.getRootView(), false);

            textView.setText(channelNames[i]);
            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String conversationName = ((TextView) v).getText().toString();
                    unhighlightTextView(currentConversationView);
                    currentConversationView = (TextView) v;
                    highlightTextView(currentConversationView);
                    ((MainActivity) getActivity()).switchToConversation(conversationName);
                }
            });

            channelsList.addView(textView,i);



            if(textView.getText().toString().equals(currentConversation)) {
                currentConversationView = textView;
                highlightTextView(textView);
            } else
                unhighlightTextView(textView);
        }

        // Check the current textviews for private messages, and add new ones if necessary
        for(int i = 0; i < privateMessageNames.length; i++) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView = (TextView) inflater.inflate(R.layout.left_drawer_list_item, (ViewGroup) view.getRootView(), false);

            textView.setText(privateMessageNames[i]);
            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String conversationName = ((TextView) v).getText().toString();
                    unhighlightTextView(currentConversationView);
                    currentConversationView = (TextView) v;
                    highlightTextView(currentConversationView);
                    ((MainActivity) getActivity()).switchToConversation(conversationName);
                }
            });

            privateMessagesList.addView(textView,i);


            if(textView.getText().toString().equals(currentConversation)) {
                currentConversationView = textView;
                highlightTextView(textView);
            } else
                unhighlightTextView(textView);
        }
    }

    public void highlightTextView(TextView textView) {
        textView.setBackgroundColor(ThemeColors.transparentHighlight);
    }
    public void unhighlightTextView(TextView textView) {
        if(textView != null)
            textView.setBackgroundColor(0x00ffffff);
    }






    private void applyTheme() {
        int backgroundPrimary, backgroundSecondary, accent,
                menuTitle1, menuTitle2, menuItem,
                chatName, chatMessage, chatAction, chatEvent;

        // get the theme preferences
        SharedPreferences themePreferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.ThemeColorPreferenceConstants.PREFS_NAME,0);

        // get the hex color codes
        backgroundPrimary = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_PRIMARY, -1);
        backgroundSecondary = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.BACKGROUND_SECONDARY, -1);
        accent = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.ACCENT, -1);
        menuTitle1 = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.MENU_TITLE_1, -1);
        menuTitle2 = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.MENU_TITLE_2, -1);
        menuItem = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.MENU_ITEM, -1);
        chatName = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_NAME, -1);
        chatMessage = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_MESSAGE, -1);
        chatAction = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_ACTION, -1);
        chatEvent = themePreferences.getInt(Constants.ThemeColorPreferenceConstants.CHAT_EVENT, -1);


        // get the views that we will be working with
        View view = getView();
        LinearLayout channelsList = (LinearLayout) view.findViewById(R.id.channelsList);
        LinearLayout privateMessagesList = (LinearLayout) view.findViewById(R.id.privateMessagesList);
        View titleSeparator = view.findViewById(R.id.leftDrawerTitleSeperator);
        View edge = view.findViewById(R.id.leftDrawerEdge);
        TextView lobbyTitle = (TextView) view.findViewById(R.id.networkLobbyText);
        TextView channelsListTitle = (TextView) view.findViewById(R.id.ChannelsListTitle);
        TextView privateMessagesListTitle = (TextView) view.findViewById(R.id.PrivateMessagesListTitle);
        Button settingsButton = (Button) view.findViewById(R.id.settings_button);
        TextView listItem;


        // apply the colors
        view.setBackgroundColor(backgroundPrimary);
        titleSeparator.setBackgroundColor(backgroundSecondary);
        edge.setBackgroundColor(backgroundSecondary);
        lobbyTitle.setTextColor(chatEvent);
        channelsListTitle.setTextColor(chatEvent);
        privateMessagesListTitle.setTextColor(chatEvent);
        settingsButton.setTextColor(chatAction);

        int childcount = channelsList.getChildCount();
        for(int i=0; i < childcount; i++) {
            listItem = (TextView) channelsList.getChildAt(i);
            listItem.setTextColor(menuItem);
        }
        childcount = privateMessagesList.getChildCount();
        for(int i=0; i < childcount; i++) {
            listItem = (TextView) privateMessagesList.getChildAt(i);
            listItem.setTextColor(menuItem);
        }
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(hidden == false) {
            final View view = getView();
            updateLists(view);
            applyTheme();

            ViewPropertyAnimator animator = view.animate();

            // special derpy animation
            if(PonyChatApplication.I_JUST_DONT_KNOW_WHAT_WENT_WRONG) {
                int xDist, yDist;
                xDist = (int) (Math.cos(Math.toRadians(DERPYS_CONSTANT))*drawerWidth*ANIMATION_TRANSLATION_SCALE);
                yDist = (int) (Math.sin(Math.toRadians(DERPYS_CONSTANT))*drawerWidth*ANIMATION_TRANSLATION_SCALE);

                view.setX(-xDist/ANIMATION_TRANSLATION_SCALE);
                view.setY(-yDist/ANIMATION_TRANSLATION_SCALE);

                animator.translationXBy(xDist);
                animator.translationYBy(yDist);
            }
            else {
                view.setX(-drawerWidth);
                animator.translationXBy(drawerWidth);
            }
            animator.setDuration(animationDuration);
            animator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationCancel(Animator animation) { }
                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
            animator.start();
        } else {
            final View view = getView();
            if(view != null) {
                ViewPropertyAnimator animator = view.animate();

                // special derpy animation
                if(PonyChatApplication.I_JUST_DONT_KNOW_WHAT_WENT_WRONG) {
                    int xDist, yDist;
                    xDist = (int) (Math.cos(Math.toRadians(DERPYS_CONSTANT))*drawerWidth*ANIMATION_TRANSLATION_SCALE);
                    yDist = (int) (Math.sin(Math.toRadians(DERPYS_CONSTANT))*drawerWidth*ANIMATION_TRANSLATION_SCALE);

                    animator.translationXBy(-xDist);
                    animator.translationYBy(-yDist);
                } else {
                    animator.translationXBy(-drawerWidth);
                }


                animator.setDuration(animationDuration);
                animator.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(view.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animator.start();



            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }






}
