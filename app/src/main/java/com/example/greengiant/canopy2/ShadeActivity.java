package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.res.Resources;
import android.widget.Toast;

import com.amazonaws.auth.policy.Resource;

/**
 * Created by Zack on 2/7/2016.
 */
public class ShadeActivity extends CustomActivity {

    private String shadeId = "";
    private Shade shade = null;
    private LinearLayout overrideLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shade);

        shadeId = getIntent().getExtras().getString("SHADE_ID");
        new GetShadeTask().execute();
    }

    private void setupActivity() {
        String shadeName = shade.getName();
        double voltage = shade.getVoltage();

        final TextView textViewShadeName = (TextView) findViewById(R.id.textViewShadeName);
        textViewShadeName.setText(shadeName);

        final TextView textViewVoltage = (TextView) findViewById(R.id.textViewVoltage);
        textViewVoltage.setText(String.valueOf(voltage));

        final ImageView iconImageView = (ImageView) findViewById(R.id.imageViewIcon);
        if(shade.getVoltage() >= Constants.BRIGHT){
            iconImageView.setImageResource(R.drawable.sun);
        }
        else if(shade.getVoltage() >= Constants.PARTIAL){
            iconImageView.setImageResource(R.drawable.cloud_sun);
        }
        else if(shade.getVoltage() >= Constants.CLOUDY){
            iconImageView.setImageResource(R.drawable.cloud);
        }
        else if(shade.getVoltage() >= Constants.NIGHT){
            iconImageView.setImageResource(R.drawable.moon);
        }

        final ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GetShadeTask().execute();
            }
        });

        final Spinner spinnerRunMode = (Spinner) findViewById(R.id.spinnerShadeMode);
        if (shade.getRun_mode() != null) {
            Resources res = getResources();
            String[] positions = res.getStringArray(R.array.shade_run_mode);
            for (int i=0; i<positions.length;i++){
                if (positions[i].equalsIgnoreCase(shade.getRun_mode())){
                    spinnerRunMode.setSelection(i,true);
                }
            }
        }

        overrideLayout = (LinearLayout) findViewById(R.id.override_layout);
        if(shade.isOverriding())
            overrideLayout.setVisibility(View.VISIBLE);

        spinnerRunMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new UpdateRunModeTask().execute(new Integer[] {position});
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        final Button connectShadeButton = (Button) findViewById(R.id.connect_shade_bttn);
        connectShadeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShadeActivity.this, ConnectShadeActivity.class);
                intent.putExtra("SHADE_ID", shade.getId());
                ShadeActivity.this.startActivityForResult(intent, 0);
            }
        });

        final Button cancelOverrideButton = (Button) findViewById(R.id.cancel_override_bttn);
        cancelOverrideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new CancelOverideTask().execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Your shade has been successfully connected", Toast.LENGTH_LONG).show();
        }
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

    private class UpdateRunModeTask extends AsyncTask<Integer, Void, Void>{

        protected Void doInBackground(Integer...position){
            shade = DynamoDBManager.getShade(shadeId);

            Resources res = getResources();
            String[] positions = res.getStringArray(R.array.shade_run_mode);
            shade.setRun_mode(positions[position[0]]);

            DynamoDBManager.updateShade(shade);
            return null;
        }

        protected void onPostExecute(Void results){
            setupActivity();
        }
    }

    private class CancelOverideTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... voids){
            shade = DynamoDBManager.getShade(shadeId);

            shade.setCancel_override(true);

            DynamoDBManager.updateShade(shade);
            return null;
        }

        protected void onPostExecute(Void results){
            setupActivity();
        }
    }
}
