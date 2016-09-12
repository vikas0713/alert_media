package com.notify.utils;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by K2A on 10/01/16.
 */
public class Constants {
    public static final String API_VERSION = "1";
    public static final String APP_VERSION = "1.0.0";
    public static final String DEVICE_TYPE = "a";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "4yLjmRX5QX9IvkbIQixVFFTUO";
    public static final String TWITTER_SECRET = "TEvVckShqbQnFquHTRoNCIxyOGWk15ay3Pv6DC9sFr1XdtARj9";


    public static String BASE_URL = "";

    public static String getDeviceId(Context context){
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }

    public static String getApiUrl(Context context){
//        BASE_URL = "http://100.96.244.205/";
//        BASE_URL = "http://10.0.2.2/";
        BASE_URL = "http://188.166.208.228:8000/";
        return BASE_URL;
    }
}
