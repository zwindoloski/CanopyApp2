package com.example.greengiant.canopy2;

/**
 * Created by Zack on 3/27/2016.
 * Determines which activity to go to on start up.
 * Dependant on whether or not the user has logged in or not.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class OpeningActivity extends Activity{
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        SharedPreferences settings = getSharedPreferences("user_data", MODE_PRIVATE);
        final Class<? extends Activity> activityClass;
        if(settings.contains("user_id")){
            activityClass = MainActivity.class;
        }
        else{
            activityClass = LoginActivity.class;
        }

        Intent newActivity = new Intent(getApplicationContext(), activityClass);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(newActivity);
        finish();
    }
}
