package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nestlabs.sdk.GlobalUpdate;
import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Thermostat;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Zack on 2/14/2016. Used to create a new shade
 */
public class CreateShadeActivity extends Activity {
    Shade shade = new Shade();
    ArrayList<Room> rooms = null;

    Spinner thermostatSpinner;
    ArrayAdapter<ThermostatSpinnerObject> thermostatAdapter;
    ArrayList<Thermostat> thermostats = null;

    User user = null;
    int userId = 10;
    NestAPI nest;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade);

        NestAPI.setAndroidContext(this);
        nest = NestAPI.getInstance();
        nest.setConfig(Constants.PRODUCT_ID, Constants.PRODUCT_SECRET, Constants.REDIRECT_URL);

        new GetRoomsAndThermostatsTask().execute();
    }

    public void setupActivity(){
        final Spinner roomSpinner = (Spinner) findViewById(R.id.spinner_rooms);
        final ArrayAdapter<Room> roomArrayAdapter = new ArrayAdapter<Room>(CreateShadeActivity.this, android.R.layout.simple_spinner_item, rooms);
        roomArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(roomArrayAdapter);

        final Spinner modeSpinner = (Spinner) findViewById(R.id.spinnerShadeModeCreate);

        final EditText shadeNameET = (EditText) findViewById(R.id.create_shade_edit_text);
        final Button createShadeButton = (Button) findViewById(R.id.new_shade_create_bttn);
        createShadeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String shadeName = shadeNameET.getText().toString();
                if(shadeName.equals("")){
                    Toast t = Toast.makeText(CreateShadeActivity.this, R.string.blank_shade_name_error_toast, Toast.LENGTH_LONG);
                    t.show();
                }
                else {
                    shade.setRoom_id(roomArrayAdapter.getItem(roomSpinner.getSelectedItemPosition()).getId());
                    shade.setName(shadeName);
                    shade.setUser_id(10);
                    shade.setAway(true);
                    shade.setStatus("Open");
                    shade.setRun_mode(modeSpinner.getSelectedItem().toString());
                    shade.setThermostat_id(thermostatAdapter.getItem(thermostatSpinner.getSelectedItemPosition()).getId());
                    new CreateShadeTask().execute();
                }
            }
        });
    }

    private class CreateShadeTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateShade(shade);
            return null;
        }

        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            Intent intent = new Intent(CreateShadeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

    }

    private class GetRoomsAndThermostatsTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){

            rooms = DynamoDBManager.getRoomList();
            String token = DynamoDBManager.getUser(userId).getAccess_token();

            System.out.println(token);

            nest.authWithToken(token, new NestListener.AuthListener() {
                @Override
                public void onAuthSuccess() {
                    // Handle success here. Start pulling from Nest API.
                    System.out.println("success");
                    fetchData();
                }

                @Override
                public void onAuthFailure(NestException e) {
                    // Handle exceptions here.
                    System.out.println("failure");
                }

                @Override
                public void onAuthRevoked() {
                    // Your previously authenticated connection has become unauthenticated.
                    // Recommendation: Relaunch an auth flow with nest.launchAuthFlow().
                    System.out.println("revoked");
                }
            });

            return null;
        }
        protected void onPostExecute(Void results){
            super.onPostExecute(results);
            setupActivity();
        }

    }

    private void fetchData() {
        nest.addGlobalListener(new NestListener.GlobalListener() {
            @Override
            public void onUpdate(@NonNull GlobalUpdate update) {
                thermostats = update.getThermostats();
                updateThermostats();
            }
        });
    }

    private void updateThermostats(){
        thermostatSpinner = (Spinner) findViewById(R.id.spinner_thermostats);
        ArrayList<ThermostatSpinnerObject> thermostatObjects = new ArrayList<>();

        for(Thermostat t : thermostats){
            thermostatObjects.add(new ThermostatSpinnerObject(t.getName(), t.getDeviceId()));
        }

        thermostatAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,thermostatObjects );
        thermostatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thermostatSpinner.setAdapter(thermostatAdapter);
    }
}
