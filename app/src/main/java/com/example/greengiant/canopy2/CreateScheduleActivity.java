package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Zachary on 2/20/2016.
 */
public class CreateScheduleActivity extends Activity {

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        final Button shadeScheduleButton = (Button) findViewById(R.id.shadeScheduleBttn);
        shadeScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(CreateScheduleActivity.this, CreateShadeScheduleActivity.class));
            }
        });

        final Button roomScheduleButton = (Button) findViewById(R.id.roomScheduleBttn);
        roomScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(CreateScheduleActivity.this, CreateRoomScheduleActivity.class));
            }
        });

        final Button userScheduleButton = (Button) findViewById(R.id.homeScheduleBttn);
        userScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(CreateScheduleActivity.this, CreateHomeScheduleActivity.class));
            }
        });
    }
}
