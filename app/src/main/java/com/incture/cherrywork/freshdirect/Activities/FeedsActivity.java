package com.incture.cherrywork.freshdirect.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.incture.cherrywork.freshdirect.Adapters.InboxAdapter;
import com.incture.cherrywork.freshdirect.Models.AttachmentModel;
import com.incture.cherrywork.freshdirect.Models.CommentModel;
import com.incture.cherrywork.freshdirect.Models.DataModel;
import com.incture.cherrywork.freshdirect.Models.FeedsDataModel;
import com.incture.cherrywork.freshdirect.Models.FeedsMainModel;
import com.incture.cherrywork.freshdirect.Models.LikeModel;
import com.incture.cherrywork.freshdirect.Models.TimesheetTaskModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.AsyncResponse;
import com.incture.cherrywork.freshdirect.Utils.FDPreferences;
import com.incture.cherrywork.freshdirect.Utils.FDUrls;
import com.incture.cherrywork.freshdirect.Utils.NetworkConnector;
import com.incture.cherrywork.freshdirect.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arun on 02-09-2016.
 */
public class FeedsActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AsyncResponse {
    private ImageView back;
    private SwipeRefreshLayout refreshLayout;
    private static Boolean refreshInProgress = false;
    private FeedsMainModel fmodel = new FeedsMainModel();
    private ArrayList<FeedsDataModel> list = new ArrayList<>();
    private ArrayList<FeedsDataModel> original_list = new ArrayList<>();
    private ArrayList<AttachmentModel> attachlist, attachlistwithcomments = new ArrayList<>();
    private ArrayList<LikeModel> likelist = new ArrayList<>();

    private ArrayList<CommentModel> commentlist = new ArrayList<>();

    private ArrayList<DataModel> dayslist = new ArrayList<>();
    private ArrayList<TimesheetTaskModel> taskslist = new ArrayList<>();
    private ArrayList<CommentModel> owner = new ArrayList<>();
    private int skip = 0;
    private InboxAdapter adapter;
    private String bpmtype = "";
    private RecyclerView lv;
    private boolean allItemsDownloaded = false;
    private int limit = 10;

    private int unreadfeedsCount;
    private boolean pagination = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        //set up swipe to refresh
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        refreshLayout.setRefreshing(false);

