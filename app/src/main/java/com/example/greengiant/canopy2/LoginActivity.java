package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Zack on 3/27/2016.
 */
public class LoginActivity extends Activity {

    String username;
    String password;
    Boolean succesfulLogin = false;
    public static AmazonClientManager clientManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        clientManager = new AmazonClientManager(this);

        final EditText usernameET = (EditText) findViewById(R.id.txtUsername);
        final EditText passwordET = (EditText) findViewById(R.id.txtPassword);
        final Button loginButton = (Button) findViewById(R.id.btnLogin);
        final Button newUserButton  = (Button) findViewById(R.id.btnNewUser);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = usernameET.getText().toString().replace(" ", "");
                password = passwordET.getText().toString().replace(" ", "");
                if (password =="" || username==""){
                    Toast t = Toast.makeText(LoginActivity.this, "Username or Password is blank.", Toast.LENGTH_LONG);
                    t.show();
                }
                else{
                    new LoginTask().execute();
                }
            }
        });

        newUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newActivity = new Intent(LoginActivity.this, NewUserActivity.class);
                startActivity(newActivity);
            }
        });
    }

    private void showInvalid() {
        Toast loginFail = Toast.makeText(LoginActivity.this, "Incorrect Username or Password.", Toast.LENGTH_LONG);
        loginFail.show();
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... inputs){
            String encPassword = md5Hash.md5(password);
            succesfulLogin = DynamoDBManager.loginUser(username, encPassword, LoginActivity.this);
            return null;
        }

        protected void onPostExecute(Void results) {
            if(succesfulLogin){
                Intent newActivity = new Intent(getApplicationContext(), MainActivity.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(newActivity);
                finish();
            }else{
                showInvalid();
            }
        }
    }
}
