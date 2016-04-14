package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Zack on 2/14/2016.
 */
public class CreateRoomActivity extends CustomActivity {
    boolean isNew = false;
    Room room;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        new GetRoomTask().execute();
    }

    public void setupActivity(){
        final EditText roomNameET = (EditText) findViewById(R.id.createRoomEditText);
        final Button createRoomButton = (Button) findViewById(R.id.newRoomCreateBttn);

        final Spinner modeSpinner = (Spinner) findViewById(R.id.spinnerRoomModeCreate);

        if(!isNew){
            final TextView textViewShadeName = (TextView) findViewById(R.id.create_new_room);
            textViewShadeName.setText(room.getName());

            modeSpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.shade_run_mode)).indexOf(room.getRun_mode()));
            roomNameET.setText(room.getName());
            createRoomButton.setText("Update");

            final Button viewScheduleButton = (Button) findViewById(R.id.view_schedule_bttn);
            viewScheduleButton.setVisibility(View.VISIBLE);
            viewScheduleButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(CreateRoomActivity.this, ScheduleGraphActivity.class);
                    intent.putExtra("ITEM_TYPE", "Room");
                    intent.putExtra("ITEM_ID", room.getId());
                    intent.putExtra("ITEM_NAME", room.getName());
                    intent.putExtra("MODES", getResources().getStringArray(R.array.room_run_mode));
                    CreateRoomActivity.this.startActivity(intent);
                }
            });
        }

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            String roomName = roomNameET.getText().toString();
            if(roomName.equals("")){
                Toast t = Toast.makeText(CreateRoomActivity.this, R.string.blank_room_name_toast, Toast.LENGTH_LONG);
                t.show();
            }
            else {
                room.setName(roomName);
                SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
                String user_id = settings.getString("user_id", "");
                room.setUser_id(user_id);
                room.setRun_mode(modeSpinner.getSelectedItem().toString());
                new CreateRoomTask().execute();
            }
            }
        });
    }

    public class GetRoomTask extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... voids){
            if(getIntent().hasExtra("ROOM_ID")) {
                String roomId = getIntent().getExtras().getString("ROOM_ID");
                room = DynamoDBManager.getRoom(roomId);
            }
            else {
                room = new Room();
                isNew = true;
            }
            return null;
        }

        protected void onPostExecute(Void results){
            super.onPostExecute(results);
            setupActivity();
        }
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
