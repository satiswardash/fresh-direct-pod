package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arun on 14-10-2016.
 */
public class ResetPasswordActivity extends Activity implements AsyncResponse {
    private TextInputEditText currentPassword, newPassword, confirmPassword;
    private TextInputLayout currentPasswordlayout, newPasswordlayout, confirmPasswordLayout;
    private ImageView back;
    private Button reset;
    private RelativeLayout parent;
    private String currentPasswordString = "";
    private String newPasswordString = "";
    private String confirmPasswordString = "";
    private String status = "";
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        currentPassword = (TextInputEditText) findViewById(R.id.current_password);
        newPassword = (TextInputEditText) findViewById(R.id.new_password);
        confirmPassword = (TextInputEditText) findViewById(R.id.confirm_password);

        currentPasswordlayout = (TextInputLayout) findViewById(R.id.current_password_layout);
        newPasswordlayout = (TextInputLayout) findViewById(R.id.new_password_layout);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.confirm_password_layout);

        parent = (RelativeLayout) findViewById(R.id.parent);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newPassword.getWindowToken(), 0);
            }
        });

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newPassword.getWindowToken(), 0);
                Reset();
            }
        });
    }

    private void Reset() {
        currentPasswordString = currentPassword.getText().toString().trim();
        newPasswordString = newPassword.getText().toString().trim();
        confirmPasswordString = confirmPassword.getText().toString().trim();
        if (Util.isOnline(this)) {
            if ((currentPasswordString.isEmpty() || currentPasswordString.length() == 0) && (newPasswordString.isEmpty() || newPasswordString.length() == 0) && (confirmPasswordString.isEmpty() || confirmPasswordString.length() == 0)) {
                currentPasswordlayout.setError("Password cannot be empty");
                newPasswordlayout.setError("Password cannot be empty");
                confirmPasswordLayout.setError("Password cannot be empty");
            } else if (currentPasswordString.isEmpty()) {
                if (newPasswordString.isEmpty()) {
                    newPasswordlayout.setError("Password cannot be empty");
                } else {
                    newPasswordlayout.setErrorEnabled(false);
                }
                if (confirmPasswordString.isEmpty()) {
                    confirmPasswordLayout.setError("Password cannot be empty");
                } else {
                    confirmPasswordLayout.setErrorEnabled(false);
                }
                currentPasswordlayout.setError("Password cannot be empty");
            } else if (newPasswordString.isEmpty()) {
                if (currentPasswordString.isEmpty()) {
                    currentPassword.setError("Password cannot be empty");
                } else {
                    currentPasswordlayout.setErrorEnabled(false);
                }
                if (confirmPasswordString.isEmpty()) {
                    confirmPasswordLayout.setError("Password cannot be empty");
                } else {
                    confirmPasswordLayout.setErrorEnabled(false);
                }

                newPasswordlayout.setError("Password cannot be empty");
            } else if (confirmPasswordString.isEmpty()) {
                if (currentPasswordString.isEmpty()) {
                    currentPasswordlayout.setError("Password cannot be empty");
                } else {
                    currentPasswordlayout.setErrorEnabled(false);
                }
                if (newPasswordString.isEmpty()) {
                    newPasswordlayout.setError("Password cannot be empty");
                } else {
                    newPasswordlayout.setErrorEnabled(false);
                }


                confirmPasswordLayout.setError("Password cannot be empty");
            } else if (!newPasswordString.equalsIgnoreCase(confirmPasswordString)) {
                Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                confirmPasswordLayout.setError("Passwords do not match");

            } else {
                resetAsync();
            }

        } else {
            Snackbar snack = Snackbar.make(parent, "No Internet Connection,Try Later", Snackbar.LENGTH_SHORT);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();

        }
    }

    private void resetAsync() {
        Map<String, String> headers = new HashMap<>();

        headers.put("x-email-id", FDPreferences.getEmail());
        headers.put("x-access-token", FDPreferences.getAccessToken());
        headers.put("Content-Type", "application/json");


        try {
            JSONObject obj = new JSONObject();
            obj.put("password", currentPasswordString);
            obj.put("newPassword", newPasswordString);
            String message = obj.toString();

            NetworkConnector connect = new NetworkConnector(ResetPasswordActivity.this, NetworkConnector.TYPE_POST, FDUrls.RESET_PASSWORD + FDPreferences.getUserId() + "/password", headers, message, ResetPasswordActivity.this);
            connect.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        if (url.contains(FDUrls.RESET_PASSWORD)) {
            try {
                JSONObject main_obj = new JSONObject(output);
                status = main_obj.optString("status");
                message = main_obj.optString("message");
                if(status.equalsIgnoreCase("success")){
                    Toast.makeText(ResetPasswordActivity.this,"Password reset successfully",Toast.LENGTH_SHORT).show();
                    FDPreferences.setAccessToken("null");
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                }else{
                    Toast.makeText(ResetPasswordActivity.this,message,Toast.LENGTH_SHORT).show();
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
