package com.sutil.rango;

import com.facebook.widget.ProfilePictureView;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;


/*
 * Used to store the data for each row in ListView
 * */
public abstract class BaseListElement {
	private ProfilePictureView profilePictureView;
	private String text1;
	private String text2;
	private int requestCode;
	
	private BaseAdapter adapter;

	public BaseListElement(ProfilePictureView profilePictureView, String text1, String text2) {
		super();
		this.profilePictureView = profilePictureView;
		this.text1 = text1;
		this.text2 = text2;
	}
	
	public void setAdapter(BaseAdapter adapter) {
	    this.adapter = adapter;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
		if (adapter != null) {
		    adapter.notifyDataSetChanged();
		}
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
		if (adapter != null) {
		    adapter.notifyDataSetChanged();
		}
	}

	public ProfilePictureView getProfilePictureView() {
		return profilePictureView;
	}
	
	protected abstract View.OnClickListener getOnClickListener();
	
}
