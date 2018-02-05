package com.incture.cherrywork.freshdirect.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.Util;


@SuppressLint("ResourceAsColor")
public class AttachmentGridAdapter extends BaseAdapter {
    private Context _context;

    // Keep all Images in array
    public String[] mThumbIds;

    // Constructor
    public AttachmentGridAdapter(Context c, String[] ThumbIds) {
        _context = c;
        mThumbIds = ThumbIds;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(_context);
        WindowManager wm = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int image_height = height / 2;
        imageView.setLayoutParams(new GridView.LayoutParams(width, image_height));
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Util.scaleDownAndLoadImage(_context, FDUrls.AVATAR_BASE + "large/" + mThumbIds[position], imageView,width,image_height);
        return imageView;
    }

}
