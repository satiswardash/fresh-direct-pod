package com.incture.cherrywork.freshdirect.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.incture.cherrywork.freshdirect.Adapters.OrdersListAdapter;
import com.incture.cherrywork.freshdirect.DB.DBUtil;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.Network.DownloadAsync;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.Constants;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.GPSTracker;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.ProfileImageView;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.String.valueOf;

/**
 * Created by Arun on 22-08-2016.
 */
public class AllOrdersActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, LocationListener, AsyncResponse {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 110;
    private static final int EXTERNAL_STORAGE = 1;
    private SwipeRefreshLayout refreshLayout;
    public static Boolean refreshInProgress = false;
    private ArrayList<OrderModel> orderModelList = new ArrayList<>();
    private ArrayList<OrderModel> Orders = new ArrayList<>();
    private ArrayList<OrderModel> packagesList = new ArrayList<>();
    private OrderModel orderModel = new OrderModel();
    private RecyclerView orders_list;
    private DrawerLayout drawerLayout;
    private ImageView menu, mapview;
    private NavigationView navigationView;
    private Button logout;
    private Button resetpassword;
    private String status = "";
    private String message = "";
    private String endLocationUpdates = "";
    private OrderModel oModel;
    private DBUtil dbUtil;
    private TextView order_count, userName, email, appVersion, tripId;
    private String orderId = "";
    private int Position = 0;
    private boolean isTripComplete;
    private String APPVERSION = "";
    private boolean updateRequired = false;
    private BroadcastReceiver broadcastReceiver;
    private ProfileImageView user_pic;
    private String downloadURL = "";
    private boolean updateText = false;
    private String feedsCount = "0";
    private int unReadFeedsCnt;
    private TextView unReadFeedsCount;
    private ImageView viewFeeds;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long updateInterval = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);
        //db initialisation
        dbUtil = new DBUtil();
        //initialise navigation view
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationClick();
        //Initialise header of navigation view
        View header = ((NavigationView) findViewById(R.id.navigation_view)).getHeaderView(0);
        //Header items
        userName = (TextView) header.findViewById(R.id.drawer_header_profile_name);
        email = (TextView) header.findViewById(R.id.drawer_header_profile_email);
        appVersion = (TextView) header.findViewById(R.id.app_version);
        user_pic = (ProfileImageView) header.findViewById(R.id.user_pic);
        //Initalise Header textview
        order_count = (TextView) findViewById(R.id.order_count);
        tripId = (TextView) findViewById(R.id.trip_id);
        unReadFeedsCount = (TextView) findViewById(R.id.unread_feeds_count);
        viewFeeds = (ImageView) findViewById(R.id.notify);

        viewFeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(AllOrdersActivity.this, FeedsActivity.class));
                Intent intent = new Intent(AllOrdersActivity.this, FeedsActivity.class);
                intent.putExtra("unReadCount", unReadFeedsCnt);
                startActivityForResult(intent, 240);
            }
        });


        userName.setText(FDPreferences.getFirstname() + "  " + FDPreferences.getLastname());
        email.setText(FDPreferences.getEmail());
        tripId.setText("Trip Id : " + FDPreferences.getTripId());
        //set userImage in navigation view
        if (!FDPreferences.getAvatarurl().equalsIgnoreCase("")) {
            Picasso.with(AllOrdersActivity.this).load(FDPreferences.getAvatarurl()).into(user_pic);
        }
        //Set app version from manifest
       /* try {
            //appVersion.setText("App version "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

        //get value from broadcastreceiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchOrders();
            }
        };


        //setup sidemenu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menu = (ImageView) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });


        //setup swipe refresh layout
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        refreshLayout.setRefreshing(false);

        //setup recyclerview and layout manager
        orders_list = (RecyclerView) findViewById(R.id.orders_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        orders_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        orders_list.requestDisallowInterceptTouchEvent(true);
        orders_list.setLayoutManager(manager);
        orders_list.setNestedScrollingEnabled(false);
        orders_list.setHasFixedSize(false);
        //getFeedsCount
        getFeedsCount();
        //check for app update
       // checkForUpdateifAvailable();
        //fetch orders
        fetchOrders();
        //logout
        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


        //resetPassword
        resetpassword = (Button) findViewById(R.id.reset);
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        //locateALl
        mapview = (ImageView) findViewById(R.id.map_view);
        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AllOrdersActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AllOrdersActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                } else {
                    if (Util.isOnline(AllOrdersActivity.this)) {
                        Intent intent = new Intent(AllOrdersActivity.this, LocateAllActivity.class);
                        intent.putExtra("latlngs", orderModelList);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AllOrdersActivity.this, "You have to be online to view maps", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        endLocationUpdates = getIntent().getStringExtra("endLocationUpdates");
        if (endLocationUpdates.equalsIgnoreCase("true")) {
            stopFusedLocation("");
        }

    }

    private void resetPassword() {
        drawerLayout.closeDrawers();
        //Toast.makeText(AllOrdersActivity.this, "Coming soon stay tuned", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllOrdersActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void checkForUpdateifAvailable() {
        if (Util.isOnline(this)) {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-device-type", "android");
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());
            NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_GET, FDUrls.APP_UPDATE, headers, null, this);
            connect.execute();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void logout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AllOrdersActivity.this);
        dialog.setTitle("Logout");
        dialog.setMessage("Are you sure to logout?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Util.isOnline(AllOrdersActivity.this)) {
                            drawerLayout.closeDrawers();
                            Util.deleteTables();
                            FDPreferences.setTripId("null");
                            FDPreferences.resetPreferences();
                            FDPreferences.setAccessToken("null");
                            dialog.dismiss();
                            startActivity(new Intent(AllOrdersActivity.this, LoginActivity.class));
                        } else {
                            drawerLayout.closeDrawers();
                            Toast.makeText(AllOrdersActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawerLayout.closeDrawers();
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

    private void navigationClick() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.leaderboard:
                        drawerLayout.closeDrawers();
                        startActivity(new Intent(AllOrdersActivity.this, LeaderBoardActivity.class));
                        // Toast.makeText(AllOrdersActivity.this,"Coming soon,stay tuned!!!",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.feeds:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(AllOrdersActivity.this, FeedsActivity.class);
                        intent.putExtra("unReadCount", unReadFeedsCnt);
                        startActivityForResult(intent, 240);
                        return true;
                    /*case R.id.checkforupdates:
                        drawerLayout.closeDrawers();
                        //checkForUpdateifAvailable();
                        updateText = true;
                        return true;*/
                    default:
                        return true;
                }

            }
        });
    }

    private void fetchOrders() {
        if (Util.isOnline(this)) {
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

                NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_POST, FDUrls.FETCH_ORDERS + FDPreferences.getTripId(), headers, payload.toString(), this);
                connect.execute();
                //setUpOrdersListData(orderModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            setUpOrdersListData(dbUtil.getAllOrders(AllOrdersActivity.this));
        }


    }

    private void setUpOrdersListData(ArrayList<OrderModel> data) {
        isTripComplete = false;
        orderModelList = data;
        for (int i = 0; i < orderModelList.size(); i++) {
            String CompleteTrip = orderModelList.get(i).orderStatus;
            if (CompleteTrip.equalsIgnoreCase(Constants.ENROUTE) || CompleteTrip.equalsIgnoreCase(Constants.PENDING)) {
                isTripComplete = true;
                break;
            }
        }
        if (!isTripComplete) {
            Intent intent = new Intent(AllOrdersActivity.this, CompleteTripActivity.class);
            intent.putExtra("totalOrders", valueOf(orderModelList.size()));
            startActivity(intent);
        }
        order_count.setText("(" + valueOf(orderModelList.size()) + ")");
        //Set adapter for all the orders(Online and Offline)
        OrdersListAdapter orderListAdapter = new OrdersListAdapter(this, orderModelList);
        orders_list.setAdapter(orderListAdapter);


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            ActivityCompat.finishAffinity(this);
        }

    }

    @Override
    public void onRefresh() {
        updateText = false;

        if (Util.isOnline(AllOrdersActivity.this)) {
            getFeedsCount();
            fetchOrders();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 3000);
        } else {
            refreshLayout.setRefreshing(false);
            Toast.makeText(AllOrdersActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
        if (refreshInProgress)
            return;
        refreshInProgress = false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(AllOrdersActivity.this, LocateAllActivity.class));
                } else {
                    Toast.makeText(AllOrdersActivity.this, "Turn on permissions in app Settings", Toast.LENGTH_SHORT).show();
                }
            }
            case EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (downloadURL != null || downloadURL.isEmpty()) {
                        new DownloadAsync(AllOrdersActivity.this, FDUrls.APP_URL + downloadURL).execute();
                    }
                } else {
                    Toast.makeText(AllOrdersActivity.this, "Turn on storage permission in app Settings", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    @Override
    public void onLocationChanged(Location location) {

        if (Util.isOnline(this)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());

            try {
                JSONObject payload = new JSONObject();
                payload.put("tripId", FDPreferences.getTripId());
                payload.put("orderId", orderId);

                JSONObject driverDetails = new JSONObject();
                driverDetails.put("firstName", FDPreferences.getFirstname());
                driverDetails.put("lastName", FDPreferences.getLastname());
                driverDetails.put("email", FDPreferences.getEmail());
                driverDetails.put("mobile", "");
                driverDetails.put("location", "");

                if (orderModelList.size() > 0) {
                    String windowEnd = orderModelList.get(Position).window_end;
                    if (windowEnd.equalsIgnoreCase("") || windowEnd.isEmpty() || windowEnd.equalsIgnoreCase("null")) {
                        payload.put("isDelayed", false);
                    }
                    String currentTime = TimeUtils.getCurrentTime();
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        formatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                        Date windowEnddate = formatter.parse(windowEnd);
                        Date currentTimedate = formatter.parse(currentTime);
                        if (currentTimedate.after(windowEnddate)) {
                            payload.put("isDelayed", true);
                        } else {
                            payload.put("isDelayed", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                payload.put("driverDetails", driverDetails);
                ArrayList<JSONObject> data = new ArrayList<>();
                JSONObject currentLocation = new JSONObject();
                currentLocation.put("lat", location.getLatitude());
                currentLocation.put("lng", location.getLongitude());
                currentLocation.put("time", TimeUtils.getCurrentTime());
                data.add(currentLocation);
                payload.put("currentLocation", new JSONArray(data));

                NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_PUT, FDUrls.UPDATE_LOCATION, headers, payload.toString(), this, true);
                connect.execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            dbUtil.addLocationsOffline(orderId, location.getLatitude(), location.getLongitude(), TimeUtils.getCurrentTime());
            Toast.makeText(AllOrdersActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void startLocationUpdates(String order_id, int pos) {
        orderId = order_id;
        Position = pos;
        startLocation(orderId);

    }

    private void startLocation(String orderId) {
        if (Util.isOnline(this)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());

            try {
                int lastOrderCount = 0;
                boolean isLastOrder = false;
                for (int i = 0; i < orderModelList.size(); i++) {
                    if (orderModelList.get(i).orderStatus.equalsIgnoreCase(Constants.PENDING) || orderModelList.get(i).orderStatus.equalsIgnoreCase(Constants.ENROUTE)) {
                        lastOrderCount = lastOrderCount + 1;
                    }
                }
                if (lastOrderCount == 1) {
                    isLastOrder = true;
                } else {
                    isLastOrder = false;
                }
                JSONObject payload = new JSONObject();
                payload.put("tripId", FDPreferences.getTripId());
                payload.put("orderId", orderId);
                payload.put("isLastOrder", isLastOrder);

                NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_PUT, FDUrls.START_LOCATION, headers, payload.toString(), this);
                connect.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AllOrdersActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            if (checkPlayServices()) {
                startFusedLocation();
            }
        }
    }

    // check if google play services is installed on the device
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "This device is supported. Please download google play services", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {
                            registerRequestUpdate(AllOrdersActivity.this);
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            mGoogleApiClient.connect();
                            registerRequestUpdate(AllOrdersActivity.this);
                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void stopFusedLocation(String order_id) {
        orderId = order_id;
        endLocation(orderId);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void endLocation(String orderId) {
        if (Util.isOnline(this)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());

            try {
                JSONObject payload = new JSONObject();
                payload.put("tripId", FDPreferences.getTripId());
                payload.put("orderId", orderId);

                NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_PUT, FDUrls.END_LOCATION, headers, payload.toString(), this);
                connect.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(updateInterval);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            if (!isGoogleApiClientConnected()) {
                mGoogleApiClient.connect();
            }
            registerRequestUpdate(listener);
        }

    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    protected void onStop() {
        // stopFusedLocation();
        super.onStop();
        LocalBroadcastManager.getInstance(AllOrdersActivity.this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        if (type == NetworkConnector.TYPE_PUT && url.equalsIgnoreCase(FDUrls.UPDATE_LOCATION)) {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }
            try {
                JSONObject mainobject = new JSONObject(actualString);
                status = mainobject.optString("status");
                message = mainobject.optString("message");
                if (status.equalsIgnoreCase("SUCCESS")) {
                } else {
                    Toast.makeText(AllOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (type == NetworkConnector.TYPE_PUT && url.equalsIgnoreCase(FDUrls.START_LOCATION)) {
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
                    if (data != null) {
                        orderId = data.optString("orderId");
                        String update_interval = data.optString("trackInterval");
                        updateInterval = Long.valueOf(update_interval);
                        if (checkPlayServices()) {
                            startFusedLocation();
                        }
                    }
                } else {
                    Toast.makeText(AllOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (type == NetworkConnector.TYPE_PUT && url.equalsIgnoreCase(FDUrls.END_LOCATION)) {
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
                    if (data != null) {
                        orderId = data.optString("orderId");
                    }

                } else {
                    Toast.makeText(AllOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else if (type == NetworkConnector.TYPE_PUT && url.equalsIgnoreCase(FDUrls.UPDATE_ORDERS + FDPreferences.getTripId())) {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }
            try {
                JSONObject mainobject = new JSONObject(actualString);
                status = mainobject.optString("status");
                message = mainobject.optString("message");
                if (status.equalsIgnoreCase("SUCCESS")) {
                    onRefresh();
                } else {
                    Toast.makeText(AllOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (url.equalsIgnoreCase(FDUrls.APP_UPDATE)) {
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
                            Log.d("APPVERSION", "" + APPVERSION);
                            Log.d("downloadURL", "" + downloadURL);


                            if (!APPVERSION.equalsIgnoreCase(Util.getCurrentAppVersion(this))) {
                                updateRequired = true;
                                showUpdateDialog();
                            } else {
                                if (updateText) {
                                    Toast.makeText(AllOrdersActivity.this, "No new updates", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.equalsIgnoreCase(FDUrls.GET_WORKITEM + "/unread")) {
            try {
                JSONObject mainobject = new JSONObject(output);
                status = mainobject.optString("status");
                message = mainobject.optString("message");
                if (status.equalsIgnoreCase("success")) {
                    JSONObject maindata = mainobject.optJSONObject("data");
                    feedsCount = maindata.optString("unreadItems");
                    unReadFeedsCnt = Integer.parseInt(feedsCount);
                    if (feedsCount.equalsIgnoreCase("0")) {
                        unReadFeedsCount.setVisibility(View.GONE);
                    }else{
                        unReadFeedsCount.setVisibility(View.VISIBLE);
                    }
                    setTextFeeds(unReadFeedsCnt);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (type == NetworkConnector.TYPE_POST) {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }

            parse(actualString);
        } else {

        }

    }

    private void setTextFeeds(int unReadFeedsCnt) {
        unReadFeedsCount.setText(String.valueOf(unReadFeedsCnt));
    }

    private void getFeedsCount() {
        if (Util.isOnline(AllOrdersActivity.this)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());
            NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_GET, FDUrls.GET_WORKITEM + "/unread", headers, null, this);
            connect.execute();
        }
    }

    private void showUpdateDialog() {
        if (updateRequired) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AllOrdersActivity.this);
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
                    Toast.makeText(AllOrdersActivity.this, "App can be updated from settings", Toast.LENGTH_SHORT).show();
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
                new DownloadAsync(AllOrdersActivity.this, FDUrls.APP_URL + downloadURL).execute();
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
                JSONObject trip = mainobject.optJSONObject("data");
                if (trip != null) {
                    orderModel.status = trip.optString("status");
                    orderModel.vehicle = trip.optString("vehicle");

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
                            dbUtil.addOrders(Orders);
                        }
                        orderModel.orders = Orders;
                    }
                    orderModelList.add(oModel);
                    setUpOrdersListData(Orders);
                } else {
                    FDPreferences.setTripId("null");
                    Toast.makeText(AllOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AllOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener

    }

    public void upDateInvalidOrder(String invalid, String order_id) {

        if (Util.isOnline(this)) {
            GPSTracker tracker = new GPSTracker(AllOrdersActivity.this);
            if (tracker.canGetLocation()) {
                double latitude = tracker.getLatitude();
                double longitude = tracker.getLongitude();
                if (!(latitude == 0.0 || longitude == 0.0)) {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("x-email-id", FDPreferences.getEmail());
                    headers.put("x-access-token", FDPreferences.getAccessToken());

                    try {
                        ArrayList<JSONObject> data = new ArrayList<>();

                        JSONObject payload = new JSONObject();
                        payload.put("event", invalid);
                        payload.put("created_at", "created_at");
                        if (orderModelList.size() > 0) {
                            payload.put("window_end", orderModelList.get(Position).window_end);
                        } else {
                            payload.put("window_end", "");
                        }
                        payload.put("delivered_at", TimeUtils.getCurrentTime());
                        payload.put("tracking_code", "tracking_code");
                        payload.put("order_reference", order_id);

                        JSONObject driver = new JSONObject();
                        driver.put("name", FDPreferences.getFirstname());
                        driver.put("photo_url", "");
                        payload.put("driver", driver);

                        JSONObject exception = new JSONObject();
                        exception.put("code", "");
                        exception.put("description", "");
                        payload.put("exception", invalid);

                        /*int lastOrderCount = 0;
                        boolean isLastOrder = false;
                        for (int i = 0; i < orderModelList.size(); i++) {
                            if (orderModelList.get(i).orderStatus.equalsIgnoreCase(Constants.PENDING) || orderModelList.get(i).orderStatus.equalsIgnoreCase(Constants.ENROUTE)) {
                                lastOrderCount = lastOrderCount + 1;
                            }
                        }
                        if (lastOrderCount == 1) {
                            isLastOrder = true;
                        } else {
                            isLastOrder = false;
                        }
                        payload.put("isLastOrder", isLastOrder);*/

                        if (orderModelList.size() > 0) {
                            String windowEnd = orderModel.window_end;
                            if (windowEnd.equalsIgnoreCase("") || windowEnd.isEmpty() || windowEnd.equalsIgnoreCase("null")) {
                                payload.put("isDelayed", false);
                            }
                            String currentTime = TimeUtils.getCurrentTime();
                            try {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                formatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                                Date windowEnddate = formatter.parse(windowEnd);
                                Date currentTimedate = formatter.parse(currentTime);
                                if (currentTimedate.after(windowEnddate)) {
                                    payload.put("isDelayed", true);
                                } else {
                                    payload.put("isDelayed", false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                        JSONObject location = new JSONObject();
                        location.put("lat", valueOf(latitude));
                        location.put("lng", valueOf(longitude));
                        location.put("time", TimeUtils.getCurrentTime());
                        payload.put("location", location);

                        if (invalid.equalsIgnoreCase("delivered")) {
                            JSONObject proof_of_delivery = new JSONObject();
                            JSONObject destination_signature = new JSONObject();
                            destination_signature.put("photo_url", "");
                            destination_signature.put("last_name", orderModelList.get(Position).last_name);
                            proof_of_delivery.put("proof_of_delivery", destination_signature);
                        }

                        data.add(payload);

                        JSONObject mainobj = new JSONObject();
                        mainobj.put("Order_status", new JSONArray(data));


                        NetworkConnector connect = new NetworkConnector(AllOrdersActivity.this, NetworkConnector.TYPE_PUT, FDUrls.UPDATE_ORDERS + FDPreferences.getTripId(), headers, mainobj.toString(), this);
                        connect.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(AllOrdersActivity.this, "Set GPS to high accuracy in Location services", Toast.LENGTH_SHORT).show();
            }
        } else {
            GPSTracker tracker = new GPSTracker(AllOrdersActivity.this);
            if (tracker.canGetLocation()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("offlineSyncEnabled", true);
                contentValues.put("event", invalid);
                contentValues.put("orderStatus", invalid);
                contentValues.put("offlinelat", tracker.getLatitude());
                contentValues.put("offlinelng", tracker.getLongitude());
                contentValues.put("offlinetime", TimeUtils.getCurrentTime());
                DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", contentValues, "order_id='" + order_id + "'");
                for (int i = 0; i < orderModelList.size(); i++) {
                    if (orderModelList.get(i).order_id.equalsIgnoreCase(order_id)) {
                        orderModelList.get(i).orderStatus = invalid;
                        orderModelList.get(i).isOrderCancel = true;
                        break;
                    }
                }
                ((OrdersListAdapter) orders_list.getAdapter()).orderEnded();
            } else {
                tracker.showSettingsAlert();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String orderId = data.getStringExtra("orderId");
            String event = data.getStringExtra("event");
            boolean orderEnded = data.getBooleanExtra("orderEnded", false);
            for (int i = 0; i < orderModelList.size(); i++) {
                if (orderModelList.get(i).order_id.equalsIgnoreCase(orderId)) {
                    orderModelList.get(i).orderStatus = event;
                    orderModelList.get(i).isOrderCancel = orderEnded;
                    break;
                }
            }
            orders_list.getAdapter().notifyDataSetChanged();
            ((OrdersListAdapter) orders_list.getAdapter()).orderEnded();
            stopFusedLocation(orderId);

        }
        if (requestCode == 240 && resultCode == RESULT_OK) {
            unReadFeedsCnt = data.getIntExtra("unReadFeedsCount", 0);
            unReadFeedsCount.setText(String.valueOf(unReadFeedsCnt));
            if (unReadFeedsCnt == 0) {
                unReadFeedsCount.setVisibility(View.GONE);
            }else{
                unReadFeedsCount.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(AllOrdersActivity.this).registerReceiver((broadcastReceiver),
                new IntentFilter("SYNC_COMPLETE")
        );
    }


}
