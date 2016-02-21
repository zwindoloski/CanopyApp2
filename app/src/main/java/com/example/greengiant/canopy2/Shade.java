package com.example.greengiant.canopy2;

/**
 * Created by Zack on 2/6/2016.
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
@DynamoDBTable(tableName = "Shades")
public class Shade {
    private String name;
    private String status;
    private String run_mode;
    private String id;
    private double voltage;
    private boolean away;
    private int current_temp;
    private int desired_temp;
    private String last_nest_update;
    private String thermostat_id;
    private int user_id;
    private String room_id;

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "voltage")
    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = "away")
    public boolean isAway() {
        return away;
    }

    public void setAway(boolean away) {
        this.away = away;
    }

    @DynamoDBAttribute(attributeName = "current_temp")
    public int getCurrent_temp() {
        return current_temp;
    }

    public void setCurrent_temp(int current_temp) {
        this.current_temp = current_temp;
    }

    @DynamoDBAttribute(attributeName = "desired_temp")
    public int getDesired_temp() {
        return desired_temp;
    }

    public void setDesired_temp(int desired_temp) {
        this.desired_temp = desired_temp;
    }

    @DynamoDBAttribute(attributeName = "last_nest_update")
    public String getLast_nest_update() {
        return last_nest_update;
    }

    public void setLast_nest_update(String last_nest_update) {
        this.last_nest_update = last_nest_update;
    }

    @DynamoDBAttribute(attributeName = "thermostat_id")
    public String getThermostat_id() {
        return thermostat_id;
    }

    public void setThermostat_id(String thermostat_id) {
        this.thermostat_id = thermostat_id;
    }

    @DynamoDBAttribute(attributeName = "user_id")
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @DynamoDBAttribute(attributeName = "room_id")
    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    @DynamoDBAttribute(attributeName = "run_mode")
    public String getRun_mode() {
        return run_mode;
    }

    public void setRun_mode(String run_mode) {
        this.run_mode = run_mode;
    }

    public String toString(){
        return name;
    }
}
