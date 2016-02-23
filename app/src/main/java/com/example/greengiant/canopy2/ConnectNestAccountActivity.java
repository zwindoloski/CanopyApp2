package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestToken;

import java.util.Calendar;

/**
 * Created by Justin on 2/21/2016.
 */
public class ConnectNestAccountActivity extends Activity {
    // A request code you can verify later.
    int AUTH_TOKEN_REQUEST_CODE = 123;

    private int userId = 10;
    private User user = null;

    // On your Activity, override the following method to receive the token:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println(resultCode + " " + AUTH_TOKEN_REQUEST_CODE);

        if (resultCode == RESULT_OK && requestCode == AUTH_TOKEN_REQUEST_CODE) {
            NestToken token = NestAPI.getAccessTokenFromIntent(intent);
            System.out.println(token);
            System.out.println(token.getToken());

            user.setLast_nest_access(Calendar.getInstance().getTimeInMillis());
            user.setAccess_token(token.getToken());
            new UpdateUserTask().execute();
//            DynamoDBManager.updateUser(user);
        }
        else
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

//        userId = getIntent().getExtras().getString("USER_ID");
        new GetUserTask().execute();
    }

    private class GetUserTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            user = DynamoDBManager.getUser(userId);
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
