package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;

/**
 * Created by Arun on 29-08-2016.
 */
public class LatLongModel implements Serializable{
    private String Title="";
    private String Snippet="";
    private String Latitude="";
    private String  Longitude="";

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getSnippet() {
        return Snippet;
    }
    public void setSnippet(String snippet) {
        Snippet = snippet;
    }
    public String getLatitude() {
        return Latitude;
    }
    public void setLatitude(String latitude) {
        Latitude = latitude;
    }
    public String getLongitude() {
        return Longitude;
    }
    public void setLongitude(String longitude) {
        Longitude = longitude;
    }


}
