package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.incture.cherrywork.freshdirect.DB.DBUtil;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.Network.DownloadAsync;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.BarcodeQrcodeCustomActivity;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.GPSTracker;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.OnSwipeTouchListener;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Arun on 18-08-2016.
 */
public class StartTripActivity extends Activity implements AsyncResponse {
    private ImageView startTrip;
    private static final int EXTERNAL_STORAGE = 1;
    private ArrayList<OrderModel> orderModelList = new ArrayList<>();
    private ArrayList<OrderModel> Orders = new ArrayList<>();
    private ArrayList<OrderModel> packagesList = new ArrayList<>();
    private OrderModel orderModel = new OrderModel();
    private String status = "";
    private String message = "";
    private OrderModel oModel;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private DBUtil db;
    private String TripId = "";
    private ImageView logout;
    private IntentIntegrator integrator;
    private boolean skipToteScan;
    private String APPVERSION="";
    private String downloadURL="";
    private boolean updateRequired = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);

        if (Util.isOnline(this)) {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-device-type", "android");
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());
            NetworkConnector connect = new NetworkConnector(StartTripActivity.this, NetworkConnector.TYPE_GET, FDUrls.APP_UPDATE, headers, null, this);
            connect.execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        db = new DBUtil();
        logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        startTrip = (ImageView) findViewById(R.id.start_trip);
        startTrip.setOnTouchListener(new OnSwipeTouchListener(StartTripActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(StartTripActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight(float xvalue) {
                // move the imageview from right to left
                GPSTracker tracker = new GPSTracker(StartTripActivity.this);
                if (tracker.canGetLocation()) {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                    if (!(latitude == 0.0 || longitude == 0.0)) {
                        if (Util.isOnline(StartTripActivity.this)) {
                            TranslateAnimation animation = new TranslateAnimation(0.0f, xvalue, 0.0f, 0.0f);
                            animation.setDuration(650);
                            animation.setRepeatCount(0);
                            animation.setRepeatMode(0);
                            animation.setFillAfter(true);
                            startTrip.startAnimation(animation);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    integrator = new IntentIntegrator(StartTripActivity.this);
                                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                                    integrator.setPrompt("Scan Manifest");
                                    integrator.setCameraId(0);  // Use a specific camera of the device
                                    integrator.setBeepEnabled(true);
                                    integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                                    integrator.initiateScan();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        } else {
                            startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
                            Toast.makeText(StartTripActivity.this, "You have to be online to scan TRIP-ID/Vehicle", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(StartTripActivity.this, "GPS has to be set to High Accuracy,change in location settings", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
                    tracker.showSettingsAlert();

                }

            }

            public void onSwipeLeft(float y) {
                // Toast.makeText(StartTripActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                // Toast.makeText(StartTripActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void logout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(StartTripActivity.this);
        dialog.setTitle("Logout");
        dialog.setMessage("Are you sure to logout?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Util.isOnline(StartTripActivity.this)) {
                            Util.deleteTables();
                            FDPreferences.setTripId("null");
                            FDPreferences.resetPreferences();
                            FDPreferences.setAccessToken("null");
                            dialog.dismiss();
                            startActivity(new Intent(StartTripActivity.this, LoginActivity.class));
                        } else {

                            Toast.makeText(StartTripActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));

            } else {
                if (Util.isOnline(StartTripActivity.this)) {
                    TripId = result.getContents();
                    startTrip();
                } else {
                    startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
                    Toast.makeText(StartTripActivity.this, "You have to be online to scan TRIP-ID/Vehicle", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        if (type == NetworkConnector.TYPE_PUT) {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }
            try {
                JSONObject mainobject = new JSONObject(actualString);
                status = mainobject.optString("status");
                message = mainobject.optString("message");
                if (status.equalsIgnoreCase("SUCCESS")) {
                    JSONObject data = mainobject.optJSONObject("data");
                    if (data.has("time")) {
                        FDPreferences.setStartTime(data.optString("time"));
                    } else {
                        FDPreferences.setStartTime("");
                    }
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("x-email-id", FDPreferences.getEmail());
                    headers.put("x-access-token", FDPreferences.getAccessToken());

                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("courier_fname", FDPreferences.getFirstname());
                        payload.put("courier_lname", FDPreferences.getLastname());
                        payload.put("courier_id", FDPreferences.getEmployeeID());
                        payload.put("scan_in", TimeUtils.getCurrentTime());
                        payload.put("scan_out", TimeUtils.getCurrentTime());
                        payload.put("carrier", FDPreferences.getCarrierName());

                        //NetworkConnector connect = new NetworkConnector(StartTripActivity.this, NetworkConnector.TYPE_POST, FDUrls.FETCH_ORDERS+result.getContents(), headers, payload.toString(), this);
                        NetworkConnector connect = new NetworkConnector(StartTripActivity.this, NetworkConnector.TYPE_POST, FDUrls.FETCH_ORDERS + TripId, headers, payload.toString(), this);
                        connect.execute();
                        //setUpOrdersListData(orderModelList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(StartTripActivity.this, message, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       else if (url.equalsIgnoreCase(FDUrls.APP_UPDATE)) {
            try {

                JSONObject main_obj = new JSONObject(output);
                status = main_obj.optString("status");
                message = main_obj.optString("message");

                if (status.equalsIgnoreCase("success")) {
                    if (main_obj.has("data")) {
                        JSONObject data = main_obj.optJSONObject("data");
                        if (data != null) {
                            APPVERSION = data.optString("appVersion");
                            downloadURL = data.optString("downloadURL");



                            if (!APPVERSION.equalsIgnoreCase(Util.getCurrentAppVersion(this))) {
                                updateRequired = true;
                                showUpdateDialog();
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }
            parse(actualString);
        }
    }

    private void showUpdateDialog() {
        if (updateRequired) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(StartTripActivity.this);
            dialog.setTitle("App update available");
            dialog.setMessage("New version of app is available, update now?");
            dialog.setCancelable(true);
            dialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //GPSTracker tracker = new GPSTracker(_context);
                            // tracker.stopUsingGPS();
                            isStoragePermissionGranted();


                        }
                    });
            dialog.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            AlertDialog alert = dialog.create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                new DownloadAsync(StartTripActivity.this, FDUrls.APP_URL + downloadURL).execute();
                return true;

            } else {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void parse(String output) {
        orderModelList = new ArrayList<>();
        orderModel = new OrderModel();
        packagesList = new ArrayList<>();
        try {
            JSONObject mainobject = new JSONObject(output);
            status = mainobject.optString("status");
            message = mainobject.optString("message");
            if (status.equalsIgnoreCase("SUCCESS")) {
                //JSONObject data = mainobject.optJSONObject("data");
                // Util.deleteTables();


                JSONObject trip = mainobject.optJSONObject("data");
                if (trip != null) {
                    orderModel.status = trip.optString("status");
                    orderModel.vehicle = trip.optString("vehicle");
                    if (trip.has("skipToteScan")) {
                        skipToteScan = trip.optBoolean("skipToteScan");
                    }

                    JSONArray orders = trip.optJSONArray("orders");
                    Orders = new ArrayList<>();
                    if (orders != null && orders.length() > 0) {
                        for (int o = 0; o < orders.length(); o++) {
                            oModel = new OrderModel();
                            JSONObject orderObject = orders.optJSONObject(o);
                            oModel.order_id = orderObject.optString("order_id");
                            oModel.erporder_id = orderObject.optString("erporder_id");
                            oModel.orderStatus = orderObject.optString("status");
                            oModel.tip = orderObject.optString("tip");
                            oModel.mobile = orderObject.optString("mobile_number");
                            oModel.window_start = orderObject.optString("window_start");
                            oModel.window_end = orderObject.optString("window_end");
                            oModel.delivery_date = orderObject.optString("delivery_date");
                            oModel.delivery_instructions = orderObject.optString("delivery_instructions");
                            oModel.unattended_instructions = orderObject.optString("unattended_instructions");
                            oModel.service_type = orderObject.optString("service_type");
                            oModel.first_name = orderObject.optString("first_name");
                            oModel.last_name = orderObject.optString("last_name");


                            JSONObject address = orderObject.optJSONObject("address");
                            oModel.address_line_1 = address.optString("address_line_1");
                            oModel.address_line_2 = address.optString("address_line_2");
                            oModel.apartment = address.optString("apartment");
                            oModel.city = address.optString("city");
                            oModel.state = address.optString("state");
                            oModel.zip = address.optString("zip");
                            oModel.lat = address.optDouble("latitude");
                            oModel.lng = address.optDouble("longitude");

                            JSONArray packageinfo = orderObject.optJSONArray("packageinfo");

                            if (packageinfo != null && packageinfo.length() > 0) {
                                ArrayList<OrderModel> individualOrderPackages = new ArrayList<>();
                                for (int p = 0; p < packageinfo.length(); p++) {
                                    JSONObject packageobject = packageinfo.optJSONObject(p);
                                    OrderModel packageModel = new OrderModel();
                                    packageModel.bag_id = packageobject.optString("bag_id");
                                    packageModel.bin_loc = packageobject.optString("bin_loc");
                                    packageModel.isAlcohol = packageobject.optBoolean("alcohol_ind");
                                    packageModel.isMeat = packageobject.optBoolean("frozen_ind");
                                    packagesList.add(packageModel);
                                    individualOrderPackages.add(packageModel);
                                }
                                oModel.packages = individualOrderPackages;
                            }

                            Orders.add(oModel);
                            //adding orders and packages into db

                            db.addOrders(Orders);

                        }

                        orderModel.orders = Orders;
                    }

                    orderModelList.add(oModel);


                    FDPreferences.setTripId(TripId);


                    if (skipToteScan) {
                        FDPreferences.isBagValid("true");
                        Intent intent = new Intent(StartTripActivity.this, AllOrdersActivity.class);
                        intent.putExtra("endLocationUpdates", "");
                        startActivity(intent);
                    } else {
                        FDPreferences.isBagValid("false");
                        scanBags(Orders);
                    }


                } else {
                    FDPreferences.setTripId("null");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(StartTripActivity.this);
                    dialog.setTitle("Invalid TripId/Vehicle");
                    dialog.setMessage("This TripId is invalid");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Try again?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    integrator = new IntentIntegrator(StartTripActivity.this);
                                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                                    integrator.setPrompt("Scan Manifest");
                                    integrator.setCameraId(0);  // Use a specific camera of the device
                                    integrator.setBeepEnabled(true);
                                    integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                                    integrator.initiateScan();


                                }
                            });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
                            //onBackPressed();
                        }
                    });

                    AlertDialog alert = dialog.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                    Toast.makeText(StartTripActivity.this, message, Toast.LENGTH_LONG).show();

                    //onBackPressed();
                }

            } else {
                FDPreferences.setTripId("null");
                startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
                //onBackPressed();
                Toast.makeText(StartTripActivity.this, message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startTrip() {
        if (!TripId.isEmpty()) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());
            try {
                JSONObject payload = new JSONObject();
                payload.put("lat", String.valueOf(latitude));
                payload.put("lng", String.valueOf(longitude));
                payload.put("time", TimeUtils.getCurrentTime());
                NetworkConnector connect = new NetworkConnector(StartTripActivity.this, NetworkConnector.TYPE_PUT, FDUrls.START_TRIP + URLEncoder.encode(TripId, "UTF-8"), headers, payload.toString(), this);
                connect.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(StartTripActivity.this, "Invalid Trip ID", Toast.LENGTH_LONG).show();
            startActivity(new Intent(StartTripActivity.this, StartTripActivity.class));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (downloadURL != null || downloadURL.isEmpty()) {
                        new DownloadAsync(StartTripActivity.this, FDUrls.APP_URL + downloadURL).execute();
                    }
                } else {
                    Toast.makeText(StartTripActivity.this, "Turn on storage permission in app Settings", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    private void scanBags(ArrayList<OrderModel> orders) {
        Intent intent = new Intent(StartTripActivity.this, ScanBagActivity.class);
        startActivity(intent);
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);


    }
}
