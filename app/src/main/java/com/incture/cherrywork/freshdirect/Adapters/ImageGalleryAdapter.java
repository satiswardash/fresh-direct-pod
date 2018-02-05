package com.incture.cherrywork.freshdirect.Adapters;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import java.util.ArrayList;


public class ImageGalleryAdapter extends BaseAdapter implements OnClickListener {
    private Context _context;
    private String[] mimages;
    private LayoutInflater mInflater;
    ArrayList<AttachmentModel> attachmentList;

    public ImageGalleryAdapter(Context context, String[] attachments, ArrayList<AttachmentModel> attachmentList) {
        _context = context;
        mimages = attachments;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.attachmentList=attachmentList;
    }

    public int getCount() {
        return mimages.length;
    }

    class ViewHolder {
        ImageView imageview;
        int id;
        public TextView uploadedby;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // Override this method according to your need
    @SuppressWarnings("deprecation")
    public View getView(int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        int width = 0;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.imageadapteritem, null);
            holder.imageview = (ImageView) view.findViewById(R.id._imageName);
            holder.uploadedby= (TextView)view.findViewById(R.id.uploadedby);
            WindowManager wm = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            width = display.getWidth(); // deprecated

            holder.imageview.setId(index);
            holder.imageview.getLayoutParams().width = width;
            holder.imageview.getLayoutParams().height = width;
            holder.imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.imageview.setId(index);
        if(attachmentList!=null){
            holder.uploadedby.setText(attachmentList.get(index).displayname+"\n"+ TimeUtils.getDateMonthFromDate(attachmentList.get(index).createdat));
        }
        Util.scaleDownAndLoadImage(_context, FDUrls.AVATAR_BASE+"large/" + mimages[index],holder.imageview,width,width);

        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}
