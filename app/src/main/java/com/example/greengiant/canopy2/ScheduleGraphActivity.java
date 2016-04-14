package com.example.greengiant.canopy2;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Justin on 4/9/2016.
 */
public class ScheduleGraphActivity extends CustomActivity {
    String itemId;
    String itemType;
    String itemName;
    String[] modes;

    private ArrayList<Schedule> schedules;

    private PointsGraphSeries<DataPoint> scheduleSeries;
    private DataPoint[] scheduleDataPoints;

    private HashMap<DataPoint, String> scheduleIds;

    private PointsGraphSeries<DataPoint> manualSeries;
    private ArrayList<DataPoint> manualDataPoints;
    private PointsGraphSeries<DataPoint> energySeries;
    private ArrayList<DataPoint> energyDataPoints;
    private PointsGraphSeries<DataPoint> openSeries;
    private ArrayList<DataPoint> openDataPoints;
    private PointsGraphSeries<DataPoint> closedSeries;
    private ArrayList<DataPoint> closedDataPoints;
    private PointsGraphSeries<DataPoint> userSeries;
    private ArrayList<DataPoint> userDataPoints;
    private PointsGraphSeries<DataPoint> roomSeries;
    private ArrayList<DataPoint> roomDataPoints;
    private PointsGraphSeries<DataPoint> preservationSeries;
    private ArrayList<DataPoint> preservationDataPoints;
    private PointsGraphSeries<DataPoint> convenienceSeries;
    private ArrayList<DataPoint> convenienceDataPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_graph);

        itemId = getIntent().getExtras().getString("ITEM_ID");
        itemType = getIntent().getExtras().getString("ITEM_TYPE");
        itemName = getIntent().getExtras().getString("ITEM_NAME");
        modes = getIntent().getExtras().getStringArray("MODES");

        new GetShadeTask().execute();
    }

    private void setupActivity() {

        final TextView textViewShadeName = (TextView) findViewById(R.id.textViewShadeName);
        textViewShadeName.setText(itemName);

        final Button addScheduleButton = (Button) findViewById(R.id.addScheduleBttn);

        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Intent intent = new Intent(ScheduleGraphActivity.this, CreateScheduleActivity.class);
            intent.putExtra("ITEM_TYPE", itemType);
            intent.putExtra("ITEM_ID", itemId);
            System.out.println(modes[0]);
            intent.putExtra("MODES", modes);
            startActivity(intent);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        new GetShadeTask().execute();
    }


    private class GetShadeTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){

            //get schedules for this shade
            schedules = DynamoDBManager.getSchedules(itemId, itemType);

            scheduleDataPoints = new DataPoint[schedules.size()];
            manualDataPoints = new ArrayList<DataPoint>();
            energyDataPoints = new ArrayList<DataPoint>();
            openDataPoints = new ArrayList<DataPoint>();
            closedDataPoints = new ArrayList<DataPoint>();
            userDataPoints = new ArrayList<DataPoint>();
            roomDataPoints = new ArrayList<DataPoint>();
            convenienceDataPoints = new ArrayList<DataPoint>();
            preservationDataPoints = new ArrayList<DataPoint>();

            scheduleIds = new HashMap<DataPoint, String>();

            for (int i=0; i<schedules.size(); i++) {
                Resources res = getResources();
                String[] positions = res.getStringArray(R.array.shade_run_mode);
                Schedule schedule = schedules.get(i);
                String runMode = schedule.getRun_mode();
                int runModeIndex = Arrays.asList(positions).indexOf(runMode);

                int time = schedules.get(i).getStart_time();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(0);
                calendar.set(2015, Calendar.NOVEMBER, 1 + (time / 10000));
                calendar.set(Calendar.HOUR, (time / 100) % 100);
                calendar.set(Calendar.MINUTE, time % 100);

                DataPoint dp = new DataPoint(calendar.getTime(), 1);

                scheduleIds.put(dp, schedule.getId());

                if(runMode.compareToIgnoreCase(getString(R.string.manual_mode)) == 0)
                    manualDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.energy_mode)) == 0)
                    energyDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.open_mode)) == 0)
                    openDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.closed_mode)) == 0)
                    closedDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.user_mode)) == 0)
                    userDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.room_mode)) == 0)
                    roomDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.preservation_mode)) == 0)
                    preservationDataPoints.add(dp);
                else if(runMode.compareToIgnoreCase(getString(R.string.convenience_mode)) == 0)
                    convenienceDataPoints.add(dp);
            }

            scheduleSeries = new PointsGraphSeries<DataPoint>(scheduleDataPoints);

            manualSeries = new PointsGraphSeries<DataPoint>(manualDataPoints.toArray(new DataPoint[0]));
            energySeries = new PointsGraphSeries<DataPoint>(energyDataPoints.toArray(new DataPoint[0]));
            openSeries = new PointsGraphSeries<DataPoint>(openDataPoints.toArray(new DataPoint[0]));
            closedSeries = new PointsGraphSeries<DataPoint>(closedDataPoints.toArray(new DataPoint[0]));
            userSeries = new PointsGraphSeries<DataPoint>(userDataPoints.toArray(new DataPoint[0]));
            roomSeries = new PointsGraphSeries<DataPoint>(roomDataPoints.toArray(new DataPoint[0]));
            preservationSeries = new PointsGraphSeries<DataPoint>(preservationDataPoints.toArray(new DataPoint[0]));
            convenienceSeries = new PointsGraphSeries<DataPoint>(convenienceDataPoints.toArray(new DataPoint[0]));

            setShape(manualSeries, R.drawable.manual);
            setShape(energySeries, R.drawable.leaf);
            setShape(openSeries, R.drawable.open);
            setShape(closedSeries, R.drawable.closed);
            setShape(userSeries, R.drawable.user);
            setShape(roomSeries, R.drawable.room);
            setShape(preservationSeries, R.drawable.preservation);
            setShape(convenienceSeries, R.drawable.convenience);

            setListener(manualSeries);
            setListener(energySeries);
            setListener(openSeries);
            setListener(closedSeries);
            setListener(userSeries);
            setListener(roomSeries);
            setListener(preservationSeries);
            setListener(convenienceSeries);

            return null;
        }

        private void setListener(PointsGraphSeries<DataPoint> series){
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Intent intent = new Intent(ScheduleGraphActivity.this, ScheduleActivity.class);
                    intent.putExtra("SCHEDULE_ID", scheduleIds.get(dataPoint));
                    intent.putExtra("MODES", modes);
                    startActivity(intent);
                }
            });
        }

        private void setShape(PointsGraphSeries<DataPoint> series, int icon){
            final int drawIcon = icon;
            series.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    System.out.println("drawing "+drawIcon);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawIcon);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);

                    System.out.println("bitmap created");
                    paint.setAntiAlias(true);
                    paint.setFilterBitmap(true);
                    paint.setDither(true);
                    System.out.println("paint created");

                    canvas.drawBitmap(bitmap, x - 40, y - 40, paint);
                    System.out.println("done drawing");
                }
            });
        }

        protected void onPostExecute(Void results) {
            GraphView graph = (GraphView) findViewById(R.id.scheduleGraph);
            graph.removeAllSeries();
//            graph.addSeries(scheduleSeries);
            if(!manualDataPoints.isEmpty()) graph.addSeries(manualSeries);
            if(!energyDataPoints.isEmpty()) graph.addSeries(energySeries);
            if(!openDataPoints.isEmpty()) graph.addSeries(openSeries);
            if(!closedDataPoints.isEmpty()) graph.addSeries(closedSeries);
            if(!userDataPoints.isEmpty()) graph.addSeries(userSeries);
            if(!roomDataPoints.isEmpty()) graph.addSeries(roomSeries);
            if(!preservationSeries.isEmpty()) graph.addSeries(preservationSeries);
            if(!convenienceSeries.isEmpty()) graph.addSeries(convenienceSeries);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(0);
            calendar.set(2015, Calendar.NOVEMBER, 0);
//            calendar.set(Calendar.DATE, 3);
            calendar.set(Calendar.HOUR, 22);
            Date firstPoint = calendar.getTime();
            calendar.set(2015, Calendar.NOVEMBER, 8);
            calendar.set(Calendar.HOUR, 0);
            Date lastPoint = calendar.getTime();

            PointsGraphSeries<DataPoint> defaultScheduleSeries = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(firstPoint,0),
                    new DataPoint(lastPoint,0)
            });
            defaultScheduleSeries.setColor(Color.argb(0, 0, 0, 0));
