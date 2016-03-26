package com.example.greengiant.canopy2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.http.HttpClient;
import com.nestlabs.sdk.GlobalUpdate;
import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Thermostat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.net.ssl.HttpsURLConnection;

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
    ArrayList<String> networks = new ArrayList<>();

    boolean connectedToShade = false;
    String deviceNetwork = "";
    String chosenNetwork;
    String password;
    String encodedPassword;
    WifiConfiguration deviceWifiConfig;
    int oldWifiConfigId;
    boolean successfullySetup = false;

    LinearLayout loadingLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shade);

        //setup nest for thermostats
        NestAPI.setAndroidContext(this);
        nest = NestAPI.getInstance();
        nest.setConfig(Constants.PRODUCT_ID, Constants.PRODUCT_SECRET, Constants.REDIRECT_URL);

        //setup wifi to connect to device
        myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wasEnabled = myWifiManager.isWifiEnabled();
        myWifiManager.setWifiEnabled(true);
        while(!myWifiManager.isWifiEnabled()){}

        //make and display dialog box to get shade id before proceeding
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
                                new GetRoomsAndThermostatsTask().execute();
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
    }

    public void setupActivity(){
        loadingLayout = (LinearLayout) findViewById(R.id.create_shade_loading_linearl);

        final Spinner networkSpinner = (Spinner) findViewById(R.id.spinner_networks);
        final ArrayAdapter<String> networkAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, networks );
        networkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkSpinner.setAdapter(networkAdapter);

        final EditText passwordET = (EditText) findViewById(R.id.network_password_edit_text);

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
                if (shadeName.equals("")) {
                    Toast t = Toast.makeText(CreateShadeActivity.this, R.string.blank_shade_name_error_toast, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    //show loading bar
                    loadingLayout.setVisibility(View.VISIBLE);

                    chosenNetwork = networkAdapter.getItem(networkSpinner.getSelectedItemPosition());
                    password = passwordET.getText().toString();
                    String secret = "somekey";

                    String hsh = "";
                    int kPos = 0;
                    for(int i=0; i<password.length(); i++){
                        hsh += (char)(Character.codePointAt(password,i) ^ Character.codePointAt(secret,kPos));
                        if(++kPos >= secret.length())
                            kPos = 0;
                    }
                    byte[] byteArray = new byte[0];
                    try {
                        byteArray = hsh.getBytes("UTF-16");
                        System.out.println(new String(Base64.decode(Base64.encode(byteArray,
                                Base64.DEFAULT), Base64.DEFAULT)));
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    encodedPassword = Base64.encodeToString(byteArray, Base64.DEFAULT);

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

    public class CreateShadeTask extends AsyncTask<Void, Void, Void>{
        public CreateShadeTask asyncObject;

        protected Void doInBackground(Void... voids){
            //connect to shade
            WifiReceiver wifiReceiver = new WifiReceiver();
            IntentFilter filter=new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(wifiReceiver, filter);

            oldWifiConfigId = myWifiManager.getConnectionInfo().getNetworkId();

            deviceWifiConfig = new WifiConfiguration();
//TODO            deviceNetwork = "\"Canopy-"+shadeID+"\"";
            deviceNetwork = "\"Connectify-Canopy\"";
            deviceWifiConfig.SSID = deviceNetwork;
            deviceWifiConfig.preSharedKey = "\"password\"";
            deviceWifiConfig.status = WifiConfiguration.Status.CURRENT;

            int id = myWifiManager.addNetwork(deviceWifiConfig);
            deviceWifiConfig.networkId = id;
            connectedToShade = false;
            myWifiManager.enableNetwork(id, true);

            System.out.println("connecting");
            //wait until connected
            while(!connectedToShade);
            System.out.println("connected");

            //give it the network and password
            HashMap<String, String> postDataParams = new HashMap<String, String>();
            postDataParams.put("ssid", chosenNetwork);
            postDataParams.put("password", encodedPassword);
            System.out.println("performing ssid post");
//TODO            String response = performPostCall("http://192.168.4.1", postDataParams);
            String response = performPostCall("http://posttestserver.com/post.php", postDataParams);
            System.out.println("received from post");
            System.out.println(response);

            //if response ok, make shade in AWS
            if(response != ""){
//TODO                DynamoDBManager.updateShade(shade);

                //send AWS id to shade
                postDataParams = new HashMap<String, String>();
                postDataParams.put("id", shade.getId());
                System.out.println("performing id post");
//TODO            String response = performPostCall("http://192.168.4.1", postDataParams);
                response = performPostCall("http://posttestserver.com/post.php", postDataParams);
                System.out.println("received from post");
                System.out.println(response);

                successfullySetup = true;
            }
            //if response failed
            else{
                successfullySetup = false;
            }
            //reconnect to users wifi
            reconnectToOldNetwork();
            return null;
        }

        @Override
        protected void onPreExecute() {
            asyncObject = this;
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    // stop async task if not in progress
                    if (asyncObject.getStatus() == AsyncTask.Status.RUNNING) {
                        asyncObject.cancel(false);
                        // Add any specific task you wish to do as your extended class variable works here as well.
                        Toast.makeText(getApplicationContext(), "We were unable to connect to your device, please double check the ID and try again.", Toast.LENGTH_LONG).show();

                        //reconnect to users old network
                        reconnectToOldNetwork();
                    }
                }
            }.start();
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);

            if(successfullySetup)
                Toast.makeText(getApplicationContext(), "Your device has been successfully set up.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "We were unable to connect your device to your network, please double check the SSID and Password and try again.\nYou may need to reset your Canopy device.", Toast.LENGTH_LONG).show();

            //done
            Intent intent = new Intent(CreateShadeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
    }

    public void reconnectToOldNetwork(){
        myWifiManager.enableNetwork(oldWifiConfigId, true);
        myWifiManager.disableNetwork(deviceWifiConfig.networkId);
        myWifiManager.removeNetwork(deviceWifiConfig.networkId);
        myWifiManager.saveConfiguration();
    }

    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info != null && info.isConnected()) {
                // Do your work.

                // e.g. To check the Network Name or other info:
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                if(ssid.contentEquals(deviceNetwork)) {
                    connectedToShade = true;
                }
            }
        }
    }

    private class GetRoomsAndThermostatsTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){

            rooms = DynamoDBManager.getRoomList();
            user = DynamoDBManager.getUser(Constants.USER_ID);
            String token = user.getAccess_token();

            //populate thermostat list
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

            //populate network list
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
                            if(scan.frequency < 2484 &&
                                    scan.SSID != "" &&
                                    !networks.contains(scan.SSID) &&
                                    (scan.capabilities.contains("WPA") ||
                                    scan.capabilities.contains("WEP"))
                                    ) {
                                networks.add(scan.SSID);
                                System.out.println(scan.SSID);
                            }
                        }
                    }
                }
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
