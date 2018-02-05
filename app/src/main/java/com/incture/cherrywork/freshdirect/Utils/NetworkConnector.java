package com.incture.cherrywork.freshdirect.Utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.Activities.LoginActivity;
import com.incture.cherrywork.freshdirect.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Deeksha on 14-12-2015.
 */
public class NetworkConnector extends AsyncTask<String, Void, String> {
    private Context _context;
    public static int TYPE_PUT = 0;
    public static int TYPE_GET = 1;
    public static int TYPE_POST = 2;
    public static int TYPE_DELETE = 3;


    private ProgressDialog dialog;
    private int type;
    private Map<String, String> headers;
    private String entity;
    private String url;
    private int status_code;
    private boolean login = false;
    private AsyncResponse listener;
    private String cardConfigValue = "";
    private boolean isAllowed = true;
    private static long TIME_OUT_IN_SECONDS = 120;

    private boolean isBackground = false;

    public NetworkConnector(Context context, int type, String url, Map<String, String> headers, String entity, AsyncResponse listener) {
        this._context = context;
        this.type = type;
        this.url = url;
        this.entity = entity;
        this.headers = headers;
        this.listener = listener;
    }


    public NetworkConnector(Context context, int type, String url, Map<String, String> headers, String entity, AsyncResponse listener, boolean isBackground) {
        this.isBackground = isBackground;
        this._context = context;
        this.type = type;
        this.url = url;
        this.entity = entity;
        this.headers = headers;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!isBackground) {
            dialog = new ProgressDialog(_context, R.style.AppDialogTheme);
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(String[] params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpresponse = null;
        String response = "";
        String actualString = "";
        switch (type) {
            case 0:
                HttpPut httpput = new HttpPut(url);


                if (headers != null) {
                    for (String key : headers.keySet()) {
                        httpput.addHeader(key, headers.get(key));
                    }
                    if (!headers.containsKey("Content-Type")) {
                        httpput.addHeader("Content-Type", "application/json");
                    }
                }


                if (entity != null && !entity.isEmpty()) {
                    try {
                        httpput.setEntity(new StringEntity(entity, "UTF8"));
                        long requestStratTime = new Date().getTime();
                        httpresponse = httpClient.execute(httpput);
                        long requestEndTime = new Date().getTime();
                        long timeOfRequest = (requestEndTime - requestStratTime) / 1000;
                        if (httpresponse == null && timeOfRequest > TIME_OUT_IN_SECONDS) {
                            Toast.makeText(_context, "Your connection timedout", Toast.LENGTH_LONG).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        status_code = 404;
                        actualString = "Connection error";
                    }

                }
                if (httpresponse == null) {

                    try {

                        httpresponse = httpClient.execute(httpput);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        status_code = 404;
                        actualString = "Connection error";
                    }
                }
                break;
            case 1:
                HttpGet httpGet = new HttpGet(url);
                if (headers != null) {
                    for (String key : headers.keySet()) {
                        httpGet.addHeader(key, headers.get(key));
                    }
                }
                try {
                    long requestStratTime = new Date().getTime();
                    httpresponse = httpClient.execute(httpGet);
                    long requestEndTime = new Date().getTime();
                    long timeOfRequest = (requestEndTime - requestStratTime) / 1000;
                    if (httpresponse == null && timeOfRequest > TIME_OUT_IN_SECONDS) {
                        Toast.makeText(_context, "Your connection timedout", Toast.LENGTH_LONG).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    status_code = 404;
                    actualString = "Connection error";
                }

                break;
            case 2:
                HttpPost httppost = new HttpPost(url);
                if (headers != null) {
                    for (String key : headers.keySet()) {
                        httppost.addHeader(key, headers.get(key));
                    }
                    if (!headers.containsKey("Content-Type")) {
                        httppost.addHeader("Content-Type", "application/json");
                    }
                }

                if (entity != null && !entity.isEmpty()) {
                    try {
                        httppost.setEntity(new StringEntity(entity, "UTF8"));
                        long requestStratTime = new Date().getTime();
                        httpresponse = httpClient.execute(httppost);
                        long requestEndTime = new Date().getTime();
                        long timeOfRequest = (requestEndTime - requestStratTime) / 1000;
                        if (httpresponse == null && timeOfRequest > TIME_OUT_IN_SECONDS) {
                            Toast.makeText(_context, "Your connection timedout", Toast.LENGTH_LONG).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        status_code = 404;
                        actualString = "Connection error";
                    }
                }

                if (httpresponse == null) {
                    try {
                        httpresponse = httpClient.execute(httppost);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        status_code = 404;
                        actualString = "Connection error";
                    }
                }
                break;

            case 3:
                HttpDelete httpDelete = new HttpDelete(url);
                if (headers != null) {
                    for (String key : headers.keySet()) {
                        httpDelete.addHeader(key, headers.get(key));
                    }
                }

                if (httpresponse == null) {
                    try {
                        long requestStratTime = new Date().getTime();
                        httpresponse = httpClient.execute(httpDelete);
                        long requestEndTime = new Date().getTime();
                        long timeOfRequest = (requestEndTime - requestStratTime) / 1000;
                        if (httpresponse == null && timeOfRequest > TIME_OUT_IN_SECONDS) {
                            Toast.makeText(_context, "Your connection timedout", Toast.LENGTH_LONG).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        status_code = 404;
                        actualString = "Connection error";
                    }
                }
                break;

            default:
                break;
        }
        if (httpresponse != null) {
            status_code = httpresponse.getStatusLine().getStatusCode();

            HttpEntity httpEntity = httpresponse.getEntity();
            InputStream stream = null;
            try {
                stream = httpEntity.getContent();


                BufferedReader buffer = new BufferedReader(new InputStreamReader(
                        stream));

                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
                Log.d("result", response);
                actualString = response;

                if (response.startsWith("null")) {
                    actualString = response.substring(4);
                }


            } catch (IOException e) {
                status_code = 404;
                actualString = "Connection error";

            }
        }
        return actualString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (status_code == 403) {

            String message = _context.getString(R.string.invalid_credential);
            if (result != null) {
                try {
                    JSONObject mainobj = new JSONObject(result);
                    if (mainobj != null) {
                        message = mainobj.optString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            if (!(url.equalsIgnoreCase(FDUrls.LOGIN_URL + "?auth=cherrywork")||url.equalsIgnoreCase(FDUrls.BASE_URL + "forgotPassword"))){
                if(_context!=null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                    dialog.setMessage(message);
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //GPSTracker tracker = new GPSTracker(_context);
                                    // tracker.stopUsingGPS();
                                    FDPreferences.resetPreferences();
                                    Util.deleteTables();
                                    _context.startActivity(new Intent(_context, LoginActivity.class));
                                }
                            });


                    AlertDialog alert = dialog.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                }

            } else {
                FDPreferences.resetPreferences();
                Util.deleteTables();
                Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
            }


        } else if (status_code == 200 || status_code == 201) {
            FDPreferences.init(_context);
            if (listener != null) {
                listener.processFinish(result, status_code, url, type);

            }

        } else if (status_code == 400 && listener != null) {
            listener.processFinish(result, status_code, url, type);
        } else if (result != null && !result.isEmpty()) {

            Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
        }
    }


    public void setLogin(boolean b) {
        login = b;
    }

    public boolean isAllowed() {
        return isAllowed;
    }
}