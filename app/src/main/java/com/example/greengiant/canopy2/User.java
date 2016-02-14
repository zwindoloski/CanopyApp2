package com.example.greengiant.canopy2;

/**
 * Created by Zack on 2/13/2016.
 * Current Class for users, will likely change when user auth is added
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
@DynamoDBTable(tableName = "Users")
public class User {
    private int id;
    private String username;
    private String access_token;
    private String nest_pin;
    private int schedule_id;
    private int behavior_id;

    @DynamoDBHashKey(attributeName = "id")
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "access_token")
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @DynamoDBAttribute(attributeName = "nest_pin")
    public String getNest_pin() {
        return nest_pin;
    }

    public void setNest_pin(String nest_pin) {
        this.nest_pin = nest_pin;
    }

    @DynamoDBAttribute(attributeName = "schedule_id")
    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    @DynamoDBAttribute(attributeName = "behavior_id")
    public int getBehavior_id() {
        return behavior_id;
    }

    public void setBehavior_id(int behavior_id) {
        this.behavior_id = behavior_id;
    }

}
