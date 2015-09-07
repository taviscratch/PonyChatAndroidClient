package com.taviscratch.ponychatandroidclient;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


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

    static IRCSession session;



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
                Chatroom.switchConversationInView(Constants.NETWORK_LOBBY);
                hideSelf();
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



        return theview;
    }

    private final void hideSelf() {
        ((MainActivity)getActivity()).hideFragment(this);
    }


    private void updateLists(View view) {
        LinearLayout channelsList = (LinearLayout) view.findViewById(R.id.channelsList);
        LinearLayout privateMessagesList = (LinearLayout) view.findViewById(R.id.privateMessagesList);

        String[] channelNames = session.getChannelNames();
        String[] privateMessageNames = session.getPrivateMessageNames();

        String currentConversation = Chatroom.getCurrentConversation();

        // Check the current textviews for channels, and add new ones if necessary
        for(int i = 0; i < channelNames.length; i++) {
            TextView t = (TextView) channelsList.getChildAt(i);
            if(t==null || !channelNames[i].equals(t.getText().toString())){

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) inflater.inflate(R.layout.left_drawer_list_item, (ViewGroup) view.getRootView(), false);

                textView.setText(channelNames[i]);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((TextView) v).getText().toString();
                        Chatroom.switchConversationInView(text);
                        hideSelf();
                    }
                });

                channelsList.addView(textView,i);

            } else  if(t.getText().toString().equals(currentConversation)) {
                t.setBackgroundColor(getResources().getColor(R.color.background_floating_material_dark));
            } else
                t.setBackgroundColor(0);
        }

        // Check the current textviews for private messages, and add new ones if necessary
        for(int i = 0; i < privateMessageNames.length; i++) {
            TextView t = (TextView) privateMessagesList.getChildAt(i);
            if(t==null || !privateMessageNames[i].equals(t.getText().toString())){

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) inflater.inflate(R.layout.left_drawer_list_item, (ViewGroup) view.getRootView(), false);

                textView.setText(privateMessageNames[i]);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((TextView) v).getText().toString();
                        Chatroom.switchConversationInView(text);
                        hideSelf();
                    }
                });

                privateMessagesList.addView(textView,i);

            } else  if(t.getText().toString().equals(currentConversation)) {
                t.setBackgroundColor(getResources().getColor(R.color.background_floating_material_dark));
            } else
                t.setBackgroundColor(0);
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
            view.setX(-drawerWidth);

            updateLists(view);

            ViewPropertyAnimator animator = view.animate();
            animator.translationXBy(drawerWidth);
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
                animator.translationXBy(-drawerWidth);
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
