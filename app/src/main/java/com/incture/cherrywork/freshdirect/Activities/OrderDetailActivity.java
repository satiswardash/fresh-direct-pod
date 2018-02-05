package com.incture.cherrywork.freshdirect.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.incture.cherrywork.freshdirect.Adapters.PackagesAdapter;
import com.incture.cherrywork.freshdirect.DB.DBUtil;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.BarcodeQrcodeCustomActivity;
import com.incture.cherrywork.freshdirect.Utils.Constants;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.GPSTracker;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Arun on 19-08-2016.
 */
public class OrderDetailActivity extends Activity
        implements FragmentManager.OnBackStackChangedListener, AsyncResponse {
    private Handler mHandler = new Handler();
    private RelativeLayout parent;
    private String endLocationUpdates = "";
    private String event = "";
    private String STATUS = "";
    private String MESSAGE = "";
    private OrderModel orderModel = new OrderModel();
    private ImageView back;
    private TextView delivery_window;
    private OrderModel offlineOrderModel = new OrderModel();
    private TextView title;
    private int bagsCount = 1;
    private DBUtil dbUtil;
    private boolean validBag = false;
    private String address2="";
    private String customerSignatureEncoded = "";
    private AllOrdersActivity allOrdersActivity;
    private PackagesAdapter packagesAdapter;
    private ArrayList<String> scannedBags = new ArrayList<>();
    private ArrayList<OrderModel> orderModelArrayList=new ArrayList<>();

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;


    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        dbUtil = new DBUtil();
        parent = (RelativeLayout) findViewById(R.id.parent);
        title = (TextView) findViewById(R.id.title);
        delivery_window = (TextView) findViewById(R.id.delivery_window);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        orderModel = (OrderModel) getIntent().getSerializableExtra("orderModel");
        orderModelArrayList= (ArrayList<OrderModel>) getIntent().getSerializableExtra("orderModelArrayList");

        title.setText("Order Id : " + orderModel.order_id);
        delivery_window.setText(TimeUtils.getTime(orderModel.window_start) + " - " + TimeUtils.getTime(orderModel.window_end));


        IntentIntegrator integrator = new IntentIntegrator(OrderDetailActivity.this);

        if (orderModel.orderStatus.equalsIgnoreCase(Constants.INVALID) || orderModel.orderStatus.equalsIgnoreCase(Constants.DELIVERED) ||
                orderModel.orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME) || orderModel.orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)
                || orderModel.orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION)) {
            parent.setVisibility(View.VISIBLE);
        } else {

            if (!dbUtil.isScanCompleted(OrderDetailActivity.this, orderModel.order_id)) {
                if (orderModel.packages.size() > 0) {
                    if (orderModel.isOrdrderStarted) {
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("Scan Bags" + "   " + "Bags" + "  " + String.valueOf(bagsCount) + "  " + "of" + "   " + String.valueOf(orderModel.packages.size()));
                        integrator.setCameraId(0);// Use a specific camera of the device
                        integrator.setBeepEnabled(true);
                        integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                        integrator.initiateScan();
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Start order to scan bags", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } else {
                    parent.setVisibility(View.VISIBLE);
                }
            } else {
                parent.setVisibility(View.VISIBLE);
            }

        }


        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new OrderDetailFragment())
                    .commit();
        }
        getFragmentManager().addOnBackStackChangedListener(this);

    }


    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources representing
                // rotations when switching to the back of the card, as well as animator
                // resources representing rotations when flipping back to the front (e.g. when
                // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                // Replace any fragments currently in the container view with a fragment
                // representing the next page (indicated by the just-incremented currentPage
                // variable).
                .replace(R.id.container, new CompleteOrderFragment())

                // Add this transaction to the back stack, allowing users to press Back
                // to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();

        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        String actualString = output;
        if (output.startsWith("null")) {
            actualString = output.substring(4);
        }
        parse(actualString);
    }

    public void parse(String output) {
        try {
            JSONObject mainobject = new JSONObject(output);
            STATUS = mainobject.optString("status");
            MESSAGE = mainobject.optString("message");
            if (STATUS.equalsIgnoreCase("SUCCESS")) {
                Intent intent = new Intent(OrderDetailActivity.this, AllOrdersActivity.class);
                intent.putExtra("endLocationUpdates", "true");
                startActivity(intent);

            } else {
                Toast.makeText(OrderDetailActivity.this, MESSAGE, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }

    @SuppressLint("ValidFragment")
    public class OrderDetailFragment extends Fragment {
        private Button completeOrder, customer_notathome, partial_rejection, order_rejection;
        private TextView customer_name, orderStatus,time_interval, delivery_instructions, unattended_instructions, customer_address;
        private RelativeLayout parent;
        private CardView status_layout;
        private LinearLayout actionsLayout;
        private ListView packages_list;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.order_detail_fragment, container, false);

            customer_name = (TextView) view.findViewById(R.id.customer_name);
            time_interval = (TextView) view.findViewById(R.id.time_interval);
            orderStatus = (TextView) view.findViewById(R.id.status);
            status_layout=(CardView)view.findViewById(R.id.status_layout);
            delivery_instructions = (TextView) view.findViewById(R.id.delivery_instructions);
            unattended_instructions = (TextView) view.findViewById(R.id.unattended_instructions);
            customer_address = (TextView) view.findViewById(R.id.customer_address);

            actionsLayout = (LinearLayout) view.findViewById(R.id.actions_layout);

            customer_name.setText(Util.capitalizeSentence(orderModel.first_name + " " + orderModel.last_name));
            if(orderModel.address_line_2.equalsIgnoreCase("null")){
                address2=orderModel.apartment+"\n";
            }else{
                address2=orderModel.address_line_2+", " +orderModel.apartment+"\n";
            }
            customer_address.setText(orderModel.address_line_1 +"\n" + address2 +
                    Util.capitalizeWords(orderModel.city) + ", " + Util.capitalizeWords(orderModel.state) + ", " + orderModel.zip);
            time_interval.setText(TimeUtils.getTime(orderModel.window_start) + " - " + TimeUtils.getTime(orderModel.window_end));
            orderStatus.setText(Util.capitalizeSentence(orderModel.orderStatus));
            if (orderModel.orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME)) {
                orderStatus.setText("Customer Not Home");
                status_layout.setCardBackgroundColor(this.getResources().getColor(R.color.customerNotHome));
            }
            if (orderModel.orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION)) {
                orderStatus.setText("Partial Rejection");
                status_layout.setCardBackgroundColor(this.getResources().getColor(R.color.partialRejection));
            }
            if (orderModel.orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)) {
                orderStatus.setText("Order Rejected");
                status_layout.setCardBackgroundColor(this.getResources().getColor(R.color.orderRejection));
            }
            if (orderModel.orderStatus.equalsIgnoreCase(Constants.INVALID)) {
                orderStatus.setText("Invalid");
                status_layout.setCardBackgroundColor(this.getResources().getColor(R.color.invalid));
            }
            if (orderModel.orderStatus.equalsIgnoreCase(Constants.ENROUTE)) {
                orderStatus.setText("Enroute");
                status_layout.setCardBackgroundColor(this.getResources().getColor(R.color.enroute));
            }
            if (orderModel.orderStatus.equalsIgnoreCase(Constants.PENDING)) {
                orderStatus.setText("Pending");
                status_layout.setCardBackgroundColor(this.getResources().getColor(R.color.pending));
            }
            if(orderModel.delivery_instructions.equalsIgnoreCase("null")){
                delivery_instructions.setText("- -");
            }else{
                delivery_instructions.setText(orderModel.delivery_instructions);
            }

            if(orderModel.unattended_instructions.equalsIgnoreCase("null")){
                unattended_instructions.setText("- -");
            }else{
                unattended_instructions.setText(orderModel.unattended_instructions);
            }



            if (orderModel.orderStatus.equalsIgnoreCase(Constants.DELIVERED) || orderModel.orderStatus.equalsIgnoreCase(Constants.INVALID) ||
                    orderModel.orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME) || orderModel.orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)
                    || orderModel.orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION)) {
                actionsLayout.setVisibility(View.GONE);
            }


            packages_list = (ListView) view.findViewById(R.id.packages_list);

            packagesAdapter = new PackagesAdapter(OrderDetailActivity.this, R.layout.packages_list_item, orderModel.packages);
            packages_list.setAdapter(packagesAdapter);
            Util.setListViewHeightBasedOnChildren(packages_list);

            customer_notathome = (Button) view.findViewById(R.id.customer_notathome);
            partial_rejection = (Button) view.findViewById(R.id.partial_rejection);
            order_rejection = (Button) view.findViewById(R.id.order_rejection);
            customer_notathome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (orderModel.isOrdrderStarted) {
                        updateOrderStatus(Constants.CUSTOMER_NOT_HOME);
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Start order to complete the order", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            partial_rejection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (orderModel.isOrdrderStarted) {
                        updateOrderStatus(Constants.PARTIAL_REJECTION);
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Start order to complete the order", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            order_rejection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (orderModel.isOrdrderStarted) {
                        updateOrderStatus(Constants.ORDER_REJECTION);
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Start order to complete the order", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            completeOrder = (Button) view.findViewById(R.id.complete_order);
            completeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipCard();
                }
            });
            return view;
        }
    }

    @SuppressLint("ValidFragment")
    public class CompleteOrderFragment extends Fragment {
        private SignaturePad mSignaturePad;
        private Button complete_order;
        private Bitmap map;

        public CompleteOrderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.complete_order_fragment, container, false);
            mSignaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
            complete_order = (Button) view.findViewById(R.id.complete_order);
            complete_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (orderModel.isOrdrderStarted) {
                        if (map != null) {
                            updateOrderStatus(Constants.DELIVERED);
                        } else {
                            Toast.makeText(OrderDetailActivity.this, "Signature is mandatory", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Start order to complete the order", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(getActivity(), "Order Completed Successfully", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getActivity(), AllOrdersActivity.class));

                }
            });
            mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                @Override
                public void onStartSigning() {

                }

                @Override
                public void onSigned() {
                    //Event triggered when the pad is signed
                    //store in bitmap
                    map = mSignaturePad.getSignatureBitmap();
                    // int x=map.getByteCount();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    map.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    customerSignatureEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    //int y=map.getByteCount();


                }

                @Override
                public void onClear() {
                    //Event triggered when the pad is cleared
                }
            });
            return view;
        }


    }

    private void updateOrderStatus(String status) {
        event = status;
        AlertDialog.Builder dialog = new AlertDialog.Builder(OrderDetailActivity.this);
        dialog.setTitle("Complete Order");
        dialog.setMessage("Do you want to mark this order as " + event + "?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //GPSTracker tracker = new GPSTracker(_context);
                        // tracker.stopUsingGPS();
                        if (Util.isOnline(OrderDetailActivity.this)) {
                            GPSTracker tracker = new GPSTracker(OrderDetailActivity.this);
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
                                        payload.put("event", event);
                                        payload.put("window_end", orderModel.window_end);
                                        payload.put("created_at", "created_at");
                                        payload.put("delivered_at", TimeUtils.getCurrentTime());
                                        payload.put("tracking_code", "tracking_code");
                                        payload.put("order_reference", orderModel.order_id);

                                        JSONObject driver = new JSONObject();
                                        driver.put("name", FDPreferences.getFirstname());
                                        driver.put("photo_url", FDPreferences.getAvatarurl());
                                        payload.put("driver", driver);

                                       /* int lastOrderCount=0;
                                        boolean isLastOrder=false;
                                        for(int i=0;i<orderModelArrayList.size();i++){
                                            if(orderModelArrayList.get(i).orderStatus.equalsIgnoreCase(Constants.PENDING)||orderModelArrayList.get(i).orderStatus.equalsIgnoreCase(Constants.ENROUTE)){
                                                lastOrderCount=lastOrderCount+1;
                                            }
                                        }
                                        if(lastOrderCount==1){
                                            isLastOrder=true;
                                        }else{
                                            isLastOrder=false;
                                        }
                                        payload.put("isLastOrder", isLastOrder);*/

                                        if (orderModelArrayList.size() > 0) {
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

                                        JSONObject exception = new JSONObject();
                                        exception.put("code", "");
                                        exception.put("description", "");
                                        payload.put("exception", event);

                                        JSONObject location = new JSONObject();
                                        location.put("lat", String.valueOf(latitude));
                                        location.put("lng", String.valueOf(longitude));
                                        location.put("time", TimeUtils.getCurrentTime());
                                        payload.put("location", location);

                                        if (event.equalsIgnoreCase("delivered")) {
                                            JSONObject proof_of_delivery = new JSONObject();
                                            JSONObject destination_signature = new JSONObject();
                                            destination_signature.put("photo_url", customerSignatureEncoded);
                                            destination_signature.put("last_name", orderModel.first_name + " " + orderModel.last_name);
                                            proof_of_delivery.put("destination_signature", destination_signature);
                                            payload.put("proof_of_delivery", proof_of_delivery);
                                            //payload.put()
                                        }

                                        data.add(payload);

                                        JSONObject mainobj = new JSONObject();
                                        mainobj.put("Order_status", new JSONArray(data));


                                        NetworkConnector connect = new NetworkConnector(OrderDetailActivity.this, NetworkConnector.TYPE_PUT, FDUrls.UPDATE_ORDERS + FDPreferences.getTripId(), headers, mainobj.toString(), OrderDetailActivity.this);
                                        connect.execute();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(OrderDetailActivity.this, "Location details not found,try again?", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            GPSTracker tracker = new GPSTracker(OrderDetailActivity.this);
                            if (tracker.canGetLocation()) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("offlineSyncEnabled", true);
                                contentValues.put("event", event);
                                contentValues.put("orderStatus", event);
                                contentValues.put("customerSignature", customerSignatureEncoded);
                                contentValues.put("offlinelat", tracker.getLatitude());
                                contentValues.put("offlinelng", tracker.getLongitude());
                                contentValues.put("offlinetime", TimeUtils.getCurrentTime());
                                int rowsUpdated = DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", contentValues, "order_id='" + orderModel.order_id + "'");
                                Toast.makeText(OrderDetailActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                orderModel.isOrdrderStarted = false;
                                intent.putExtra("orderId", orderModel.order_id);
                                intent.putExtra("event", event);
                                intent.putExtra("orderEnded", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                tracker.showSettingsAlert();
                            }
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
                //Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                onBackPressed();
            } else {
                if (scannedBags.contains(result.getContents())) {
                    Toast.makeText(OrderDetailActivity.this, "Bag already scanned", Toast.LENGTH_SHORT).show();
                    IntentIntegrator integrator = new IntentIntegrator(OrderDetailActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt("Scan Bags" + "   " + "Bags" + "  " + String.valueOf(bagsCount) + "  " + "of" + "   " + String.valueOf(orderModel.packages.size()));
                    integrator.setCameraId(0);// Use a specific camera of the device
                    integrator.setBeepEnabled(true);
                    integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                    integrator.initiateScan();
                } else {
                    scannedBags.add(result.getContents());


                    for (int i = 0; i < orderModel.packages.size(); i++) {
                        if (result.getContents().equalsIgnoreCase(orderModel.packages.get(i).bag_id)) {
                            validBag = true;
                            break;
                        } else {
                            validBag = false;
                            // break;
                        }

                    }
                    if (validBag) {
                        IntentIntegrator integrator = new IntentIntegrator(OrderDetailActivity.this);
                        bagsCount = bagsCount + 1;
                        if (bagsCount > orderModel.packages.size()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("scanCompleted", true);
                            DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", contentValues, "order_id='" + orderModel.order_id + "'");
                            parent.setVisibility(View.VISIBLE);
                        } else {
                            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                            integrator.setPrompt("Scan Bags" + "   " + "Bags" + "  " + String.valueOf(bagsCount) + "  " + "of" + "   " + String.valueOf(orderModel.packages.size()));
                            integrator.setCameraId(0);// Use a specific camera of the device
                            integrator.setBeepEnabled(true);
                            integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                            integrator.initiateScan();
                        }
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(OrderDetailActivity.this);
                        dialog.setTitle("Invalid Order");
                        dialog.setMessage("This bag does not belong to this order");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("Try again?",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        IntentIntegrator integrator = new IntentIntegrator(OrderDetailActivity.this);

                                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                                        integrator.setPrompt("Scan Bags" + "   " + "Bags" + "  " + String.valueOf(bagsCount) + "  " + "of" + "   " + String.valueOf(orderModel.packages.size()));
                                        integrator.setCameraId(0);// Use a specific camera of the device
                                        integrator.setBeepEnabled(true);
                                        integrator.setCaptureActivity(BarcodeQrcodeCustomActivity.class);
                                        integrator.initiateScan();


                                    }
                                });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                onBackPressed();
                            }
                        });

                        AlertDialog alert = dialog.create();
                        alert.setCanceledOnTouchOutside(true);
                        alert.show();
                    }
                }
            }


        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


