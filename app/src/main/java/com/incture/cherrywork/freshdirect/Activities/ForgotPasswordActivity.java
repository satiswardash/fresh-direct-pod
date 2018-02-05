package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arun on 14-09-2016.
 */
public class ForgotPasswordActivity extends Activity implements AsyncResponse {
    private Button send;
    private RelativeLayout parent;
    private EditText email;
    private TextInputLayout email_layout;
    private String status = "";
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        send = (Button) findViewById(R.id.send);
        email = (EditText) findViewById(R.id.email);
        email_layout = (TextInputLayout) findViewById(R.id.email_layout);
        parent=(RelativeLayout)findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                String email_value = email.getText().toString().trim();
                if (email_value.isEmpty()) {
                    email_layout.setError("Email cannot be empty");
                } else {
                    email_layout.setErrorEnabled(false);
                    if (Util.isOnline(ForgotPasswordActivity.this)) {

                        forgotPassword(email_value);

                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }


                }

            }

        });
    }

    private void forgotPassword(String email_value) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");


        try {
            JSONObject obj = new JSONObject();
            obj.put("email", email_value);
            String message = obj.toString();
            NetworkConnector connect = new NetworkConnector(ForgotPasswordActivity.this, NetworkConnector.TYPE_POST, FDUrls.BASE_URL + "forgotPassword", headers, message, ForgotPasswordActivity.this);
            connect.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        try {

            JSONObject mainobj = new JSONObject(output);
            status = mainobj.optString("status");
            message = mainobj.optString("message");
            if (status.equalsIgnoreCase("success")) {
                Toast.makeText(ForgotPasswordActivity.this, "Reset instructions sent successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }
}
