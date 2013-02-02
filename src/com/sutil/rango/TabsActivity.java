package com.sutil.rango;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.sutil.rango.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

public class TabsActivity extends Activity {
	
	public static final String TAG = "TabsActivity";
	
	
	
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	
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
	    
	    
	    
	    mTabsAdapter = new TabsAdapter(this, mViewPager);
	    
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_one_text),FragmentOne.class, null);
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_two_text), FragmentTwo.class, null);
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_three_text), FragmentThree.class, null);
	    
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