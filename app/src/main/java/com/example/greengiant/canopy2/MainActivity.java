package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Zack on 2/6/2016.
 */
public class MainActivity extends Activity{
    public static AmazonClientManager clientManager = null;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        clientManager = new AmazonClientManager(this);

        final Button listShadesButton = (Button) findViewById(R.id.list_shades_bttn);
        listShadesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick( View v){
                new DynamoDBManagerTask().execute(DynamoDBManagerType.LIST_SHADES);
            }
        });

        final Button createShadeButton = (Button) findViewById(R.id.add_shades_bttn);
        createShadeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v){
                new DynamoDBManagerTask().execute(DynamoDBManagerType.CREATE_SHADE);
            }
        });

        final Button listRoomsButton = (Button) findViewById(R.id.list_rooms_bttn);
        listRoomsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v){
                new DynamoDBManagerTask().execute(DynamoDBManagerType.LIST_ROOMS);
            }
        });

        final Button createRoomButton = (Button) findViewById(R.id.add_rooms_bttn);
        createRoomButton.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v){
                new DynamoDBManagerTask().execute(DynamoDBManagerType.CREATE_ROOM);
            }
        });

        final Button userSettingsButton = (Button) findViewById(R.id.user_settings_bttn);
        userSettingsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v){
                new DynamoDBManagerTask().execute(DynamoDBManagerType.USER_SETTINGS);
            }
        });
    }

    private class DynamoDBManagerTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
            DynamoDBManagerType... types){

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTaskType(types[0]);

            if(types[0] == DynamoDBManagerType.LIST_SHADES){
                DynamoDBManager.getShadeList();
            }
            else if (types[0] == DynamoDBManagerType.LIST_ROOMS){
                DynamoDBManager.getRoomList();
            }
            else if (types[0] == DynamoDBManagerType.USER_SETTINGS){
            }

            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result){
            if (result.getTaskType() == DynamoDBManagerType.LIST_SHADES) {
                startActivity(new Intent(MainActivity.this, ShadeListActivity.class));
            }
            else if (result.getTaskType() == DynamoDBManagerType.LIST_ROOMS){
                startActivity(new Intent(MainActivity.this, RoomListActivity.class));
            }
            else if (result.getTaskType() == DynamoDBManagerType.USER_SETTINGS){
                startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
            }
            else if (result.getTaskType() == DynamoDBManagerType.CREATE_ROOM){
                startActivity(new Intent(MainActivity.this, CreateRoomActivity.class));
            }
            else if (result.getTaskType() == DynamoDBManagerType.CREATE_SHADE){
                startActivity(new Intent(MainActivity.this, CreateShadeActivity.class));
            }
        }
    }
    private enum DynamoDBManagerType {
        LIST_SHADES, LIST_ROOMS, USER_SETTINGS, CREATE_ROOM, CREATE_SHADE
    }

    private class DynamoDBManagerTaskResult {
        private DynamoDBManagerType taskType;

        public DynamoDBManagerType getTaskType() { return taskType; }

        public void setTaskType(DynamoDBManagerType taskType) {
            this.taskType = taskType;
        }
    }
}
