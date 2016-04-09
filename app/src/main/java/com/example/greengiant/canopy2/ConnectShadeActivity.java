package com.example.greengiant.canopy2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.os.SystemClock;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.res.Resources;
import android.widget.Toast;

import com.amazonaws.auth.policy.Resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Justin on 3/29/2016.
 */
public class ConnectShadeActivity extends CustomActivity{

    private String shadeID = "";
    private Shade shade = null;

    WifiManager myWifiManager;
    boolean wasEnabled;

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

    WifiReceiver wifiReceiver;
    IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_shade);

        shadeID = getIntent().getExtras().getString("SHADE_ID");

        //setup wifi to connect to device
        myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wasEnabled = myWifiManager.isWifiEnabled();
        myWifiManager.setWifiEnabled(true);
        while(!myWifiManager.isWifiEnabled()){}

//        wifiReceiver = new WifiReceiver();
//        filter = new IntentFilter();
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        new GetShadeTask().execute();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    private void setupActivity() {
        String shadeName = shade.getName();

        final TextView textViewShadeName = (TextView) findViewById(R.id.textViewShadeName);
        textViewShadeName.setText(shadeName);

        loadingLayout = (LinearLayout) findViewById(R.id.create_shade_loading_linearl);

        final Spinner networkSpinner = (Spinner) findViewById(R.id.spinner_networks);
        final ArrayAdapter<String> networkAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, networks );
        networkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkSpinner.setAdapter(networkAdapter);

        final EditText passwordET = (EditText) findViewById(R.id.network_password_edit_text);

        final Button connectShadeButton = (Button) findViewById(R.id.connect_shade_bttn);
        connectShadeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //show loading bar
                loadingLayout.setVisibility(View.VISIBLE);

                //read network info and encode password
                chosenNetwork = networkAdapter.getItem(networkSpinner.getSelectedItemPosition());
                password = passwordET.getText().toString();
                encodedPassword = encodeString(password);

                //start shade connection setup
                new ConnectShadeTask().execute();
            }
        });
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }

    public void onResume(){
        super.onResume();

        wifiReceiver = new WifiReceiver();
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(wifiReceiver, filter);
    }


    public String encodeString(String str){
        String secret = "somekey";

        String hsh = "";
        int kPos = 0;
        for(int i=0; i<str.length(); i++){
            hsh += (char)(Character.codePointAt(str,i) ^ Character.codePointAt(secret,kPos));
            if(++kPos >= secret.length())
                kPos = 0;
        }
        byte[] byteArray = new byte[0];
        try {
            byteArray = hsh.getBytes("UTF-8");
            System.out.println(new String(Base64.decode(Base64.encode(byteArray,
                    Base64.DEFAULT), Base64.DEFAULT)));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public class ConnectShadeTask extends AsyncTask<Void, Void, Void>{
        public ConnectShadeTask asyncObject;

        protected Void doInBackground(Void... voids){
            Date currentDate = new Date();

            //connect to shade
            oldWifiConfigId = myWifiManager.getConnectionInfo().getNetworkId();

            deviceWifiConfig = new WifiConfiguration();
            deviceNetwork = "\"Canopy-"+shade.getDevice_serial_number()+"\"";
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

            successfullySetup = false;

            //give it the network and password
            HashMap<String, String> postDataParams = new HashMap<String, String>();
            postDataParams.put("ssid", chosenNetwork);
            postDataParams.put("password", encodedPassword);
            postDataParams.put("aws_id", shade.getId());
            System.out.println("performing ssid post");
            String response = performPostCall("http://192.168.4.1", postDataParams);
//            String response = performPostCall("http://posttestserver.com/post.php", postDataParams);
            System.out.println("received: " + response);

            //reconnect to users wifi
            reconnectToOldNetwork();

            //if response ok, data was received
            if(response.compareToIgnoreCase("") != 0){
                //wait 10 seconds before checking if connection was succcessful
                SystemClock.sleep(10000);

                //check if aws has been updated
                shade = DynamoDBManager.getShade(shade.getId());
                DateFormat iso8601 = new SimpleDateFormat("yyyyMMddHHmmss");
                Date shadeDate = currentDate;
//                20160329221623
                try {
                    shadeDate = iso8601.parse(shade.getUpdated_at());
                }catch(java.text.ParseException e){
                    System.out.println(e);
                }

                //if shadeDate has been set and is after the last time we captured then we good
                if(currentDate.before(shadeDate)){
                    successfullySetup = true;
                }

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            asyncObject = this;
            new CountDownTimer(30000, 1000) {
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

            if(successfullySetup) {
                setResult(RESULT_OK);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "We were unable to connect your device to your network, please double check the SSID and Password and try again.\nYou may need to reset your Canopy device.", Toast.LENGTH_LONG).show();
                loadingLayout.setVisibility(View.GONE);
            }
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

    private class GetShadeTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids){
            shade = DynamoDBManager.getShade(shadeID);

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
