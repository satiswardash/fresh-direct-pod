package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.incture.cherrywork.freshdirect.DB.DBUtil;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.BarcodeQrcodeCustomActivity;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arun on 25-08-2016.
 */
public class ScanBagActivity extends Activity implements AsyncResponse {
    private String tripId = "";
    private TextView trip_id, bags_count, orders_count;
    private Button scanbag, cancelTrip;
    private ArrayList<OrderModel> orders;
    private ArrayList<OrderModel> packages = new ArrayList<>();
    private boolean validBag = false;
    private DBUtil db;
    private String status = "";
    private String message = "";
    private boolean isTripStarted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bag);
        //tripId=getIntent().getStringExtra("tripId");
        bags_count = (TextView) findViewById(R.id.bags_count);
        orders_count = (TextView) findViewById(R.id.orders_count);
        db = new DBUtil();


        orders = db.getAllOrders(ScanBagActivity.this);

        orders_count.setText(String.valueOf(orders.size()));
        for (int i = 0; i < orders.size(); i++) {
            if (Util.isOnline(ScanBagActivity.this)) {
                packages.addAll(orders.get(i).packages);
            } else {
                packages.addAll(db.getPackagesForEachOrder(ScanBagActivity.this, orders.get(i).order_id));
            }
        }
        bags_count.setText(String.valueOf(packages.size()));
        trip_id = (TextView) findViewById(R.id.tripId);
        trip_id.setText("Trip ID : " + " " + FDPreferences.getTripId());
        scanbag = (Button) findViewById(R.id.scan_bag);
        scanbag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(ScanBagActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan TOTE");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                integrator.initiateScan();
            }
        });

        cancelTrip = (Button) findViewById(R.id.cancel_trip);
        cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ScanBagActivity.this,"Currently disabled",Toast.LENGTH_LONG).show();

                /*AlertDialog.Builder dialog = new AlertDialog.Builder(ScanBagActivity.this);
                dialog.setTitle("Cancel Trip");
                dialog.setMessage("Are you sure?Cancelling the trip will mark all orders invalid");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelTrip();
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
                alert.show();*/


            }
        });


    }

    private void cancelTrip() {

        if (Util.isOnline(ScanBagActivity.this)) {
            /*for(int i=0;i<orders.size();i++){
                if(!orders.get(i).orderStatus.equalsIgnoreCase(Constants.PENDING)){
                    Toast.makeText(ScanBagActivity.this, "This trip is already enroute", Toast.LENGTH_SHORT).show();
                    isTripStarted=true;
                    break;
                }
            }*/
            //if(!isTripStarted) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-email-id", FDPreferences.getEmail());
                headers.put("x-access-token", FDPreferences.getAccessToken());
                NetworkConnector connect = new NetworkConnector(ScanBagActivity.this, NetworkConnector.TYPE_PUT, FDUrls.CANCEL_TRIP + FDPreferences.getTripId(), headers, null, this);
                connect.execute();
            //}
        } else {
            Toast.makeText(ScanBagActivity.this, "No Internet", Toast.LENGTH_SHORT).show();

        }
        //setUpOrdersListData(orderModelList);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {
                //Log.d("MainActivity", "Scanned");
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //startActivity(new Intent(ScanBagActivity.this,ScanBagActivity.class));

                    for (int i = 0; i < orders.size(); i++) {
                        if (result.getContents().equalsIgnoreCase(orders.get(i).erporder_id)) {
                            validBag = true;
                            break;
                        } else {
                            validBag = false;
                            // break;
                        }

                }
                if (validBag) {
                    FDPreferences.isBagValid("true");
                    Intent intent = new Intent(ScanBagActivity.this, AllOrdersActivity.class);
                    intent.putExtra("endLocationUpdates", "");
                    startActivity(intent);
                } else {
                    FDPreferences.isBagValid("false");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ScanBagActivity.this);
                    dialog.setTitle("Invalid TOTE");
                    dialog.setMessage("This TOTE does not belong to this trip");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Try again?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    IntentIntegrator integrator = new IntentIntegrator(ScanBagActivity.this);
                                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                                    integrator.setPrompt("Scan TOTE");
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
                            //onBackPressed();
                        }
                    });

                    AlertDialog alert = dialog.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
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
                    FDPreferences.setTripId("null");
                    Util.deleteTables();
                    startActivity(new Intent(ScanBagActivity.this, StartTripActivity.class));
                } else {
                    Toast.makeText(ScanBagActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }
}
