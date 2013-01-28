package com.sutil.rango;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

import com.example.holotest.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class FragmentOne extends Fragment {
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
	    View v = inflater.inflate(R.layout.fragment_one, container, false);

	    return  v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

}
