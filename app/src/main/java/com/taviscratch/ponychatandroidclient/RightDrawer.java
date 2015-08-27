package com.taviscratch.ponychatandroidclient;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;


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
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_right_drawer, container, false);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        theview.setX(screenWidth);

        drawerWidth = Util.convertDpToPixel(240f, this.getActivity());

        return theview;
    }




    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(hidden == false) {
            final View view = getView();
            view.setX(screenWidth);

            ViewPropertyAnimator animator = view.animate();
            animator.translationXBy(-drawerWidth);
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
                animator.translationXBy(drawerWidth);
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
