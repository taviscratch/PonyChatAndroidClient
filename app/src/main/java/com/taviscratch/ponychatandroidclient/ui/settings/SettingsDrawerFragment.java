package com.taviscratch.ponychatandroidclient.ui.settings;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.ui.MainActivity;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Util;

public class SettingsDrawerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    IRCSettingsActivity activity;

    int screenWidth;
    float drawerWidth;

    public SettingsDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (IRCSettingsActivity) getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_settings_drawer, container, false);


        screenWidth = getResources().getDisplayMetrics().widthPixels;
        theview.setX(screenWidth);

        drawerWidth = Util.convertDpToPixel(240f, this.getActivity());

        return theview;
    }







    // Animates the Drawer
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        final View view = getView();

        int animationDuration = PonyChatApplication.getAppContext().getResources().getInteger(R.integer.animation_duration);

        if(!hidden) {
            view.setX(-drawerWidth);

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

        } else if(view != null) {
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
