package com.example.greengiant.canopy2;

import android.content.Context;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;



/**
 * Class is used to get clients to the various AWS services.
 */
public class AmazonClientManager {

    private AmazonDynamoDBClient ddb = null;
    private Context context;

    public AmazonClientManager(Context context){
        this.context = context;
    }

    public AmazonDynamoDBClient ddb() {
        validateCredentials();
        return ddb;
    }

    public void validateCredentials() {
        if (ddb == null){
            initClients();
        }
    }

    public void initClients() {
        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                context,
                Constants.ACCOUNT_ID,
                Constants.IDENTITY_POOL_ID,
                Constants.UNAUTH_ROLE_ARN,
                null,
                Regions.US_EAST_1);

        ddb = new AmazonDynamoDBClient(credentials);
    }
}