        if(getIntent().getExtras()!=null) {
            unreadfeedsCount = getIntent().getIntExtra("unReadCount", 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //  getWindow().setStatusBarColor(Color.parseColor("#f9ed47"));
        }

        //fetch feeds
        getAllTasks();
        //back pressed
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getAllTasks() {
        //Fetch all workitems
        if (Util.isOnline(FeedsActivity.this)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());
            NetworkConnector connect = new NetworkConnector(FeedsActivity.this, NetworkConnector.TYPE_GET, FDUrls.GET_WORKITEM + "?" + "limit=" + limit + "&skip=" + skip, headers, null, this);
            connect.execute();
        } else {
            Toast.makeText(FeedsActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processFinish(String output, int status_code, String url, int type) {
        String actualString = output;
        if (output.startsWith("null")) {
            actualString = output.substring(4);
        }
        parse(actualString);
    }

    public void parse(String output) {
        try {
            if (list == null) {
                list = new ArrayList<>();
            }


            fmodel = new FeedsMainModel();
            dayslist = new ArrayList<>();
            taskslist = new ArrayList<>();
            CommentModel ccmodel = new CommentModel();

            JSONObject mainobject = new JSONObject(output);
            fmodel.status = mainobject.optString("status");
            fmodel.message = mainobject.optString("message");
            if (fmodel.status.equals("success")) {
                if (!pagination) {
                    list.clear();
                    original_list.clear();
                }
                JSONObject maindata=mainobject.optJSONObject("data");

                JSONArray data = (JSONArray) maindata.opt("workitems");
                if (data != null && data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject dataobject = data.getJSONObject(i);
                            JSONObject cardObject = dataobject.getJSONObject("card");
                            FeedsDataModel model = new FeedsDataModel();

                            model.type = cardObject.optString("type");
                            model.subtype = cardObject.optString("subtype");
                            if (model.subtype.equalsIgnoreCase(bpmtype) || bpmtype.isEmpty()) {
                                model.title = Util.capitalizeSentence(dataobject.optString("title"));
                                model.description = Util.capitalizeSentence(dataobject.optString("description"));
                                model.createdAt = dataobject.optString("createdAt");
                                model.Public = dataobject.optString("public");
                                model.id = dataobject.optString("_id");
                                model.lastModified = dataobject.optString("updatedAt");
                                model.status = dataobject.optString("status");
                                if (dataobject.optJSONObject("lastActivity") != null) {
                                    JSONObject activity = dataobject.optJSONObject("lastActivity");
                                    if (activity.length() > 0) {

                                        model.activity = "<b> <font color='#000000'>" + Util.capitalizeWords(activity.optString("user")) + "</font></b>" + " " +
                                                activity.optString("activity");

                                        model.time = activity.optString("time");
                                        model.action = activity.optString("action");
                                    }
                                } else {
                                    model.activity = dataobject.optString("lastActivity");
                                }

                                if (dataobject.optJSONObject("content") != null) {
                                    JSONObject content = dataobject.optJSONObject("content");

                                } else {
                                    model.content = dataobject.optString("content");
                                }

                                JSONArray read=dataobject.optJSONArray("read");
                                if(read!=null&&read.length()>0){
                                    ArrayList<String> readList=new ArrayList<>();
                                    for(int r=0;r<read.length();r++){
                                        readList.add((String) read.get(r));
                                    }
                                    model.readList=readList;
                                }

                                likelist = new ArrayList<LikeModel>();
                                JSONArray likearray = (JSONArray) dataobject.opt("likes");
                                if (likearray != null && likearray.length() > 0) {
                                    for (int l = 0; l < likearray.length(); l++) {
                                        LikeModel likemodel = new LikeModel();
                                        JSONObject likeobject = likearray.optJSONObject(l);
                                        likemodel.likeid = likeobject.optString("id");
                                        likelist.add(likemodel);
                                    }
                                }
                                model.likes = likelist;
                                model.setLikescount(dataobject.optInt("likeCount"));
                                int isliked = dataobject.optInt("likedByUser");
                                if (isliked == 0)
                                    model.setLiked(false);
                                else
                                    model.setLiked(true);

                                commentlist = new ArrayList<CommentModel>();
                                attachlistwithcomments = new ArrayList<AttachmentModel>();
                                attachlist = new ArrayList<AttachmentModel>();
                                JSONObject commentobject = dataobject.optJSONObject("comments");
                                if (commentobject != null) {
                                    CommentModel commentmodel = new CommentModel();
                                    commentmodel.time = commentobject.optString("time");
                                    commentmodel.id = commentobject.optString("id");
                                    commentmodel.text = commentobject.optString("text");
                                    commentmodel.body = commentobject.optString("text");
                                    commentmodel.deleted = commentobject.optString("deleted");


                                    JSONObject createdBy = commentobject.optJSONObject("commentedBy");
                                    commentmodel.userid = createdBy.optString("id");
                                    commentmodel.useremail = createdBy.optString("email");
                                    commentmodel.userphoneno = createdBy.optString("phoneNo");
                                    commentmodel.userdisplayname = Util.capitalizeWords(createdBy.optString("displayName"));
                                    commentmodel.useravatarurl = createdBy.optString("avatar");


                                    JSONArray attachmentsarray = (JSONArray) commentobject.opt("attachments");
                                    if (attachmentsarray != null && attachmentsarray.length() > 0) {
                                        for (int attachment = 0; attachment < attachmentsarray.length(); attachment++) {
                                            AttachmentModel attachmentmodel = new AttachmentModel();

                                            JSONObject attachmentobject = attachmentsarray.optJSONObject(attachment);
                                            attachmentmodel.time = attachmentobject.optString("time");
                                            attachmentmodel.url = attachmentobject.optString("url");
                                            attachmentmodel.displayname = attachmentobject.optString("displayName");
                                            attachmentmodel.type = attachmentobject.optString("type");
                                            attachmentmodel.id = attachmentobject.optString("id");
                                            attachmentmodel.createdAt = attachmentobject.optString("createdAt");
                                            attachlistwithcomments.add(attachmentmodel);
                                        }
                                    }
                                    commentlist.add(commentmodel);

                                    model.comments = commentlist;
                                    model.attachmentwithcomments = attachlistwithcomments;
                                    model.setCommentsCount(dataobject.optInt("commentsCount"));

                                }

                                attachlist = new ArrayList<AttachmentModel>();
                                JSONArray atachments_array = (JSONArray) dataobject.opt("attachments");
                                if (atachments_array != null && atachments_array.length() > 0) {
                                    for (int a = 0; a < atachments_array.length(); a++) {
                                        AttachmentModel attachmodel = new AttachmentModel();
                                        JSONObject attachobject = atachments_array.optJSONObject(a);
                                        attachmodel.id = attachobject.optString("id");
                                        attachmodel.createdAt = attachobject.optString("createdAt");
                                        attachmodel.url = attachobject.optString("url");
                                        attachmodel.displayname = attachobject.optString("displayName");
                                        attachmodel.type = attachobject.optString("type");

                                        JSONObject user = attachobject.optJSONObject("user");
                                        attachmodel.userid = user.optString("_id");
                                        attachmodel.userdisplayname = user.optString("displayName");
                                        attachmodel.useravatarurl = user.optString("avatar");
                                        attachlist.add(attachmodel);
                                    }
                                }

                                model.attachments = attachlist;

                                owner = new ArrayList<CommentModel>();
                                JSONArray assigned = (JSONArray) dataobject.opt("assignedTo");
                                if (assigned != null && assigned.length() > 0) {
                                    for (int o = 0; o < assigned.length(); o++) {
                                        CommentModel citem = new CommentModel();
                                        JSONObject oobj = assigned.getJSONObject(o);
                                        citem.oemail = oobj.optString("email");
                                        citem.oid = oobj.optString("_id");
                                        citem.odisplayName = Util.capitalizeWords(oobj.optString("displayName"));
                                        citem.oavatarUrl = oobj.optString("avatar");
                                        citem.ophoneNo = oobj.optString("phoneNo");
                                        owner.add(citem);
                                        ccmodel = citem;
                                    }
                                }
                                model.owners = owner;
                                model.assigned = ccmodel;

                                ArrayList<CommentModel> subscriber = new ArrayList<CommentModel>();
                                JSONArray subscribers = (JSONArray) dataobject.opt("subscribers");
                                if (subscribers != null && subscribers.length() > 0) {
                                    for (int sb = 0; sb < subscribers.length(); sb++) {
                                        CommentModel item = new CommentModel();
                                        JSONObject sobj = subscribers.getJSONObject(sb);
                                        item.semail = sobj.optString("email");
                                        item.sid = sobj.optString("_id");
                                        item.sdisplayName = Util.capitalizeWords(sobj.optString("displayName"));
                                        item.savatarUrl = sobj.optString("avatarUrl");
                                        item.sphoneNo = sobj.optString("phoneNo");
                                        subscriber.add(item);
                                    }
                                }
                                model.subscribers = subscriber;

                                if (dataobject.optJSONObject("createdBy") != null) {
                                    JSONObject creator = dataobject.optJSONObject("createdBy");
                                    model.creatorid = creator.optString("_id");
                                    model.creatoremail = creator.optString("email");
                                    model.creatorphoneno = creator.optString("phoneNo");
                                    model.creatordisplayname = Util.capitalizeWords(creator.optString("displayName"));
                                    model.creatoravatarurl = creator.optString("avatar");
                                } else if (!dataobject.optString("createdBy").isEmpty()) {
                                    model.creatordisplayname = Util.capitalizeWords(dataobject.optString("createdBy"));
                                }


                                if (dataobject.optJSONObject("updatedBy") != null) {
                                    JSONObject updator = dataobject.optJSONObject("updatedBy");
                                    model.ucreatorid = updator.optString("_id");
                                    model.ucreatoremail = updator.optString("email");
                                    model.ucreatorphoneno = updator.optString("phoneNo");
                                    model.ucreatordisplayname = updator.optString("displayName");
                                    model.ucreatoravatarurl = updator.optString("avatar");
                                } else if (!dataobject.optString("updatedBy").isEmpty()) {
                                    model.ucreatordisplayname = dataobject.optString("updatedBy");
                                }

                                list.add(model);
                                original_list.add(model);
                            }
                        } catch (JSONException jEx) {
                            System.out.println(jEx.getLocalizedMessage());
                        }


                    }

                } else {
                    allItemsDownloaded = true;
                }
                setadapter("");
            } else {
                Toast.makeText(FeedsActivity.this, fmodel.message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }

    }

    public void setadapter(String s) {
        if (lv == null) {
            lv = (RecyclerView) findViewById(R.id.tasklist);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            lv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            lv.requestDisallowInterceptTouchEvent(true);
            lv.setLayoutManager(manager);
            lv.setNestedScrollingEnabled(false);
            lv.setHasFixedSize(false);
        }
        if (list.size() == 0) {
            adapter = new InboxAdapter(FeedsActivity.this, list,unreadfeedsCount);
            //adapter.setUndoOn(true);
            lv.setAdapter(adapter);
        } else {

            if (lv.getAdapter() == null) {
                lv.setBackgroundColor(Color.parseColor("#cecece"));
                adapter = new InboxAdapter(FeedsActivity.this, list,unreadfeedsCount);
                lv.setAdapter(adapter);
            } else {
                ((InboxAdapter) (lv.getAdapter())).notifyDataSetChanged();
            }

        }
        lv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = lv.getLayoutManager().getChildCount();
                int totalItemCount = lv.getLayoutManager().getItemCount();
                int currentFirstVisibleItem = ((LinearLayoutManager) lv.getLayoutManager()).findFirstVisibleItemPosition();

                int topRowVerticalPosition = (lv == null || lv.getLayoutManager().getChildCount() == 0) ? 0 : lv.getChildAt(0).getTop();
                refreshLayout.setEnabled(currentFirstVisibleItem == 0 && topRowVerticalPosition >= 0);
                if (dy > 0) //check for scroll down
                {
                    int lastInScreen = currentFirstVisibleItem + visibleItemCount;
                    if (totalItemCount > 0) {
                        if (lastInScreen == totalItemCount - 1 && !pagination) {
                            if (original_list.size() % 10 == 0 && !allItemsDownloaded)
                                pagination(lastInScreen);
                        }
                    }
                }
            }

        });


        pagination = false;

    }

