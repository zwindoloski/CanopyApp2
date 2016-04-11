package com.example.greengiant.canopy2;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

/**
 * Created by Zachary on 2/20/2016.
 */
public class CreateScheduleActivity extends CustomActivity {
    Schedule schedule;
    String itemId;
    String itemType;
    String[] modes;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        itemId = getIntent().getExtras().getString("ITEM_ID");
        itemType = getIntent().getExtras().getString("ITEM_TYPE");
        modes = getIntent().getExtras().getStringArray("MODES");

        setupActivity();
    }

    public void setupActivity() {

        final Spinner shadeModeSpinner = (Spinner) findViewById(R.id.spinnerShadeScheduleMode);
        ArrayAdapter<String> shadeModeAdapter = new ArrayAdapter<String>(CreateScheduleActivity.this, android.R.layout.simple_spinner_item, modes);
        shadeModeSpinner.setAdapter(shadeModeAdapter);
        final Spinner daySpinner = (Spinner) findViewById(R.id.spinner_day_of_week);
        final Button createShadeScheduleButton = (Button) findViewById(R.id.create_shade_schedule_bttn);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.schedule_time_picker);

        createShadeScheduleButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                int dayNumber  = daySpinner.getSelectedItemPosition();
                int time = dayNumber*10000 + AppUtils.getHour(timePicker)*100 + AppUtils.getMinute(timePicker);

                schedule = new Schedule();
                schedule.setDay(daySpinner.getSelectedItem().toString());
                schedule.setRun_mode(shadeModeSpinner.getSelectedItem().toString());
                schedule.setItem_type(itemType);
                schedule.setItem_id(itemId);
                schedule.setStart_time(time);
                new CreateScheduleTask().execute();
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
}
