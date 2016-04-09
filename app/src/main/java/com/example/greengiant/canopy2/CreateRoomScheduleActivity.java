package com.example.greengiant.canopy2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
/**
 * Created by Zachary on 2/20/2016.
 */
public class CreateRoomScheduleActivity extends Activity {
    ArrayList<Room> rooms = null;
    Schedule schedule;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room_schedule);
        new GetShadesTask().execute();
    }

    public void setupActivity() {
        final Spinner roomSpinner = (Spinner) findViewById(R.id.spinner_rooms);
        final ArrayAdapter<Room> roomArrayAdapter = new ArrayAdapter<Room>(CreateRoomScheduleActivity.this, android.R.layout.simple_spinner_item, rooms);
        roomArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(roomArrayAdapter);

        final EditText scheduleNameET = (EditText) findViewById(R.id.schedule_name_edit_text);
        final Spinner shadeModeSpinner = (Spinner) findViewById(R.id.spinnerRoomScheduleMode);
        final Spinner daySpinner = (Spinner) findViewById(R.id.spinner_day_of_week);
        final Button createRoomScheduleButton = (Button) findViewById(R.id.create_room_schedule_bttn);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.schedule_time_picker);

        createRoomScheduleButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                String scheduleName = scheduleNameET.getText().toString();
                if (scheduleName.equals("")) {
                    Toast t = Toast.makeText(CreateRoomScheduleActivity.this, R.string.bank_schedule_name_error, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    int dayNumber  = daySpinner.getSelectedItemPosition();
                    int time = dayNumber*10000 + AppUtils.getHour(timePicker)*100 + AppUtils.getMinute(timePicker);

                    schedule = new Schedule();
                    schedule.setDay(daySpinner.getSelectedItem().toString());
                    schedule.setRun_mode(shadeModeSpinner.getSelectedItem().toString());
                    schedule.setItem_type("Room");
                    schedule.setName(scheduleName);
                    schedule.setItem_id(roomArrayAdapter.getItem(roomSpinner.getSelectedItemPosition()).getId());
                    schedule.setStart_time(time);
                    new CreateScheduleTask().execute();
                }


            }
        });
    }

    private class CreateScheduleTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateSchedule(schedule);
            return null;
        }

        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            setResult(RESULT_OK);
            finish();
        }

    }

    private class GetShadesTask extends AsyncTask<Void, Void, Void>{
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
