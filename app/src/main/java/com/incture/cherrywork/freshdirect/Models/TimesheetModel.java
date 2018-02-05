package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;

public class TimesheetModel implements Serializable {
	boolean isSubmitted=false;
	String lastModified="";
	String week="";

	public boolean isSubmitted() {
		return isSubmitted;
	}
	public void setSubmitted(boolean isSubmitted) {
		this.isSubmitted = isSubmitted;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}

	
	

}
