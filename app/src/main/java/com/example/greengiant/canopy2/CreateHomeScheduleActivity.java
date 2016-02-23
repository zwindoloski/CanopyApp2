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
public class CreateHomeScheduleActivity extends Activity {
    Schedule schedule;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_home_schedule);
        setupActivity();
    }

    public void setupActivity() {

        final EditText scheduleNameET = (EditText) findViewById(R.id.schedule_name_edit_text);
        final Spinner shadeStatusSpinner = (Spinner) findViewById(R.id.spinner_shade_position);
        final Spinner daySpinner = (Spinner) findViewById(R.id.spinner_day_of_week);
        final Button createRoomScheduleButton = (Button) findViewById(R.id.create_home_schedule_bttn);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.schedule_time_picker);

        createRoomScheduleButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                String scheduleName = scheduleNameET.getText().toString();
                if (scheduleName.equals("")) {
                    Toast t = Toast.makeText(CreateHomeScheduleActivity.this, R.string.bank_schedule_name_error, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    schedule = new Schedule();
                    schedule.setDay(daySpinner.getSelectedItem().toString());
                    schedule.setStatus(shadeStatusSpinner.getSelectedItem().toString());
                    schedule.setItem_type("User");
                    schedule.setItem_id("10");
                    schedule.setName(scheduleName);
                    schedule.setStart_time(String.format("%02d%02d", timePicker.getHour(), timePicker.getMinute()));
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
            Intent intent = new Intent(CreateHomeScheduleActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

    }

}