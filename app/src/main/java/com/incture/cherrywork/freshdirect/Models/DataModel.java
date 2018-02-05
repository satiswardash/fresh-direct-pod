package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;

public class DataModel implements Serializable {

    private static final long serialVersionUID = 1L;
    public String id = "";
    public String startDate = "";
    public String endDate = "";
    public String week = "";



    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


}
