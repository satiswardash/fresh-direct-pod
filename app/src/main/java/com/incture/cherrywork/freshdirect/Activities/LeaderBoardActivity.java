package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arun on 30-08-2016.
 */
public class LeaderBoardActivity extends Activity {
    private ImageView back;
    private WebView webView;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(Color.parseColor("#f9ed47"));
        }
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        url = FDUrls.LEADERBOARD;

        if (Util.isOnline(this)) {
            startWebView(url);
            CookieManager.getInstance().setCookie(FDUrls.LEADERBOARD_COOKIE, "token=" + FDPreferences.getAccessToken());
            CookieManager.getInstance().setCookie(FDUrls.LEADERBOARD_COOKIE, "email=" + FDPreferences.getEmail());
            CookieSyncManager.getInstance().sync();
        } else {
            Toast.makeText(LeaderBoardActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void startWebView(String url) {


        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LeaderBoardActivity.this);
                String message = "SSL Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }
                message += " Do you want to continue anyway?";

                builder.setTitle("SSL Certificate Error");
                builder.setMessage(message);
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();

            }

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);

                //headers.put("x-email-id",CWIncturePreferences.getEmail());
                view.loadUrl(url);

                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {

                if (url.contains("session-expired")) {
                    Util.alertForLogout(LeaderBoardActivity.this);
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(LeaderBoardActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
                if (url.contains("download?")) {
                    webView.setDownloadListener(new DownloadListener() {
                        public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimetype, long contentLength) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                }

            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        //progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (url.contains("session-expired")) {
                    Util.alertForLogout(LeaderBoardActivity.this);
                }

            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        Map<String, String> headers = new HashMap<String, String>();

        //headers.put("x-email-id",AricentPreferences.getEmail());
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
