package com.notify.notify;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notify.network.LoopjClient;
import com.notify.network.loopjResponseHandler;
import com.notify.storage.PreferenceHelper;
import com.notify.utils.FileUtils;
import com.notify.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity implements View.OnClickListener, LocationListener {
    ListView itemListView;
    ImageView add;
    TextView tab1;
    TextView tab2;
    private Uri fileUri;
    private static int TAKE_IMAGE = 1;
    private static int SELECT_IMAGE = 2;
    TextView tab3;
    private ArrayList<HashMap<String, Object>> itemList = null;
    private ItemListAdapter adapter = null;
    private String latitude = "12.98";
    private  String longitude= "77.85";
    LocationManager locationManager;
    public static final int LOCATION_PERMISSION_CHECK = 111;
    private PreferenceHelper preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemListView = (ListView) findViewById(R.id.itemListView);
        add = (ImageView) findViewById(R.id.add);

        preference = new PreferenceHelper(this);

        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        tab3 = (TextView) findViewById(R.id.tab3);

        tab1.setBackgroundResource(R.color.dark_blue);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        add.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CHECK);
        } else {
            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // check if enabled and if not send user to the GSP settings
            // Better solution would be to display a dialog and suggesting to
            // go to the settings
            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

        JSONObject params = new JSONObject();
        getServerData(params);
    }

    private void getServerData(JSONObject params){

        LoopjClient.post(MainActivity.this, "api/", params, new loopjResponseHandler(MainActivity.this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    String jsonString = response.getString("posts");
                    try {
                        itemList = Utils.toArrayList(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new ItemListAdapter(MainActivity.this, 0, itemList);
                    itemListView.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case LOCATION_PERMISSION_CHECK: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.showPicUploadOption();
                } else {
                    Toast.makeText(getApplicationContext(), "Location data cannot be got !!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void showPicUploadOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.NotifyAlertDialog));
        String options[] = {"Take Photo", "Choose Photo From Library"};
        builder.setTitle("Choose an option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                fileUri = Uri.fromFile(FileUtils.getOutputMediaFile(1));
                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                takePhotoIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                startActivityForResult(takePhotoIntent, TAKE_IMAGE);
                                break;
                            case 1:
                                Intent pickIntent = new Intent();
                                pickIntent.setType("image/*");
                                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                                pickIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                startActivityForResult(pickIntent, SELECT_IMAGE);
                                break;
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (R.id.tab1 == id) {
            tab1.setBackgroundResource(R.color.dark_blue);
            tab2.setBackgroundResource(R.color.blue);
            tab3.setBackgroundResource(R.color.blue);
            JSONObject params = new JSONObject();
            getServerData(params);
        } else if (R.id.tab2 == id) {
            tab2.setBackgroundResource(R.color.dark_blue);
            tab1.setBackgroundResource(R.color.blue);
            tab3.setBackgroundResource(R.color.blue);
            JSONObject params = new JSONObject();
            try {
                params.put("latitude",latitude);
                params.put("longitude",longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getServerData(params);
        } else if (R.id.tab3 == id) {
            tab3.setBackgroundResource(R.color.dark_blue);
            tab1.setBackgroundResource(R.color.blue);
            tab2.setBackgroundResource(R.color.blue);
            JSONObject params = new JSONObject();
            try {
                params.put("user_id",preference.getStringValue("user_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getServerData(params);
        } else if (R.id.add == id) {
            Intent intent = new Intent(MainActivity.this, AddPost.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBar.setTitle("Notify");
        getMenuInflater().inflate(R.menu.dashboard, menu);
        actionBar.setDisplayHomeAsUpEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logout) {
            Utils.logout(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        Log.d("Message: --------------","Location changed, " + location.getAccuracy() + " , " + latitude+ "," + latitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
