package com.example.greengiant.canopy2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;

/**
 * Created by Zack on 2/28/2016.
 */
public class ScheduleActivity extends Activity {
    private String scheduleId = "";
    private Schedule schedule = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        scheduleId = getIntent().getExtras().getString("SCHEDULE_ID");
        new GetScheduleTask().execute();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupActivity() {
        final String scheduleName = schedule.getName();

        final TextView textViewScheduleName = (TextView) findViewById(R.id.textViewScheduleName);
        textViewScheduleName.setText(scheduleName);

        final Spinner spinnerDayPosition = (Spinner) findViewById(R.id.spinnerDayOfWeek);
        if (schedule.getDay() != null) {
            Resources res = getResources();
            String[] position = res.getStringArray(R.array.days_of_week);
            for (int i = 0; i < position.length; i++) {
                if (position[i].equalsIgnoreCase(schedule.getDay())) {
                    spinnerDayPosition.setSelection(i, true);
                    break;
                }
            }
        }

        final Spinner spinnerScheduleMode = (Spinner) findViewById(R.id.spinnerScheduleMode);
        if (schedule.getRun_mode() != null) {
            Resources res = getResources();
            String[] positions = res.getStringArray(R.array.shade_run_mode);
            for (int i = 0; i < positions.length; i++) {
                if (positions[i].equalsIgnoreCase(schedule.getRun_mode())) {
                    spinnerScheduleMode.setSelection(i, true);
                    break;
                }
            }
        }

        final TimePicker scheduleTimePicker = (TimePicker) findViewById(R.id.schedule_update_time_picker);
        int minutes = schedule.getStart_time()%100;
        int hours = (schedule.getStart_time()/100)%100;
        AppUtils.setMinute(scheduleTimePicker, minutes);
        AppUtils.setHour(scheduleTimePicker, hours);

        final Button updateScheduleButton = (Button) findViewById(R.id.update_schedule_bttn);
        updateScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                schedule.setDay(spinnerDayPosition.getSelectedItem().toString());
                schedule.setRun_mode(spinnerScheduleMode.getSelectedItem().toString());
                int dayNumber = spinnerDayPosition.getSelectedItemPosition();
                int time = dayNumber*10000 + AppUtils.getHour(scheduleTimePicker)*100 + AppUtils.getMinute(scheduleTimePicker);
                schedule.setStart_time(time);
                new UpdateAttributeTask().execute();
            }
        });

    }

    private class GetScheduleTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            schedule = DynamoDBManager.getSchedule(scheduleId);
            return null;
        }

        protected void onPostExecute(Void results) {
            setupActivity();
        }
    }

    private class UpdateAttributeTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            DynamoDBManager.updateSchedule(schedule);
            return null;
        }

        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }


    }
}
