package com.example.greengiant.canopy2;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Zack on 2/6/2016.
 */
public class RoomListActivity extends ListActivity {
    private ArrayList<Room> items = null;
    private ArrayList<String> labels = null;
    private int currentPosition = 0;
    private ArrayAdapter<String> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetRoomListTask().execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
        intent.putExtra("ROOM_ID", items.get(position).getId() + "");
        startActivity(intent);
    }

    private class GetRoomListTask extends AsyncTask<Void,Void, Void>{

        protected Void doInBackground(Void... inputs) {
            labels = new ArrayList<>();
            items = DynamoDBManager.getRoomList();

            for (Room room : items) {
                labels.add(room.getName());
            }
            return null;
        }

        protected void onPostExecute(Void result){
            arrayAdapter = new ArrayAdapter<>(RoomListActivity.this,
                    R.layout.room_list_item, labels);
            setListAdapter(arrayAdapter);
        }
    }
}