package com.sutil.rango;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

import com.facebook.widget.LoginButton;
import com.sutil.rango.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class FragmentThree extends Fragment {
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
	    View v = inflater.inflate(R.layout.fragment_three, container, false);
	    
	    // This allows the LoginButton to be embedded inside a Fragment,
	    // and will allow the fragment to receive the onActivityResult.onActivityResult()
	    // call rather than the Activity.
	    LoginButton auth_btn = (LoginButton) v.findViewById(R.id.login_button);
	    auth_btn.setFragment(this);
	    
	    return  v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

}
