package com.incture.cherrywork.freshdirect.Models;



import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Arun on 28-09-2015.
 */
public class FeedsDataModel implements Serializable {

    public String name = "";
    public String message = "";
    public String content = "";
    public ArrayList<AttachmentModel> attachmentwithcomments = new ArrayList<>();
    public TimesheetModel timesheet = new TimesheetModel();

    public CommentModel assigned = new CommentModel();
    public String ucreatorid = "";
    public String ucreatoremail = "";
    public String ucreatorphoneno = "";
    public String ucreatordisplayname = "";
    public String ucreatoravatarurl = "";
    public FeedsDataModel all;
    public String type = "";
    public String title = "";
    public String description = "";
    public String source = "";
    public String createdAt = "";

    public String Public = "";
    public String id = "";
    public String lastModified = "";
    public String status = "";
    public String deleted = "";
    public String time = "";
    public String activity = "";

    public String displayname = "";
    public String avatarurl = "";
    public ArrayList<AttachmentModel> attachments = new ArrayList<AttachmentModel>();
    public ArrayList<CommentModel> comments = new ArrayList<CommentModel>();
    public String creatorid = "";
    public String creatoremail = "";
    public String creatorphoneno = "";
    public String creatoravatarurl = "";
    public String creatordisplayname = "";
    public String date = "";
   public boolean isEdited=false;
    public String body = "";

    public String action = "";
    public ArrayList<LikeModel> likes = new ArrayList<LikeModel>();


    public ArrayList<CommentModel> subscribers = new ArrayList<CommentModel>();
    public ArrayList<CommentModel> owners = new ArrayList<CommentModel>();
    public String subtype = "";
    public String value = "";
    public boolean liked = false;
    public String header = "";
    public String bpmCreatedTime="";
    public String bpmId="";
    public String bpmModelId="";
    public String bpmName="";
    public String bpmPriority="";
    public String bpmStatus="";
    public String bpmSubject="";
    public String bpmTaskInitiator="";
    public String bpmTaskType="";
    public String requestId="";
    public String vendor="";
    public String companyCode="";
    public String currency="";
    public String lorfCreatedTime="";
    public String lorfId="";
    public String lorfModelId="";
    public String lorfName="";
    public String Priority="";
    public String lorfStatus="";
    public String lorfSubject="";
    public String lorfTaskInitiator="";
    public String lorfTaskType="";
    public String trainId="";
    public String incidentStatus="";
    public String milePost="";
    public String subDivision="";
    public String workOrder="";
    public String LORFEventCode="";
    public String incidentId="";
    public String actualCost="";
    public String plantCost="";
    public String EventCode="";
    public String Description="";
    public String unreadfeedsCount="";

    public ArrayList<String> getReadList() {
        return readList;
    }

    public void setReadList(ArrayList<String> readList) {
        this.readList = readList;
    }

    public ArrayList<String> readList=new ArrayList<>();

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }


    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getPublic() {
        return Public;
    }

    /**
     * @param public1 the public to set
     */
    public void setPublic(String public1) {
        Public = public1;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the deleted
     */
    public String getDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the activity
     */
    public String getActivity() {
        return activity;
    }

    /**
     * @param activity the activity to set
     */
    public void setActivity(String activity) {
        this.activity = activity;
    }

    /**
     * @return the activityLogArrayId
     */

    public String getDisplayname() {
        return displayname;
    }

    /**
     * @param displayname the displayname to set
     */
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    /**
     * @return the avatarurl
     */
    public String getAvatarurl() {
        return avatarurl;
    }

    /**
     * @param avatarurl the avatarurl to set
     */
    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    /**
     * @return the attachments
     */
    public ArrayList<AttachmentModel> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(ArrayList<AttachmentModel> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the comments
     */
    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }



    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setShowlikes(boolean showlikes) {
        this.showlikes = showlikes;
    }

    public boolean showlikes = true;


    public int getLikescount() {
        return likescount;
    }

    public void setLikescount(int likescount) {
        this.likescount = likescount;
    }

    public int likescount = 0;

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int commentsCount = 0;
}


