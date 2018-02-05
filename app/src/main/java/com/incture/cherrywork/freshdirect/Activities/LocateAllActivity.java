package com.incture.cherrywork.freshdirect.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.incture.cherrywork.freshdirect.Models.LatLongModel;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Arun on 29-08-2016.
 */
public class LocateAllActivity extends FragmentActivity {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 32;
    private GoogleMap googleMap;
    private ArrayList<LatLng> listLatLng;
    private ImageView back;
    private ArrayList<OrderModel> latlngs=new ArrayList<>();
    private RelativeLayout rlMapLayout;
    private HashMap<Marker, LatLongModel> hashMapMarker = new HashMap<Marker, LatLongModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_all);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        latlngs= (ArrayList<OrderModel>) getIntent().getSerializableExtra("latlngs");
        rlMapLayout = (RelativeLayout) findViewById(R.id.rlMapLayout);

        setUpMapIfNeeded();
        setData();

    }



    private void setData() {

        ArrayList<LatLongModel> arrayList = new ArrayList<LatLongModel>();
        for(int i=0;i<latlngs.size();i++) {
            LatLongModel bean = new LatLongModel();
            bean.setTitle(latlngs.get(i).first_name+" "+latlngs.get(i).last_name);
            bean.setSnippet("OrderId : "+latlngs.get(i).order_id);
            bean.setLatitude(String.valueOf(latlngs.get(i).lat));
            bean.setLongitude(String.valueOf(latlngs.get(i).lng));
            arrayList.add(bean);
        }
        LoadingGoogleMap(arrayList);

    }

    /**
     * @author Hasmukh Bhadani
     * Set googleMap if require
     */
    private void setUpMapIfNeeded() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Google Play Services are not available
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {
            if (googleMap == null) {
                googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
                if (googleMap != null) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                    }
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            }
        }
    }

    /**
     * @author Hasmukh Bhadani
     * Loading Data to the GoogleMap
     * @param arrayList
     */
    // -------------------------Google Map
    void LoadingGoogleMap(ArrayList<LatLongModel> arrayList) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            if(arrayList.size()>0)
            {
                try
                {
                    listLatLng=new ArrayList<LatLng>();
                    for (int i = 0; i < arrayList.size(); i++)
                    {
                        LatLongModel bean=arrayList.get(i);
                        if(bean.getLatitude().length()>0 && bean.getLongitude().length()>0)
                        {
                            double lat=Double.parseDouble(bean.getLatitude());
                            double lon=Double.parseDouble(bean.getLongitude());

                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat,lon))
                                    .title(bean.getTitle())
                                    .snippet(bean.getSnippet())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            //Add Marker to Hashmap
                            hashMapMarker.put(marker,bean);

                            //Set Zoom Level of Map pin
                            LatLng object=new LatLng(lat, lon);
                            listLatLng.add(object);
                        }
                    }
                    SetZoomlevel(listLatLng);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker position)
                    {
                        LatLongModel bean=hashMapMarker.get(position);
                        Toast.makeText(getApplicationContext(), bean.getTitle(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

        else
        {
            Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * @author Hasmukh Bhadani
     * Set Zoom level all pin withing screen on GoogleMap
     */
    public void  SetZoomlevel(ArrayList<LatLng> listLatLng)
    {
        if (listLatLng != null && listLatLng.size() == 1)
        {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
        }
        else if (listLatLng != null && listLatLng.size() > 1)
        {
            final Builder builder = LatLngBounds.builder();
            for (int i = 0; i < listLatLng.size(); i++)
            {
                builder.include(listLatLng.get(i));
            }

            final ViewTreeObserver treeObserver = rlMapLayout.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout()
                {
                    if(googleMap != null){
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), findViewById(R.id.map)
                                .getWidth(), findViewById(R.id.map).getHeight(), 80));
                        rlMapLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });

        }
    }

}
