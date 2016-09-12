package com.notify.notify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.notify.network.LoopjClient;
import com.notify.network.loopjResponseHandler;
import com.notify.storage.PreferenceHelper;
import com.notify.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Login extends BaseActivity implements View.OnClickListener{

    Button loginBtn;
    EditText mobile_number;
    private PreferenceHelper preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionBar.hide();
        preference = new PreferenceHelper(this);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        mobile_number = (EditText)findViewById(R.id.mobile_number);

        loginBtn.setOnClickListener(this);

        if(! preference.getStringValue("user_id").equals("")){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (R.id.loginBtn == id) {
            JSONObject params = new JSONObject();
            try {
                params.put("mobile_number",mobile_number.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LoopjClient.post(Login.this, "api/login", params, new loopjResponseHandler(Login.this) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        preference.setStringValue("user_id", response.getString("user_id"));
                        preference.setStringValue("mobile_number", response.getString("mobile_number"));
                        Intent intent = new Intent(Login.this, Otp.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
