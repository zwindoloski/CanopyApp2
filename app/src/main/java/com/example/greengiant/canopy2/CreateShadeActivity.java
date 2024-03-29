package com.example.greengiant.canopy2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nestlabs.sdk.GlobalUpdate;
import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Thermostat;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zack on 2/14/2016. Used to create a new shade
 */
public class CreateShadeActivity extends CustomActivity {
    boolean isNew = false;
    Shade shade;
    ArrayList<Room> rooms = null;

    ThermostatSpinnerObject currentThermostat;
    Room currentRoom;

    Spinner thermostatSpinner;
    ArrayAdapter<ThermostatSpinnerObject> thermostatAdapter;
    ArrayList<Thermostat> thermostats = null;

    User user = null;
    NestAPI nest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_shade);

        //setup nest for thermostats
        NestAPI.setAndroidContext(this);
        nest = NestAPI.getInstance();
        nest.setConfig(Constants.PRODUCT_ID, Constants.PRODUCT_SECRET, Constants.REDIRECT_URL);

        //make and display dialog box to get shade id before proceeding
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        new GetRoomsAndThermostatsTask().execute();
    }

    public void setupActivity(){

        final Spinner roomSpinner = (Spinner) findViewById(R.id.spinner_rooms);
        final ArrayAdapter<Room> roomArrayAdapter = new ArrayAdapter<Room>(CreateShadeActivity.this, android.R.layout.simple_spinner_item, rooms);
        roomArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(roomArrayAdapter);

        final Spinner modeSpinner = (Spinner) findViewById(R.id.spinnerShadeModeCreate);

        final EditText shadeNameET = (EditText) findViewById(R.id.create_shade_edit_text);
        final EditText shadeSerialET = (EditText) findViewById(R.id.create_shade_serial_edit_text);
        final Button createShadeButton = (Button) findViewById(R.id.new_shade_create_bttn);

        if(!isNew){
            final TextView textViewShadeName = (TextView) findViewById(R.id.create_new_shade);
            textViewShadeName.setText(shade.getName());

            if(currentRoom != null)
                roomSpinner.setSelection(roomArrayAdapter.getPosition(currentRoom));
            if(currentThermostat != null)
                thermostatSpinner.setSelection(thermostatAdapter.getPosition(currentThermostat));
            modeSpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.shade_run_mode)).indexOf(shade.getRun_mode()));
            shadeNameET.setText(shade.getName());
            shadeSerialET.setText(shade.getDevice_serial_number());
            createShadeButton.setText("Update");
        }

        createShadeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String shadeName = shadeNameET.getText().toString();
                String shadeSerial = shadeSerialET.getText().toString();
                if (shadeName.equals("")) {
                    Toast t = Toast.makeText(CreateShadeActivity.this, R.string.blank_shade_name_error_toast, Toast.LENGTH_LONG);
                    t.show();
                } else if (shadeSerial.equals("")) {
                    Toast t = Toast.makeText(CreateShadeActivity.this, R.string.blank_shade_serial_error_toast, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    shade.setDevice_serial_number(shadeSerial);
                    if(roomSpinner.getSelectedItemPosition() != -1)
                        shade.setRoom_id(roomArrayAdapter.getItem(roomSpinner.getSelectedItemPosition()).getId());
                    shade.setName(shadeName);
                    SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
                    String user_id = settings.getString("user_id", "");
                    shade.setUser_id(user_id);
                    shade.setRun_mode(modeSpinner.getSelectedItem().toString());
                    if(thermostatSpinner.getSelectedItemPosition() != -1)
                        shade.setThermostat_id(thermostatAdapter.getItem(thermostatSpinner.getSelectedItemPosition()).getId());
                    new CreateShadeTask().execute();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            //toast
            Toast.makeText(getApplicationContext(), "Your shade has successfully been set up", Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            //toast
            Toast.makeText(getApplicationContext(), "Your shade was created but is not connected to a network.\n Please go to the shade page to connect it.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public class CreateShadeTask extends AsyncTask<Void, Void, Void>{
        public CreateShadeTask asyncObject;

        protected Void doInBackground(Void... voids){
            DynamoDBManager.updateShade(shade);
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);

            //done
            if(isNew) {
                Intent intent = new Intent(CreateShadeActivity.this, ConnectShadeActivity.class);
                intent.putExtra("SHADE_ID", shade.getId());
                CreateShadeActivity.this.startActivityForResult(intent, 0);
            }
            else
                finish();
        }
    }

    private class GetRoomsAndThermostatsTask extends AsyncTask<Void, Void, Void>{
        protected  Void doInBackground(Void... voids){

            if(getIntent().hasExtra("SHADE_ID")) {
                String shadeId = getIntent().getExtras().getString("SHADE_ID");
                shade = DynamoDBManager.getShade(shadeId);
                if(shade.getRoom_id() != null)
                    currentRoom = DynamoDBManager.getRoom(shade.getRoom_id());
            }
            else {
                shade = new Shade();
                isNew = true;
            }


            thermostatSpinner = (Spinner) findViewById(R.id.spinner_thermostats);

            rooms = DynamoDBManager.getRoomList();

            SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
            String user_id = settings.getString("user_id", "");
            user = DynamoDBManager.getUser(user_id);
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
        ArrayList<ThermostatSpinnerObject> thermostatObjects = new ArrayList<>();

        for(Thermostat t : thermostats){
            ThermostatSpinnerObject tso = new ThermostatSpinnerObject(t.getName(), t.getDeviceId());
            thermostatObjects.add(tso);

            if(!isNew) {
                if (shade.getId().compareToIgnoreCase(tso.getId()) == 0)
                    currentThermostat = tso;
            }
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
