package com.incture.cherrywork.freshdirect.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.Activities.FeedsActivity;
import com.incture.cherrywork.freshdirect.Adapters.DetailCommentAdapter;
import com.incture.cherrywork.freshdirect.Adapters.InboxAdapter;
import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.Models.CommentModel;
import com.incture.cherrywork.freshdirect.Models.FeedsDataModel;
import com.incture.cherrywork.freshdirect.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Deeksha on 04-02-2016.
 */
public class Actions implements View.OnClickListener, AsyncResponse, View.OnLongClickListener {

    private boolean likeShown = true;
    private ListView _listToUpdate;
    Context _context = null;
    String _actionId = "";
    Object _workitem = null;
    CardView parent;
    String ID = "";
    String bpmtype = "";
    String timesheet_type = "";
    TextView likescount, liketext;
    ImageView likeimage;
    int type = 0;
    boolean isBackground = false;
    EditText _comment = null;
    ArrayList<String> attachements = new ArrayList<String>();
    AlertDialog alertDialog;
    EditText txt;
    String reasonOrComment, url = "";
    String commentText = "";
    private InboxAdapter adapter;

    public Actions(Context context, String actionId, Object workitem, String workitem_id) {
        _context = context;
        _actionId = actionId;
        _workitem = workitem;
        ID = workitem_id;
    }

    public Actions(Context context, String actionId, Object workitem, String workitem_id, CardView prnt, String typ) {
        _context = context;
        _actionId = actionId;
        _workitem = workitem;
        ID = workitem_id;
        parent = prnt;
        bpmtype = typ;
    }


    public Actions(Context context, String actionId, Object workitem) {
        _context = context;
        _actionId = actionId;
        _workitem = workitem;
    }

    //For background action
    public Actions(Context context, String actionId, String workitem_id, String reasonOrComment, boolean isBackground) {
        _context = context;
        _actionId = actionId;
        ID = workitem_id;
        this.reasonOrComment = reasonOrComment;
        _workitem = new FeedsDataModel();
        ((FeedsDataModel) _workitem).id = ID;
        this.isBackground = isBackground;
    }

    //Constructor for Like
    public Actions(Context context, String actionId, Object workitem, TextView likescount, TextView liketext, ImageView likeimage, int type, boolean isShown) {
        _context = context;
        _actionId = actionId;
        _workitem = workitem;
        this.likescount = likescount;
        this.liketext = liketext;
        this.likeimage = likeimage;
        this.type = type;
        this.likeShown = isShown;
    }

    //Constructor for comments in detail screen
    public Actions(Context context, String actionId, Object workitem, EditText comment, ListView listToUpdate) {
        _context = context;
        _actionId = actionId;
        _workitem = workitem;
        _comment = comment;
        _listToUpdate = listToUpdate;
    }

    //For timesheet Approval/Rejection

    public Actions(Context context, Object workitem, String actionId, String timesheetId, String type) {
        _context = context;
        _actionId = actionId;
        _workitem = workitem;
        ID = timesheetId;
        this.timesheet_type = type;


    }

    public void setAdapter(InboxAdapter adapter) {
        this.adapter = adapter;
    }

