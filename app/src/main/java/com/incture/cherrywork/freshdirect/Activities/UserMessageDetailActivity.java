package com.incture.cherrywork.freshdirect.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.Adapters.DetailCommentAdapter;
import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.Models.CommentModel;
import com.incture.cherrywork.freshdirect.Models.FeedsDataModel;
import com.incture.cherrywork.freshdirect.Models.LikeModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.Actions;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.MaterialImageView;
import com.incture.cherrywork.freshdirect.Utils.NestedListView;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class UserMessageDetailActivity extends Activity implements
        AsyncResponse {
    private Context _context = UserMessageDetailActivity.this;
    private ArrayList<CommentModel> associatedUsers = new ArrayList<>();
    private ArrayList<CommentModel> added_list = new ArrayList<>();
    private FeedsDataModel userMessageModel = new FeedsDataModel();
    private boolean addcollaborator;
    private EditText search_user;
    private TextView add_member, done, cancel;
    private ImageView addicon, actionline, done_cancel_line, cancel_search, search_icon;
    private String work_item_id = "";
    private String displayName = "";
    private String email = "";
    private LinearLayout actionlayout, done_cancel;
    private AlertDialog.Builder dialog;
    private AlertDialog remove_user_alert;
    private int deleteposition = 0;
    private String status = "";
    private String delete_id = "";

    public ArrayList<LikeModel> likeList = new ArrayList<>();
    public ArrayList<CommentModel> commentList = new ArrayList<>();
    public ArrayList<AttachmentModel> attachList = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermsg_detail);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(getResources().getColor(R.color.my_primary_dark));

        }
        ImageButton back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText comment = (EditText) findViewById(R.id.commentsEd);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        userMessageModel = (FeedsDataModel) intent.getSerializableExtra("WorkItem");

        work_item_id = intent.getStringExtra("WorkItemId");


        if (Util.isOnline(this)) {

            Map<String, String> headers = new HashMap<>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());
            headers.put("Content-type", "application/json");
            NetworkConnector connect = new NetworkConnector(UserMessageDetailActivity.this, NetworkConnector.TYPE_GET, FDUrls.GET_WORKITEM_DETAIL + work_item_id, headers, null, UserMessageDetailActivity.this);
            connect.execute();
        } else {
            Toast.makeText(_context, "No Internet", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }


    /**
     * Add to collab list
     *
     * @param list user
     */
    public void associatedArray(ArrayList<CommentModel> list) {
        associatedUsers = list;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        //menu.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth() * 1 / 4);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        String actualString = output;
        if (output.startsWith("null")) {
            actualString = output.substring(4);
        }

        parse(actualString);
    }//end of process finish

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }

    public void parse(String output) {
        String status = "";
        String message = "";
        try {
            JSONObject mainObject = new JSONObject(output);
            status = mainObject.optString("status");
            message = mainObject.optString("message");

            if (status.contentEquals("success")) {
                JSONObject dataObject = mainObject.optJSONObject("data");
                if (dataObject != null) {
                    JSONObject card = dataObject.getJSONObject("card");
                    userMessageModel = new FeedsDataModel();
                    userMessageModel.type = card.optString("type");
                    userMessageModel.subtype = card.optString("subtype");
                    userMessageModel.title = dataObject.optString("title");
                    userMessageModel.description = dataObject.optString("description");
                    userMessageModel.createdAt = dataObject.optString("createdAt");
                    userMessageModel.Public = dataObject.optString("public");
                    userMessageModel.id = dataObject.optString("_id");
                    userMessageModel.lastModified = dataObject.optString("updatedAt");

                    if (dataObject.optJSONObject("lastActivity") != null) {
                        JSONObject activity = dataObject.optJSONObject("lastActivity");
                        if (activity.length() > 0) {

                            userMessageModel.activity = "<b> <font color='#000000'>" + Util.capitalizeWords(activity.optString("user")) + "</font></b>" + " " +
                                    activity.optString("activity");

                            userMessageModel.time = activity.optString("time");
                            userMessageModel.action = activity.optString("action");
                        }
                    } else {
                        userMessageModel.activity = dataObject.optString("lastActivity");
                    }


                    JSONArray attachmentsArray = (JSONArray) dataObject.opt("attachments");
                    if (attachmentsArray != null && attachmentsArray.length() > 0) {
                        ArrayList<AttachmentModel> attachments = new ArrayList<>();
                        for (int attachment = 0; attachment < attachmentsArray.length(); attachment++) {
                            AttachmentModel attachmentModel = new AttachmentModel();

                            JSONObject attachmentobject = attachmentsArray.getJSONObject(attachment);
                            attachmentModel.time = attachmentobject.optString("time");
                            attachmentModel.url = attachmentobject.optString("url");
                            attachmentModel.displayname = Util.capitalizeWords(attachmentobject.optString("displayName"));
                            attachmentModel.type = attachmentobject.optString("type");
                            attachmentModel.id = attachmentobject.optString("id");
                            attachmentModel.createdat = attachmentobject.optString("createdAt");
                            attachments.add(attachmentModel);
                        }
                        userMessageModel.attachments = attachments;
                    }

                    JSONArray read=dataObject.optJSONArray("read");
                    if(read!=null&&read.length()>0){
                        ArrayList<String> readList=new ArrayList<>();
                        for(int r=0;r<read.length();r++){
                            readList.add((String) read.get(r));
                        }
                        userMessageModel.readList=readList;
                    }
                    if(!userMessageModel.readList.contains(FDPreferences.getEmail())){
                        userMessageModel.readList.add(FDPreferences.getEmail());
                        userMessageModel.isEdited=true;

                    }

                    likeList = new ArrayList<>();
                    JSONArray likeArray = (JSONArray) dataObject.opt("likes");
                    if (likeArray.length() > 0) {
                        for (int l = 0; l < likeArray.length(); l++) {
                            LikeModel likemodel = new LikeModel();
                            JSONObject likeObject = likeArray.optJSONObject(l);
                            likemodel.likeid = likeObject.optString("id");
                            likeList.add(likemodel);
                        }
                    }
                    userMessageModel.likes = likeList;
                    userMessageModel.setLikescount(dataObject.optInt("likeCount"));

                    int isliked = dataObject.optInt("likedByUser");
                    if (isliked == 0)
                        userMessageModel.setLiked(false);
                    else
                        userMessageModel.setLiked(true);


                    commentList = new ArrayList<>();
                    JSONArray commentsArray = (JSONArray) dataObject
                            .get("comments");

                    if (commentsArray != null && commentsArray.length() > 0) {
                        for (int i1 = 0; i1 < commentsArray.length(); i1++) {
                            CommentModel commentModel = new CommentModel();
                            JSONObject commentObject = commentsArray
                                    .getJSONObject(i1);

                            commentModel.time = commentObject.optString("createdAt");
                            commentModel.body = commentObject.optString("text");
                            commentModel.deleted = commentObject
                                    .optString("deleted");

                            JSONObject user = commentObject.optJSONObject("commentedBy");
                            commentModel.userid = user.optString("_id");
                            commentModel.userdisplayname = Util.capitalizeWords(user
                                    .optString("displayName"));
                            commentModel.useravatarurl = user
                                    .optString("avatar");
                            commentModel.id = commentObject.optString("_id");

                            attachmentsArray = (JSONArray) commentObject.opt("attachments");
                            if (attachmentsArray != null && attachmentsArray.length() > 0) {
                                attachList = new ArrayList<>();
                                for (int attachment = 0; attachment < attachmentsArray.length(); attachment++) {
                                    AttachmentModel attachmentModel = new AttachmentModel();

                                    JSONObject attachmentobject = attachmentsArray.getJSONObject(attachment);
                                    attachmentModel.time = attachmentobject.optString("time");
                                    attachmentModel.url = attachmentobject.optString("url");
                                    attachmentModel.displayname = attachmentobject.optString("displayName");
                                    attachmentModel.type = attachmentobject.optString("type");
                                    attachmentModel.id = attachmentobject.optString("id");
                                    attachmentModel.createdat = attachmentobject.optString("createdAt");
                                    attachList.add(attachmentModel);
                                }
                                commentModel.setAttachments(attachList);
                            }

                            if (commentModel.deleted.equalsIgnoreCase("false"))
                                commentList.add(commentModel);
                        }
                    }
                    userMessageModel.comments = commentList;

                    ArrayList<CommentModel> subscriber = new ArrayList<>();
                    JSONArray subscribers = (JSONArray) dataObject.opt("subscribers");
                    if (subscribers != null && subscribers.length() > 0) {
                        for (int sb = 0; sb < subscribers.length(); sb++) {
                            CommentModel item = new CommentModel();
                            JSONObject sobj = subscribers.getJSONObject(sb);
                            if (!checkIfAdded(sobj.optString("_id"), userMessageModel.subscribers)) {
                                item.semail = sobj.optString("email");
                                item.sid = sobj.optString("_id");
                                item.sdisplayName = Util.capitalizeWords(sobj.optString("displayName"));
                                item.savatarUrl = sobj.optString("avatar");
                                item.sphoneNo = sobj.optString("phoneNo");
                                subscriber.add(item);
                            }
                        }
                        userMessageModel.subscribers = subscriber;
                    }

                    ArrayList<CommentModel> assigneList = new ArrayList<CommentModel>();
                    JSONArray assigned = (JSONArray) dataObject.opt("assignedTo");
                    if (assigned != null && assigned.length() > 0) {
                        for (int o = 0; o < assigned.length(); o++) {
                            CommentModel citem = new CommentModel();
                            JSONObject oobj = assigned.getJSONObject(o);
                            if (!checkIfAdded(oobj.optString("_id"), userMessageModel.subscribers)) {
                                citem.semail = oobj.optString("email");
                                citem.sid = oobj.optString("_id");
                                citem.sdisplayName = Util.capitalizeWords(oobj.optString("displayName"));
                                citem.savatarUrl = oobj.optString("avatar");
                                citem.sphoneNo = oobj.optString("phoneNo");
                                assigneList.add(citem);
                                userMessageModel.subscribers.add(citem);
                            }

                        }
                    }
                    userMessageModel.owners = assigneList;

                    if (dataObject.optJSONObject("createdBy") != null) {
                        JSONObject creator = dataObject.optJSONObject("createdBy");
                        userMessageModel.creatorid = creator.optString("_id");
                        userMessageModel.creatoremail = creator.optString("email");
                        userMessageModel.creatorphoneno = creator.optString("phoneNo");
                        userMessageModel.creatordisplayname = Util.capitalizeWords(creator.optString("displayName"));
                        userMessageModel.creatoravatarurl = creator.optString("avatar");
                    } else if (!dataObject.optString("createdBy").isEmpty()) {
                        userMessageModel.creatordisplayname = Util.capitalizeWords(dataObject.optString("createdBy"));
                    }
                }
            }

        }//end of try
        catch (JSONException e) {

            System.out.println(e.getLocalizedMessage());

        }//end of catch

        if (status.equals("success")) {

            ((UserMessageDetailActivity) _context).associatedArray(userMessageModel.subscribers);

            setData(userMessageModel);

        } else if (message != null && !message.isEmpty()) {
            Toast.makeText(UserMessageDetailActivity.this, message, Toast.LENGTH_SHORT).show();
        }

    }//end of parse() function

    private void setData(FeedsDataModel userMessageModel) {
        final FeedsDataModel user_model = (FeedsDataModel) userMessageModel;
        final RelativeLayout user_layout = (RelativeLayout) ((UserMessageDetailActivity) _context).findViewById(R.id.parent);
        final RelativeLayout lay = (RelativeLayout) ((UserMessageDetailActivity) _context).findViewById(R.id.lay);
        lay.setVisibility(View.VISIBLE);
        user_layout.setVisibility(View.VISIBLE);

        TextView _usermsgDispNameTv1 = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.FirstName);
        TextView _usermsgDateTv1 = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.SecondName);
        TextView _usermTitleTv1 = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.message1);
        final TextView _usermsgDescriptionTv1 = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.message2);
        LinearLayout galleryView = (LinearLayout) ((UserMessageDetailActivity) _context).findViewById(R.id.gallery);
        RelativeLayout AttachButton1 = (RelativeLayout) ((UserMessageDetailActivity) _context).findViewById(R.id.AttachButton);
        final TextView attachment_count = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.attach_count);
        ImageView user_attach1 = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.Attach1);
        MaterialImageView profilepic1 = (MaterialImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.profilepic);
        ImageView user_attach2 = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.Attach2);
        ImageView user_attach3 = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.Attach3);
        ImageView Back = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.back);
        ImageView user_attach5 = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.Attach5);
        TextView user_attach5_text = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.attachText);
        final ImageView like = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.website122);
        LinearLayout shareLayout = (LinearLayout) ((UserMessageDetailActivity) _context).findViewById(R.id.share_layout);
        final LinearLayout LIKE = (LinearLayout) ((UserMessageDetailActivity) _context).findViewById(R.id.LIKE2);
        final TextView Likescounts = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.LIKES2);
        final TextView liketext2 = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.likebutton2);
        ImageView publi_user1 = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.public1);
        ImageView image_user = (ImageView) ((UserMessageDetailActivity) _context).findViewById(R.id.image);
        TextView descrip_user = (TextView) ((UserMessageDetailActivity) _context).findViewById(R.id.descrip);


        if (user_model.Public.contains("true")) {
            publi_user1.setVisibility(View.VISIBLE);
        } else {
            publi_user1.setVisibility(View.INVISIBLE);
        }
        if (user_model.getLikescount() > 0) {
            if (user_model.getLikescount() == 1) {
                Likescounts.setText(user_model.getLikescount() + " " + "Like");
            } else {
                Likescounts.setText(user_model.getLikescount() + " " + "Likes");
            }
            if (user_model.isLiked()) {
                liketext2.setTextColor(_context.getResources().getColor(R.color.blue));
                like.setImageResource(R.mipmap.like_true);
                user_model.setLiked(true);
            } else {
                liketext2.setTextColor(_context.getResources().getColor(android.R.color.darker_gray));
                like.setImageResource(R.mipmap.like_false);
                user_model.setLiked(false);
            }

        } else {
            Likescounts.setVisibility(View.GONE);

        }

        LIKE.setOnClickListener(new Actions(_context, "LIKE", user_model, Likescounts, liketext2, like, NetworkConnector.TYPE_POST, true));

        _usermsgDispNameTv1.setText(Util.capitalizeWords(user_model.creatordisplayname));

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter2.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        try {
            if (!user_model.createdAt.isEmpty()) {
                Date date = formatter2.parse(user_model.createdAt);
                _usermsgDateTv1.setText(TimeUtils.getTimeString(date, TimeUtils.getDateMonthFromDate(user_model.createdAt)));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (user_model.title.contains("null")) {
            _usermTitleTv1.setVisibility(View.GONE);
        } else {
            _usermTitleTv1.setText(Util.capitalizeSentence(user_model.title));
        }


        _usermsgDescriptionTv1.setText(Html.fromHtml(user_model.description));

        if (user_model.creatoravatarurl != null && !user_model.creatoravatarurl.isEmpty())
            Util.loadAttachmentImageMaterial(_context, user_model.creatoravatarurl, profilepic1);

        // profilepic1.setOnClickListener(new ProfileClicked(user_model.creatoravatarurl));


        // Comments stuff
        int comments_Size = user_model.comments.size();
        NestedListView user_lv = (NestedListView) ((UserMessageDetailActivity) _context)
                .findViewById(R.id.mainlist333);
        if (comments_Size == 0) {
            // don't show Activity timeline and UI other
            ((UserMessageDetailActivity) _context)
                    .findViewById(R.id.TimeLine11)
                    .setVisibility(View.GONE);
            ((UserMessageDetailActivity) _context)
                    .findViewById(R.id.line11).setVisibility(View.GONE);
            ((UserMessageDetailActivity) _context)
                    .findViewById(R.id.line252).setVisibility(View.GONE);
        } else {

            user_lv.setVisibility(View.VISIBLE);

            DetailCommentAdapter adapter = new DetailCommentAdapter(
                    _context, R.layout.comments_list_item, user_model);
            user_lv.setAdapter(adapter);

        }

        //((CherryworkApplication) _context.getApplicationContext()).clearAttachments();

        RelativeLayout user_post = (RelativeLayout) ((UserMessageDetailActivity) _context)
                .findViewById(R.id.CommentButton);

        EditText user_comment = (EditText) ((UserMessageDetailActivity) _context).findViewById(R.id.commentsEd);
        user_post.setOnClickListener(new Actions(_context, "COMMENT_DETAIL", user_model, user_comment, user_lv));


        LinearLayout likescount1 = (LinearLayout) ((UserMessageDetailActivity) _context)
                .findViewById(R.id.likescount2);
        likescount1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (userMessageModel.isEdited) {
            Intent intent = new Intent();
            intent.putExtra("WORKITEM", userMessageModel);

            setResult(RESULT_OK, intent);
        }

        super.onBackPressed();
    }

    private boolean checkIfAdded(String userIdTOCheck, ArrayList<CommentModel> listTocheck) {
        boolean exists = false;
        if (listTocheck != null && listTocheck.size() > 0) {
            for (int user = 0; user < listTocheck.size(); user++) {
                if (listTocheck.get(user).sid.equalsIgnoreCase(userIdTOCheck)) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;

    }
}



