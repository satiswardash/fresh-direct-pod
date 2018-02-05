package com.incture.cherrywork.freshdirect.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.Models.CommentModel;
import com.incture.cherrywork.freshdirect.Models.FeedsDataModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.MaterialImageView;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;



public class DetailCommentAdapter extends ArrayAdapter<CommentModel> implements
        AsyncResponse {
    private ArrayList<CommentModel> stateList=new ArrayList<>();
    private ArrayList<AttachmentModel> attachList=new ArrayList<>();
    boolean deleted=false;
    public static final String PREFS_NAME = "AppPeristantData";
    private SharedPreferences.Editor appPeristantDataEditor;
    private SharedPreferences appPeristantData;
    private Context _context;
    private String workItemId="";
    CommentModel item=new CommentModel();
    int commentPos=0;


    class ViewHolder {
        TextView FirstName;
        TextView SecondName;
        TextView Date;
        MaterialImageView mainPic;
        //GridView grid;
        RelativeLayout commentLayout;
    }

    public DetailCommentAdapter(Context context, int textViewResourceId, FeedsDataModel model) {
        super(context, textViewResourceId, model.getComments());
        this.stateList = new ArrayList<CommentModel>();
        this.stateList=(model.getComments());
        //Collections.reverse(stateList);

        this.attachList = new ArrayList<AttachmentModel>();
        this.attachList.addAll(model.getAttachments());
        this._context = context;

        this.workItemId = model.id;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.comments_list_item, null);
            holder = new ViewHolder();

            holder.FirstName = (TextView) convertView.findViewById(R.id.Text1);
            holder.Date = (TextView) convertView.findViewById(R.id.date1);
            holder.SecondName = (TextView) convertView.findViewById(R.id.TextView01);
            holder.mainPic = (MaterialImageView) convertView.findViewById(R.id.imageView1);
            //holder.grid = (GridView) convertView.findViewById(R.id.gridview);
            holder.commentLayout = (RelativeLayout)convertView.findViewById(R.id.parent3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        item = stateList.get(position);
        deleted = Boolean.parseBoolean(item.deleted);
        holder.FirstName.setText(item.userdisplayname);
        holder.SecondName.setText(item.body);
        if (item.body.startsWith("http://")) {
            Pattern pattern = Pattern.compile(item.body);
            Linkify.addLinks(holder.SecondName, pattern, "http://");
        }
        final String Url = item.useravatarurl;
        if (Url.contentEquals("") || item.useravatarurl.contentEquals("null")|| item.useravatarurl.isEmpty()) {
            holder.mainPic.setBackgroundResource(R.mipmap.defaultmedium);
        } else {
            Util.loadImage(_context, Url, holder.mainPic);

        }

//        holder.mainPic.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (item.useravatarurl.contentEquals("null")) {
//                    Toast toast = Toast.makeText(_context,  "No Image available", Toast.LENGTH_LONG);
//                    toast.show();
//                } else {
//                    Intent i = new Intent(_context, FullScreenProfile.class);
//                    i.putExtra("Img_Url",stateList.get(position).useravatarurl );
//                    _context.startActivity(i);
//                }
//            }
//        });


        if(deleted == false)
        {
            holder.commentLayout.setOnLongClickListener(new onLongClickListener(position));
        }

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter2.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        try {
            Date ActivityDate = formatter2.parse(stateList.get(position).time);
            holder.Date.setText(TimeUtils.getTimeString(ActivityDate, TimeUtils.getDateMonthFromDate(stateList.get(position).time)));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        appPeristantData = _context.getSharedPreferences(PREFS_NAME, 0);
        return convertView;
    }


    private class onLongClickListener implements View.OnLongClickListener
    {

        int commentPosition;

        public onLongClickListener(int position) {
            this.commentPosition = position;
        }

        @Override
        public boolean onLongClick(View view) {


            return true;

        }
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type)
    {


    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }


    @Override
    public int getCount() {
        return stateList.size();
    }
}