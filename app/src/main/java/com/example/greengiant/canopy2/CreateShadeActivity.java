package com.example.greengiant.canopy2;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    NestAPI nest;

    WifiManager myWifiManager;
    boolean wasEnabled;

    String shadeID;
    Spinner networkSpinner;
    ArrayList<String> networks = new ArrayList<>();
    ArrayAdapter<String> networkAdapter;
//    WifiP2pManager mManager;
//    WifiP2pManager.Channel mChannel;
//    BroadcastReceiver mReceiver;
//    IntentFilter mIntentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade);

        NestAPI.setAndroidContext(this);
        nest = NestAPI.getInstance();
        nest.setConfig(Constants.PRODUCT_ID, Constants.PRODUCT_SECRET, Constants.REDIRECT_URL);

        new GetRoomsAndThermostatsTask().execute();

        myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wasEnabled = myWifiManager.isWifiEnabled();
        myWifiManager.setWifiEnabled(true);
        while(!myWifiManager.isWifiEnabled()){}

//        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//        mChannel = mManager.initialize(this, getMainLooper(), null);
//        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
//
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void setupActivity(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                shadeID = userInput.getText().toString();
                                new GetNetworksTask().execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        networkSpinner = (Spinner) findViewById(R.id.spinner_networks);

        networkAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, networks );
        networkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkSpinner.setAdapter(networkAdapter);



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
                    shade.setUser_id(Constants.USER_ID);
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

    private class GetNetworksTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){
            boolean foundShade = false;

            networks = new ArrayList<String>();

            if(myWifiManager.isWifiEnabled()){
                if(myWifiManager.startScan()){
                    // List available APs
                    List<ScanResult> scans = myWifiManager.getScanResults();
                    if(scans != null && !scans.isEmpty()){
                        for (ScanResult scan : scans) {
                            if(scan.SSID.contains(shadeID))
                                foundShade = true;

                            networks.add(scan.SSID);
                            System.out.println(scan.SSID);
                        }
                    }
                }
            }

//            CreateShadeActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    networkAdapter.notifyDataSetChanged();
//                }
//            });


            return null;
        }
        protected void onPostExecute(Void results){
            super.onPostExecute(results);
        }
    }

    private class GetRoomsAndThermostatsTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){

            rooms = DynamoDBManager.getRoomList();
            user = DynamoDBManager.getUser(Constants.USER_ID);
            String token = user.getAccess_token();

            if(token != null) {
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

                        user.setAccess_token("");
                        new RemoveUserAccessTokenTask().execute();
                        //toast to alert them
                        Toast.makeText(getApplicationContext(), "Your Nest account has been disconnected. Please reconnect it from the Settings menu.", Toast.LENGTH_LONG).show();
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

}
