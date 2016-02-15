package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Zack on 2/14/2016. Used to create a new shade
 */
public class CreateShadeActivity extends Activity {
    Shade shade = new Shade();
    ArrayList<Room> rooms = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade);
        new GetRoomTask().execute();
    }

    public void setupActivity(){
        final Spinner roomSpinner = (Spinner) findViewById(R.id.spinner_rooms);
        final ArrayAdapter<Room> roomArrayAdapter = new ArrayAdapter<Room>(CreateShadeActivity.this, android.R.layout.simple_spinner_item, rooms);
        roomArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(roomArrayAdapter);

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
                    shade.setRoom_id(roomArrayAdapter.getItem(roomSpinner.getSelectedItemPosition()).getId());
                    shade.setName(shadeName);
                    shade.setUser_id(10);
                    shade.setAway(true);
                    shade.setStatus("Open");
                    new CreateShadeTask().execute();
                }
            }
        });
    }

    private class CreateShadeTask extends AsyncTask<Void, Void, Void> {

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

    private class GetRoomTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){

            rooms = DynamoDBManager.getRoomList();

            return null;
        }
        protected void onPostExecute(Void results){
            super.onPostExecute(results);
            setupActivity();
        }

    }
}
