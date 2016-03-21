package com.example.greengiant.canopy2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.res.Resources;

import com.amazonaws.auth.policy.Resource;

/**
 * Created by Zack on 2/7/2016.
 */
public class ShadeActivity extends Activity {

    private String shadeId = "";
    private Shade shade = null;

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

        spinnerRunMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Resources res = getResources();
                String[] positions = res.getStringArray(R.array.shade_run_mode);
                shade.setRun_mode(positions[position]);
                new UpdateAttributeTask().execute();
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

    private class UpdateAttributeTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateShade(shade);
            return null;
        }

    }
}
