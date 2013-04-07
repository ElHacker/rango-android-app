package com.sutil.rango;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.FacebookException;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.android.gcm.GCMRegistrar;
import com.sutil.rango.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class TabsActivity extends Activity {
	// GCM service sender id
	private static final String SENDER_ID = "108747417910";
	public static final String TAG = "TabsActivity";
	
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	String my_fb_id;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	    
	    // Get current user's facebook id
	    SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
		my_fb_id = settings.getString("my_fb_id", "");
		// Register the device with the GCM service
	 		GCMRegistrar.checkDevice(this);
	 		GCMRegistrar.checkManifest(this);
	 		final String gcm_reg_id = GCMRegistrar.getRegistrationId(this);
	 		if (gcm_reg_id.equals("")) {
	 			Log.v(TAG, "REGISTERING");
	 			GCMRegistrar.register(this, SENDER_ID);
	 		} else {
	 			Log.v(TAG, "Already registered");
	 			Log.v(TAG, gcm_reg_id);
	 			// Set the id for posting the reg id
            	RestClient.post_user_gcm_id(my_fb_id, gcm_reg_id);
	 			
	 		}
	    
	    // The UiLifecycleHelper class constructor takes in a Session.StatusCallback listener
	    // implementation that you can use to respond to session state changes
	    // by overriding the listener's call() method. 
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	
	    mViewPager = new ViewPager(this);
	    mViewPager.setId(R.id.pager);
	    setContentView(mViewPager);
	    
	    // Create action bar
	    ActionBar bar = getSupportActionBar();
	    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	    bar.setTitle("Rango");
	    bar.setIcon(R.drawable.rango_logo);
	    
	    mTabsAdapter = new TabsAdapter(this, mViewPager);
	    
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_friends_list), FriendsListFragment.class, null);
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_calls_list),CallsListFragment.class, null);
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_user_profile), UserProfileFragment.class, null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.invite_button:
				sendRequestDialog();
				return true;
			case R.id.notifications_button:
				Intent intent = new Intent(this, RequestsListActivity.class);
				startActivity(intent);
				return true;
		}
		return false;
	}
	
	/*
	 * Facebook friend request dialog
	 * */
	private void sendRequestDialog() {
	    Bundle params = new Bundle();
	    params.putString("message", "Habla con tus amigos por radio.");
	    final Context context = (Context) this;

	    WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(context,
	            Session.getActiveSession(),
	            params))
	            .setOnCompleteListener(new OnCompleteListener() {

	                @Override
	                public void onComplete(Bundle values,
	                    FacebookException error) {
	                    if (error != null) {
	                        if (error instanceof FacebookOperationCanceledException) {
	                            Toast.makeText(context, 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(context, 
	                                "Network Error", 
	                                Toast.LENGTH_SHORT).show();
	                        }
	                    } else {
	                        final String requestId = values.getString("request");
	                        if (requestId != null) {
	                        	// Get invited friends ids 
	                        	// and make a friend request on rango server too
	                        	int invited_friends_ids_size = values.keySet().size() - 1;
	                        	for (int i = 0; i < invited_friends_ids_size; i++) {
	                        		String friend_id = values.getString("to[" + i + "]");
	                        		// Rango invite friend
	                        		RestClient.post_friend_request(my_fb_id, friend_id);
	                        	}
	                            Toast.makeText(context, 
	                                "Request sent",  
	                                Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(context, 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        }
	                    }   
	                }

	            })
	            .build();
	    requestsDialog.show();
	}
	
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        finish();
	    }
	}
	
}