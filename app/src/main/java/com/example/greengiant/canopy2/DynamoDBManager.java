package com.example.greengiant.canopy2;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
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
        ArrayList<Shade> resultList = new ArrayList<Shade>();
        for (Shade shade : result) {
            resultList.add(shade);
        }
        return resultList;
    }

    public static Shade getShade(int id){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        Shade shade = mapper.load(Shade.class, id);

        return shade;
    }

    public static void updateShade(Shade shade){

        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(shade);
    }
}
