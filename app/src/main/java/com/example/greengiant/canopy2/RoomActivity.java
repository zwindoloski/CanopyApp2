package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Zack on 2/13/2016.
 */
public class RoomActivity extends CustomActivity {
    private String roomId = "";
    private Room room = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        roomId = getIntent().getExtras().getString("ROOM_ID");
        new GetRoomTask().execute();
    }

    private void setupActivity() {
        String roomName = room.getName();

        final TextView textViewRoomName = (TextView) findViewById(R.id.textViewRoomName);
        textViewRoomName.setText(roomName);

        final Spinner spinnerRunMode = (Spinner) findViewById(R.id.spinnerRoomMode);
        if (room.getRun_mode() != null) {
            Resources res = getResources();
            String[] positions = res.getStringArray(R.array.room_run_mode);
            for (int i=0; i<positions.length;i++){
                if (positions[i].equalsIgnoreCase(room.getRun_mode())){
                    spinnerRunMode.setSelection(i,true);
                }
            }
        }

        spinnerRunMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Resources res = getResources();
                String[] positions = res.getStringArray(R.array.room_run_mode);
                room.setRun_mode(positions[position]);
                new UpdateAttributeTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

    }

    private class GetRoomTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            room = DynamoDBManager.getRoom(roomId);
            return null;
        }

        protected void onPostExecute(Void results) {
            setupActivity();
        }
    }

    private class UpdateAttributeTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateRoom(room);
            return null;
        }

    }
}
