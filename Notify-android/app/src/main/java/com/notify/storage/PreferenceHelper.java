package com.notify.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by K2A on 15/11/15.
 */
public class PreferenceHelper {
    private static final String NOTIFY_PREFERENCE = "Notify Preference";

    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PreferenceHelper(Context context)
    {
        mContext = context;
        preferences = context.getSharedPreferences(NOTIFY_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static PreferenceHelper getInstance(Context context) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        return preferenceHelper;
    }

    public void setStringValue(String key, String value)
    {
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringValue(String key)
    {
        return preferences.getString(key,"");
    }

    public void removeValue(String key)
    {
        editor.remove(key);
        editor.apply();
    }

    public void emptyPreference(){
        editor.clear();
        editor.commit();
    }

    public boolean loggedIn(){
        try {
            if(getStringValue("session_id").equals("")){
                return false;
            }
            else
            {
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }
}
