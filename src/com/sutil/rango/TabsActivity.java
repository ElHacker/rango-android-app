package com.sutil.rango;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sutil.rango.adapters.TabsAdapter;
import com.sutil.rango.fragments.CallsListFragment;
import com.sutil.rango.fragments.FriendsListFragment;
import com.sutil.rango.fragments.UserProfileFragment;
import com.sutil.rango.lib.RestClient;

public class TabsActivity extends Activity {
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	
	/**
	 * Default lifespan (7 days) of a reservation until it is considered expired. 
	 */
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	
	/** 
	 * GCM service sender id
	 */
	private static final String SENDER_ID = "108747417910";
	public static final String TAG = "TabsActivity";
	
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	String my_fb_id;
	
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	
	String regId;
	
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
		
		context = getApplicationContext();
		regId = getRegistrationId(context);
		
		if (regId.length() == 0) {
			// Register the device with the GCM service
			registerBackground();
		}
		
		gcm  = GoogleCloudMessaging.getInstance(this);
	    
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
				sendRequestDialog(findViewById(item.getItemId()));
				return true;
			case R.id.notifications_button:
				Intent intent = new Intent(this, RequestsListActivity.class);
				startActivity(intent);
				return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		// Send user to the device home screen
		// TODO: this is not the best method to exit the app
		// because it does not end the app. It just sends it to the background
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	/*
	 * Facebook friend request dialog
	 * */
	public void sendRequestDialog(View view) {
	    Bundle params = new Bundle();
	    params.putString("message", "Habla con tus amigos por radio.");
	    final Context context = (Context) this;

	    WebDialog requestsDialog = (
	        new WebDialog.Builder(context,
	            Session.getActiveSession(),
	            "apprequests",
	            params))
	            .setOnCompleteListener(
	            	new OnCompleteListener() {

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
		                            // Track the 'facebook_invite' event on analytics
		                            Tracker myTracker = EasyTracker.getTracker();
		                            myTracker.sendEvent("growth_action", "invite_friends", "facebook_invite", Long.valueOf(invited_friends_ids_size) );
		                        } else {
		                            Toast.makeText(context, 
		                                "Request cancelled", 
		                                Toast.LENGTH_SHORT).show();
		                        }
		                    }   
		                }
	            	}
	            ).build();
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
	public void onStart() {
		super.onStart();
		// Set Context before using EasyTracker. Note that the SDK will
		// use the application context.
		EasyTracker.getInstance().setContext(this);
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
	
	/**
	 * Gets the current registration id for application on GCM service.
	 * <p>
	 * If result is empty, the registration has failed.
	 *
	 * @return registration id, or empty string if the registration is not
	 *         complete.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.length() == 0) {
	        Log.v(TAG, "Registration not found.");
	        return "";
	    }
	    // check if app was updated; if so, it must clear registration id to
	    // avoid a race condition if GCM sends a message
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion || isRegistrationExpired()) {
	        Log.v(TAG, "App version changed or registration expired.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(TabsActivity.class.getSimpleName(), 
    		Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Checks if the registration has expired.
	 *
	 * <p>To avoid the scenario where the device sends the registration to the
	 * server but the server loses it, the app developer may choose to re-register
	 * after REGISTRATION_EXPIRY_TIME_MS.
	 *
	 * @return true if the registration has expired.
	 */
	private boolean isRegistrationExpired() {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    // checks if the information is not stale
	    long expirationTime =
	            prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
	    return System.currentTimeMillis() > expirationTime;
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration id, app versionCode, and expiration time in the 
	 * application's shared preferences.
	 */
	private void registerBackground() {
		new RegisterGCMAsyncTask().execute(this);
	}
	
	/**
	 * Stores the registration id, app versionCode, and expiration time in the
	 * application's {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration id
	 */
	private void setRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.v(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

	    Log.v(TAG, "Setting registration expiry time to " +
	            new Timestamp(expirationTime));
	    editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
	    editor.commit();
	}
	
    // Internal class that executes an async task
    // registers the device with the Google cloud messaging service
    private class RegisterGCMAsyncTask extends AsyncTask<Context, Void, String> {
    	
		@Override
		protected String doInBackground(Context... contexts) {
			Context context = contexts[0];
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regId = gcm.register(SENDER_ID);
				msg = "Device registered, registration id=" + regId;
				// Save the registration id to server
				// and to device - no need to register again
				RestClient.post_user_gcm_id(my_fb_id, regId);
				setRegistrationId(context, regId);
			} catch (IOException ex) {
				msg = "Error: " + ex.getMessage();
			}
			return msg;
			
		}
		
		protected void onPostExecute(String msg) {
			Log.d(TAG, msg);
		}
    }
	
}