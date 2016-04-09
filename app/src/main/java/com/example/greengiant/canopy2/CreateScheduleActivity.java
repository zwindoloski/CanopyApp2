package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Zachary on 2/20/2016.
 */
public class CreateScheduleActivity extends CustomActivity {
    int SHADE_SCHED = 0;
    int ROOM_SCHED = 1;
    int USER_SCHED = 2;

    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        final Button shadeScheduleButton = (Button) findViewById(R.id.shadeScheduleBttn);
        shadeScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreateScheduleActivity.this.startActivityForResult(new Intent(CreateScheduleActivity.this, CreateShadeScheduleActivity.class), SHADE_SCHED);
            }
        });

        final Button roomScheduleButton = (Button) findViewById(R.id.roomScheduleBttn);
        roomScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreateScheduleActivity.this.startActivityForResult(new Intent(CreateScheduleActivity.this, CreateRoomScheduleActivity.class), ROOM_SCHED);
            }
        });

        final Button userScheduleButton = (Button) findViewById(R.id.homeScheduleBttn);
        userScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreateScheduleActivity.this.startActivityForResult(new Intent(CreateScheduleActivity.this, CreateHomeScheduleActivity.class), USER_SCHED);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Your schedule has been successfully created", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "We were unable to create your schedule", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