    public void pagination(int lastInScreen) {
        if (Util.isOnline(FeedsActivity.this)) {
            pagination = true;
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("x-email-id", FDPreferences.getEmail());
            headers.put("x-access-token", FDPreferences.getAccessToken());

            NetworkConnector connect = new NetworkConnector(FeedsActivity.this, NetworkConnector.TYPE_GET, FDUrls.GET_WORKITEM_WITH_PAGINATION + limit + "&skip=" + lastInScreen, headers, null, this, true);
            connect.execute();
        } else {
            Toast.makeText(FeedsActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void configurationUpdated(boolean configUpdated) {

    }

    @Override
    public void onRefresh() {
        if (Util.isOnline(FeedsActivity.this)) {
            // list.clear();
            allItemsDownloaded = false;
            getAllTasks();

            refreshInProgress = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 4000);


        } else {
            refreshLayout.setRefreshing(false);
            Toast.makeText(FeedsActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
        if (refreshInProgress)
            return;
        refreshInProgress = false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 240) {
            if (data != null) {
                FeedsDataModel workItem = (FeedsDataModel) data.getSerializableExtra("WORKITEM");
                for (int item = 0; item < list.size(); item++) {
                    if (list.get(item).id.equals(workItem.id)) {
                        list.remove(item);
                        list.add(item, workItem);
                        break;
                    }
                }

                ((InboxAdapter) (lv.getAdapter())).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("unReadFeedsCount",unreadfeedsCount);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void getUnreadFeedsCount(int unReadFeedsCount) {
        unreadfeedsCount=unReadFeedsCount;
    }
}
