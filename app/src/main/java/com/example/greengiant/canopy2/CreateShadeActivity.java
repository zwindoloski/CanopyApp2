package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Zack on 2/14/2016. Used to create a new shade
 */
public class CreateShadeActivity extends Activity {
    Shade shade = new Shade();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade);

        final EditText shadeNameET = (EditText) findViewById(R.id.create_shade_edit_text);
        final Button createShadeButton = (Button) findViewById(R.id.new_shade_create_bttn);
        createShadeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String shadeName = shadeNameET.getText().toString();
                if(shadeName.equals("")){
                    Toast t = Toast.makeText(CreateShadeActivity.this, R.string.blank_shade_name_error_toast, Toast.LENGTH_LONG);
                    t.show();
                }
                else {
                    shade.setName(shadeName);
                    shade.setUser_id(10);
                    shade.setAway(true);
                    shade.setStatus("Open");
                    new CreateRoomTask().execute();
                }
            }
        });
    }

    private class CreateRoomTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateShade(shade);
            return null;
        }

        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            Intent intent = new Intent(CreateShadeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

    }
}
