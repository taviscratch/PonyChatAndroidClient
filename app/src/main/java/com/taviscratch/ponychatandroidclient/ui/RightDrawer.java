package com.taviscratch.ponychatandroidclient.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.irc.IRCSession;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Util;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RightDrawer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RightDrawer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RightDrawer extends Fragment {
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
    private static float DERPYS_CONSTANT = 5.0f;
    private static float ANIMATION_TRANSLATION_SCALE = 0.9f;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RightDrawer.
     */
    // TODO: Rename and change types and number of parameters
    public static RightDrawer newInstance(String param1, String param2) {
        RightDrawer fragment = new RightDrawer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RightDrawer() {
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
        ArrayAdapter<String> adapter;

        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_right_drawer, container, false);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        theview.setX(screenWidth);

        drawerWidth = Util.convertDpToPixel(240f, this.getActivity());

        ListView userList = (ListView) theview.findViewById(R.id.userList);


        String currentConversation = Chatroom.getCurrentConversation();
        if(Util.isChannel(currentConversation)) {
            String[] userlist = IRCSession.getInstance().getUserList(currentConversation);
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_dark_text_view, userlist);
        } else
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_dark_text_view);
        userList.setAdapter(adapter);


        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = ((TextView) view).getText().toString();
                /*if (mListener != null) {
                    mListener.onUserNameSelected(username);
                }*/
                ((MainActivity) getActivity()).switchToConversation(username);
            }
        });

        userList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });


        if(PonyChatApplication.I_JUST_DONT_KNOW_WHAT_WENT_WRONG) theview.setRotation(-DERPYS_CONSTANT);
        return theview;
    }

    private final void hideSelf() {
        ((MainActivity)getActivity()).hideFragment(this);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(hidden == false) {
            final View view = getView();
            view.setX(screenWidth);

            ListView userList = (ListView) view.findViewById(R.id.userList);
            String[] userlist = IRCSession.getInstance().getUserList(Chatroom.getCurrentConversation());
            SimpleThemedArrayAdapter adapter = new SimpleThemedArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, userlist);
            userList.setAdapter(adapter);
            applyTheme();

            ViewPropertyAnimator animator = view.animate();

            // special derpy animation
            if(PonyChatApplication.I_JUST_DONT_KNOW_WHAT_WENT_WRONG) {
                int xDist, yDist;
                xDist = (int) (Math.cos(Math.toRadians(DERPYS_CONSTANT))*drawerWidth*ANIMATION_TRANSLATION_SCALE);
                yDist = (int) (Math.sin(Math.toRadians(DERPYS_CONSTANT))*drawerWidth*ANIMATION_TRANSLATION_SCALE);

                view.setY(-yDist);

                animator.translationXBy(-xDist);
                animator.translationYBy(yDist);
            } else {
                animator.translationXBy(-drawerWidth);
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

                    animator.translationXBy(xDist);
                    animator.translationYBy(-yDist);
                } else {
                    animator.translationXBy(drawerWidth);
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
                    public void onAnimationCancel(Animator animation) { }
                    @Override
                    public void onAnimationRepeat(Animator animation) { }
                });
                animator.start();
            }
        }
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


        // check for invalid values
        if(backgroundPrimary==-1 || menuItem==-1 || backgroundSecondary==-1 || accent==-1 || menuTitle1==-1 || menuTitle2==-1)
            throw new IllegalArgumentException("error in retrieving theme preferences");

        // get the views that we will be working with
        View view = getView();
        ListView userlist = (ListView) view.findViewById(R.id.userList);
        View titleSeparator = view.findViewById(R.id.rightDrawerTitleSeperator);
        View edge = view.findViewById(R.id.rightDrawerEdge);
        TextView title = (TextView) view.findViewById(R.id.userListTitle);


        // apply the colors
        view.setBackgroundColor(backgroundPrimary);
        titleSeparator.setBackgroundColor(backgroundSecondary);
        edge.setBackgroundColor(backgroundSecondary);
        title.setTextColor(chatEvent);
        SimpleThemedArrayAdapter adapter = (SimpleThemedArrayAdapter) userlist.getAdapter();
        adapter.setTextColor(menuItem);





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
        public void onUserNameSelected(String username);
    }







}