//            defaultScheduleSeries.setColor(Color.rgb(128, 128, 128));
            graph.addSeries(defaultScheduleSeries);

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        // show normal x values
                        String label = super.formatLabel(value, isValueX);

                        long milliSeconds = Long.valueOf(label.replaceAll(",", "").toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis(milliSeconds);

                        DateFormat formatter = new SimpleDateFormat("h aa");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
                            formatter = new SimpleDateFormat("E");
                            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        }

                        return formatter.format(calendar.getTime());
                    } else {
                        // show default for y values
//                        return super.formatLabel(value, isValueX);
                        return "";
                    }
                }
            });

            graph.getViewport().setXAxisBoundsManual(true);
            calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(0);
//            calendar.set(Calendar.DATE, 4);
            calendar.set(2015, Calendar.NOVEMBER, 1);
            calendar.set(Calendar.HOUR, 0);
            graph.getViewport().setMinX(calendar.getTimeInMillis());
//            calendar.set(Calendar.DATE, 5);
            calendar.set(2015, Calendar.NOVEMBER, 2);
            graph.getViewport().setMaxX(calendar.getTimeInMillis());
            graph.getGridLabelRenderer().setNumHorizontalLabels(6);

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(2);
            graph.getGridLabelRenderer().setNumVerticalLabels(2);

            graph.getViewport().setScrollable(true);

            setupActivity();
        }
    }
}