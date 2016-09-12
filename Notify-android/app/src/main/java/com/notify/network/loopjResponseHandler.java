package com.notify.network;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.notify.utils.ProgressLoader;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by K2A on 16/01/16.
 */
public class loopjResponseHandler extends JsonHttpResponseHandler{

    private Context context;
    private ProgressLoader mProgressHUD;

    public loopjResponseHandler(Context context){
        this.context = context;
    }

    @Override
    public void onStart() {
        mProgressHUD = ProgressLoader.show(context, "Connecting", true, false, null );
        mProgressHUD.setMessage("loading..");
    }

    @Override
    public void onFinish() {
        mProgressHUD.dismiss();
   }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try{
            if(response.getString("status").equals("200")){
                super.onSuccess(statusCode, headers, response);
            }
            else if(response.getString("status").equals("401")){
                Toast.makeText(this.context,"Please Login Again.",Toast.LENGTH_LONG).show();
//                Utils.logout(this.context);
            }
            else
            {
                Toast.makeText(this.context,response.getString("error_message"),Toast.LENGTH_LONG).show();
            }
        }catch (JSONException e){
            //K2A========================K2A
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        try {
            Toast.makeText(this.context, "Network issues, please try after some time", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

