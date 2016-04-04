package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Zack on 2/14/2016.
 */
public class CreateRoomActivity extends Activity {
    Room room = new Room();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        final EditText roomNameET = (EditText) findViewById(R.id.createRoomEditText);
        final Button createRoomButton = (Button) findViewById(R.id.newRoomCreateBttn);

        final Spinner modeSpinner = (Spinner) findViewById(R.id.spinnerRoomModeCreate);

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            String roomName = roomNameET.getText().toString();
            if(roomName.equals("")){
                Toast t = Toast.makeText(CreateRoomActivity.this, R.string.blank_room_name_toast, Toast.LENGTH_LONG);
                t.show();
            }
            else {
                room.setName(roomName);
                room.setUser_id(Constants.USER_ID);
                room.setRun_mode(modeSpinner.getSelectedItem().toString());
                new CreateRoomTask().execute();
            }
            }
        });
    }

    private class CreateRoomTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateRoom(room);
            return null;
        }

        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            setResult(RESULT_OK);
            Toast.makeText(getApplicationContext(), "Your room has been successfully created", Toast.LENGTH_LONG).show();
            finish();
        }

    }
}
