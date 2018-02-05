package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class TimesheetTaskModel implements Serializable {


    private static final long serialVersionUID = 1L;
    String day = "";
    ArrayList<TaskTypeModel> tasks = new ArrayList<>();


    float totalHours = 0;
    String allocatedHours = "";
    private String date;


    public void setAllocatedHours(String allocatedHours) {
        this.allocatedHours = allocatedHours;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<TaskTypeModel> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskTypeModel> tasks) {
        this.tasks = tasks;
    }

    public float getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(float totalHours) {
        this.totalHours = totalHours;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
