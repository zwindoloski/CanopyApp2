package com.example.greengiant.canopy2;

/**
 * Created by Zack on 2/13/2016.
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
@DynamoDBTable(tableName = "Rooms")
public class Room {
    private int id;
    private String name;
    private int user_id;
    private int schedule_id;
    private int behavior_id;

    @DynamoDBHashKey(attributeName = "id")
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "user_id")
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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