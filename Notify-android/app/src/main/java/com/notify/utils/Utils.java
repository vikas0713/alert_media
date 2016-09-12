package com.notify.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.notify.storage.PreferenceHelper;
import com.squareup.picasso.Picasso;


import org.json.JSONException;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;



/**
 * Created by K2A on 15/11/15.
 */
public class Utils {
    private static PreferenceHelper preference ;

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected())
        {
            return true;
        } else
        {
            return false;
        }
    }

    public static void logout(Context context){
        preference = new PreferenceHelper(context);
        preference.emptyPreference();
    }


    // json String to arraylist
    public static ArrayList<HashMap<String, Object>> toArrayList(String jsonString) throws JSONException {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
        if (jsonString != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                arrayList = mapper.readValue(jsonString, new TypeReference<List<HashMap>>() {});
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return arrayList;
    }

}
