package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
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

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade);

        NestAPI.setAndroidContext(this);
        nest = NestAPI.getInstance();
        nest.setConfig(Constants.PRODUCT_ID, Constants.PRODUCT_SECRET, Constants.REDIRECT_URL);

        new GetRoomsAndThermostatsTask().execute();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
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
                    shade.setUser_id(userId);
                    shade.setAway(true);
                    shade.setStatus("Open");
                    shade.setRun_mode(modeSpinner.getSelectedItem().toString());
                    shade.setThermostat_id(thermostatAdapter.getItem(thermostatSpinner.getSelectedItemPosition()).getId());
                    new CreateShadeTask().execute();
                }
            }
        });

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast t = Toast.makeText(CreateShadeActivity.this, "Success!", Toast.LENGTH_LONG);
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers){
                        Toast t = Toast.makeText(CreateShadeActivity.this, "Peers!", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onFailure(int reasonCode) {
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
            user = DynamoDBManager.getUser(userId);
            String token = user.getAccess_token();

            if(token != null) {
                System.out.println(token);

                nest.authWithToken(token, new NestListener.AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        // Handle success here. Start pulling from Nest API.
                        System.out.println("success");
                        fetchData();

                        user.setAccess_token("");
                        new RemoveUserAccessTokenTask().execute();
                        //toast to alert them
                        Toast.makeText(getApplicationContext(), "Your Nest account has been disconnected. Please reconnect it from the Settings menu.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAuthFailure(NestException e) {
                        // Handle exceptions here.
                        System.out.println("failure");
                    }

                    @Override
                    public void onAuthRevoked() {
                        // Your previously authenticated connection has become unauthenticated.
                        //remove access_token from user
                        user.setAccess_token("");
                        new RemoveUserAccessTokenTask().execute();
                        //toast to alert them
                        Toast.makeText(getApplicationContext(), "Your Nest account has been disconnected. Please reconnect it from the Settings menu.", Toast.LENGTH_LONG).show();
                    }
                });
            }

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

    private class RemoveUserAccessTokenTask extends AsyncTask<Void, Void, Void>{

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateUser(user);
            return null;
        }
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
