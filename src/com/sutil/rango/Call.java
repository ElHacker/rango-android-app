package com.sutil.rango;

import java.sql.Date;
import java.sql.Time;

/*
 * This class represents a recent call 
 */
public class Call {
	private int id;
	private String fbId;
	private String firstName;
	private String lastName;
	private Date date;
	private Time time;
	
	// Empty Constructor
	public Call () {}

	public Call(int id, String fbId, String firstName, String lastName,
			Date date, Time time) {
		super();
		this.id = id;
		this.fbId = fbId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.date = date;
		this.time = time;
	}
	
	public Call(String fbId, String firstName, String lastName,
			Date date, Time time) {
		super();
		this.fbId = fbId;
		this.firstName = firstName;
		this.lastName = lastName;
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

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
