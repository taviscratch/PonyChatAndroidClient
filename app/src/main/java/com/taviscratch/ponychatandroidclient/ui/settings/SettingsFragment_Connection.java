package com.taviscratch.ponychatandroidclient.ui.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Constants;
import com.taviscratch.ponychatandroidclient.utility.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment_Connection extends Fragment {

    // widgets
    EditText usernameEdit, passwordEdit, hostnameEdit, portEdit, realnameEdit, defaultChannelsEdit;
    CheckBox randomizeUsernameCheckBox;

    LinearLayout usernameRow, passwordRow;


    public SettingsFragment_Connection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theview = inflater.inflate(R.layout.fragment_settings_connection, container, false);





        // initialize the two rows whose views may change
        usernameRow = (LinearLayout) theview.findViewById(R.id.settings_username_row);
        passwordRow = (LinearLayout) theview.findViewById(R.id.settings_password_row);

        // initialize the view's widgets
        usernameEdit = (EditText) theview.findViewById(R.id.settings_usernameEdit);
        passwordEdit = (EditText) theview.findViewById(R.id.settings_passwordEdit);
        hostnameEdit = (EditText) theview.findViewById(R.id.settings_hostnameEdit);
        portEdit = (EditText) theview.findViewById(R.id.settings_portEdit);
        realnameEdit = (EditText) theview.findViewById(R.id.settings_realnameEdit);
        defaultChannelsEdit = (EditText) theview.findViewById(R.id.settings_defaultChannelsEdit);
        randomizeUsernameCheckBox = (CheckBox) theview.findViewById(R.id.settings_randomusername_checkbox);


        // get the shared preferences object
        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(Constants.PreferenceConstants.PREFS_NAME,0);


        // get the data stored in the shared preferences
        String username, password, hostname, realname;
        int port;
        Set<String> defaultChannels;
        boolean alwaysRandomizeUsername;

        username = preferences.getString(Constants.PreferenceConstants.USERNAME, null);
        password = preferences.getString(Constants.PreferenceConstants.PASSWORD, null);
        hostname = preferences.getString(Constants.PreferenceConstants.HOSTNAME, null);
        port = preferences.getInt(Constants.PreferenceConstants.PORT, -1);
        realname = preferences.getString(Constants.PreferenceConstants.REALNAME, null);
        defaultChannels = preferences.getStringSet(Constants.PreferenceConstants.DEFAULT_CHANNELS, null);
        alwaysRandomizeUsername = preferences.getBoolean(Constants.PreferenceConstants.ALWAYS_RANDOMIZE_USERNAME, true);

        // populate the widgets with the data
        usernameEdit.setText(username);
        passwordEdit.setText(password);
        hostnameEdit.setText(hostname);
        portEdit.setText(String.valueOf(port));
        realnameEdit.setText(realname);
        randomizeUsernameCheckBox.setChecked(alwaysRandomizeUsername);
        String defaultChannelsText = "";
        if(defaultChannels!=null && defaultChannels.size() >= 1) {
            String[] channels = new String[0];
            channels = defaultChannels.toArray(channels);

            defaultChannelsText += channels[0];
            for(int i = 1; i< channels.length; i++) {
                defaultChannelsText += ", " + channels[i];
            }
        }
        defaultChannelsEdit.setText(defaultChannelsText);


        // set listeners
        randomizeUsernameCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();

                if(checked) {
                    usernameEdit.setText(Util.getRandomUsername());
                    disableUsernameEntry();
                    disablePasswordEntry();
                } else {
                    enableUsernameEntry();
                    enablePasswordEntry();
                }
            }
        });
        // this new listener allows the swipe controls to work
        ScrollView scrollView = (ScrollView) theview.findViewById(R.id.connection_settings_scrollview);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });


        // Last second view configuration changes
        if(randomizeUsernameCheckBox.isChecked()) {
            disableUsernameEntry();
            disablePasswordEntry();
        }


        return theview;
    }

    public void disablePasswordEntry() {
        passwordEdit.setText("");
        passwordEdit.setClickable(false);
        passwordEdit.setEnabled(false);
        passwordEdit.clearFocus();
        passwordRow.setVisibility(View.GONE);
    }
    public void enablePasswordEntry() {
        passwordEdit.setClickable(true);
        passwordEdit.setEnabled(true);
        passwordEdit.clearFocus();
        passwordRow.setVisibility(View.VISIBLE);
    }
    public void disableUsernameEntry() {
        usernameEdit.setClickable(false);
        usernameEdit.setEnabled(false);
        usernameEdit.clearFocus();
        usernameRow.setAlpha(.3f);
    }
    public void enableUsernameEntry() {
        usernameEdit.setClickable(true);
        usernameEdit.setEnabled(true);
        usernameEdit.clearFocus();
        usernameRow.setAlpha(1);
    }

    public Set<String> parseDefaultChannels(String defaultChannelsEntry) {
        defaultChannelsEntry = defaultChannelsEntry.replaceAll(" ", "");
        String[] defaultChannels = defaultChannelsEntry.split(",");
        return new HashSet<String>(new ArrayList<String>(Arrays.asList(defaultChannels)));
    }






}
