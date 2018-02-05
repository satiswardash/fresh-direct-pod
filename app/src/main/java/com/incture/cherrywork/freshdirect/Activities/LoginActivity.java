package com.incture.cherrywork.freshdirect.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.Models.LoginModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Arun on 08-08-2016.
 */
public class LoginActivity extends Activity implements AsyncResponse {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private Button login_button;
    private String _deviceToken = "";
    private LoginModel loginModel;
    private RelativeLayout parent;
    private String userName, passWord, deviceId = "";
    private TelephonyManager TelephonyMgr;
    private TextInputLayout username_layout, password_layout;
    private TextInputEditText user_name, password;
    private TextView forgot_password;
    private static final String PROJECT_NUMBER = "691064224085";
    private String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fabric.with(this, new Crashlytics());
        DbAdapter.getDbAdapterInstance().open(this);
        FDPreferences.init(LoginActivity.this);
        //ask for Runtime-permissions for android 6.0 and above
        checkPermissions();
        //init
        login_button = (Button) findViewById(R.id.login_button);
        parent = (RelativeLayout) findViewById(R.id.parent);
        user_name = (TextInputEditText) findViewById(R.id.user_name);
        username_layout = (TextInputLayout) findViewById(R.id.user_name_layout);
        password_layout = (TextInputLayout) findViewById(R.id.pwd_layout);
        password = (TextInputEditText) findViewById(R.id.password);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        //forgot passord
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                login();
            }
        });
        //RegisterDevice for PushNotifications etc.
        getDeviceGcmToken();

    }

    private void getDeviceGcmToken() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                GoogleCloudMessaging gcm;
                String deviceToken = "";
                try {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    deviceToken = gcm.register(PROJECT_NUMBER);

                } catch (IOException ex) {

                }
                return deviceToken;
            }

            @Override
            protected void onPostExecute(String token) {
                _deviceToken = token;

                if (token != null) {
                    FDPreferences.init(LoginActivity.this);
                    FDPreferences.setDeviceToken(token);
                }

            }
        }.execute(null, null, null);
    }

    private void login() {
        userName = user_name.getText().toString().trim();
        passWord = password.getText().toString();
        if (Util.isOnline(this)) {
            if (userName.isEmpty() && (passWord.isEmpty() || passWord.length() == 0)) {
                username_layout.setError("Username cannot be empty");
                password_layout.setError("Password cannot be empty");
            } else if (passWord.isEmpty()) {
                username_layout.setErrorEnabled(false);
                password_layout.setError("Password cannot be empty");
            } else if (userName.isEmpty()) {
                username_layout.setError("Username cannot be empty");
                password_layout.setErrorEnabled(false);
            } else {
                username_layout.setErrorEnabled(false);
                password_layout.setErrorEnabled(false);
                loginAsync();
            }

        } else {
            Snackbar snack = Snackbar.make(parent, "No Internet Connection,Try Later", Snackbar.LENGTH_SHORT);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();

        }
    }

    private void loginAsync() {
        if (checkPermissions()) {
            TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            deviceId = TelephonyMgr.getDeviceId();
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-device-id", deviceId);
                headers.put("x-device-type", "android");
                headers.put("x-device-token", FDPreferences.getDeviceToken());
                headers.put("Content-Type", "application/json");


                try {
                    JSONObject obj = new JSONObject();
                    obj.put("email", userName);
                    obj.put("password", passWord);
                    obj.put("appVersion", Util.getCurrentAppVersion(this));
                    String message = obj.toString();
                    Log.d("DEVICE TOKEN", FDPreferences.getDeviceToken());
                    NetworkConnector connect = new NetworkConnector(LoginActivity.this, NetworkConnector.TYPE_POST, FDUrls.LOGIN_URL + "?auth=cherrywork", headers, message, LoginActivity.this);
                    if (connect.isAllowed()) {
                        connect.execute();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.action_not_allowed), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } else {
            Toast.makeText(LoginActivity.this, "Turn on permissions in app Settings", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String Permissions : permissions) {
            result = ContextCompat.checkSelfPermission(LoginActivity.this, Permissions);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Permissions);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        if (url.equalsIgnoreCase(FDUrls.LOGIN_URL + "?auth=cherrywork")) {
            loginModel = new LoginModel();

            try {
                JSONObject main_obj = new JSONObject(output);
                if (main_obj.has("data")) {
                    JSONObject data = main_obj.optJSONObject("data");
                    if (data != null) {
                        loginModel.accesstoken = data.optString("token");
                        loginModel.firstname = Util.capitalizeWords(data.optString("firstName"));
                        loginModel.lastName = Util.capitalizeWords(data.optString("lastName"));
                        loginModel.avatarUrl = data.optString("avatar");
                        loginModel.userId = data.optString("_id");
                        loginModel.employeeId = data.optString("employee_id");
                        loginModel.email = data.optString("email");
                        loginModel.designation = data.optString("designation");
                        loginModel.carrierName = data.optString("carrier_name");
                    }
                }
                if (main_obj.has("status")) {
                    loginModel.status = main_obj.optString("status");
                }
                if (main_obj.has("message")) {
                    loginModel.message = main_obj.optString("message");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (loginModel.status.equals("success")) {
                FDPreferences.init(LoginActivity.this);
                FDPreferences.setAccessToken(loginModel.getAccesstoken());
                FDPreferences.setUserId(loginModel.getUserId());
                FDPreferences.setFirstname(loginModel.getFirstname());
                FDPreferences.setLastname(loginModel.getLastName());
                FDPreferences.setAvatarurl(loginModel.getAvatarUrl());
                FDPreferences.setEmail(loginModel.getEmail());
                FDPreferences.setEmployeeID(loginModel.getEmployeeId());
                FDPreferences.setDesignation(loginModel.getDesignation());
                FDPreferences.setTripId("null");
                FDPreferences.setCarrierName(loginModel.getCarrierName());
                startActivity(new Intent(LoginActivity.this, StartTripActivity.class));
            } else {
                Snackbar.make(parent, loginModel.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }


}