    public void likeMethod() {
        Map<String, String> headers = new HashMap<String, String>();

        headers.put("x-email-id", FDPreferences.getEmail());
        headers.put("x-access-token", FDPreferences.getAccessToken());

        if (((FeedsDataModel) _workitem).isLiked()) {
            NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_DELETE, FDUrls.LIKE + ((FeedsDataModel) _workitem).id, headers, null, Actions.this, true);
            connect.execute();
        } else if (!((FeedsDataModel) _workitem).isLiked()) {
            headers.put("Content-Type", "application/json");
            NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.LIKE + ((FeedsDataModel) _workitem).id, headers, null, Actions.this, true);
            connect.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (_actionId) {


            case "COMMENT": {

                if (!isBackground) {

                    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View vi = inflater.inflate(R.layout.comment_dialog, null);

                    AlertDialog.Builder alert = new AlertDialog.Builder(_context);

                    alert.setView(vi);

                    alertDialog = alert.create();
                    alertDialog.show();

                    //((CherryworkApplication) _context.getApplicationContext()).clearAttachments();
                    final EditText commentsEd = (EditText) vi.findViewById(R.id.commentsEd);


                    //final TextView count = (TextView) vi.findViewById(R.id.attach_count);
                    //((CherryworkApplication) _context.getApplicationContext()).setAttachmentCountView(count);

                    Button send = (Button) vi.findViewById(R.id.post_comment);

                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View current_view = ((Activity) _context).getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(current_view.getWindowToken(), 0);
                            }
                            if (commentsEd == null) {
                                commentText = reasonOrComment;
                            } else {
                                commentText = commentsEd.getText().toString().trim();

                            }
                            //attachements = ((CherryworkApplication) _context.getApplicationContext()).getAttachments();

                            if (commentText.length() == 0) {
                                Toast.makeText(_context, "Nothing to post!!!!!", Toast.LENGTH_SHORT).show();
                            } else if (commentText.length() == 0) {
                                AlertDialog alertDialog;
                                AlertDialog.Builder alert = new AlertDialog.Builder(_context);

                                alert.setTitle("Post Comment");
                                alert.setMessage("Are you sure you want to post without any comment ?");

                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        Map<String, String> headers = new HashMap<String, String>();

                                        headers.put("x-email-id", FDPreferences.getEmail());
                                        headers.put("x-access-token", FDPreferences.getAccessToken());
                                        headers.put("Content-Type", "application/json");

                                        JSONObject object = new JSONObject();
                                        try {
                                            object.put("text", commentText);
                                            JSONArray images = new JSONArray();
                                            object.put("files", images);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        String comment = object.toString();

                                        NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.ADD_COMMENT + ((FeedsDataModel) _workitem).id, headers, comment, Actions.this);
                                        connect.execute();
                                        dialog.cancel();

                                    }
                                });
                                if (Util.isOnline(_context)) {
                                    alertDialog = alert.create();
                                    alertDialog.show();
                                } else if (!isBackground) {
                                    Toast.makeText(_context, "Cannot post attachments when offline", Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                Map<String, String> headers = new HashMap<String, String>();

                                headers.put("x-email-id", FDPreferences.getEmail());
                                headers.put("x-access-token",
                                        FDPreferences.getAccessToken());
                                headers.put("Content-Type",
                                        "application/json");

                                JSONObject object = new JSONObject();
                                try {
                                    object.put("text", commentText);
                                    JSONArray images = new JSONArray();
                                    object.put("files", images);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String comment = object.toString();

                                NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.ADD_COMMENT + ((FeedsDataModel) _workitem).id, headers, comment, Actions.this);
                                connect.execute();
                                alertDialog.dismiss();


                            }
                        }
                    });

                } else {
                    Map<String, String> headers = new HashMap<String, String>();

                    headers.put("x-email-id", FDPreferences.getEmail());
                    headers.put("x-access-token", FDPreferences.getAccessToken());
                    headers.put("Content-Type", "application/json");
                    JSONObject object = new JSONObject();
                    try {
                        object.put("text", reasonOrComment);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String comment = object.toString();


                    NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.ADD_COMMENT + ((FeedsDataModel) _workitem).id, headers, comment, Actions.this, isBackground);
                    connect.execute();


                }
                break;
            }

            //Add comment from workitem detail screen
            case "COMMENT_DETAIL": {

                if (_comment == null) {
                    commentText = reasonOrComment;
                } else {
                    commentText = _comment.getText().toString().trim();
                }
                if (!isBackground) {
                    View view = ((Activity) _context).getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    if (commentText.length() == 0) {
                        Toast.makeText(_context, "Nothing to post", Toast.LENGTH_SHORT).show();
                    } else if (commentText.length() == 0) {
                        AlertDialog alertDialog;
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(_context);

                        alert1.setTitle("Post Comment");
                        alert1.setMessage("Are you sure you want to post without any comment ?");

                        alert1.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        alert1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Map<String, String> headers = new HashMap<String, String>();

                                headers.put("x-email-id", FDPreferences.getEmail());
                                headers.put("x-access-token", FDPreferences.getAccessToken());
                                headers.put("Content-Type", "application/json");

                                JSONObject object = new JSONObject();
                                try {
                                    object.put("text", commentText);
                                    JSONArray images = new JSONArray();
                                    object.put("files", images);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String comment = object.toString();

                                NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.ADD_COMMENT + ((FeedsDataModel) _workitem).id, headers, comment, Actions.this);
                                connect.execute();


                                dialog.cancel();
                            }
                        });
                        if (Util.isOnline(_context)) {
                            alertDialog = alert1.create();
                            alertDialog.show();
                        } else if (!isBackground) {
                            Toast.makeText(_context, "Cannot add attachments when offline", Toast.LENGTH_SHORT).show();
                        }
                    }//end of (_comment.length==0)


                    else {

                        Map<String, String> headers = new HashMap<String, String>();

                        headers.put("x-email-id", FDPreferences.getEmail());
                        headers.put("x-access-token", FDPreferences.getAccessToken());
                        headers.put("Content-Type", "application/json");

                        JSONObject object = new JSONObject();
                        try {
                            object.put("text", commentText);
                            JSONArray images = new JSONArray();
                            object.put("files", images);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String comment = object.toString();

                        NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.ADD_COMMENT + ((FeedsDataModel) _workitem).id, headers, comment, Actions.this);

                        if (Util.isOnline(_context)) {
                            connect.execute();
                        } else if (!isBackground) {
                            if (_comment != null) {
                                _comment.setText("");
                            }

                        } else {
                            Toast.makeText(_context, _context.getString(R.string.action_not_allowed), Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {
                    Map<String, String> headers = new HashMap<String, String>();

                    headers.put("x-email-id", FDPreferences.getEmail());
                    headers.put("x-access-token",
                            FDPreferences.getAccessToken());
                    headers.put("Content-Type",
                            "application/json");

                    JSONObject object = new JSONObject();
                    try {
                        object.put("text", commentText);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String comment = object.toString();

                    NetworkConnector connect = new NetworkConnector(_context, NetworkConnector.TYPE_POST, FDUrls.ADD_COMMENT + ((FeedsDataModel) _workitem).id, headers, comment, Actions.this, true);


                    connect.execute();


                }

            }


            break;


            case "LIKE":
                likeMethod();
                break;


        }


    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {

        if (url.contains(FDUrls.LIKE) && type == NetworkConnector.TYPE_DELETE) {

            try {
                JSONObject object = new JSONObject(output);
                if (object.getString("status").equalsIgnoreCase("success")) {
                    likescount.setText(Integer.toString(((FeedsDataModel) _workitem).getLikescount() - 1) + " " + "likes");
                    if (likeShown) {
                        if (((FeedsDataModel) _workitem).getLikescount() - 1 == 0) {
                            likescount.setVisibility(View.GONE);
                        } else {
                            likescount.setVisibility(View.VISIBLE);
                        }
                    }
                    ((FeedsDataModel) _workitem).setLikescount(((FeedsDataModel) _workitem).getLikescount() - 1);
                    liketext.setTextColor(_context.getResources().getColor(android.R.color.darker_gray));
                    likeimage.setImageResource(R.mipmap.like_false);
                    ((FeedsDataModel) _workitem).setLiked(false);
                    ((FeedsDataModel) _workitem).setShowlikes(false);
                    ((FeedsDataModel) _workitem).isEdited = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (url.contains(FDUrls.LIKE) && type == NetworkConnector.TYPE_POST) {

            try {
                JSONObject object = new JSONObject(output);
                if (object.getString("status").equalsIgnoreCase("success")) {
                    liketext.setTextColor(_context.getResources().getColor(R.color.blue));
                    likeimage.setImageResource(R.mipmap.like_true);
                    likescount.setText(Integer.toString(((FeedsDataModel) _workitem).getLikescount() + 1) + " " + "likes");
                    if (likeShown) {
                        if (((FeedsDataModel) _workitem).getLikescount() + 1 == 0) {
                            likescount.setVisibility(View.GONE);
                        } else {
                            likescount.setVisibility(View.VISIBLE);
                        }
                    }
                    ((FeedsDataModel) _workitem).setLikescount(((FeedsDataModel) _workitem).getLikescount() + 1);
                    ((FeedsDataModel) _workitem).setLiked(true);
                    ((FeedsDataModel) _workitem).setShowlikes(true);
                    ((FeedsDataModel) _workitem).isEdited = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (url.contains(FDUrls.ADD_COMMENT) && type == NetworkConnector.TYPE_POST && _actionId.equalsIgnoreCase("COMMENT_DETAIL")) {
            //((CherryworkApplication) _context.getApplicationContext()).clearAttachments();

            try {
                JSONObject object = new JSONObject(output);
                if (object.getString("status").equalsIgnoreCase("success") && !isBackground) {
                    CommentModel commentmodel = new CommentModel();

                    {
                        JSONObject commentobject = object.getJSONObject("data");

                        commentmodel.time = commentobject.optString("createdAt");
                        commentmodel.body = commentobject.optString("text");
                        commentmodel.text = commentobject.optString("text");
                        commentmodel.deleted = commentobject
                                .optString("deleted");

                        JSONObject user = commentobject.optJSONObject("commentedBy");
                        commentmodel.userid = user.optString("_id");
                        commentmodel.userdisplayname = Util.capitalizeWords(user.optString("displayName"));
                        commentmodel.useravatarurl = user
                                .optString("avatar");
                        commentmodel.id = commentobject.optString("_id");

                        JSONArray attachmentsarray = (JSONArray) commentobject.opt("attachments");
                        if (attachmentsarray != null && attachmentsarray.length() > 0) {
                            ArrayList<AttachmentModel> attachlist = new ArrayList<AttachmentModel>();
                            for (int attachment = 0; attachment < attachmentsarray.length(); attachment++) {
                                AttachmentModel attachmentmodel = new AttachmentModel();

                                JSONObject attachmentobject = attachmentsarray.getJSONObject(attachment);
                                attachmentmodel.time = attachmentobject.optString("time");
                                attachmentmodel.url = attachmentobject.optString("url");
                                attachmentmodel.displayname = attachmentobject.optString("displayName");
                                attachmentmodel.type = attachmentobject.optString("type");
                                attachmentmodel.id = attachmentobject.optString("id");
                                attachmentmodel.createdAt = attachmentobject.optString("createdAt");
                                attachlist.add(attachmentmodel);
                            }
                            commentmodel.setAttachments(attachlist);
                        }

                    }

                    ((FeedsDataModel) _workitem).getComments().add(commentmodel);
                    ((FeedsDataModel) _workitem).isEdited = true;
                    ((FeedsDataModel) _workitem).setCommentsCount(((FeedsDataModel) _workitem).getComments().size());

                    if (_listToUpdate.getAdapter() != null) {
                        ((DetailCommentAdapter) _listToUpdate.getAdapter()).notifyDataSetChanged();
                    } else {
                        DetailCommentAdapter adapter = new DetailCommentAdapter(_context, R.layout.comments_list_item, ((FeedsDataModel) _workitem));
                        _listToUpdate.setAdapter(adapter);
                    }
                    _listToUpdate.smoothScrollToPosition(_listToUpdate.getChildCount() - 1);
                    Util.setListViewHeightBasedOnChildren(_listToUpdate);
                    _comment.setText("");

                } else if (!isBackground && !object.optString("message").isEmpty()) {

                    Toast.makeText(_context, object.optString("message"), Toast.LENGTH_SHORT).show();

                } else {
                    // DbUtil.delete(_context, Constants.PENDING_OFFLINE_ACTION, Constants.ACTION_TYPE + "= '" + _actionId + "' and " + Constants.WORKITEM_ID + "= '" + ID + "' and " + Constants.TEXT + " = '" + reasonOrComment + "'", null);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (url.contains(FDUrls.ADD_COMMENT) && type == NetworkConnector.TYPE_POST) {

            //((CherryworkApplication) _context.getApplicationContext()).clearAttachments();
            try {
                JSONObject object = new JSONObject(output);
                if (object.getString("status").equalsIgnoreCase("success")) {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    if (!isBackground) {
                        Intent intent = new Intent(_context, FeedsActivity.class);
                        _context.startActivity(intent);
                        ((Activity)_context).finish();
                    } else {
                        //DbUtil.delete(_context, Constants.PENDING_OFFLINE_ACTION, Constants.ACTION_TYPE + "= '" + _actionId + "' and " + Constants.WORKITEM_ID + "= '" + ID + "' and " + Constants.TEXT + " = '" + reasonOrComment + "'", null);
                    }

                } else if (!object.optString("message").isEmpty()) {
                    Toast.makeText(_context, object.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }


    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}

