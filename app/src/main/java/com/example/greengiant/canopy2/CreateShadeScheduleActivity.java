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
public class CreateShadeScheduleActivity extends Activity {
    ArrayList<Shade> shades = null;
    Schedule schedule;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade_schedule);
        new GetShadesTask().execute();
    }

    public void setupActivity() {
        final Spinner shadeSpinner = (Spinner) findViewById(R.id.spinner_shades);
        final ArrayAdapter<Shade> shadeArrayAdapter = new ArrayAdapter<Shade>(CreateShadeScheduleActivity.this, android.R.layout.simple_spinner_item, shades);
        shadeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shadeSpinner.setAdapter(shadeArrayAdapter);

        final EditText scheduleNameET = (EditText) findViewById(R.id.schedule_name_edit_text);
        final Spinner shadeModeSpinner = (Spinner) findViewById(R.id.spinnerShadeScheduleMode);
        final Spinner daySpinner = (Spinner) findViewById(R.id.spinner_day_of_week);
        final Button createShadeScheduleButton = (Button) findViewById(R.id.create_shade_schedule_bttn);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.schedule_time_picker);

        createShadeScheduleButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                String scheduleName = scheduleNameET.getText().toString();
                if (scheduleName.equals("")) {
                    Toast t = Toast.makeText(CreateShadeScheduleActivity.this, R.string.bank_schedule_name_error, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    schedule = new Schedule();
                    int dayNumber  = daySpinner.getSelectedItemPosition();
                    schedule.setDay(daySpinner.getSelectedItem().toString());
                    schedule.setRun_mode(shadeModeSpinner.getSelectedItem().toString());
                    schedule.setItem_type("Shade");
                    schedule.setName(scheduleName);
                    schedule.setItem_id(shadeArrayAdapter.getItem(shadeSpinner.getSelectedItemPosition()).getId());
                    schedule.setStart_time(String.format(dayNumber+"%02d%02d", timePicker.getHour(), timePicker.getMinute()));
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
            Intent intent = new Intent(CreateShadeScheduleActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

    }

    private class GetShadesTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){

            shades = DynamoDBManager.getShadeList();

            return null;
        }
        protected void onPostExecute(Void results){
            super.onPostExecute(results);
            setupActivity();
        }

    }
}
