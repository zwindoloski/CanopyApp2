package com.example.greengiant.canopy2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Zack on 2/13/2016.
 */
public class UserSettingsActivity extends Activity {
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

    }

    private class GetUserSettingsTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            user = DynamoDBManager.getUser(10);
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
