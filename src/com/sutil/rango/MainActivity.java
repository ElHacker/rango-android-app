package com.sutil.rango;


import java.util.Arrays;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class MainActivity extends FragmentActivity {
	
	private static final String TAG = "MainActivity";
	private static final int SPLASH = 0;
	private static final int FRAGMENT_COUNT = SPLASH +1;
	private static Intent tabsScreen = null;	
	private boolean isResumed = false;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
		    @Override
		    public void call(Session session, 
		            SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		

	    FragmentManager fm = getSupportFragmentManager();
	    fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);

	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	    
	    // Set the facebook login permissions
	    LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
	    login_button.setReadPermissions(Arrays.asList("email"));
	    
	    // Starting a new intent for TabsActivity
	    tabsScreen = new Intent(getApplicationContext(), TabsActivity.class);
	    
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to start the TabsScreen activity
	    	Log.d(TAG, "SESSION OPENED");
	    	changeUIWhenLogin();
	    } else {
	        // otherwise present the splash screen
	        // and ask the user to login.
	    	Log.d(TAG, "SESSION NOT OPENED");
	        showFragment(SPLASH, false);
	        changeUIWhenLogout();
	    }
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	        	Log.d(TAG, "SSC SESION OPENED");
	        	changeUIWhenLogin();
	        	// Logged in
	        	// Get my user information from facebook
	        	makeFacebookMeRequest(session);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	        	Log.d(TAG, "SSC SESION NOT OPENED");
	        	changeUIWhenLogout();
	            showFragment(SPLASH, false);
	        }
	    }
	}
	
	private void changeUIWhenLogin() {
		findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
    	findViewById(R.id.loginProgressBar).setVisibility(View.VISIBLE);
	}
	
	private void changeUIWhenLogout() {
		findViewById(R.id.login_button).setVisibility(View.VISIBLE);
    	findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
	}
	
	private void makeFacebookMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                	Log.d(TAG, "Saving Shared preferences");
	                	// Save my fb user information in shared preferences
	                	SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
	                	SharedPreferences.Editor editor = settings.edit();
	                	editor.putString("my_fb_id", user.getId());
	                	editor.putString("my_fb_first_name", user.getFirstName());
	                	editor.putString("my_fb_last_name", user.getLastName());
	                	editor.commit();
	                	// post a new user in rango if non existent
	                	RestClient.post_user(user.getId(), (String) user.asMap().get("email"), user.getFirstName(), user.getLastName());
	                	// Start the tab screen activity
	                    startActivity(tabsScreen);
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
}