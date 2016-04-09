package com.example.greengiant.canopy2;

import android.app.Application;

/**
 * Created by Zack on 4/9/2016.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
