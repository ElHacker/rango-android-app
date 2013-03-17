package com.sutil.rango;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;

public class RequestsListActivity extends ListActivity {
	TextView showMessage;
	
	private final String TAG = "RequestsListFragment"; 
	
	private ListView listView;
	private List<BaseListElement> listElements;
	
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
	    
		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<BaseListElement>();
		// Add an item for the friend picker
		// listElements.add(new PeopleListElement(0));

		// Get my friends list
		SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
		String my_fb_id = settings.getString("my_fb_id", "");
		makeRequestsListRequest(my_fb_id);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.requests_list_activity, container, false);
	    
	    listView = (ListView) view.findViewById(android.R.id.list);
	    
		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<BaseListElement>();
		// Add an item for the friend picker
		// listElements.add(new PeopleListElement(0));

		// Get my friends list
		SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
		String my_fb_id = settings.getString("my_fb_id", "");
		makeRequestsListRequest(my_fb_id);
		
	    return  view;
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
	
	// Create the requests list pulling the data from Rango server
	private void makeRequestsListRequest(String user_id) { 
		try {
			JSONArray json_friends = RestClient.get_friend_requests(user_id);
			for(int i = 0; i < json_friends.length(); i++) {
				JSONObject friend = json_friends.getJSONObject(i);
				ProfilePictureView profilePic = new ProfilePictureView(this);
				profilePic.setCropped(true);
				profilePic.setProfileId(friend.getString("fb_id"));
				String friend_full_name = friend.getString("first_name") + " " + friend.getString("last_name");
				// Create a list element with profile picture, name and description
				RequestsListElement peopleListElement = new RequestsListElement(
						profilePic, friend_full_name, "Quiere ser tu amigo");
				listElements.add(peopleListElement);
			}
			// Set the list view adapter
			listView.setAdapter(new ActionListAdapter(this, 
						android.R.id.list, listElements, R.layout.requests_list_item));
			// Set an empty view to the list view
			listView.setEmptyView(findViewById(android.R.id.empty));
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
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
	    protected View.OnClickListener getOnClickListener() {
	        return new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	Log.d(TAG, "ID: " + getProfilePictureView().getProfileId());
	            }
	        };
	    }
	}
}