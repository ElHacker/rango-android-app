package com.sutil.rango;

import java.sql.Date;
import java.sql.Time;

/*
 * This class represents a recent call 
 */
public class Call {
	private int id;
	private String fb_id;
	private String first_name;
	private String last_name;
	private Date date;
	private Time time;
	
	// Empty Constructor
	public Call () {}

	public Call(int id, String fb_id, String first_name, String last_name,
			Date date, Time time) {
		super();
		this.id = id;
		this.fb_id = fb_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.date = date;
		this.time = time;
	}
	
	public Call(String fb_id, String first_name, String last_name,
			Date date, Time time) {
		super();
		this.fb_id = fb_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.date = date;
		this.time = time;
	}
	
	// Getters and setters
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFb_id() {
		return fb_id;
	}

	public void setFb_id(String fb_id) {
		this.fb_id = fb_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
	
}
