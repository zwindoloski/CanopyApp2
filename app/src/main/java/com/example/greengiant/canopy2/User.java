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
    private String run_mode;

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

    @DynamoDBAttribute(attributeName = "run_mode")
    public String getRun_mode() {
        return run_mode;
    }

    public void setRun_mode(String run_mode) {
        this.run_mode = run_mode;
    }

}
