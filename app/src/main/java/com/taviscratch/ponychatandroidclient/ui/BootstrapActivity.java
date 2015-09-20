package com.taviscratch.ponychatandroidclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.taviscratch.ponychatandroidclient.R;
import com.taviscratch.ponychatandroidclient.utility.Constants;

public class BootstrapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootstrap);



        SharedPreferences preferences = getSharedPreferences(Constants.AppPreferenceConstants.PREFS_NAME, 0);
        boolean isFirstRun = preferences.getBoolean(Constants.AppPreferenceConstants.IS_FIRST_RUN, true);
        if(isFirstRun) {
            Intent intent = new Intent(this,FirstRunSetupActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }


    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
