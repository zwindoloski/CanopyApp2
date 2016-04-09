package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Zack on 2/13/2016.
 */
public class UserSettingsActivity extends CustomActivity {
    private User user = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);

        new GetUserSettingsTask().execute();
    }

    private void setupActivity() {
        String userName = user.getUsername();

        final TextView textViewUsername = (TextView) findViewById(R.id.textViewUserName);
        textViewUsername.setText(userName);

        final Spinner spinnerRunMode = (Spinner) findViewById(R.id.spinnerUserMode);
        if (user.getRun_mode() != null) {
            Resources res = getResources();
            String[] positions = res.getStringArray(R.array.user_run_mode);
            for (int i=0; i<positions.length;i++){
                if (positions[i].equalsIgnoreCase(user.getRun_mode())){
                    spinnerRunMode.setSelection(i,true);
                }
            }
        }

        spinnerRunMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Resources res = getResources();
                String[] positions = res.getStringArray(R.array.user_run_mode);
                user.setRun_mode(positions[position]);
                new UpdateAttributeTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    private class GetUserSettingsTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            user = DynamoDBManager.getUser(Constants.USER_ID);
            return null;
        }

        protected void onPostExecute(Void results) {
            setupActivity();
        }
    }

    private class UpdateAttributeTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateUser(user);
            return null;
        }

    }
}
