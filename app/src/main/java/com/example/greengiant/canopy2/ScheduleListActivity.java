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
 * Created by Zack on 2/28/2016.
 */
public class ScheduleListActivity extends CustomListActivity {
    private ArrayList<Schedule> items = null;
    private ArrayList<String> labels = null;
    private int currentPosition = 0;
    private ArrayAdapter<String> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetScheduleListTask().execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        Intent intent = new Intent(ScheduleListActivity.this, ScheduleActivity.class);
        intent.putExtra("SCHEDULE_ID", items.get(position).getId() + "");
        startActivity(intent);
    }

    private class GetScheduleListTask extends AsyncTask<Void,Void, Void> {

        protected Void doInBackground(Void... inputs) {
            labels = new ArrayList<>();
            items = DynamoDBManager.getScheduleList();

            for (Schedule schedule : items) {
                labels.add(schedule.getName());
            }
            return null;
        }

        protected void onPostExecute(Void result){
            arrayAdapter = new ArrayAdapter<>(ScheduleListActivity.this,
                    R.layout.schedule_list_item, labels);
            setListAdapter(arrayAdapter);
        }
    }
}
