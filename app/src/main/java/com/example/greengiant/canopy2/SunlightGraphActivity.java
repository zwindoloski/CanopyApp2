package com.example.greengiant.canopy2;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 4/9/2016.
 */
public class SunlightGraphActivity extends CustomActivity {

    private String shadeId;
    private Shade shade;
    private Date[] sunlightDates;
    private int[] sunlightValues;
    private DataPoint[] sunlightDataPoints;
    private LineGraphSeries<DataPoint> sunlightSeries;
    private Spinner spinnerGranularity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sunlight_graph);

        //**** temp data to mock dates
        DateFormat iso8601 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        try {
            date = iso8601.parse("20160329221623");
        }catch(java.text.ParseException e){
            System.out.println(e);
        }

        System.out.println(date);

        int size = 365*24*4;
        Random r = new Random();
        sunlightDates = new Date[size];
        sunlightValues = new int[size];
        for (int i = 0; i < size; i++) {
            sunlightDates[i] = (Date) date.clone();
//            sunlightValues[i] = 512 + (512*date.getMonth()/12)*(date.getHours()-12)/12; //r.nextInt(1024);
            sunlightValues[i] = (int)(256*Math.sin(2*3.14*date.getHours()/24)+256*(date.getMonth()*30+date.getDay())/365) + 512;
            date.setTime(date.getTime() + 900000);
        }
        //****

        shadeId = getIntent().getExtras().getString("SHADE_ID");
        new GetShadeTask().execute();
    }

    private void setupActivity() {
        String shadeName = shade.getName();

        final TextView textViewShadeName = (TextView) findViewById(R.id.textViewShadeName);
        textViewShadeName.setText(shadeName);

        new UpdateGranularityTask().execute(new Integer[] {0});

        spinnerGranularity = (Spinner) findViewById(R.id.spinnerGranularity);

        spinnerGranularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new UpdateGranularityTask().execute(new Integer[] {position});
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }


    private class GetShadeTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            shade = DynamoDBManager.getShade(shadeId);
            return null;
        }

        protected void onPostExecute(Void results) {
            setupActivity();
        }
    }

    private class UpdateGranularityTask extends AsyncTask<Integer, Void, Void>{

        protected Void doInBackground(Integer...position){
            //get the required data points and do averaging

            if(position[0] == 0) {
                sunlightDataPoints = new DataPoint[sunlightDates.length];

                for (int i = 0; i < sunlightDataPoints.length; i++) {
                    sunlightDataPoints[i] = new DataPoint(sunlightDates[i], sunlightValues[i]);
                }
            }else{
                int days = sunlightDates.length/96;
                sunlightDataPoints = new DataPoint[days];

                int currentDate = 0;
                for(int i=0;i<sunlightDataPoints.length;i++){
                    int average = 0;

                    //read until a new date is found and average them
                    int numberToAverage = 0;
                    do {
                        average += sunlightValues[currentDate];
                        numberToAverage++;
                        currentDate++;
                    }while(currentDate < sunlightDates.length && sunlightDates[currentDate].getDay() == sunlightDates[currentDate-1].getDay());
                    average /= numberToAverage;
                    sunlightDataPoints[i] = new DataPoint(sunlightDates[currentDate-1], average);
                }
            }

            sunlightSeries = new LineGraphSeries<DataPoint>(sunlightDataPoints);
            sunlightSeries.setColor(Color.rgb(255, 148, 77));
            sunlightSeries.setThickness(8);

            return null;
        }

        protected void onPostExecute(Void results){
            GraphView graph = (GraphView) findViewById(R.id.sunlightGraph);
            graph.removeAllSeries();
            graph.addSeries(sunlightSeries);
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(SunlightGraphActivity.this));

            double min = 0.0;
            double max = sunlightDataPoints[sunlightDataPoints.length-1].getX();
            int horizontalLabels = 2;

            switch (spinnerGranularity.getSelectedItemPosition()){
                case(0): {
                    long offset = 86400000;
                    if (sunlightDates.length > 96)
                        min = max - offset;
                    else {
                        min = sunlightDataPoints[0].getX();
                        max = offset;
                    }
                    horizontalLabels = 6;

                    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                // show normal x values
                                String label = super.formatLabel(value, isValueX);

                                DateFormat formatter = new SimpleDateFormat("h a");
                                long milliSeconds = Long.valueOf(label.replaceAll(",", "").toString());
                                Date d = new Date(milliSeconds);
                                formatter.format(d.getTime());

                                return formatter.format(d.getTime());
                            } else {
                                // show default for y values
                                return super.formatLabel(value, isValueX);
                            }
                        }
                    });

                    break;
                }
                case(1): {
                    long offset = 604800000;
                    if (sunlightDates.length > 672)
                        min = max - offset;
                    else {
                        min = sunlightDataPoints[0].getX();
                        max = offset;
                    }
                    horizontalLabels = 4;
                    break;
                }
                case(2): {
                    long offset = 2592000000L;
                    if (sunlightDates.length > 2880)
                        min = max - offset;
                    else {
                        min = sunlightDataPoints[0].getX();
                        max = offset;
                    }
                    horizontalLabels = 4;
                    break;
                }
            }

            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(min);
            graph.getViewport().setMaxX(max);
            graph.getGridLabelRenderer().setNumHorizontalLabels(horizontalLabels);

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(1024);
            graph.getGridLabelRenderer().setNumVerticalLabels(4);

            graph.getViewport().setScrollable(true);
        }
    }
}