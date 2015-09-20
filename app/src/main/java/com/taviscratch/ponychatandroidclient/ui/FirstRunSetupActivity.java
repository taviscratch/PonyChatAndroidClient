package com.taviscratch.ponychatandroidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.taviscratch.ponychatandroidclient.PonyChatApplication;
import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Constants.AppPreferenceConstants;
import com.taviscratch.ponychatandroidclient.utility.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FirstRunSetupActivity extends Activity {

    EditText usernameEdit, passwordEdit, defaultChannelsEdit;
    TextView usernameLabel, passwordLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run_setup);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        usernameEdit = (EditText) findViewById(R.id.firstrun_username);
        passwordEdit = (EditText) findViewById(R.id.firstrun_password);
        defaultChannelsEdit = (EditText) findViewById(R.id.firstrun_default_channels);
        passwordLabel = (TextView) findViewById(R.id.firstrun_password_label);
        usernameLabel = (TextView) findViewById(R.id.firstrun_username_label);

        SharedPreferences preferences = getSharedPreferences(AppPreferenceConstants.PREFS_NAME, 0);

        String defaultChannelsText = "";
        Set<String> channelsData = preferences.getStringSet(AppPreferenceConstants.DEFAULT_CHANNELS,null);
        if(channelsData.size() >= 1) {
            String[] channels = new String[0];
            channels = channelsData.toArray(channels);

            defaultChannelsText += channels[0];
            for(int i = 1; i< channels.length; i++) {
                defaultChannelsText += ", " + channels[i];
            }
        }


        defaultChannelsEdit.setText(defaultChannelsText);
    }



    public void startMainActivity(View v) {


        if(usernameEdit.getText().toString().equals("")) {
            Toast.makeText(this, "username is required", Toast.LENGTH_SHORT).show();
            return;
        }


        SharedPreferences preferences = PonyChatApplication.getAppContext().getSharedPreferences(AppPreferenceConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        CheckBox randomUsernameCheckbox = (CheckBox) findViewById(R.id.firstrun_randomusername_checkbox);

        Set<String> defaultChannels;
        defaultChannels = parseDefaultChannels(defaultChannelsEdit.getText().toString());

        editor.putStringSet(AppPreferenceConstants.DEFAULT_CHANNELS, defaultChannels);
        editor.putString(AppPreferenceConstants.USERNAME, usernameEdit.getText().toString());

        if(randomUsernameCheckbox.isChecked()) {
            editor.putBoolean(AppPreferenceConstants.ALWAYS_RANDOMIZE_USERNAME, true);
            editor.putString(AppPreferenceConstants.PASSWORD, null);
        } else {
            editor.putBoolean(AppPreferenceConstants.ALWAYS_RANDOMIZE_USERNAME, false);
            editor.putString(AppPreferenceConstants.PASSWORD, passwordEdit.getText().toString());
        }

        editor.putBoolean(AppPreferenceConstants.IS_FIRST_RUN, false);

        editor.commit();


        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }



    public void randomUsernameCheckboxOnClick(View v) {
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

    public void disablePasswordEntry() {
        passwordEdit.setText("");
        passwordEdit.setClickable(false);
        passwordEdit.setEnabled(false);
        passwordEdit.setAlpha(0);
        passwordEdit.clearFocus();
        passwordLabel.setTextColor(getResources().getColor(R.color.dim_foreground_disabled_material_light));
    }
    public void enablePasswordEntry() {
        passwordEdit.setClickable(true);
        passwordEdit.setAlpha(1);
        passwordEdit.setEnabled(true);
        passwordEdit.clearFocus();
        passwordLabel.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
    }
    public void disableUsernameEntry() {
        usernameEdit.setClickable(false);
        usernameEdit.setEnabled(false);
        usernameEdit.clearFocus();
        usernameEdit.setTextColor(getResources().getColor(R.color.dim_foreground_disabled_material_light));
    }
    public void enableUsernameEntry() {
        usernameEdit.setClickable(true);
        usernameEdit.setEnabled(true);
        usernameEdit.clearFocus();
        usernameEdit.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
    }

    public Set<String> parseDefaultChannels(String defaultChannelsEntry) {
        defaultChannelsEntry = defaultChannelsEntry.replaceAll(" ", "");
        String[] defaultChannels = defaultChannelsEntry.split(",");

        return new HashSet<String>(new ArrayList<String>(Arrays.asList(defaultChannels)));
    }



}
