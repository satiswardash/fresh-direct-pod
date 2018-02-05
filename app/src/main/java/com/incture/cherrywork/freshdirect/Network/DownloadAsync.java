package com.incture.cherrywork.freshdirect.Network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.Utils.Util;

import java.io.File;

/**
 * Created by Arun on 06-01-2017.
 */
public class DownloadAsync extends AsyncTask<Void,Void,File> {
    Context context;
    String url;
    File file = null;
    private ProgressDialog dialog;

    public DownloadAsync(Context context, String url){
        this.context = context;
        this.url = url ;
    }


    @Override
    protected File doInBackground(Void... params) {
        file= Util.downloadInstall(url,context);
        return file;
    }

    @Override
    protected void onPostExecute(File aVoid) {
        super.onPostExecute(aVoid);
        if(file!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        else{
            Toast.makeText(context, "Download error!", Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading");
        dialog.setCancelable(false);
        dialog.show();
    }
}
