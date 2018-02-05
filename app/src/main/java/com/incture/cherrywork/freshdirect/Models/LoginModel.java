package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;

/**
 * Created by Arun on 08-09-2016.
 */
public class LoginModel implements Serializable {
    public String accesstoken="";
    public String firstname="";
    public String lastName="";
    public String avatarUrl="";
    public String userId="";
    public String email="";
    public String designation="";
    public String employeeId="";

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String status="";
    public String message="";
    public String carrierName="";

    public String getAccesstoken() {
        return accesstoken;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getDesignation() {
        return designation;
    }

    public String getMessage() {
        return message;
    }
}
