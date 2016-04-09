package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

/**
 * Created by Zack on 3/28/2016.
 */
public class NewUserActivity extends Activity {
    User newUser = new User();
    String username;
    String password;
    Boolean validUsername;
    public static AmazonClientManager clientManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
        clientManager = new AmazonClientManager(this);

        final EditText usernameET = (EditText) findViewById(R.id.txtNewUsername);
        final EditText passwordET = (EditText) findViewById(R.id.txtNewPassword);
        final EditText passwordConfirmET = (EditText) findViewById(R.id.txtNewPasswordConfirm);
        final Button createUserButton = (Button) findViewById(R.id.btnCreateUser);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = usernameET.getText().toString().replaceAll(" ", "");
                password = passwordET.getText().toString().replaceAll(" ", "");
                String passwordConfirm = passwordConfirmET.getText().toString();
                if (username == "") {
                    Toast noUsername = Toast.makeText(NewUserActivity.this, R.string.username_blank, Toast.LENGTH_LONG);
                    noUsername.show();
                }
                else if (!password.equals(passwordConfirm)) {
                    Toast notMatching = Toast.makeText(NewUserActivity.this, R.string.passwords_dont_match, Toast.LENGTH_LONG);
                    notMatching.show();
                }else if (password == ""){
                    Toast noPassword = Toast.makeText(NewUserActivity.this, R.string.password_blank, Toast.LENGTH_LONG);
                    noPassword.show();
                }else {
                    new CreateUserTask().execute();
                }

            }
        });
    }

    private void showInvalid() {
        if (!validUsername) {
            Toast invalidName = Toast.makeText(NewUserActivity.this, R.string.username_taken, Toast.LENGTH_LONG);
            invalidName.show();
        }
    }


    private class CreateUserTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... inputs){
            validUsername = DynamoDBManager.validUsername(username);
            if(validUsername){
                newUser.setUsername(username);
                newUser.setPassword(md5Hash.md5(password));
                DynamoDBManager.createUser(newUser);
            }
            return null;
        }

        protected void onPostExecute(Void results){
            if(validUsername){
                SharedPreferences settings = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();
                edit.clear();
                edit.putString("user_id", newUser.getId());
                edit.commit();
                Intent newActivity = new Intent(getApplicationContext(), MainActivity.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(newActivity);
                finish();
            }
            else{
                showInvalid();
            }
        }

    }
}