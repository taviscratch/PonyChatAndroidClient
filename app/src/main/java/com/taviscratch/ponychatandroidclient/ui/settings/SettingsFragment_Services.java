package com.taviscratch.ponychatandroidclient.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taviscratch.ponychatandroidclient.R;

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
        return inflater.inflate(R.layout.fragment_settings_services, container, false);
    }


}
