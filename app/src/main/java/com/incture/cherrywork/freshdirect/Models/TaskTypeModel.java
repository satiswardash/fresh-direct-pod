package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskTypeModel implements Serializable {
    String taskName = "";
    String taskLevel = "";
    String taskLevelCode = "";

    float totalHours = 0;
    String counter = "";
    String taskId;
    String mileStone;
    String taskType;
    String associatedProjectID;
    String associatedProjectName;
    ArrayList<TaskTypeModel> taskTypes = new ArrayList<>();



    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setMileStone(String mileStone) {
        this.mileStone = mileStone;
    }

    public String getAssociatedProjectID() {
        return associatedProjectID;
    }

    public void setAssociatedProjectID(String associatedProjectID) {
        this.associatedProjectID = associatedProjectID;
    }

    public String getAssociatedProjectName() {
        return associatedProjectName;
    }

    public void setAssociatedProjectName(String associatedProjectName) {
        this.associatedProjectName = associatedProjectName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCounter() {
        return counter;
    }


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Float getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(float totalHours) {
        this.totalHours = totalHours;
    }

    public ArrayList<TaskTypeModel> getTaskTypes() {
        return taskTypes;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getTaskLevelCode() {
        return taskLevelCode;
    }

    public void setTaskLevelCode(String taskLevelCode) {
        this.taskLevelCode = taskLevelCode;
    }


}
