package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestToken;

import java.util.Calendar;

/**
 * Created by Justin on 2/21/2016.
 */
public class ConnectNestAccountActivity extends CustomActivity {
    // A request code you can verify later.
    int AUTH_TOKEN_REQUEST_CODE = 123;

    private User user = null;

    // On your Activity, override the following method to receive the token:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println(resultCode + " " + AUTH_TOKEN_REQUEST_CODE);

        if (resultCode == RESULT_OK && requestCode == AUTH_TOKEN_REQUEST_CODE) {
            NestToken token = NestAPI.getAccessTokenFromIntent(intent);
            System.out.println(token);
            System.out.println(token.getToken());

            user.setAccess_token(token.getToken());
            new UpdateUserTask().execute();
            Toast.makeText(getApplicationContext(), "Your Nest account has been connected to Canopy", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "We were unable to link your Nest account to Canopy, please try again", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        NestAPI.setAndroidContext(this);
        NestAPI nest = NestAPI.getInstance();
        nest.setConfig(Constants.PRODUCT_ID, Constants.PRODUCT_SECRET, Constants.REDIRECT_URL);
        nest.launchAuthFlow(this, AUTH_TOKEN_REQUEST_CODE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_nest_account);

        new GetUserTask().execute();
    }

    private class GetUserTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
            String user_id = settings.getString("user_id", "");
            user = DynamoDBManager.getUser(user_id);
            return null;
        }
    }

    private class UpdateUserTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateUser(user);
            return null;
        }

        protected void onPostExecute(Void results){
            finish();
        }
    }
}
