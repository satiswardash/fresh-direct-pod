package com.incture.cherrywork.freshdirect.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.incture.cherrywork.freshdirect.Activities.LoginActivity;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Arun on 22-08-2016.
 */
public class Util {

    public static boolean isOnline(Context _context) {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void logoutfunc(Context context) {
    }

    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder(str);
        int i = 0;
        do {
            if (b.length() > i + 1)
                b.replace(i, i + 1, b.substring(i, i + 1).toUpperCase());
            else {
                b.replace(i, i + 1, String.valueOf(Character.toUpperCase(b.charAt(i))));
            }
            i = b.indexOf(" ", i) + 1;
        } while (i > 0 && i < b.length());

        return b.toString();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            // if (i == 0)
           /* view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));*/
            view.setLayoutParams(new ViewGroup.LayoutParams(0,0));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (2 * (listAdapter.getCount() - 1)) + 50;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void deleteTables() {
        DbAdapter.getDbAdapterInstance().delete("ORDERS_TABLE");
        DbAdapter.getDbAdapterInstance().delete("PACKAGES_TABLE");
        DbAdapter.getDbAdapterInstance().delete("LOCATIONS_TABLE");

    }

    public static String capitalizeSentence(String line) {
        if (line.length() > 1)
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        else if (line.length() > 0) {
            return String.valueOf(Character.toUpperCase(line.charAt(0)));
        } else
            return "";
    }

    public static void loadAttachmentImageMaterial(Context _context, String url, MaterialImageView imageView) {

        String URL = url;
        if (URL.contentEquals("")) {
            imageView.setBackgroundResource(R.mipmap.defaultmedium);
            imageView.setImageResource(R.mipmap.defaultmedium);
        } else {
            loadImageWithPlaceHolder(_context, URL, imageView, R.mipmap.defaultmedium);
        }
    }

    public static void loadImageWithPlaceHolder(Context _context, String url, ImageView imageView, int place_holder) {
        if (isOnline(_context)) {
            Picasso.with(_context).load(url).fit().placeholder(place_holder).into(imageView);
        } else {
            Picasso.with(_context).load(url).fit().placeholder(place_holder).into(imageView);

        }
    }

    public static void resizeAndLoadImageSquare(Context _context, String url, ImageView imageView, int width, int height) {

        String URL = FDUrls.AVATAR_BASE + "square/"
                + url;
        if (URL.contentEquals(FDUrls.AVATAR_BASE + "square/")) {
            imageView.setImageResource(R.mipmap.defaultmedium);
        } else {
            resizeAndLoadImage(_context, URL, imageView, width, height);
        }
    }

    public static void resizeAndLoadImage(Context _context, String url, ImageView imageView, int width, int height) {
        if (isOnline(_context)) {
            Picasso.with(_context).load(url).resize(width, height).into(imageView);
        } else {
            Picasso.with(_context).load(url).resize(width, height).into(imageView);

        }
    }

    public static void scaleDownAndLoadImage(Context _context, String url, ImageView imageView, int width, int height) {
        if (isOnline(_context)) {
            Picasso.with(_context).load(url).resize(width, height).onlyScaleDown().into(imageView);
        } else {
            Picasso.with(_context).load(url).resize(width, height).onlyScaleDown().into(imageView);

        }
    }

    public static void loadImage(Context _context, String url, ImageView imageView) {

        Picasso.with(_context).load(url).fit().into(imageView);


    }

    public static void alertForLogout(final Context _context) {
        LayoutInflater inflater = ((Activity) _context)
                .getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(_context).setTitle(null).setCancelable(false);

        //alertbox.setTitle("ARICENT");

        alertbox.setMessage("Session expired. Please login to continue.");
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                FDPreferences.resetPreferences();

                deleteTables();

                //AricentApplication.getInstance().clearApplicationData();
                _context.startActivity(new Intent(_context, LoginActivity.class));
            }
        });

        AlertDialog dialog = alertbox.create();

        dialog.show();


    }

    public  static String getCurrentAppVersion(Context mcontext) {
        PackageInfo pInfo;
        String version="";
        try {
            pInfo = mcontext.getApplicationContext().getPackageManager().getPackageInfo(mcontext.getApplicationContext().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        return version;
    }
    public static File downloadInstall(String apkurl, Context _context){
        try {
            URL url = new URL(apkurl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            //c.setDoOutput(true);
            c.connect();
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "FreshDirect-POD");
            folder.mkdir();

            File file = new File(folder, "app.apk");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = new FileOutputStream(file);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
            return file;


        } catch (IOException e) {
            Log.d("DOWNLOAD ERROR",e.getMessage());
            return null;
        }
    }
}
