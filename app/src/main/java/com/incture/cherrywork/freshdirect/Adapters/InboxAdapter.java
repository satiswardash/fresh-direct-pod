package com.incture.cherrywork.freshdirect.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.incture.cherrywork.freshdirect.Activities.FeedsActivity;
import com.incture.cherrywork.freshdirect.Activities.UserMessageDetailActivity;
import com.incture.cherrywork.freshdirect.Models.FeedsDataModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.Actions;
import com.incture.cherrywork.freshdirect.Utils.AttachmentGridActivity;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FullGallery;
import com.incture.cherrywork.freshdirect.Utils.MaterialImageView;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by Arun on 22-08-2016.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    private ArrayList<FeedsDataModel> _feedsList = new ArrayList<FeedsDataModel>();
    private ArrayList<FeedsDataModel> originalList = new ArrayList<FeedsDataModel>();
    private Context _context;
    private int unReadFeedsCount;

    public InboxAdapter(Context context, ArrayList<FeedsDataModel> data,int feedsCount) {
        _context = context;
        _feedsList.clear();
        _feedsList = data;
        unReadFeedsCount=feedsCount;
        originalList.addAll(_feedsList);

    }

    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_feed_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final InboxAdapter.ViewHolder holder, final int position) {
        //setdata to views
        final FeedsDataModel userMessage = _feedsList.get(position);
        WindowManager wm = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();


        holder.firstName.setText(userMessage.creatordisplayname);
        holder.action.setText(Html.fromHtml(userMessage.activity));
        holder.title.setText(userMessage.title);
        holder.description.setText(userMessage.description);
        holder.likeCount.setVisibility(View.GONE);
        holder.commentCount.setVisibility(View.GONE);

        if (userMessage.getLikescount() != 0 ) {
            holder.likeCount.setVisibility(View.VISIBLE);

            if (userMessage.getLikescount() == 1) {
                holder.likeCount.setText(Integer.toString(userMessage.getLikescount()) + " " + "Like");
            } else {
                holder.likeCount.setText(Integer.toString(userMessage.getLikescount()) + " " + "Likes");
            }
        } else {
            holder.likeCount.setVisibility(View.GONE);
        }

        if (userMessage.getCommentsCount() != 0 ) {


            if (userMessage.getCommentsCount() == 1) {
                holder.commentCount.setText(" " + userMessage.getCommentsCount() + " " + "Comment");
            } else {
                holder.commentCount.setText(" " + userMessage.getCommentsCount() + " " + "Comments");
            }

            holder.commentCount.setVisibility(View.VISIBLE);

        } else {
            holder.commentCount.setVisibility(View.GONE);

        }

       // holder.action3Linear.setOnClickListener(new Actions(_context, "ADD_ATTACHMENT", userMessage, userMessage.id));
       // holder.action3Linear.setVisibility(View.GONE);

        Util.loadAttachmentImageMaterial(_context, userMessage.creatoravatarurl, holder.image);
        //holder.image.setOnClickListener(new ProfileClicked(userMessage.creatoravatarurl));

        if (userMessage.getLikescount() == 0) {
            holder.likeText.setTextColor(_context.getResources().getColor(android.R.color.darker_gray));
            holder.likeImage.setImageResource(R.mipmap.like_false);
            userMessage.setLiked(false);
        } else {
            if (userMessage.isLiked()) {
                holder.likeText.setTextColor(_context.getResources().getColor(R.color.blue));
                holder.likeImage.setImageResource(R.mipmap.like_true);
                userMessage.setLiked(true);
            }
        }


        if (userMessage.comments.size() > 0 ) {
            Util.loadAttachmentImageMaterial(_context, userMessage.comments.get(userMessage.comments.size() - 1).useravatarurl, holder.commentPic);
            holder.commentedBy.setText(userMessage.comments.get(userMessage.comments.size() - 1).userdisplayname);
            holder.comment.setText(userMessage.comments.get(userMessage.comments.size() - 1).body);
            holder.commentBox.setVisibility(View.VISIBLE);
        } else {
            holder.commentBox.setVisibility(View.GONE);
        }

        holder.userMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userMessage.readList.contains(FDPreferences.getEmail())&&unReadFeedsCount>0){
                    unReadFeedsCount--;
                    ((FeedsActivity)_context).getUnreadFeedsCount(unReadFeedsCount);
                }
                Intent intent = new Intent(_context, UserMessageDetailActivity.class);
                intent.putExtra("WorkItemId", userMessage.id);
                intent.putExtra("WorkItem", userMessage);

                ((Activity) _context).startActivityForResult(intent, 240);
            }
        });

        if(userMessage.readList.size()>0){
            if(!userMessage.readList.contains(FDPreferences.getEmail())){
                holder.userMessageLayout.setBackgroundResource(R.color.feeds_unread);
            }else {
                holder.userMessageLayout.setBackgroundResource(R.color.white);
            }

        }else{
            holder.userMessageLayout.setBackgroundResource(R.color.feeds_unread);
        }

        holder.commentLinear.setOnClickListener(new Actions(_context, "COMMENT", userMessage, userMessage.id));
        holder.likeLinear.setOnClickListener(new Actions(_context, "LIKE", userMessage, holder.likeCount, holder.likeText, holder.likeImage, NetworkConnector.TYPE_POST, true));


        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter2.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        try {
            if (!userMessage.time.isEmpty()) {
                Date date = formatter2.parse(userMessage.time);
                holder.action_time.setText(TimeUtils.getTimeString(date, TimeUtils.getDateMonthFromDate(userMessage.time)));
            }
            Date Createddate = formatter2.parse(userMessage.createdAt);
            holder.createdDate.setText(TimeUtils.getTimeString(Createddate, TimeUtils.getDateMonthFromDate(userMessage.createdAt)));


        } catch (ParseException e) {
            e.printStackTrace();
        }


        int width2 = display.getWidth(); // deprecated


        int image_width1 = width2 / (5);


        int size_attach = userMessage.attachments.size();


        if (size_attach == 0) {

            holder.galleryLay.setVisibility(View.GONE);
            holder.single.setVisibility(View.GONE);

        } else if (size_attach == 1 ) {

            holder.galleryLay.setVisibility(View.VISIBLE);
            holder.single.setVisibility(View.GONE);
            holder.attach1.setVisibility(View.VISIBLE);
            holder.attach2.setVisibility(View.GONE);
            holder.attach3.setVisibility(View.GONE);
            holder.attach5.setVisibility(View.GONE);
            holder.attach5text.setVisibility(View.GONE);
            holder.attach1.getLayoutParams().height = image_width1;

            holder.attach1.getLayoutParams().width = image_width1;
            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(0).url, holder.attach1, width2, width2);
            final String[] all_path = new String[userMessage.attachments.size()];
            for (int j = 0; j < userMessage.attachments.size(); j++) {

                all_path[j] = userMessage.attachments.get(j).url;
            }
            final Bundle b = new Bundle();
            b.putStringArray("SELECTED", all_path);
            holder.attach1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(_context, FullGallery.class);
                    i.putExtra("Position", 0);
                    i.putExtras(b);
                    i.putExtra("Attachments", userMessage.attachments);
                    _context.startActivity(i);
                }
            });

        } else if (size_attach == 2 ) {
            holder.galleryLay.setVisibility(View.VISIBLE);
            holder.single.setVisibility(View.GONE);
            holder.attach2.setVisibility(View.VISIBLE);
            holder.attach1.setVisibility(View.VISIBLE);
            holder.attach3.setVisibility(View.GONE);
            holder.attach5.setVisibility(View.GONE);
            holder.attach5text.setVisibility(View.GONE);

            holder.attach1.getLayoutParams().height = image_width1;

            holder.attach1.getLayoutParams().width = image_width1;

            holder.attach2.getLayoutParams().height = image_width1;

            holder.attach2.getLayoutParams().width = image_width1;

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(0).url, holder.attach1, image_width1, image_width1);
            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(1).url, holder.attach2, image_width1, image_width1);

        } else if (size_attach == 3 ) {
            holder.galleryLay.setVisibility(View.VISIBLE);
            holder.single.setVisibility(View.GONE);
            holder.attach2.setVisibility(View.VISIBLE);
            holder.attach3.setVisibility(View.VISIBLE);
            holder.attach5.setVisibility(View.GONE);
            holder.attach5text.setVisibility(View.GONE);

            holder.attach1.getLayoutParams().height = image_width1;

            holder.attach1.getLayoutParams().width = image_width1;

            holder.attach2.getLayoutParams().height = image_width1;

            holder.attach2.getLayoutParams().width = image_width1;

            holder.attach3.getLayoutParams().height = image_width1;

            holder.attach3.getLayoutParams().width = image_width1;

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(0).url, holder.attach1, image_width1, image_width1);

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(1).url, holder.attach2, image_width1, image_width1);

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(2).url, holder.attach3, image_width1, image_width1);

        } else if (size_attach > 3 ) {
            holder.galleryLay.setVisibility(View.VISIBLE);
            holder.single.setVisibility(View.GONE);
            holder.attach2.setVisibility(View.VISIBLE);
            holder.attach3.setVisibility(View.VISIBLE);
            holder.attach5.setVisibility(View.VISIBLE);
            holder.attach5text.setVisibility(View.VISIBLE);

            holder.attach1.getLayoutParams().height = image_width1;

            holder.attach1.getLayoutParams().width = image_width1;

            holder.attach2.getLayoutParams().height = image_width1;

            holder.attach2.getLayoutParams().width = image_width1;

            holder.attach3.getLayoutParams().height = image_width1;

            holder.attach3.getLayoutParams().width = image_width1;

            holder.attach5.getLayoutParams().height = image_width1;

            holder.attach5.getLayoutParams().width = image_width1;

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(0).url, holder.attach1, image_width1, image_width1);

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(1).url, holder.attach2, image_width1, image_width1);

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(2).url, holder.attach3, image_width1, image_width1);

            Util.resizeAndLoadImageSquare(_context, userMessage.attachments.get(3).url, holder.attach5, image_width1, image_width1);

            holder.attach5text.setText("+" + (userMessage.attachments.size() - 3));

        }
        final String[] all_path1 = new String[userMessage.attachments.size()];
        for (int j = 0; j < userMessage.attachments.size(); j++) {

            all_path1[j] = userMessage.attachments.get(j).url;
        }
        final Bundle b1 = new Bundle();
        b1.putStringArray("SELECTED", all_path1);
        for (int i = 0; i < userMessage.attachments.size(); i++) {
            if (i == 0) {
                holder.attach1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(_context, FullGallery.class);
                        i.putExtra("Attachments", userMessage.attachments);
                        i.putExtra("Position", 0);
                        i.putExtras(b1);
                        _context.startActivity(i);

                    }
                });
            }
            if (i == 1) {

                holder.attach2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(_context, FullGallery.class);
                        i.putExtra("Attachments", userMessage.attachments);

                        i.putExtra("Position", 1);

                        i.putExtras(b1);
                        _context.startActivity(i);

                    }
                });

            }
            if (i == 2) {
                holder.attach3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(_context, FullGallery.class);
                        i.putExtra("Position", 2);
                        i.putExtras(b1);
                        i.putExtra("Attachments", userMessage.attachments);

                        _context.startActivity(i);

                    }
                });

            } else if (userMessage.attachments.size() > 3) {
                if (userMessage.attachments.size() > 3) {


                    holder.attach5.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub

                            Intent i1 = new Intent(_context, AttachmentGridActivity.class);
                            i1.putExtras(b1);
                            i1.putExtra("Attachments", userMessage.attachments);
                            _context.startActivity(i1);
                        }
                    });
                } else {
                }
            }
        }

        if (userMessage.Public.equals("true")) {
            holder.publicFeed.setVisibility(View.VISIBLE);
        } else {
            holder.publicFeed.setVisibility(View.INVISIBLE);
        }


    }







    public class ViewHolder extends RecyclerView.ViewHolder {
        //declare views
        TextView firstName,createdDate,action,action_time,title,description,commentCount,commentedBy,comment,likeText,
        likeCount,exit_from_feed,attach5text;
        MaterialImageView commentPic,image;
        ImageView likeImage,attach1,attach2,attach3,single,publicFeed,attach5;
        RelativeLayout commentBox,userMessageLayout,latestActivity;
        LinearLayout action3Linear,commentLinear,likeLinear,galleryLay;


        public ViewHolder(View view) {
            super(view);
            //initialize views
            firstName = (TextView) view.findViewById(R.id.firstName);
            createdDate = (TextView) view.findViewById(R.id.date1);
            action = (TextView) view.findViewById(R.id.Top);
            action_time = (TextView) view.findViewById(R.id.Top4);
            title = (TextView) view.findViewById(R.id.message1);
            description = (TextView) view.findViewById(R.id.message2);
            commentCount = (TextView) view.findViewById(R.id.CommentsCount2);
            commentedBy = (TextView) view.findViewById(R.id.FirstNameComment);
            comment = (TextView) view.findViewById(R.id.SecondNameComment);

            likeText = (TextView) view.findViewById(R.id.liketext);
            likeCount = (TextView) view.findViewById(R.id.likesCount2);
            exit_from_feed = (TextView) view.findViewById(R.id.exit);


            image = (MaterialImageView) view.findViewById(R.id.quickContactBadge1);
            commentPic = (MaterialImageView) view.findViewById(R.id.imageView1);
            likeImage = (ImageView) view.findViewById(R.id.like2);
            attach1 = (ImageView) view.findViewById(R.id.Attach111);
            attach2 = (ImageView) view.findViewById(R.id.Attach222);
            attach3 = (ImageView) view.findViewById(R.id.Attach333);


            commentBox = (RelativeLayout) view.findViewById(R.id.secondLay);
            userMessageLayout = (RelativeLayout) view.findViewById(R.id.parent1);

            action3Linear = (LinearLayout) view.findViewById(R.id.AttachButton);
            commentLinear = (LinearLayout) view.findViewById(R.id.CommentButton);
            likeLinear = (LinearLayout) view.findViewById(R.id.likebutton11);
            galleryLay = (LinearLayout) view.findViewById(R.id.gallery_layout);
            latestActivity = (RelativeLayout) view.findViewById(R.id.tv1);
            action3Linear = (LinearLayout) view.findViewById(R.id.AttachButton);
            commentLinear = (LinearLayout) view.findViewById(R.id.CommentButton);
            likeLinear = (LinearLayout) view.findViewById(R.id.likebutton11);
            galleryLay = (LinearLayout) view.findViewById(R.id.gallery_layout);
            latestActivity = (RelativeLayout) view.findViewById(R.id.tv1);

            single = (ImageView) view.findViewById(R.id.singleimage);
            publicFeed = (ImageView) view
                    .findViewById(R.id.public1);
            attach5 = (ImageView) view.findViewById(R.id.Attach555);

            attach5text = (TextView)view.findViewById(R.id.attachText);

        }
    }

    @Override
    public int getItemCount() {
        return this._feedsList.size();
    }




}
