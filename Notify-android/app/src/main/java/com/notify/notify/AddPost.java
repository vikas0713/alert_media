package com.notify.notify;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.provider.Settings;
import android.support.v7.view.ContextThemeWrapper;
import android.Manifest;
import android.support.v4.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.notify.network.LoopjClient;
import com.notify.network.loopjResponseHandler;
import com.notify.storage.PreferenceHelper;
import com.notify.utils.Constants;
import com.notify.utils.FileUtils;
import com.notify.utils.Utils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.content.ContextCompat;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class AddPost extends BaseActivity implements View.OnClickListener,LocationListener{

    private Uri fileUri;
    private static int TAKE_IMAGE = 1;
    private static int SELECT_IMAGE = 2;
    public static final int PERMISSION_CHECK = 123;
    public static final int LOCATION_PERMISSION_CHECK = 111;
    Spinner category;
    EditText description;
    private ProgressDialog pd;
    private PreferenceHelper preference;
    private File finalFile;
    ImageView photo;
    LocationManager locationManager;
    private String URL = "";
    private String latitude = "12.98";
    private  String longitude= "77.85";
    private static AsyncHttpClient client = new AsyncHttpClient();
    String[] categoryList = {"Select Category", "Waste Management", "Crime or Accident"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        category = (Spinner) findViewById(R.id.category);
        description = (EditText) findViewById(R.id.description);
        photo = (ImageView) findViewById(R.id.photo);

        photo.setOnClickListener(this);
        preference = new PreferenceHelper(this);
        URL = "";

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddPost.this, R.layout.custom_spinner_item,categoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);
        category.setSelection(0);


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

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (R.id.photo == id) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_CHECK);
            } else {
                this.showPicUploadOption();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBar.setTitle("Notify");
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if(id == R.id.done_btn) {
            if(category.getSelectedItemPosition() == 0){
                Toast.makeText(this,"Please select a category",Toast.LENGTH_LONG).show();
                return false;
            }
            if(URL.equals("")){
                Toast.makeText(getApplicationContext(), "Upload an Image before posting", Toast.LENGTH_SHORT).show();
                return true;
            }

            JSONObject params = new JSONObject();
            try {
                params.put("description",description.getText().toString());
                params.put("latitude",latitude);
                params.put("longitude",longitude);
                params.put("category", category.getSelectedItem().toString());
                params.put("user_id",preference.getStringValue("user_id"));
                params.put("image_url",URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LoopjClient.post(AddPost.this, "api/add", params, new loopjResponseHandler(AddPost.this) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Intent intent = new Intent(AddPost.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String filePath = "";
            if (requestCode == SELECT_IMAGE) {
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                if (uri == null || uri.toString().isEmpty()) {
                    return;
                }
                filePath = FileUtils.getPath(this, uri);
            } else if (requestCode == TAKE_IMAGE) {
                filePath = fileUri.getPath().toString();
            }

            try {
                pd = new ProgressDialog(AddPost.this);
                pd.setMessage("Uploading Image");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                finalFile = new File(filePath);

                RequestParams params = new RequestParams();
                params.put("photo", finalFile);

                client.post(Constants.getApiUrl(this) + "api/upload_image", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        pd.hide();
                        photo.setImageBitmap(null);
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        pd.hide();
                        photo.setImageBitmap(null);
                        try {
                            if (response.get("status").equals("200")) {
                                URL = response.getString("post_image_url");
                                photo.setImageBitmap(null);
                                Picasso.with(AddPost.this).invalidate(URL);
                                if (!URL.equals("")) {
                                    Picasso.with(AddPost.this)
                                            .load(URL)
                                            .placeholder(R.drawable.progress_animation)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .into(photo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                pd.hide();
                Toast.makeText(getApplicationContext(), "Image is corrupted", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getString("fileUri") != null) {
            fileUri = Uri.parse(savedInstanceState.getString("fileUri"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (fileUri != null && !fileUri.equals("")) {
            savedInstanceState.putString("fileUri", fileUri.getPath().toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CHECK: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.showPicUploadOption();
                } else {
                    Toast.makeText(getApplicationContext(), "image could not be uploaded", Toast.LENGTH_LONG).show();
                }
                return;
            }
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
