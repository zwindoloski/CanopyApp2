package com.example.greengiant.canopy2;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;

/**
 * Created by Zack on 2/6/2016.
 */

public class DynamoDBManager {

    public static ArrayList<Shade> getShadeList(){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Shade> result = mapper.scan(
                Shade.class, scanExpression);
        ArrayList<Shade> resultList = new ArrayList<>();
        for (Shade shade : result) {
            resultList.add(shade);
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
        DynamoDBMapper mapper = new DynamoDBMapper(ddb, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES));

        mapper.delete(shade);
    }

    public static ArrayList<Room> getRoomList(){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Room> result = mapper.scan(
                Room.class, scanExpression);
        ArrayList<Room> resultList = new ArrayList<>();
        for (Room room : result) {
            resultList.add(room);
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

    public static ArrayList<Schedule> getScheduleList(){
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Schedule> result = mapper.scan(
                Schedule.class, scanExpression);
        ArrayList<Schedule> resultList = new ArrayList<>();
        for (Schedule schedule : result) {
            resultList.add(schedule);
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
}
