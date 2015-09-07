package com.taviscratch.ponychatandroidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.taviscratch.ponychatandroidclient.R;

public class BootstrapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootstrap);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
