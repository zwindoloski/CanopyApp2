package com.example.greengiant.canopy2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

        final Button logoutButton = (Button) findViewById(R.id.logoutBttn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new LogoutTask().execute();
            }
        });
    }

    private void setupActivity() {
        final Spinner spinnerRunMode = (Spinner) findViewById(R.id.spinnerUserMode);
        final TextView textViewUsername = (TextView) findViewById(R.id.textViewUserName);
        if(user != null) {
            String userName = user.getUsername();
            textViewUsername.setText(userName);
            if (user.getRun_mode() != null) {
                Resources res = getResources();
                String[] positions = res.getStringArray(R.array.user_run_mode);
                for (int i=0; i<positions.length;i++){
                    if (positions[i].equalsIgnoreCase(user.getRun_mode())){
                        spinnerRunMode.setSelection(i,true);
                    }
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

        final Button viewScheduleButton = (Button) findViewById(R.id.view_schedule_bttn);
        viewScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingsActivity.this, ScheduleGraphActivity.class);
                intent.putExtra("ITEM_TYPE", "User");
                intent.putExtra("ITEM_ID", user.getId());
                intent.putExtra("ITEM_NAME", user.getUsername());
                intent.putExtra("MODES", getResources().getStringArray(R.array.user_run_mode));
                UserSettingsActivity.this.startActivity(intent);
            }
        });
    }

    private class GetUserSettingsTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
            String user_id = settings.getString("user_id", "");
            user = DynamoDBManager.getUser(user_id);
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

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            SharedPreferences settings = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor edit = settings.edit();
            edit.clear();
            edit.commit();

            return null;
        }

        protected void onPostExecute(Void results) {
            Intent newActivity = new Intent(getApplicationContext(), LoginActivity.class);
            newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(newActivity);
            finish();
        }
    }
}
