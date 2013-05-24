package com.sutil.rango.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.R;

public class UserProfileFragment extends Fragment {
	TextView showMessage;
	Context context;
	
	private static final String TAG = "FragmentThree";
	private ProfilePictureView profilePictureView;
	private TextView userFirstNameText;
	private TextView userLastNameText;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		context = getActivity();
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.profile_fragment, container, false);
	    
	    // Find the user's profile picture custom view
	    profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
	    profilePictureView.setCropped(true);

	    // Find the user's name view
	    userFirstNameText = (TextView) view.findViewById(R.id.profile_user_first_name);
	    userLastNameText = (TextView) view.findViewById(R.id.profile_user_last_name);
	    
	    
	    // This allows the LoginButton to be embedded inside a Fragment,
	    // and will allow the fragment to receive the onActivityResult.onActivityResult()
	    // call rather than the Activity.
	    LoginButton auth_btn = (LoginButton) view.findViewById(R.id.login_button);
	    auth_btn.setFragment(this);
	    
	    // Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	    	setProfileInfo();
	       
	    }
	    return  view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	private void setProfileInfo() {
		SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
		String my_fb_id = settings.getString("my_fb_id", "");
		String my_fb_first_name = settings.getString("my_fb_first_name", "Foo");
		String my_fb_last_name = settings.getString("my_fb_last_name", "Bar");
		// Set the id for the ProfilePictureView
		// view that in turn displays the profile picture.
		profilePictureView.setProfileId(my_fb_id);
		// Set the Textview's text to the user's name.
		userFirstNameText.setText(my_fb_first_name);
		userLastNameText.setText(my_fb_last_name);
	}
	
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        setProfileInfo();
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

}
