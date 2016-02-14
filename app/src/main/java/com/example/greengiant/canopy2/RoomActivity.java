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
public class RoomActivity extends Activity {
    private int roomId = 0;
    private Room room = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        roomId = Integer.valueOf(getIntent().getExtras().getString("ROOM_ID"));
        new GetRoomTask().execute();
    }

    private void setupActivity() {
        String roomName = room.getName();

        final TextView textViewRoomName = (TextView) findViewById(R.id.textViewRoomName);
        textViewRoomName.setText(roomName);

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
