package com.incture.cherrywork.freshdirect.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Arun on 31-08-2016.
 */
public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        DbAdapter.getDbAdapterInstance().open(this);
        FDPreferences.init(SplashActivity.this);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Main Activity. */
                if (FDPreferences.getAccessToken().equalsIgnoreCase(FDPreferences.EMPTY_STRING_DEFAULT_VALUE)) {
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }else{

                    if (FDPreferences.getTripId().equalsIgnoreCase(FDPreferences.EMPTY_STRING_DEFAULT_VALUE)){
                        Intent mainIntent = new Intent(SplashActivity.this, StartTripActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }else if (!FDPreferences.isBagValid().equalsIgnoreCase("true")){
                        Intent mainIntent = new Intent(SplashActivity.this, ScanBagActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                    else{
                        Intent mainIntent = new Intent(SplashActivity.this, AllOrdersActivity.class);
                        mainIntent.putExtra("endLocationUpdates","");
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
