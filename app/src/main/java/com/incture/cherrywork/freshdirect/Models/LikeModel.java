package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;

public class LikeModel implements Serializable {

    public String likeid = "";

    public String getLikeid() {
        return likeid;
    }

    public void setLikeid(String likeid) {
        this.likeid = likeid;
    }

    public String displayname = "";
    public String avatarurl = "";

}
