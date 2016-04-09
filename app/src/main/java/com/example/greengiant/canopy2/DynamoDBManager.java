package com.example.greengiant.canopy2;

import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zack on 2/6/2016.
 */

public class DynamoDBManager {

    public static ArrayList<Shade> getShadeList(){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
        String user_id = settings.getString("user_id", "");
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Shade> result = mapper.scan(
                Shade.class, scanExpression);
        ArrayList<Shade> resultList = new ArrayList<>();
        for (Shade shade : result) {
            if(shade.getUser_id().equals(user_id)) {
                resultList.add(shade);
            }
        }
        return resultList;
    }

    public static Shade getShade(String id){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        return mapper.load(Shade.class, id);
    }

    public static void updateShade(Shade shade){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(shade);
    }

    public static void deleteShade(Shade shade){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.delete(shade);
    }

    public static ArrayList<Room> getRoomList(){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
        String user_id = settings.getString("user_id", "");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Room> result = mapper.scan(
                Room.class, scanExpression);
        ArrayList<Room> resultList = new ArrayList<>();
        for (Room room : result) {
            if(room.getUser_id().equals(user_id)) {
                resultList.add(room);
            }
        }
        return resultList;
    }

    public static Room getRoom(String id){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        return mapper.load(Room.class, id);
    }

    public static void updateRoom(Room room){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(room);
    }

    public static User getUser(String id){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        return mapper.load(User.class, id);
    }

    public static void updateUser(User user){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(user);
    }

    public static void createUser(User user){
        AmazonDynamoDBClient ddb = NewUserActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(user);
    }

    public static ArrayList<Schedule> getScheduleList(){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        SharedPreferences settings = MyApplication.getInstance().getSharedPreferences("user_data", MyApplication.getInstance().MODE_PRIVATE);
        String user_id = settings.getString("user_id", "");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Schedule> result = mapper.scan(
                Schedule.class, scanExpression);
        ArrayList<Schedule> resultList = new ArrayList<>();
        for (Schedule schedule : result) {
            if(schedule.getUser_id().equals(user_id)) {
                resultList.add(schedule);
            }
        }
        return resultList;
    }
    public static void updateSchedule(Schedule schedule){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(schedule);
    }

    public static Schedule getSchedule(String id){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        return mapper.load(Schedule.class, id);
    }

    public static boolean validUsername(String username){
        AmazonDynamoDBClient ddb = NewUserActivity.clientManager.ddb();
        Condition scanFilterCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(username));
        Map<String, Condition> conditions = new HashMap<String, Condition>();
        conditions.put("username", scanFilterCondition);

        ScanRequest scanRequest = new ScanRequest()
                .withTableName("Users")
                .withScanFilter(conditions);

        ScanResult result = ddb.scan(scanRequest);
        return (result.getCount()==0);
    }

    public static boolean loginUser(String username, String password, Context context){
        AmazonDynamoDBClient ddb = LoginActivity.clientManager.ddb();
        Condition scanFilterCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(username));
        Condition scanFilterCondition2 = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(password));
        Map<String, Condition> conditions = new HashMap<String, Condition>();
        conditions.put("username", scanFilterCondition);
        conditions.put("password", scanFilterCondition2);

        ScanRequest scanRequest = new ScanRequest()
                .withTableName("Users")
                .withScanFilter(conditions)
                .withConditionalOperator(ConditionalOperator.AND);

        ScanResult result = ddb.scan(scanRequest);

        if(result.getCount() == 1){
            SharedPreferences settings = context.getSharedPreferences("user_data", context.MODE_PRIVATE);
            SharedPreferences.Editor edit = settings.edit();
            edit.clear();
            String user_id = result.getItems().get(0).get("id").getS();
            edit.putString("user_id", user_id);
            edit.commit();
            return true;
        }else{
            return false;
        }
    }
}
