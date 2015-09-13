package com.taviscratch.ponychatandroidclient.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.taviscratch.ponychatandroidclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment_UI extends Fragment {


    public SettingsFragment_UI() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_settings_ui, container, false);

        ScrollView scrollView = (ScrollView) theview.findViewById(R.id.ui_settings_scrollview);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });
        return theview;
    }


}
