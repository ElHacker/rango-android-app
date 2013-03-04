package com.sutil.rango;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

import com.sutil.rango.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentTwo extends Fragment {
	TextView showMessage;
	Context context;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		context = getActivity();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.fragment_two, container, false);
	    
	    Button notify = (Button) v.findViewById(R.id.notify);
	    
	    notify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			
			}
	    	
	    });

	    return  v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

}
