package com.sutil.rango;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.adapters.ActionListAdapter;
import com.sutil.rango.lib.BaseListElement;
import com.sutil.rango.lib.RestClient;

public class RequestsListActivity extends ListActivity {
	TextView showMessage;
	
	private final String TAG = "RequestsListFragment"; 
	
	private ListView listView;
	private List<BaseListElement> listElements;
	ProgressBar loadingProgress;
	Toast networkErrorToast;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.requests_list_activity);
	    
	    // Create action bar
	    ActionBar bar = getSupportActionBar();
	    bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	    bar.setTitle("Rango");
	    bar.setIcon(R.drawable.rango_logo);
	    bar.setDisplayHomeAsUpEnabled(true);
	    
	    listView = (ListView) findViewById(android.R.id.list);
	    loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
		networkErrorToast = Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT);
	    
		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<BaseListElement>();
		// Get my friends list
		SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
		String my_fb_id = settings.getString("my_fb_id", "");

		new RequestsListRequestAsyncTask(this).execute(my_fb_id);

	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
    }
	
	private class RequestsListRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

		Context context;
		
		public RequestsListRequestAsyncTask(Activity activity) {
			this.context = activity;
		}
		
		@Override
		protected void onPreExecute() {
			loadingProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String user_id = params[0];
			try {
				JSONArray json_friends = RestClient.get_friend_requests(user_id);
				for(int i = 0; i < json_friends.length(); i++) {
					JSONObject friend = json_friends.getJSONObject(i);
					ProfilePictureView profilePic = new ProfilePictureView(context);
					profilePic.setCropped(true);
					profilePic.setProfileId(friend.getString("fb_id"));
					String friend_full_name = friend.getString("first_name") + " " + friend.getString("last_name");
					// Create a list element with profile picture, name and description
					RequestsListElement peopleListElement = new RequestsListElement(
							profilePic, friend_full_name, "Quiere ser tu amigo");
					listElements.add(peopleListElement);
				}
				return true;
			} catch (Exception e) {
				Log.e(TAG, "Exception: " + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		
		protected void onPostExecute(Boolean hasLoaded) {
			if(hasLoaded) {
				loadingProgress.setVisibility(View.GONE);
				// Set the list view adapter
				listView.setAdapter(new ActionListAdapter(context, 
							android.R.id.list, listElements, R.layout.requests_list_item));
				// Set an empty view to the list view
				listView.setEmptyView(findViewById(android.R.id.empty));
			} else {
				// Network Error
				networkErrorToast.show();
			}
		}
		
	}
	
	// Represents an element of the friends list, supports profile picture
	// name, and a brief description
	private class RequestsListElement extends BaseListElement {

	    public RequestsListElement(ProfilePictureView profilePictureView, String name, String description) {
	        super(profilePictureView,
	              name,
	              description);
	    }

	    @Override
		public View.OnClickListener getOnClickListener() {
	        return new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	Log.d(TAG, "ID: " + getProfilePictureView().getProfileId());
	            }
	        };
	    }
	}
}