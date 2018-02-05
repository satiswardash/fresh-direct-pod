package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.GPSTracker;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.OnSwipeTouchListener;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Arun on 04-10-2016.
 */
public class CompleteTripActivity extends Activity implements AsyncResponse {
    private TextView tripId, ordersDelivered, TimeTaken;
    private ImageView complete_trip;
    private String status = "";
    private String message = "";
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_trip);
        tripId = (TextView) findViewById(R.id.tripId);
        //tripId.setText("TripId : "+FDPreferences.getTripId());
        ordersDelivered = (TextView) findViewById(R.id.orders_count);
        //ordersDelivered.setText(getIntent().getStringExtra("totalOrders"));


        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            date = formatter.parse(FDPreferences.getStartTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimeTaken = (TextView) findViewById(R.id.time_taken);
        // TimeTaken.setText(TimeUtils.getTimeString(date,TimeUtils.getCurrentTime()));


        complete_trip = (ImageView) findViewById(R.id.complete_trip);
        complete_trip.setOnTouchListener(new OnSwipeTouchListener(CompleteTripActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(StartTripActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight(float xvalue) {
                // move the imageview from right to left


            }

            public void onSwipeLeft(float y) {
                if (Util.isOnline(CompleteTripActivity.this)) {
                    TranslateAnimation animation = new TranslateAnimation(y, 0.0f, 0.0f, 0.0f);
                    animation.setDuration(650);
                    animation.setRepeatCount(0);
                    animation.setRepeatMode(0);
                    animation.setFillAfter(true);
                    complete_trip.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            commpleteTrip();

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    Toast.makeText(CompleteTripActivity.this, "You have to be online to CompleteTrip", Toast.LENGTH_SHORT).show();
                }
            }

            public void onSwipeBottom() {
                // Toast.makeText(StartTripActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void commpleteTrip() {

        GPSTracker tracker = new GPSTracker(CompleteTripActivity.this);
        if (tracker.canGetLocation()) {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();
            if (!(latitude == 0.0 || longitude == 0.0)) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-email-id", FDPreferences.getEmail());
                headers.put("x-access-token", FDPreferences.getAccessToken());
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("lat", String.valueOf(latitude));
                    payload.put("lng", String.valueOf(longitude));
                    payload.put("time", TimeUtils.getCurrentTime());
                    NetworkConnector connect = new NetworkConnector(CompleteTripActivity.this, NetworkConnector.TYPE_PUT, FDUrls.COMPLETE_TRIP + FDPreferences.getTripId(), headers, payload.toString(), this);
                    connect.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CompleteTripActivity.this, "Gps has to be set to High Accuracy,please change in location services", Toast.LENGTH_SHORT).show();
            }
        } else {
            tracker.showSettingsAlert();
        }
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        if (url.equalsIgnoreCase(FDUrls.COMPLETE_TRIP + FDPreferences.getTripId())) {
            String actualString = output;
            if (output.startsWith("null")) {
                actualString = output.substring(4);
            }
            try {
                JSONObject mainobject = new JSONObject(actualString);
                status = mainobject.optString("status");
                message = mainobject.optString("message");
                if (status.equalsIgnoreCase("SUCCESS")) {
                    JSONObject data=mainobject.optJSONObject("data");
                    if(data.has("time")) {
                        FDPreferences.setEndTime(data.optString("time"));
                    }else{
                        FDPreferences.setEndTime("");
                    }
                    Util.deleteTables();

                    Intent intent=new Intent(CompleteTripActivity.this, OrderHistoryActivity.class);
                    intent.putExtra("totalOrders",getIntent().getStringExtra("totalOrders"));
                    intent.putExtra("tripID",FDPreferences.getTripId());
                    FDPreferences.setTripId("null");
                    startActivity(intent);

                } else {
                    Toast.makeText(CompleteTripActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(CompleteTripActivity.this);
    }
}
