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
//        System.out.println("hour "+currentApiVersion+" "+android.os.Build.VERSION_CODES.LOLLIPOP_MR1+" "+(currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1));
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            return timePicker.getHour();
        else
            return timePicker.getCurrentHour();
    }

    @SuppressLint("NewApi")
    public static int getMinute(TimePicker timePicker) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
//        System.out.println("minute "+currentApiVersion+" "+android.os.Build.VERSION_CODES.LOLLIPOP_MR1+" "+(currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1));
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            return timePicker.getMinute();
        else
            return timePicker.getCurrentMinute();
    }

}
