package com.example.greengiant.canopy2;

/**
 * Created by Zack on 2/6/2016.
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
@DynamoDBTable(tableName = "Shades")
public class Shade {
    private String name;
    private String status;
    private int id;
    private double voltage;
    private int createdAt;

    @DynamoDBHashKey(attributeName = "id")
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "created_at")
    public int getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(int createdAt){
        this.createdAt = createdAt;
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
}
