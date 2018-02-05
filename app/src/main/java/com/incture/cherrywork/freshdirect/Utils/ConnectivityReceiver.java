package com.incture.cherrywork.freshdirect.Utils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.DB.DBUtil;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.Models.OrderModel;

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

/**
 * Created by Arun on 24-09-2016.
 */

public class ConnectivityReceiver extends BroadcastReceiver implements AsyncResponse {
    private DBUtil dbUtil = new DBUtil();
    private ArrayList<OrderModel> getOrdersforOfflineSyncEnabled = new ArrayList<>();
    private ArrayList<OrderModel> getOrderIDSforOfflineLocations = new ArrayList<>();
    private ArrayList<OrderModel> getLatLongsForEachOrder = new ArrayList<>();
    private OrderModel orderModel = new OrderModel();
    private Context _context;
    private String STATUS = "";
    private String MESSAGE = "";
    private String orderId = "";
    private ArrayList<OrderModel> getAllOrders = new ArrayList<>();
    private Runnable runnable;
    private Handler handler;
    private ProgressDialog progressDialog;


    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        _context = context;
        DbAdapter.getDbAdapterInstance().open(context);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Util.isOnline(context)) {

            FDPreferences.init(_context.getApplicationContext());

            //Update Order Status
            getOrdersforOfflineSyncEnabled = dbUtil.getOrdersforOfflineSyncEnabled(context);
            for (int i = 0; i < getOrdersforOfflineSyncEnabled.size(); i++) {
                orderModel = dbUtil.getOrderDetails(context, getOrdersforOfflineSyncEnabled.get(i).order_id);
                updateOrders(getOrdersforOfflineSyncEnabled.get(i).order_id, getOrdersforOfflineSyncEnabled.get(i).event,
                        getOrdersforOfflineSyncEnabled.get(i).customerSignature, getOrdersforOfflineSyncEnabled.get(i).offlinelat,
                        getOrdersforOfflineSyncEnabled.get(i).offlinelng, getOrdersforOfflineSyncEnabled.get(i).offlinetime, orderModel.window_end);
            }

            //Update Location
            getOrderIDSforOfflineLocations = dbUtil.getOrderIDSforOfflineLocations(context);
            for (int i = 0; i < getOrderIDSforOfflineLocations.size(); i++) {
                orderModel = dbUtil.getOrderDetails(context, getOrderIDSforOfflineLocations.get(i).order_id);
                getLatLongsForEachOrder = dbUtil.getLatLongsForEachOrder(context, getOrderIDSforOfflineLocations.get(i).order_id);
                ArrayList<JSONObject> data = new ArrayList<>();
                for (int j = 0; j < getLatLongsForEachOrder.size(); j++) {
                    JSONObject currentLocation = new JSONObject();
                    try {
                        currentLocation.put("lat", getLatLongsForEachOrder.get(j).offlinelat);
                        currentLocation.put("lng", getLatLongsForEachOrder.get(j).offlinelat);
                        currentLocation.put("time", getLatLongsForEachOrder.get(j).offlinetime);
                        data.add(currentLocation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateLocations(data, getOrderIDSforOfflineLocations.get(i).order_id, orderModel.window_end);
            }


        } else {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (Util.isOnline(_context)) {
                        FDPreferences.init(_context.getApplicationContext());
                        //Update Order Status
                        getOrdersforOfflineSyncEnabled = dbUtil.getOrdersforOfflineSyncEnabled(_context);
                        for (int i = 0; i < getOrdersforOfflineSyncEnabled.size(); i++) {
                            orderModel = dbUtil.getOrderDetails(_context, getOrdersforOfflineSyncEnabled.get(i).order_id);
                            updateOrders(getOrdersforOfflineSyncEnabled.get(i).order_id, getOrdersforOfflineSyncEnabled.get(i).event,
                                    getOrdersforOfflineSyncEnabled.get(i).customerSignature, getOrdersforOfflineSyncEnabled.get(i).offlinelat,
                                    getOrdersforOfflineSyncEnabled.get(i).offlinelng, getOrdersforOfflineSyncEnabled.get(i).offlinetime, orderModel.window_end);
                        }

                        //Update Location
                        getOrderIDSforOfflineLocations = dbUtil.getOrderIDSforOfflineLocations(_context);
                        for (int i = 0; i < getOrderIDSforOfflineLocations.size(); i++) {
                            orderModel = dbUtil.getOrderDetails(_context, getOrderIDSforOfflineLocations.get(i).order_id);
                            getLatLongsForEachOrder = dbUtil.getLatLongsForEachOrder(_context, getOrderIDSforOfflineLocations.get(i).order_id);
                            ArrayList<JSONObject> data = new ArrayList<>();
                            for (int j = 0; j < getLatLongsForEachOrder.size(); j++) {
                                JSONObject currentLocation = new JSONObject();
                                try {
                                    currentLocation.put("lat", getLatLongsForEachOrder.get(j).offlinelat);
                                    currentLocation.put("lng", getLatLongsForEachOrder.get(j).offlinelat);
                                    currentLocation.put("time", getLatLongsForEachOrder.get(j).offlinetime);
                                    data.add(currentLocation);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            updateLocations(data, getOrderIDSforOfflineLocations.get(i).order_id, orderModel.window_end);
                        }


                    } else {
                        handler.postDelayed(runnable, 2000);
                    }
                }
            };
            handler.postDelayed(runnable, 2000);
        }


    }

    private void updateLocations(ArrayList<JSONObject> getLatLongsForEachOrder, String order_id, String window) {


        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-email-id", FDPreferences.getEmail());
        headers.put("x-access-token", FDPreferences.getAccessToken());

        try {
            JSONObject payload = new JSONObject();
            payload.put("tripId", FDPreferences.getTripId());
            payload.put("orderId", order_id);

            JSONObject driverDetails = new JSONObject();
            driverDetails.put("firstName", FDPreferences.getFirstname());
            driverDetails.put("lastName", FDPreferences.getLastname());
            driverDetails.put("email", FDPreferences.getEmail());
            driverDetails.put("mobile", "");
            driverDetails.put("location", "KA");

            payload.put("driverDetails", driverDetails);


            String windowEnd = window;
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


            payload.put("currentLocation", new JSONArray(getLatLongsForEachOrder));


            NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_PUT, FDUrls.UPDATE_LOCATION, headers, payload.toString(), this, true);
            connect.execute();
            //setUpOrdersListData(orderModelList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateOrders(String order_id, String event, String customerSignature, double offlinelat, double offlinelng, String offlinetime, String windowend) {
        if (!(_context == null)) {

            Toast.makeText(_context.getApplicationContext(), "Syncing order details", Toast.LENGTH_SHORT).show();

        }

        getAllOrders = dbUtil.getAllOrders(_context);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-email-id", FDPreferences.getEmail());
        headers.put("x-access-token", FDPreferences.getAccessToken());

        try {
            ArrayList<JSONObject> data = new ArrayList<>();

            JSONObject payload = new JSONObject();
            payload.put("event", event);
            payload.put("created_at", "created_at");
            payload.put("window_end", windowend);
            payload.put("delivered_at", "delivered_at");
            payload.put("tracking_code", "tracking_code");
            payload.put("order_reference", order_id);

            JSONObject driver = new JSONObject();
            driver.put("name", "name");
            driver.put("photo_url", FDPreferences.getAvatarurl());
            payload.put("driver", driver);

            JSONObject exception = new JSONObject();
            exception.put("code", "");
            exception.put("description", "");
            payload.put("exception", exception);

            /*int lastOrderCount=0;
            boolean isLastOrder=false;
            for(int i=0;i<getAllOrders.size();i++){
                if(getAllOrders.get(i).orderStatus.equalsIgnoreCase(Constants.PENDING)||getAllOrders.get(i).orderStatus.equalsIgnoreCase(Constants.ENROUTE)){
                    lastOrderCount=lastOrderCount+1;
                }
            }
            if(lastOrderCount==1){
                isLastOrder=true;
            }else{
                isLastOrder=false;
            }
            payload.put("isLastOrder", isLastOrder);*/

            if (getAllOrders.size() > 0) {
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
            location.put("lat", String.valueOf(offlinelat));
            location.put("lng", String.valueOf(offlinelng));
            location.put("time", offlinetime);
            payload.put("location", location);

            if (event.equalsIgnoreCase("delivered")) {
                JSONObject proof_of_delivery = new JSONObject();
                JSONObject destination_signature = new JSONObject();
                destination_signature.put("photo_url", customerSignature);
                destination_signature.put("last_name", FDPreferences.getLastname());
                proof_of_delivery.put("destination_signature", destination_signature);
                payload.put("proof_of_delivery", proof_of_delivery);
                //payload.put()
            }

            data.add(payload);

            JSONObject mainobj = new JSONObject();
            mainobj.put("Order_status", new JSONArray(data));


            NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_PUT, FDUrls.UPDATE_ORDERS + FDPreferences.getTripId(), headers, mainobj.toString(), this, true);
            connect.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                STATUS = mainobject.optString("status");
                MESSAGE = mainobject.optString("message");
                if (STATUS.equalsIgnoreCase("SUCCESS")) {
                    if (!(_context == null)) {
                        // Toast.makeText(_context.getApplicationContext(), "Location update successful", Toast.LENGTH_SHORT).show();
                       /* Intent intent = new Intent(_context, AllOrdersActivity.class);
                        intent.putExtra("endLocationUpdates", "");
                        _context.startActivity(intent);*/
                        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(_context);
                        Intent intent = new Intent("SYNC_COMPLETE");
                        //sending intent through BroadcastManager
                        broadcastManager.sendBroadcast(intent);
                    }
                } else {
                    //Toast.makeText(_context, MESSAGE, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }
            parse(actualString);
        }
    }

    public void parse(String output) {
        try {
            JSONObject mainobject = new JSONObject(output);
            STATUS = mainobject.optString("status");
            MESSAGE = mainobject.optString("message");
            if (STATUS.equalsIgnoreCase("SUCCESS")) {

                if (mainobject.has("data")) {
                    JSONArray data = mainobject.optJSONArray("data");
                    if (data.length() > 0 && data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObject = data.optJSONObject(i);
                            orderId = dataObject.optString("orderId");
                            if (!(_context == null)) {
                                //progressDialog.dismiss();
                                Toast.makeText(_context.getApplicationContext(), "Order updated for orderId " + orderId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put("offlineSyncEnabled", false);
                DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", contentValues, "order_id='" + orderId + "'");
                //Toast.makeText(_context, "ORDER UPDATED", Toast.LENGTH_SHORT).show();
                //onBackPressed();
            } else {
                //Toast.makeText(_context, MESSAGE, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }
}
