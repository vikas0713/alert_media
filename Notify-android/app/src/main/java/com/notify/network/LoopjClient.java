package com.notify.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.notify.storage.PreferenceHelper;
import com.notify.utils.Constants;

import cz.msebera.android.httpclient.Header;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by K2A on 10/01/16.
 */
public class LoopjClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
//    private static ProgressLoader mProgressHUD;
    private static JSONObject JSONreturnObj;

    public static void post(final Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(60 * 1000);
        PreferenceHelper preference = PreferenceHelper.getInstance(context);
        String userAgent = System.getProperty("http.agent");
        client.addHeader("USER_AGENT", userAgent);
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        StringEntity entity = null;
        try {
            params.put("device_id", Constants.getDeviceId(context));
            params.put("device_type", Constants.DEVICE_TYPE);

            entity = new StringEntity(params.toString());
        }catch (JSONException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, getAbsoluteUrl(context,url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(Context context,String relativeUrl) {
        return Constants.getApiUrl(context) + relativeUrl;
    }


}
