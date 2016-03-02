package com.example.greengiant.canopy2;

import android.annotation.SuppressLint;
import android.widget.TimePicker;

/**
 * Created by Justin on 3/2/2016.
 */
public final class AppUtils {

    private AppUtils() {
    }

    @SuppressLint("NewApi")
    public static int getHour(TimePicker timePicker) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            return timePicker.getHour();
        else
            return timePicker.getCurrentHour();
    }

    @SuppressLint("NewApi")
    public static int getMinute(TimePicker timePicker) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            return timePicker.getMinute();
        else
            return timePicker.getCurrentMinute();
    }

    @SuppressLint("NewApi")
    public static void setMinute(TimePicker timePicker, int minute) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            timePicker.setMinute(minute);
        else
            timePicker.setCurrentMinute(minute);
    }
    
    @SuppressLint("NewApi")
    public static void setHour(TimePicker timePicker, int hour) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            timePicker.setHour(hour);
        else
            timePicker.setCurrentHour(hour);
    }
}
