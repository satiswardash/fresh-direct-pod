package com.incture.cherrywork.freshdirect.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Gallery;

import com.incture.cherrywork.freshdirect.Adapters.ImageGalleryAdapter;
import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.R;

import java.util.ArrayList;



/**
 * @author shridharjoshi
 */
public class FullGallery extends Activity {

    String[] attachments;
    ArrayList<AttachmentModel> attachmentList=new ArrayList<>();
    Gallery gallery;
    int position;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullgallery);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            attachments = extras.getStringArray("SELECTED");
            position = extras.getInt("Position");
            attachmentList= (ArrayList<AttachmentModel>) extras.getSerializable("Attachments");

        }

        gallery = (Gallery) findViewById(R.id.gallery2);

        gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, new KeyEvent(0, 0));


        gallery.setAdapter(new ImageGalleryAdapter(FullGallery.this,
                attachments,attachmentList));

        gallery.setSelected(true);
        gallery.setSelection(position);


    }

}
