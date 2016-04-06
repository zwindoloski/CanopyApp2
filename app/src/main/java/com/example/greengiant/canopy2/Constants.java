package com.example.greengiant.canopy2;

/**
 * Created by Zack on 2/6/2016.
 */
public class Constants {

    //TODO let the user log in and store this value somehow
    public static final String USER_ID = "10";

    public static final String ACCOUNT_ID = "191556085409";
    public static final String IDENTITY_POOL_ID = "us-east-1:6b99a131-41a3-44a9-9739-9a6ce27e5b91";
    public static final String UNAUTH_ROLE_ARN = "arn:aws:iam::191556085409:role/Cognito_CanopyUnauth_Role_new";

    public static final String PRODUCT_ID = "41eebbb2-6456-48b5-bd08-e1a8fb8e458a";
    public static final String PRODUCT_SECRET = "zo7ZYVakbA918vlWtIELwD9EE";
    public static final String REDIRECT_URL = "http://localhost/";

    public static final int BRIGHT = 700;
    public static final int PARTIAL = 500;
    public static final int CLOUDY = 200;
    public static final int NIGHT = 0;
}
