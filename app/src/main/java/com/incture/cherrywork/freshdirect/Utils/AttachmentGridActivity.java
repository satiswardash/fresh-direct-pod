package com.incture.cherrywork.freshdirect.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.incture.cherrywork.freshdirect.Adapters.AttachmentGridAdapter;
import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.R;

import java.util.ArrayList;




public class AttachmentGridActivity extends Activity {

    String[] attachments;
    String name = "", date = "";
    private ArrayList<AttachmentModel> attachmentList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.grid_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            //getWindow().setStatusBarColor(getResources().getColor(R.color.my_primary_dark));

        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            attachments = bundle.getStringArray("SELECTED");
            name = bundle.getString("Name");
            date = bundle.getString("Date");
            attachmentList= (ArrayList<AttachmentModel>) bundle.getSerializable("Attachments");

        }
        final Bundle bundleForGallery = new Bundle();
        bundleForGallery.putStringArray("SELECTED", attachments);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new AttachmentGridAdapter(this, attachments));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullGallery.class);
                // passing array index
                i.putExtra("Position", position);
                i.putExtra("Attachments", attachmentList);
                i.putExtra("Name", name);
                i.putExtra("Date", date);
                i.putExtras(bundleForGallery);
                startActivity(i);
            }
        });


    }
}