package com.taviscratch.ponychatandroidclient.ui.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment_Services extends Fragment {


    public SettingsFragment_Services() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_settings_services, container, false);


        CheckBox keepIRCServiceRunningCheckBox, notificationsEnabledCheckBox;
        keepIRCServiceRunningCheckBox = (CheckBox) theview.findViewById(R.id.settings_keep_service_running_checkbox);
        notificationsEnabledCheckBox = (CheckBox) theview.findViewById(R.id.settings_enable_notifications_checkbox);

        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME,0);

        boolean keepIRCServiceRunning, notificationsEnabled;
        keepIRCServiceRunning = preferences.getBoolean(Constants.PreferenceConstants.KEEP_IRC_SERVICE_RUNNING_IN_BACKGROUND,false);
        notificationsEnabled = preferences.getBoolean(Constants.PreferenceConstants.NOTIFICATIONS_ENABLED,false);


        // set the checkboxes to the correct state
        keepIRCServiceRunningCheckBox.setChecked(keepIRCServiceRunning);
        notificationsEnabledCheckBox.setChecked(notificationsEnabled);



        // listeners
        ScrollView scrollView = (ScrollView) theview.findViewById(R.id.services_settings_scrollview);
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
