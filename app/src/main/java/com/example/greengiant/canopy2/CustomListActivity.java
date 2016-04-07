package com.example.greengiant.canopy2;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Justin on 4/6/2016.
 */

public class CustomListActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //check if custom title is supported BEFORE setting the content view!
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        //set contentview
        setContentView(R.layout.list);

        //set custom titlebar
        if (customTitleSupported)
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.header);
    }
}