package com.sutil.rango;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class RequestsListFragment extends ListFragment {
	TextView showMessage;
	Context context;
	
	private final String TAG = "RequestsListFragment"; 
	
	private ListView listView;
	private List<BaseListElement> listElements;
	
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
	    View view = inflater.inflate(R.layout.fragment_two, container, false);
	    
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
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.d(TAG, "Facebook Session Opened");
		}
	}
	
	// Create the requests list pulling the data from Rango server
	private void makeRequestsListRequest(String user_id) { 
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
			// Set the list view adapter
			listView.setAdapter(new ActionListAdapter(getActivity(), 
						android.R.id.list, listElements, R.layout.requests_list_item));
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
	                // Get the fb_id of the clicked element of list
	            	String target_id = getProfilePictureView().getProfileId();
	            	// Get the name and description of the clicked element
	            	String target_name = getText1();
	            	String target_desc = getText2();
	            	// Get the current user's fb_id
	            	SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
	     			String my_fb_id = settings.getString("my_fb_id", "");
	            	
	            }
	        };
	    }
	}
}
