package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Arun on 17-10-2016.
 */
public class OrderHistoryActivity extends Activity{
    private TextView tripId,orderCount,timeTakenHours,timeTakenMinutes;
    private Button startTrip;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
        tripId=(TextView)findViewById(R.id.tripId);
        orderCount=(TextView)findViewById(R.id.orders_count);
        timeTakenHours=(TextView)findViewById(R.id.time_taken_hrs);
        timeTakenMinutes=(TextView)findViewById(R.id.time_taken_mins);
        tripId.setText("Trip ID : "+getIntent().getStringExtra("tripID"));
        orderCount.setText(getIntent().getStringExtra("totalOrders"));
        String startTime = FDPreferences.getStartTime();
        String endTime = FDPreferences.getEndTime();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            Date startDate = formatter.parse(startTime);
            Date endDate = formatter.parse(endTime);
            long diff = endDate.getTime() - startDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;


            timeTakenHours.setText(String.valueOf(hours));
            timeTakenMinutes.setText(String.valueOf(minutes%60));


        } catch (ParseException e) {
            e.printStackTrace();
        }


        // timeTaken.setText(TimeUtils.getTimeString(date,TimeUtils.getCurrentTime()));
        startTrip=(Button) findViewById(R.id.start_trip);
        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderHistoryActivity.this, StartTripActivity.class));
                FDPreferences.setTripId("null");
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(OrderHistoryActivity.this);
    }
}
