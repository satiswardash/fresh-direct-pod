package com.incture.cherrywork.freshdirect.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Network.HttpConnection;
import com.incture.cherrywork.freshdirect.Utils.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Arun on 26-08-2016.
 */
public class LocateActivity extends FragmentActivity {
    private FloatingActionButton navigate;
    private TextView customer_name,order_id;
    private String customerName,orderId="";

    private static  LatLng SOURCE = new LatLng(0.0, 0.0);
    private static  LatLng DESTINATION = new LatLng(0.0, 0.0);
    private static double srclatitude=0.0;
    private static double srclongitude=0.0;
    private static double destlatitude=0.0;
    private static double destlongitude=0.0;

    private GoogleMap googleMap;
    private ImageView back;
    final String TAG = "PathGoogleMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_google_map);
        customer_name=(TextView)findViewById(R.id.customer_name);
        order_id=(TextView)findViewById(R.id.order_id);
        customerName=getIntent().getStringExtra("customer_name");
        orderId=getIntent().getStringExtra("order_id");
        srclatitude=getIntent().getDoubleExtra("srclatitude",0.0);
        srclongitude=getIntent().getDoubleExtra("srclongitude",0.0);
        destlatitude=getIntent().getDoubleExtra("destlatitude",0.0);
        destlongitude=getIntent().getDoubleExtra("destlongitude",0.0);
        SOURCE=new LatLng(srclatitude,srclongitude);
        DESTINATION=new LatLng(destlatitude,destlongitude);
        customer_name.setText(customerName);
        order_id.setText(orderId);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        navigate=(FloatingActionButton)findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("google.navigation:q="+destlatitude+","+destlongitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(SOURCE);
        options.position(DESTINATION);
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DESTINATION, 11));
        addMarkers();

    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + SOURCE.latitude + "," + SOURCE.longitude
                + "|" + "|" + DESTINATION.latitude + ","
                + DESTINATION.longitude ;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+
       "json?origin="+SOURCE.latitude+","+SOURCE.longitude+"&destination="+DESTINATION.latitude +","+DESTINATION.longitude+"&sensor=false";
        return url;
    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(SOURCE).title("Your Location"));
            googleMap.addMarker(new MarkerOptions().position(DESTINATION).title(customerName));
            googleMap.addMarker(new MarkerOptions().position(DESTINATION).title("OrderId : "+orderId));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.parseColor("#4e8f2d"));
            }
            if(polyLineOptions!=null) {
                googleMap.addPolyline(polyLineOptions);
            }else{
                Toast.makeText(LocateActivity.this,"No route found",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }
}
